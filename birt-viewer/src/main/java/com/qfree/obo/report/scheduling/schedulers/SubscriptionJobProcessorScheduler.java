package com.qfree.obo.report.scheduling.schedulers;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.scheduling.jobs.SubscriptionJobProcessorScheduledJob;

/**
 * This bean manages the scheduling of SubscriptionJobProcessorScheduledJob.
 * 
 * This bean has @PostConstruct and @PreDestroy methods, which will get called
 * because Spring uses "eager" loading/initialization of beans. This class's
 * package is "component scanned" from SchedulingConfig.java, which causes a
 * bean to be created due to the @Component annotation here. That starts the
 * life cycle of this bean. In other words, it is not necessary for this bean to
 * get injected somewhere in order for the @PostConstruct method to run.
 * 
 * @author jeffreyz
 *
 */
@Component
@PropertySource("classpath:config.properties")
public class SubscriptionJobProcessorScheduler {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduler.class);

	private static final String JOB_NAME = "JobProcessor_JobName";
	private static final String JOB_GROUP = "JobProcessor_JobGroup";
	private static final JobKey JOB_KEY = JobKey.jobKey(JOB_NAME, JOB_GROUP);

	private static final String TRIGGER_NAME = "JobProcessor_TriggerName";
	private static final String TRIGGER_GROUP = "JobProcessor_TriggerGroup";
	private static final TriggerKey TRIGGER_KEY = TriggerKey.triggerKey(TRIGGER_NAME, TRIGGER_GROUP);

	private final SchedulerFactoryBean schedulerFactoryBean;
	private final SubscriptionJobProcessorScheduledJob subscriptionJobProcessorScheduledJob;
	private final JobRepository jobRepository;
	private final JobStatusRepository jobStatusRepository;

	@Autowired
	public SubscriptionJobProcessorScheduler(
			SchedulerFactoryBean schedulerFactoryBean,
			SubscriptionJobProcessorScheduledJob subscriptionJobProcessorScheduledJob,
			JobRepository jobRepository,
			JobStatusRepository jobStatusRepository) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.subscriptionJobProcessorScheduledJob = subscriptionJobProcessorScheduledJob;
		this.jobRepository = jobRepository;
		this.jobStatusRepository = jobStatusRepository;
	}

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation above.
	 */
	@Autowired
	private Environment env;

	@PostConstruct
	public void startup() {

		if (env.getProperty("startup.schedule.jobprocessor").equals("true")) {

			logger.info("Starting subscription job processor...");

			long sleepMs = 1000L;
			long maxWaitMs = 10000L;
			long currentWaitMs = 0;
			/*
			 * It is possible that this method runs before the Quartz scheduler
			 * has had time to start up. If so, we wait here in a loop for a 
			 * certain maximum time and wait for it to start. Within the loop we
			 * request the scheduler to start - this seems to be necessary even 
			 * though the scheduler will eventually start later in the startup
			 * process anyway even if we do not request that here. If it does 
			 * not start within this period of time, we proceed anyway.
			 */
			while (!schedulerFactoryBean.isRunning()) {
				logger.info("Waiting for Quartz scheduler to start up...");
				schedulerFactoryBean.start(); // This is necessary. Do not remove!
				try {
					Thread.sleep(sleepMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentWaitMs += sleepMs;
				if (currentWaitMs > maxWaitMs) {
					logger.warn("Quartz scheduler still not started after waiting {} seconds. Proceeding.",
							Long.valueOf(maxWaitMs).doubleValue() / 1000D);
					break;
				}
			}

			try {
				scheduleJob();
			} catch (ClassNotFoundException | NoSuchMethodException | SchedulerException e) {
				logger.error("Could not start the subscription job processor during application startup", e);
			}

		} else {
			logger.info("Scheduling of the subscription job processor is disabled in config.properties.");
		}

	}

	public void scheduleJob() throws ClassNotFoundException, NoSuchMethodException, SchedulerException {

		String repeatIntervalAsString = env.getProperty("schedule.jobprocessor.repeatinterval");
		logger.debug("schedule.jobprocessor.repeatinterval = {}", repeatIntervalAsString);

		String startDelayAsString = env.getProperty("schedule.jobprocessor.startDelay");
		logger.debug("schedule.jobprocessor.startDelay = {}", startDelayAsString);

		long repeatIntervalSeconds = Long.parseLong(repeatIntervalAsString);
		long startDelaySeconds = Long.parseLong(startDelayAsString);

		scheduleJob(repeatIntervalSeconds, startDelaySeconds);

	}

	public void scheduleJob(long repeatIntervalSeconds, long startDelaySeconds)
			throws ClassNotFoundException, NoSuchMethodException, SchedulerException {

		if (!schedulerFactoryBean.isRunning()) {
			logger.warn("Attempt to schedule the subscription job processor, but the scheduler is not running");
		}

		/*
		 * Get the underlying Quartz Scheduler. According to the Javadoc for
		 * org.springframework.scheduling.quartz.SchedulerFactoryBean :
		 * 
		 *   For dynamic registration of jobs at runtime, use a bean reference 
		 *   to this SchedulerFactoryBean to get direct access to the Quartz 
		 *   Scheduler (org.quartz.Scheduler). This allows you to create new 
		 *   jobs and triggers, and also to control and monitor the entire 
		 *   Scheduler.
		 * 
		 * So it seems that in order to schedule jobs dynamically (which is what
		 * we are doing here), one *must* use the Quartz Scheduler object
		 * obtained here.
		 */
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		/*
		 * Do not re-schedule the subscription job processor if it is already
		 * scheduled.
		 */
		if (!scheduler.checkExists(JOB_KEY)) {

			if (env.getProperty("startup.schedule.jobprocessor.requeuerunningjobs").equals("true")) {
				/*
				 * If any Job entities have status "RUNNING", we change that to
				 * "QUEUED" here. This is to treat the (hopefully unlikely) case
				 * where something goes wrong when the Job processor, i.e.,
				 * SubscriptionJobProcessorScheduledJob.run(), is processing a
				 * subscription Job. This could be a power failure, nasty 
				 * exception, ... In that case, the Job will be left in the 
				 * state "RUNNING", and it will not get processed whenever the 
				 * Job processor is restarted.
				 * 
				 * Under the assumption that any Job entities found here, before
				 * the Job processor is started/scheduled, have somehow been 
				 * left in the state "RUNNING" but were never fully processed,
				 * we set their status back to "QUEUED" so that they will be
				 * picked up the next itme that the Job processor rubs.
				 * 
				 * It is essential that we do not run the Job processor in 
				 * multiple threads; otherwise, this block of code might requeue
				 * Jobs that are actively being processed. If we *do* implement
				 * a multi-threaded Job processor, then we should only perform
				 * this requeuing that is done here if we know that *NONE* of
				 * those threads are running.
				 */

				JobStatus jobStatus_QUEUED = jobStatusRepository.findOne(JobStatus.QUEUED_ID);

				List<Job> runningJobs = jobRepository.findByJobStatusJobStatusId(JobStatus.RUNNING_ID);
				if (runningJobs.size() > 0) {
					logger.warn("{} running Jobs to re-queue", runningJobs.size());
					for (Job job : runningJobs) {
						logger.debug("job = {}", job);
						logger.info("Setting status to \"QUEUED\" for job = {}", job);
						job.setJobStatus(jobStatus_QUEUED);
						job = jobRepository.save(job);
					}
				}

			}

			/*
			 * Create a factory for obtaining a Quartz JobDetail.
			 */
			MethodInvokingJobDetailFactoryBean subscriptionJobProcessorJobDetailFactory = new MethodInvokingJobDetailFactoryBean();
			subscriptionJobProcessorJobDetailFactory.setTargetObject(subscriptionJobProcessorScheduledJob);
			subscriptionJobProcessorJobDetailFactory.setTargetMethod("run");
			subscriptionJobProcessorJobDetailFactory.setName(JOB_NAME);
			subscriptionJobProcessorJobDetailFactory.setGroup(JOB_GROUP);
			/*
			 * This is important. Without it, the "run" method of the bean
			 * "subscriptionJobProcessorScheduledJob" might run simultaneously in 
			 * multiple threads. This would happen if:
			 * 
			 *   1. The "run" method runs for a time interval that is longer than
			 *      repeatIntervalSeconds. In this case, another copy of the
			 *      job scheduler will still be started "repeatIntervalSeconds"
			 *      after the previous invocation, even if the previous invocation
			 *      is still running.
			 *      
			 *   2. The subscription job processor is forced to run immediately
			 *      with triggerJob() just before the job is scheduled to run anyway
			 *      according to its trigger. In this case, the trigger will start 
			 *      another copy, even though we *just* forced a copy to run 
			 *      immediately with triggerJob().
			 */
			subscriptionJobProcessorJobDetailFactory.setConcurrent(false);
			subscriptionJobProcessorJobDetailFactory.afterPropertiesSet();

			/*
			 * Create a factory for obtaining a Quartz SimpleTrigger, to be used for
			 * subscriptionJobProcessorJobDetailFactory.
			 */
			SimpleTriggerFactoryBean subscriptionJobProcessorTriggerFactory = new SimpleTriggerFactoryBean();
			subscriptionJobProcessorTriggerFactory
					.setJobDetail(subscriptionJobProcessorJobDetailFactory.getObject());
			subscriptionJobProcessorTriggerFactory.setName(TRIGGER_NAME);
			subscriptionJobProcessorTriggerFactory.setGroup(TRIGGER_GROUP);
			subscriptionJobProcessorTriggerFactory.setStartDelay(startDelaySeconds * 1000L);
			subscriptionJobProcessorTriggerFactory.setRepeatInterval(repeatIntervalSeconds * 1000L);
			subscriptionJobProcessorTriggerFactory.afterPropertiesSet();

			/*
			 * Schedule the subscription job processor to run according the schedule
			 * defined by its trigger.
			 */
			scheduler.scheduleJob(
					subscriptionJobProcessorJobDetailFactory.getObject(),
					subscriptionJobProcessorTriggerFactory.getObject());

		} else {
			logger.warn(
					"Attempt to schedule the subscription job processor, but it is already registered with the scheduler");
			/*
			 * TODO At this point we could try resuming the job IN CASE it was paused
			 * However, it is not clear at this point if that is the best behaviour to 
			 * implement, so I will do nothing for now.
			 */
		}

	}

	/**
	 * Forces the subscription job processor to run immediately.
	 * 
	 * This will force the processor to run even if the job is paused in the
	 * Quartz scheduler; however, it will run only once, which means that this
	 * will not UNpause the job.
	 * 
	 * @throws SchedulerException
	 */
	public void triggerJob() throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Triggering the subscription job processor to run immediately");
				scheduler.triggerJob(JOB_KEY);
			} else {
				logger.warn(
						"Attempt to trigger the subscription job processor, but it is not registered with the scheduler");
			}
		} else {
			logger.warn(
					"Attempt to trigger the subscription job processor, but the scheduler is not running");
		}
	}

	public void pauseJob() throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Pausing the subscription job processor");
				scheduler.pauseJob(JOB_KEY);
			} else {
				logger.warn(
						"Attempt to pause the subscription job processor, but it is not registered with the scheduler");
			}
		} else {
			logger.warn(
					"Attempt to pause the subscription job processor, but the scheduler is not running");
		}
	}

	public void resumeJob() throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Resuming the subscription job processor");
				scheduler.resumeJob(JOB_KEY);
			} else {
				logger.warn(
						"Attempt to resume the subscription job processor, but it is not registered with the scheduler");
			}
		} else {
			logger.warn(
					"Attempt to resume the subscription job processor, but the scheduler is not running");
		}
	}

	public void unscheduleJob() throws SchedulerException {
		//if (schedulerFactoryBean.isRunning()) {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		if (scheduler.checkExists(JOB_KEY)) {
			logger.info("Deleting the subscription job processor from the Quartz scheduler");
			scheduler.deleteJob(JOB_KEY);
		} else {
			logger.warn(
					"Attempt to unschedule the subscription job processor, but it is not registered with the scheduler");
		}

		//} else {
		//	logger.warn("Attempt to unschedule the subscription job processor, but the scheduler is not running");
		//	}
	}

	@PreDestroy
	public void shutdown() {
		try {
			unscheduleJob();
		} catch (SchedulerException e) {
			logger.error(
					"Exception thrown when attempting to delete the subscription job processor from the Quartz scheduler",
					e);
		}
	}

}
