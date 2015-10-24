package com.qfree.obo.report.scheduling.schedulers;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
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
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.dto.SchedulingStatusResource;
import com.qfree.obo.report.exceptions.NoScheduleForSubscriptionException;
import com.qfree.obo.report.scheduling.jobs.SubscriptionScheduledJob;
import com.qfree.obo.report.util.DateUtils;

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

	/**
	 * If <code>false</code>, the entity field
	 * Subscription.deliveryDatetimeRunAt will be assumed to be expressed
	 * relative to UTC/GMT. In addition, datetimes submitted in ReST resources
	 * via HTTP POST or PUT must be expressed in ISO-8601 format, which requires
	 * the time zone to be specified, either as "Z" or "±hh:mm"
	 * 
	 * <p>
	 * If <code>true</code>, the entity field Subscription.deliveryDatetimeRunAt
	 * will be assumed to be expressed relative to the time zone specified by
	 * the entity field Subscription.deliveryTimeZoneId. In addition, datetimes
	 * submitted in ReST resources via HTTP POST or PUT must be expressed in a
	 * format that can be parse by {@link LocalDateTime#parse(CharSequence)},
	 * e.g., "2015-11-29T10:15:30".
	 * 
	 * <p>
	 * In order for the treatment of {@link Subscription#deliveryCronSchedule} and
	 * {@link Subscription#deliveryDatetimeRunAt} to appear consistent to the report server
	 * user, it is probably best to set this to <code>true</code>. For this
	 * case, the time zone for both {@link Subscription#deliveryCronSchedule} <i>and</i>
	 * {@link Subscription#deliveryDatetimeRunAt} is specified by
	 * {@link Subscription#deliveryTimeZoneId}. The treatment of
	 * {@link Subscription#deliveryDatetimeRunAt} for when <code>false</code> is set has
	 * been retained only for documenting this case, as well as so that this can
	 * be recovered if, for some reason, it is desired in the future.
	 */
	public static final boolean RUNAT_ENTITY_DATE_TZ_DYNAMIC = true;

	private static final String JOB_GROUP = "Subscription_JobGroup";
	private static final String TRIGGER_GROUP = "Subscription_TriggerGroup";

	private final SchedulerFactoryBean schedulerFactoryBean;
	private final ObjectFactory<SubscriptionScheduledJob> subscriptionScheduledJobFactory;
	//	private SubscriptionService subscriptionService;
	private final SubscriptionRepository subscriptionRepository;

	@Autowired
	public SubscriptionScheduler(
			SchedulerFactoryBean schedulerFactoryBean,
			ObjectFactory<SubscriptionScheduledJob> subscriptionScheduledJobFactory,
			//SubscriptionService subscriptionService,
			SubscriptionRepository subscriptionRepository) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.subscriptionScheduledJobFactory = subscriptionScheduledJobFactory;
		//		this.subscriptionService = subscriptionService;
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

			logger.info("Scheduling all enabled subscriptions...");

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
			} catch (SchedulerException | ParseException | ClassNotFoundException | NoSuchMethodException
					| NoScheduleForSubscriptionException e) {
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
	 * @throws NoScheduleForSubscriptionException
	 */
	//TODO Add synchronized here?
	public void scheduleJob(Subscription subscription)
			throws SchedulerException, ParseException, ClassNotFoundException, NoSuchMethodException,
			NoScheduleForSubscriptionException {

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

			//			subscriptionScheduledJob.setSubscriptionService(subscriptionService);

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

			/*
			 * Get the time zone for the cron expression or the "run at" 
			 * timestamp.
			 */
			String deliveryTimeZoneId = subscription.getDeliveryTimeZoneId();
			logger.info("deliveryTimeZoneId = {}", deliveryTimeZoneId);
			ZoneId cronZoneId = null;
			if (deliveryTimeZoneId != null && !deliveryTimeZoneId.isEmpty()) {
				try {
					cronZoneId = ZoneId.of(deliveryTimeZoneId);
				} catch (DateTimeException e) {
					logger.error(
							"deliveryTimeZoneId '{}' is not legal. The default time zone will be used for the cron expression",
							deliveryTimeZoneId);
				}
			} else {
				logger.warn(
						"deliveryTimeZoneId is not defined. The default time zone will be used for the cron expression");
			}
			if (cronZoneId == null) {
				/*
				 * Use the default time-zone.
				 */
				cronZoneId = ZoneId.systemDefault();
			}
			logger.info("cronZoneId = {}", cronZoneId);

			if (subscription.getDeliveryCronSchedule() != null) {

				/*
				 * Create a factory for obtaining a Quartz CronTrigger, to 
				 * be used for subscriptionJobDetail.
				 */
				CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
				cronTriggerFactory.setJobDetail(subscriptionJobDetailFactory.getObject());
				cronTriggerFactory.setName(subscription.getSubscriptionId().toString());
				cronTriggerFactory.setGroup(TRIGGER_GROUP);
				cronTriggerFactory.setCronExpression(subscription.getDeliveryCronSchedule());

				/*
				 * Set the time zone for the cron expression.
				 */
				TimeZone cronTimeZone = TimeZone.getTimeZone(cronZoneId);
				logger.info("cronTimeZone = {}", cronTimeZone);
				cronTriggerFactory.setTimeZone(cronTimeZone);

				cronTriggerFactory.afterPropertiesSet();

				/*
				 * Schedule the subscription to run according the schedule
				 * defined by its cron-based trigger.
				 */
				scheduler.scheduleJob(
						subscriptionJobDetailFactory.getObject(),
						cronTriggerFactory.getObject());

			} else if (subscription.getDeliveryDatetimeRunAt() != null) {

				/*
				 * Create a factory for obtaining a Quartz SimpleTrigger, to
				 * be used for subscriptionJobDetail.
				 */
				SimpleTriggerFactoryBean simpleTriggerFactory = new SimpleTriggerFactoryBean();
				simpleTriggerFactory.setJobDetail(subscriptionJobDetailFactory.getObject());
				simpleTriggerFactory.setName(subscription.getSubscriptionId().toString());
				simpleTriggerFactory.setGroup(TRIGGER_GROUP);
				/*
				 * If RUNAT_ENTITY_DATE_TZ_DYNAMIC = true:
				 * 
				 *   We assume that subscription.getRunOnceAt() is stored 
				 *   relative to the server's own time zone. In order to express
				 *   this datetime value relative to the correct time zone so
				 *   that the job is triggered at the correct time, we make
				 *   use of subscription.getCronScheduleZoneId().
				 * 
				 * If RUNAT_ENTITY_DATE_TZ_DYNAMIC = false:
				 * 
				 *   We assume that subscription.getRunOnceAt() is stored 
				 *   relative to UTC/GMT (which is the convention we have 
				 *   chosen for most entity Date fields that represent datetime
				 *   values). The value of subscription.getCronScheduleZoneId()
				 *   is *not* used for this case because we assume that the 
				 *   entity Date field value is expressed relative to UTC/GMT.
				 * 
				 * It is necessary to adjust subscription.getRunOnceAt() here so
				 * that the subscription is triggered at the appropriate time. 
				 * Since java.util.Date objects don't have time zone support, we
				 * have to perform some magic here with DateUtils. The 
				 * The adjustment we make depends on how we interpret the time
				 * zone of the entity Date subscription.getRunOnceAt().
				 */
				logger.info("subscription.getRunOnceAt() = {}", subscription.getDeliveryDatetimeRunAt());
				Date runOnceAtServerTimezone = null;
				if (RUNAT_ENTITY_DATE_TZ_DYNAMIC) {
					runOnceAtServerTimezone = DateUtils
							.entityTimestampToServerTimezoneDate(subscription.getDeliveryDatetimeRunAt(), cronZoneId);
				} else {
					runOnceAtServerTimezone = DateUtils
							.entityTimestampToServerTimezoneDate(subscription.getDeliveryDatetimeRunAt());
				}
				logger.info("runOnceAtServerTimezone = {}", runOnceAtServerTimezone);

				/*
				 * Only schedule the job if the trigger time is in the future.
				 * Testing shows that the subscription job will get fired once
				 * after executing "scheduler.scheduleJob(...)" below even if 
				 * the "start time" is in the past. This is incorrect behaviour;
				 * otherwise a user will receive a report via e-mail each time
				 * the report server starts up, even though the "start time" is
				 * in the past.
				 */
				if (runOnceAtServerTimezone.getTime() > new Date().getTime()) {
					simpleTriggerFactory.setStartTime(runOnceAtServerTimezone);
					simpleTriggerFactory.setRepeatCount(0); // trigger will fire only once
					simpleTriggerFactory.afterPropertiesSet();
					/*
					 * Schedule the subscription to run according the schedule
					 * defined by its one-shot trigger.
					 */
					scheduler.scheduleJob(
							subscriptionJobDetailFactory.getObject(),
							simpleTriggerFactory.getObject());
				} else {
					if (RUNAT_ENTITY_DATE_TZ_DYNAMIC) {
						logger.info(
								"Subscription with id = '{}' not scheduled because \"deliveryDatetimeRunAt\" = '{}' (relative to time zone '{}') is in the past",
								subscription.getSubscriptionId(), subscription.getDeliveryDatetimeRunAt(),
								subscription.getDeliveryTimeZoneId());
					} else {
						logger.info(
								"Subscription with id = '{}' not scheduled because \"deliveryDatetimeRunAt\" = '{}' (relative to UTC) is in the past",
								subscription.getSubscriptionId(), subscription.getDeliveryDatetimeRunAt());
					}
				}

			} else {
				throw new NoScheduleForSubscriptionException();
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
	 * Get the Quartz "trigger key" for a Subscription.
	 * 
	 * @param subscription
	 * @return
	 */
	private TriggerKey subscriptionTriggerKey(Subscription subscription) {
		TriggerKey triggerKey = TriggerKey.triggerKey(subscription.getSubscriptionId().toString(), TRIGGER_GROUP);
		return triggerKey;
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

	public void unscheduleJob(Subscription subscription) throws SchedulerException {
		unscheduleJob(subscriptionJobKey(subscription));
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
		TriggerKey triggerKey = subscriptionTriggerKey(subscription);
		SchedulingStatusResource schedulingStatusResource = new SchedulingStatusResource();
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			/*
			 * Check if the Subscription is scheduled at all:
			 */
			if (scheduler.checkExists(jobKey)) {
				schedulingStatusResource.setScheduled(true);
				/*
				 * Get job's trigger. We have only set one trigger, but there
				 * may actually be two - see the comments in
				 * SubscriptionJobProcessorScheduler.getJobProcessorResource()
				 * for a discussion of how there can be two triggers. Anyway,
				 * instead of assuming that the first Trigger in the list 
				 * "triggers" is the Trigger we are looking for, we iterate
				 * through the list and match its key.
				 */
				Trigger triggerWithMatchingKey = null;
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				for (Trigger trigger : triggers) {
					if (trigger.getKey().equals(triggerKey)) {
						logger.info("Found trigger with key {}", triggerKey);
						triggerWithMatchingKey = trigger;
						break;
					}
				}
				if (triggerWithMatchingKey != null) {
					TriggerState triggerState = scheduler.getTriggerState(triggerKey);
					logger.info("triggerState = {}", triggerState);

					Date nextFireTime = triggerWithMatchingKey.getNextFireTime();

					//				List<Trigger> triggers = null; //new ArrayList<>();
					//				triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					//				if (triggers != null && triggers.size() > 0) {
					//					Date nextFireTime = triggers.get(0).getNextFireTime();

					/*
					 * REST resources returned to a client are expressed in 
					 * ISO-8601 format with time zone "Z" (which means that they
					 * are relative to UTC). Here, we convert nextFireTime to
					 * a different java.util.Date object that will provide this
					 * behaviour.
					 */
					schedulingStatusResource.setNextFireTime(DateUtils.normalDateToUtcTimezoneDate(nextFireTime));
				} else {
					/*
					 * This is not necessarily an error. It can occur during 
					 * normal after a one-shot trigger is has fired. Such a 
					 * trigger is used if Subscription.deliveryDatetimeRunAt has a value
					 * and Subscription.deliveryCronSchedule = null.
					 */
					logger.warn("Trigger does not exist for subscription: {}", subscription);
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

	/**
	 * Returns true if the job associated with the specified {@link JobKey}
	 * exists.
	 * 
	 * @param jobKey
	 * @return
	 * @throws SchedulerException
	 */
	public boolean isSubscriptionScheduled(Subscription subscription) throws SchedulerException {
		return isScheduled(subscriptionJobKey(subscription));
	}

	/**
	 * Returns true if the job associated with the specified {@link JobKey}
	 * exists.
	 * 
	 * @param jobKey
	 * @return
	 * @throws SchedulerException
	 */
	private boolean isScheduled(JobKey jobKey) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		return scheduler.checkExists(jobKey);
	}

	@PreDestroy
	public void shutdown() {
		unscheduleAllJobs();
	}
}
