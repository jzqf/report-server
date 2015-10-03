package com.qfree.obo.report.scheduling.schedulers;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.dto.SchedulingStatusResource;
import com.qfree.obo.report.scheduling.jobs.SubscriptionScheduledJob;

/**
 * This bean manages the scheduling of SubscriptionScheduledJob instances.
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
public class SubscriptionScheduler {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduler.class);

	private static final String JOB_GROUP = "Subscription_JobGroup";
	private static final String TRIGGER_GROUP = "Subscription_TriggerGroup";

	private final SchedulerFactoryBean schedulerFactoryBean;
	private final ObjectFactory<SubscriptionScheduledJob> subscriptionScheduledJobFactory;
	private final SubscriptionRepository subscriptionRepository;

	@Autowired
	public SubscriptionScheduler(
			SchedulerFactoryBean schedulerFactoryBean,
			ObjectFactory<SubscriptionScheduledJob> subscriptionScheduledJobFactory,
			SubscriptionRepository subscriptionRepository) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.subscriptionScheduledJobFactory = subscriptionScheduledJobFactory;
		this.subscriptionRepository = subscriptionRepository;
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

		if (env.getProperty("startup.schedule.subscriptions").equals("true")) {

			logger.info("Sceduling all enabled subscriptions...");

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

			scheduleAllJobs();

		} else {
			logger.info("Scheduling of report subscriptions is disabled in config.properties.");
		}
	}

	//TODO Add synchronized here?
	public void scheduleAllJobs() {
		/*
		 * Only "active" and "enabled" Subscription entities are considered for
		 * scheduling.
		 */
		List<Subscription> subscriptions = subscriptionRepository.findByActiveTrueAndEnabledTrue();
		logger.debug("Considering {} subscriptions to be scheduled", subscriptions.size());
		for (Subscription subscription : subscriptions) {
			/*
			 * We catch Exceptions here, instead of passing them upwards so that
			 * we can get as many subscriptions scheduled as possible. 
			 */
			try {
				scheduleJob(subscription);
			} catch (SchedulerException | ParseException | ClassNotFoundException | NoSuchMethodException e) {
				logger.error("Could not schedule Subscription entity: {}. Exception thrown = {}", subscription, e);
			}
		}
	}

	/**
	 * Schedules a Subscription as a job with the Quartz Scheduler.
	 * 
	 * This method ...
	 * 
	 * @param subscription
	 * @throws SchedulerException
	 * @throws ParseException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	//TODO Add synchronized here?
	public void scheduleJob(Subscription subscription)
			throws SchedulerException, ParseException, ClassNotFoundException, NoSuchMethodException {

		if (!schedulerFactoryBean.isRunning()) {
			logger.warn("Attempt to schedule subscription {}, but the scheduler is not running", subscription);
		}

		/*
		 * Get the underlying Quartz Scheduler. According to the Javadoc for
		 * org.springframework.scheduling.quartz.SchedulerFactoryBean :
		 * 
		 *   For dynamic registration of jobs at runtime, use a bean 
		 *   reference to this SchedulerFactoryBean to get direct access to
		 *   the Quartz Scheduler (org.quartz.Scheduler). This allows you to
		 *   create new jobs and triggers, and also to control and monitor 
		 *   the entire Scheduler.
		 * 
		 * So it seems that in order to schedule jobs dynamically (which is
		 * what  we are doing here), one *must* use the Quartz Scheduler 
		 * object obtained here.
		 */
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		JobKey jobKey = subscriptionJobKey(subscription);

		/*
		 * Do not re-schedule the subscription if it is already scheduled.
		 */
		if (!scheduler.checkExists(jobKey)) {

			/*
			 * Get Spring managed bean that will run as the scheduled job.
			 */
			SubscriptionScheduledJob subscriptionScheduledJob = subscriptionScheduledJobFactory.getObject();
			/*
			 * Tell the bean which Subscription it is for.
			 */
			subscriptionScheduledJob.setSubscriptionId(subscription.getSubscriptionId());
			/*
			 * Create a factory for obtaining a Quartz JobDetail.
			 */
			MethodInvokingJobDetailFactoryBean subscriptionJobDetailFactory = new MethodInvokingJobDetailFactoryBean();
			subscriptionJobDetailFactory.setTargetObject(subscriptionScheduledJob);
			subscriptionJobDetailFactory.setTargetMethod("run");
			subscriptionJobDetailFactory.setName(subscription.getSubscriptionId().toString());
			subscriptionJobDetailFactory.setGroup(JOB_GROUP);
			/*
			 * This is important. Without it, the "run" method of the bean
			 * "subscriptionScheduledJob" might run simultaneously in 
			 * multiple threads. This would happen if:
			 * 
			 *   1. The "run" method runs for a period of time that is 
			 *      longer than the time interval provided by the trigger. 
			 *      In this case, another copy of the subscription job will 
			 *      be started, even if the previous invocation is still 
			 *      running.
			 *      
			 *   2. The subscription job is forced to run immediately with 
			 *      triggerJob() just before the job is scheduled to run 
			 *      anyway according to its trigger. In this case, the 
			 *      trigger will start another copy, even though we *just* 
			 *      forced a copy to run immediately with triggerJob().
			 */
			subscriptionJobDetailFactory.setConcurrent(false);
			subscriptionJobDetailFactory.afterPropertiesSet();

			/*
			 * Alternate approach, using a JobDetailFactoryBean instead of
			 * a MethodInvokingJobDetailFactoryBean.
			 */
			//	/*
			//	 * This map will be used to initialize fields of the instance of 
			//	 * SubscriptionScheduledJob that is associated with the
			//	 * JobDetailFactoryBean created here (as well as the Quartz
			//	 * JobDetail instance that it creates). This is the mechanism
			//	 * used to associate a Subscription entity with a scheduled
			//	 * subscription job.
			//	 */
			//	Map<String, Object> jobDataMap = new HashMap<>();
			//	jobDataMap.put("subscriptionId", subscription.getSubscriptionId());
			//	/*
			//	 * Create a factory for obtaining a Quartz JobDetail.
			//	 */
			//	JobDetailFactoryBean subscriptionJobDetailFactory = new JobDetailFactoryBean();
			//	subscriptionJobDetailFactory.setJobClass(SubscriptionScheduledJob.class);
			//	subscriptionJobDetailFactory.setJobDataAsMap(jobDataMap);
			//	subscriptionJobDetailFactory.setDurability(false);
			//	subscriptionJobDetailFactory.setName(subscription.getSubscriptionId().toString());
			//	subscriptionJobDetailFactory.setGroup(JOB_GROUP);
			//	subscriptionJobDetailFactory.afterPropertiesSet();

			if (subscription.getCronSchedule() != null) {

				/*
				 * Create a factory for obtaining a Quartz CronTrigger, to 
				 * be used for subscriptionJobDetail.
				 */
				CronTriggerFactoryBean cronTrigger = new CronTriggerFactoryBean();
				cronTrigger.setJobDetail(subscriptionJobDetailFactory.getObject());
				cronTrigger.setName(subscription.getSubscriptionId().toString());
				cronTrigger.setGroup(TRIGGER_GROUP);
				//cronTrigger.setStartDelay(1000L);
				cronTrigger.setCronExpression(subscription.getCronSchedule());
				cronTrigger.afterPropertiesSet();

				/*
				 * Schedule the subscription to run according the schedule
				 * defined by its trigger.
				 */
				scheduler.scheduleJob(
						subscriptionJobDetailFactory.getObject(),
						cronTrigger.getObject());

			} else if (subscription.getRunOnceAt() != null) {
				throw new RuntimeException("WRITE ME (treat case: subscription.getRunOnceAt() != null)");
			} else {
				throw new RuntimeException("WRITE ME (throw custom exception?)");
			}

		} else {
			logger.warn(
					"Attempt to schedule the subscription, but it is already registered with the scheduler");
			/*
			 * TODO At this point we could try resuming the job IN CASE it was paused
			 * However, it is not clear at this point if that is the best behaviour to 
			 * implement, so I will do nothing for now.
			 */
		}

	}

	/**
	 * Get the Quartz "job key" for a Subscription.
	 * 
	 * @param subscription
	 * @return
	 */
	private JobKey subscriptionJobKey(Subscription subscription) {
		JobKey jobKey = JobKey.jobKey(subscription.getSubscriptionId().toString(), JOB_GROUP);
		return jobKey;
	}

	/**
	 * Get the Quartz "job key" from a job name.
	 * 
	 * @param jobName
	 * @return
	 */
	private JobKey subscriptionJobKey(String jobName) {
		JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP);
		return jobKey;
	}

	/**
	 * Forces the scheduled subscription job to run immediately.
	 * 
	 * This will force the scheduled subscription job to run even if the job is
	 * paused in Quartz scheduler; however, it will run only once, which means
	 * that this will not UNpause the job.
	 * 
	 * @throws SchedulerException
	 */
	public void triggerJob(JobKey jobKey) throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)) {
				logger.info("Triggering the subscription '{}' to run immediately", jobKey);
				scheduler.triggerJob(jobKey);
			} else {
				logger.warn(
						"Attempt to trigger the subscription '{}', but it is not registered with the scheduler",
						jobKey);
			}
		} else {
			logger.warn(
					"Attempt to trigger the subscription '{}', but the scheduler is not running", jobKey);
		}
	}

	public void pauseJob(JobKey jobKey) throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)) {
				logger.info("Pausing the subscription '{}'", jobKey);
				scheduler.pauseJob(jobKey);
			} else {
				logger.warn(
						"Attempt to pause the subscription, but it is not registered with the scheduler");
			}
		} else {
			logger.warn(
					"Attempt to pause the subscription, but the scheduler is not running");
		}
	}

	public void resumeJob(JobKey jobKey) throws SchedulerException {
		if (schedulerFactoryBean.isRunning()) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			if (scheduler.checkExists(jobKey)) {
				logger.info("Resuming the subscription '{}'", jobKey);
				scheduler.resumeJob(jobKey);
			} else {
				logger.warn(
						"Attempt to resume the subscription, but it is not registered with the scheduler");
			}
		} else {
			logger.warn(
					"Attempt to resume the subscription, but the scheduler is not running");
		}
	}

	private void unscheduleAllJobs() {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JOB_GROUP))) {
				//String jobName = jobKey.getName();
				//String jobGroup = jobKey.getGroup();

				/*
				 * Get job's trigger (not really needed here, but we can log a
				 * little more information with it).
				 */
				List<Trigger> triggers = null; //new ArrayList<>();
				triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				if (triggers != null && triggers.size() > 0) {
					Date nextFireTime = triggers.get(0).getNextFireTime();
					logger.info("Unscheduling subscription with: jobKey = {}, nextFireTime = {}", jobKey, nextFireTime);
					if (triggers.size() > 1) {
						logger.error("There are {} triggers for subscription job key: {}", triggers.size(), jobKey);
					}
				} else {
					logger.error("The is no trigger for subscription job key: {}", jobKey);
				}
				try {
					unscheduleJob(jobKey);
				} catch (SchedulerException e) {
					/*
					 * Catch exception here so that we can unschedule as many 
					 * subscriptions scheduled as possible. 
					 */
					logger.error("Exception thrown attempting to unschedule all subscriptions: {}", e);
				}
			}
		} catch (SchedulerException e) {
			logger.error("Exception thrown attempting to unschedule all subscriptions: {}", e);
		}
	}

	public void unscheduleJob(JobKey jobKey) throws SchedulerException {
		//if (schedulerFactoryBean.isRunning()) {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		if (scheduler.checkExists(jobKey)) {
			logger.info("Deleting the subscription with job key = '{}' from the Quartz scheduler", jobKey);
			scheduler.deleteJob(jobKey);
		} else {
			logger.warn(
					"Attempt to unschedule the subscription with job key = '{}', but it is not registered with the scheduler",
					jobKey);
		}

		//} else {
		//	logger.warn("Attempt to unschedule the subscription with job key = '{}', but the scheduler is not running", jobKey);
		//}
	}

	public SchedulingStatusResource getSchedulingStatusResource(Subscription subscription) {

		JobKey jobKey = subscriptionJobKey(subscription);
		SchedulingStatusResource schedulingStatusResource = new SchedulingStatusResource();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			/*
			 * Check if the Subscription is scheduled at all:
			 */
			if (scheduler.checkExists(jobKey)) {
				schedulingStatusResource.setScheduled(true);
				/*
				 * Get job's trigger. There should only be one trigger.
				 */
				List<Trigger> triggers = null; //new ArrayList<>();
				triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				if (triggers != null && triggers.size() > 0) {
					Date nextFireTime = triggers.get(0).getNextFireTime();
					schedulingStatusResource.setNextFireTime(nextFireTime);
					logger.debug("jobKey = {}, nextFireTime = {}", jobKey, nextFireTime);
					if (triggers.size() > 1) {
						logger.error("There are {} triggers for subscription: {}", triggers.size(), subscription);
						schedulingStatusResource.setSchedulingNotice("There are " + triggers.size() + " triggers");
					}
				} else {
					logger.error("Trigger does not exist for subscription: {}", subscription);
					schedulingStatusResource.setSchedulingNotice("Trigger does not exist");
				}
			} else {
				schedulingStatusResource.setScheduled(false);
			}

			if (!schedulerFactoryBean.isRunning()) {
				//if(schedulingStatusResource.getSchedulingNotice()==null){
				schedulingStatusResource.setSchedulingNotice("The scheduler is not running");
				//}
			}

		} catch (SchedulerException e) {
			logger.error("Exception thrown attempting to get scheduing status for subscription {}. Exception = {}",
					subscription, e);
			schedulingStatusResource.setSchedulingNotice("Problem retrieving subscription status");
		}

		return schedulingStatusResource;
	}

	@PreDestroy
	public void shutdown() {
		unscheduleAllJobs();
	}
}
