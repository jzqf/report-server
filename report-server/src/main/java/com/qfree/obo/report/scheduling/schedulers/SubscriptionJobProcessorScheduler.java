package com.qfree.obo.report.scheduling.schedulers;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
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
import com.qfree.obo.report.dto.JobProcessorResource;
import com.qfree.obo.report.exceptions.JobProcessorAlreadyScheduledException;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotPause;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotResume;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotStop;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotTrigger;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotPause;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotResume;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotTrigger;
import com.qfree.obo.report.scheduling.jobs.SubscriptionJobProcessorScheduledJob;
import com.qfree.obo.report.util.DateUtils;

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
	 * annotation.
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
			} catch (ClassNotFoundException | NoSuchMethodException | SchedulerException
					| JobProcessorAlreadyScheduledException e) {
				logger.error("Could not start the subscription job processor during application startup", e);
			}

		} else {
			logger.info("Scheduling of the subscription job processor is disabled in config.properties.");
		}

	}

	public void scheduleJob() throws ClassNotFoundException, NoSuchMethodException, SchedulerException,
			JobProcessorAlreadyScheduledException {

		//		String repeatIntervalAsString = env.getProperty("schedule.jobprocessor.repeatinterval");
		//		logger.debug("schedule.jobprocessor.repeatinterval = {}", repeatIntervalAsString);
		//
		//		String startDelayAsString = env.getProperty("schedule.jobprocessor.startDelay");
		//		logger.debug("schedule.jobprocessor.startDelay = {}", startDelayAsString);
		//
		//		long repeatIntervalSeconds = Long.parseLong(repeatIntervalAsString);
		//		long startDelaySeconds = Long.parseLong(startDelayAsString);
		//
		//		scheduleJob(repeatIntervalSeconds, startDelaySeconds);
		scheduleJob(null, null);

	}

	public void scheduleJob(Long repeatIntervalSeconds, Long startDelaySeconds)
			throws ClassNotFoundException, NoSuchMethodException, SchedulerException,
			JobProcessorAlreadyScheduledException {

		if (repeatIntervalSeconds == null) {
			String repeatIntervalAsString = env.getProperty("schedule.jobprocessor.repeatinterval");
			logger.debug("schedule.jobprocessor.repeatinterval = {}", repeatIntervalAsString);
			repeatIntervalSeconds = Long.parseLong(repeatIntervalAsString);
		}

		if (startDelaySeconds == null) {
			String startDelayAsString = env.getProperty("schedule.jobprocessor.startDelay");
			logger.debug("schedule.jobprocessor.startDelay = {}", startDelayAsString);
			startDelaySeconds = Long.parseLong(startDelayAsString);
		}

		if (!schedulerFactoryBean.isRunning()) {
			/*
			 * I let this go with a warning, because I think it it _probably_
			 * possible to schedule jobs while the scheduler is not running, 
			 * although the jobs certainly will not get triggered while the
			 * scheduler is not running.
			 */
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

			if (env.getProperty("startup.schedule.jobprocessor.requeue.jobs.running").equals("true")) {
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
				 * picked up the next item that the Job processor rubs.
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
					logger.warn("{} Jobs with status RUNNING to re-queue", runningJobs.size());
					for (Job job : runningJobs) {
						logger.info("Setting status to \"QUEUED\" for job = {}", job);
						job.setJobStatus(jobStatus_QUEUED);
						job = jobRepository.save(job);
					}
				}
			}

			if (env.getProperty("startup.schedule.jobprocessor.requeue.jobs.delivering").equals("true")) {
				/*
				 * If any Job entities have status "DELIVERING", we change that
				 * to "QUEUED" here. The explanation for this action is 
				 * identical to the bloc of code above that re-queues jobs with
				 * status "RUNNING".
				 */
				JobStatus jobStatus_QUEUED = jobStatusRepository.findOne(JobStatus.QUEUED_ID);

				List<Job> deliveringJobs = jobRepository.findByJobStatusJobStatusId(JobStatus.DELIVERING_ID);
				if (deliveringJobs.size() > 0) {
					logger.warn("{} Jobs with status DELIVERING to re-queue", deliveringJobs.size());
					for (Job job : deliveringJobs) {
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
			//scheduler.addJob(subscriptionJobProcessorJobDetailFactory.getObject(), true);
			//scheduler.scheduleJob(subscriptionJobProcessorTriggerFactory.getObject());

		} else {
			//logger.warn(
			//		"Attempt to schedule the subscription job processor, but it is already registered with the scheduler");
			throw new JobProcessorAlreadyScheduledException();
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
	 * @throws JobProcessorSchedulerNotRunningCannotTrigger
	 * @throws JobProcessorNotScheduledCannotTrigger
	 */
	public void triggerJob() throws SchedulerException, JobProcessorSchedulerNotRunningCannotTrigger,
			JobProcessorNotScheduledCannotTrigger {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Triggering the subscription job processor to run immediately");
				scheduler.triggerJob(JOB_KEY);
			} else {
				//logger.warn(
				//		"Attempt to trigger the subscription job processor, but it is not registered with the scheduler");
				throw new JobProcessorNotScheduledCannotTrigger();
			}
		} else {
			//logger.warn(
			//		"Attempt to trigger the subscription job processor, but the scheduler is not running");
			throw new JobProcessorSchedulerNotRunningCannotTrigger();
		}
	}

	public void pauseJob()
			throws SchedulerException, JobProcessorNotScheduledCannotPause, JobProcessorSchedulerNotRunningCannotPause {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Pausing the subscription job processor");
				scheduler.pauseJob(JOB_KEY);
			} else {
				//logger.warn(
				//		"Attempt to pause the subscription job processor, but it is not registered with the scheduler");
				throw new JobProcessorNotScheduledCannotPause();
			}
		} else {
			//logger.warn(
			//		"Attempt to pause the subscription job processor, but the scheduler is not running");
			throw new JobProcessorSchedulerNotRunningCannotPause();
		}
	}

	public void resumeJob() throws SchedulerException, JobProcessorNotScheduledCannotResume,
			JobProcessorSchedulerNotRunningCannotResume {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(JOB_KEY)) {
				logger.info("Resuming the subscription job processor");
				scheduler.resumeJob(JOB_KEY);
			} else {
				//logger.warn(
				//		"Attempt to resume the subscription job processor, but it is not registered with the scheduler");
				throw new JobProcessorNotScheduledCannotResume();
			}
		} else {
			//logger.warn(
			//		"Attempt to resume the subscription job processor, but the scheduler is not running");
			throw new JobProcessorSchedulerNotRunningCannotResume();
		}
	}

	public void unscheduleJob() throws SchedulerException, JobProcessorNotScheduledCannotStop {
		//if (schedulerFactoryBean.isRunning()) {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		if (scheduler.checkExists(JOB_KEY)) {
			logger.info("Deleting the subscription job processor from the Quartz scheduler");
			scheduler.deleteJob(JOB_KEY);
		} else {
			//logger.warn(
			//		"Attempt to unschedule the subscription job processor, but it is not registered with the scheduler");
			throw new JobProcessorNotScheduledCannotStop();
		}

		//} else {
		//	logger.warn("Attempt to unschedule the subscription job processor, but the scheduler is not running");
		//	}
	}

	public JobProcessorResource getJobProcessorResource() {

		JobProcessorResource jobProcessorResource = new JobProcessorResource();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			/*
			 * Check if the Subscription Job Processor is scheduled at all:
			 */
			if (scheduler.checkExists(JOB_KEY)) {
				jobProcessorResource.setScheduled(true);

				/*
				 * Get job's trigger. We have only set one trigger, but testing
				 * has shown that there are ALWAYS 2 triggers for the job. The
				 * second trigger has characteristics that indicate it is 
				 * automatically created by Quartz or Spring. Examples of these
				 * characteristics are:
				 * 
				 *   trigger.getJobKey()       = JobProcessor_JobGroup.JobProcessor_JobName
				 *   trigger.getKey()          = DEFAULT.MT_1n8n14enj1rpi
				 *   trigger.getPriority()     = 5
				 *   trigger.getNextFireTime() = null
				 * 
				 * I don't know how to eliminate this trigger or even if that
				 * would be a good idea, so I just ignore it here. 
				 * 
				 * It seems that this is always the 2nd trigger in the list of
				 * triggers, but to be safe here, I locate the Trigger that
				 * matches the key that we know it should have.
				 */
				Trigger triggerWithMatchingKey = null;
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(JOB_KEY);
				for (Trigger trigger : triggers) {
					if (trigger.getKey().equals(TRIGGER_KEY)) {
						triggerWithMatchingKey = trigger;
						break;
					}
				}
				if (triggerWithMatchingKey != null) {
					TriggerState triggerState = scheduler.getTriggerState(TRIGGER_KEY);
					logger.debug("triggerState = {}", triggerState);
					jobProcessorResource.setTriggerState(triggerState.toString());

					Date nextFireTime = triggerWithMatchingKey.getNextFireTime();
					/*
					 * REST resources returned to a client are expressed in 
					 * ISO-8601 format with time zone "Z" (which means that they
					 * are relative to UTC). Here, we convert nextFireTime to
					 * a different java.util.Date object that will provide this
					 * behaviour.
					 */
					jobProcessorResource.setNextFireTime(DateUtils.normalDateToUtcTimezoneDate(nextFireTime));
				} else {
					logger.warn("Trigger does not exist for Subscription Job Processor");
					jobProcessorResource.setSchedulingNotice("Trigger does not exist");
				}

			} else {
				jobProcessorResource.setScheduled(false);
			}

			if (!schedulerFactoryBean.isRunning()) {
				//if(schedulingStatusResource.getSchedulingNotice()==null){
				jobProcessorResource.setSchedulingNotice("The scheduler is not running");
				//}
			}

		} catch (SchedulerException e) {
			logger.error("Exception thrown attempting to get scheduing status for Subscription Job Processor", e);
			jobProcessorResource.setSchedulingNotice("Problem retrieving subscription status");
		}

		return jobProcessorResource;
	}

	@PreDestroy
	public void shutdown() {
		try {
			unscheduleJob();
		} catch (SchedulerException e) {
			logger.error(
					"Exception thrown when attempting to delete the subscription job processor from the Quartz scheduler",
					e);
		} catch (JobProcessorNotScheduledCannotStop e) {
			logger.warn(
					"Attempt to unschedule the subscription job processor, but it is not registered with the scheduler");
		}
	}

}
