package com.qfree.obo.report.scheduling.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;

/*
 * This class is instantiated by Quartz and therefore Spring-based dependency
 * injection will not work. There are three work-arounds to this:
 * 
 * 1. Use JobDetailFactoryBean to create a JobDetail: The job bean class, i.e.,
 *    this class, SubscriptionScheduledJob, is passed to the 
 *    JobDetailFactoryBean via a call to setJobClass(...). In order for an 
 *    instance of SubscriptionScheduledJob to have access to Spring-managed 
 *    beans, they can be passed to it in the "job data map" that is passed to 
 *    the JobDetailFactoryBean when setting up the scheduled job. They can be 
 *    beans of any type: Spring Data-generated repositories, custom service 
 *    classes, etc. I have tested this and it appears to work just fine.
 * 
 *    Since for this case this class is not managed by Spring, it makes no sense 
 *    to annotate it with @Component. If it _were_ a Spring-managed class, it 
 *    would probably need to be given a scope of "prototype" because we need 
 *    separate instances to maintain the state, in particular the field 
 *    "subscriptionId". But since for this case this class is not managed by 
 *    Spring, it makes no sense to annotate it with @Scope, either.
 * 
 * 2. Use JobDetailFactoryBean to create a JobDetail: Create a 
 *    SpringBeanJobFactory that supports Spring's @Autowired dependency 
 *    injection. The Quartz scheduler can be configured to be aware of this 
 *    factory. Then @Autowired DI will just work in any Quartz-instantiated job 
 *    beans created from a JobDetailFactoryBean. I have tested this and it works
 *    fine, but I have chosen the next approach in order to be able to work with
 *    a fully Spring-managed job bean. Therefore, I have commented out these 
 *    configuration details in SchedulingConfig.
 * 
 * 3. Use a MethodInvokingJobDetailFactoryBean to create a scheduled job: For
 *    this to work this SubscriptionScheduledJob class must be a prototype-
 *    scoped  Spring bean and the instance of this class that is passed to the
 *    MethodInvokingJobDetailFactoryBean must be created from an ObjectFactory 
 *    (so we get a new instance for each subscription job). This approach 
 *    produces a Quartz scheduled job bean that is fully Spring-managed (I 
 *    think).
 *    
 *    This is the approach I have chose, since the scheduled job bean is a fully
 *    Spring-managed bean.
 *    
 *    However, I encountered a confusing exception trying to autowire a bean of 
 *    type SubscriptionService in this class. The exception message listed a 
 *    long chain of beans injected to each other and then ended with:
 *    
 *         Requested bean is currently in creation: Is there an unresolvable 
 *         circular reference?
 *    
 *    I never figured that one out. It may have been related to how this is 
 *    a prototype-scoped bean and *not* due to the autowiring itself. This is
 *    because I also tried the following:
 *    
 *      1. Autowire the SubscriptionService bean in the singleton-scoped
 *         SubscriptionScheduler.
 *         
 *      2. Create the SubscriptionScheduledJob prototype bean in 
 *         SubscriptionScheduler using the object factory
 *         ObjectFactory<SubscriptionScheduledJob> (as I do here in treatment
 *         #3).
 *      
 *      3. Set the SubscriptionService service bean on the 
 *         SubscriptionScheduledJob object in SubscriptionScheduledJob using: 
 *         subscriptionScheduledJob.setSubscriptionService(subscriptionService).
 *    
 *    Everything worked fine up to this point, but the same exception was STILL 
 *    thrown from the SubscriptionScheduledJob. I "solved" this, eventually, by
 *    autowiring Repository classes in the SubscriptionScheduledJob instead of 
 *    autowiring a single SubscriptionService service class. Somehow, the 
 *    Repository classes did not trigger this problem in the same was as the 
 *    service bean did.
 *    
 *  I use @Transactional so that entities will not be persisted to the database
 *  if an exception is thrown before a complete collection of Job, 
 *  JobParameter and JobParameterValue entities are saved.
 */
@Transactional
@Component
@Scope(value = "prototype")
//public class SubscriptionScheduledJob extends QuartzJobBean {   <- for approaches 1 & 2 above
public class SubscriptionScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduledJob.class);

	//@Autowired
	//private SubscriptionService subscriptionService;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	private UUID subscriptionId;

	/*
	 * This is used to avoid the unfortunate, although probably unlikely, 
	 * possibility that this job is triggered at a very high frequency (due
	 * to an error in the cron schedule or whatever. If that should happen, then
	 * a very large number of Job entities could be created quickly, resulting 
	 * in a huge number of reports being run and sent out by email. 
	 * 
	 * To avoid this possibility, the "run()" method (which performs all of the
	 * work to process a Subscription and create a Job), will do nothing until
	 * a certain minimum time has elapsed. This minimum time is specified by
	 * MIN_TIME_BETWEEN_RUNS_MS.
	 */
	private long lastRunMs = 0;

	/*
	 * This is the minimum time in milliseconds between runs. The "run()" method
	 * will do nothing until at least this much time has elapsed since the last
	 * time it processed the subscription.
	 */
	private final long MIN_TIME_BETWEEN_RUNS_MS = 60L * 1000L;

	/*
	 * QUESTION: Is it possible for more than one instance of this class to be
	 * created for the same subscription? Could this happen if a ridiculously
	 * frequent schedule was set and the code run from this class ran for longer
	 * than the repeat time? If so, we might need to be extra careful about making
	 * everything thread-safe. If so, should I declare this method as 
	 * "synchronized"?
	 */
	/* 
	 * This is for approaches 1 & 2 above where we use a JobDetailFactoryBean 
	 * instead of a MethodInvokingJobDetailFactoryBean to create a scheduled 
	 * job. For this case the method signature must be different because this 
	 * class extends QuartzJobBean:
	 */
	//	@Override
	//	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
	/**
	 * Runs according to the subscription schedule. On each run a new Job is
	 * created.
	 * 
	 * Creating a new Job and its related entities can be done very quickly.
	 * Another scheduled Quartz job, SubscriptionJobProcessorScheduledJob will
	 * discover these Job entities and run them.
	 */
	public void run() {

		logger.info("***** Only process subscription if a sufficient amount of time has passed since last run *****");

		logger.info("Creating Job for subscriptionId = {}...", subscriptionId);

		Subscription subscription = subscriptionRepository.findOne(subscriptionId);
		if (subscription != null) {

			JobStatus jobStatusQueued = jobStatusRepository.findOne(JobStatus.QUEUED_ID);

			Job job = new Job(
					subscription,
					jobStatusQueued,
					null,
					subscription.getReportVersion(),
					subscription.getRole(),
					subscription.getDocumentFormat(),
					null,
					null,
					null,
					null);

			List<JobParameter> jobParameters = new ArrayList<>(0);
			job.setJobParameters(jobParameters);

			/*
			 * Create one JobParameter for each SubscriptionParameter:
			 */
			for (SubscriptionParameter subscriptionParameter : subscription.getSubscriptionParameters()) {

				JobParameter jobParameter = new JobParameter(job, subscriptionParameter.getReportParameter());
				jobParameters.add(jobParameter);

				List<JobParameterValue> jobParameterValues = new ArrayList<>(0);
				jobParameter.setJobParameterValues(jobParameterValues);

				/*
				 * Create one JobParameterValue for each 
				 * SubscriptionParameterValue:
				 */
				List<SubscriptionParameterValue> subscriptionParameterValues = subscriptionParameter
						.getSubscriptionParameterValues();
				for (SubscriptionParameterValue subscriptionParameterValue : subscriptionParameterValues) {

					/*
					 * SubscriptionParameterValue entities can represent either:
					 * 
					 *   1. A static value for a report parameter, or
					 *   
					 *   2. Details for how to compute a report parameter value.
					 *      This applies in particular to report parameters of type
					 *      "datetime".
					 */
					if (subscriptionParameterValues.size() == 1
							&& (subscriptionParameterValue.getYearNumber() != null ||
									subscriptionParameterValue.getYearsAgo() != null ||
									subscriptionParameterValue.getMonthNumber() != null ||
									subscriptionParameterValue.getMonthsAgo() != null ||
									subscriptionParameterValue.getWeeksAgo() != null ||
									subscriptionParameterValue.getDayOfWeekInMonthOrdinal() != null ||
									subscriptionParameterValue.getDayOfWeekInMonthNumber() != null ||
									subscriptionParameterValue.getDayOfWeekNumber() != null ||
									subscriptionParameterValue.getDayOfMonthNumber() != null ||
									subscriptionParameterValue.getDaysAgo() != null ||
									subscriptionParameterValue.getDurationToAddYears() != null ||
									subscriptionParameterValue.getDurationToAddMonths() != null ||
									subscriptionParameterValue.getDurationToAddWeeks() != null ||
									subscriptionParameterValue.getDurationToAddDays() != null ||
									subscriptionParameterValue.getDurationToAddHours() != null ||
									subscriptionParameterValue.getDurationToAddMinutes() != null ||
									subscriptionParameterValue.getDurationToAddSeconds() != null)) {

						//TODO Create single JobParameterValue that must be COMPUTED. Place this code in
						// the JobParameterValue constructor (must remove check for size of list = 1).
						logger.error(
								"\n*****\n*****\nCreate single JobParameterValue that must be COMPUTED\n*****\n*****");

					} else {
						/*
						 * The SubscriptionParameterValue entity represents a static value.
						 */
						JobParameterValue jobParameterValue = new JobParameterValue(jobParameter,
								subscriptionParameterValue);
						jobParameterValues.add(jobParameterValue);
					}
				}
			}

			/*
			 * This should save all entities created.
			 */
			job = jobRepository.save(job);
			logger.info("Saved job ={}", job);

			logger.info("Finsihed creating Job for subscriptionId = {}", subscriptionId);

		} else {
			logger.error("No Subscription exists for subscriptionId = {}", subscriptionId);
		}

		//TODO After creating a Job, force the job processor to run with triggerJob(),
		logger.info("***** Write code to trigger the job processor here *****");

		/*
		 * It might be wise to implement some mechanism to try to avoid a buggy 
		 * situation where zillions of new [job] & [job_parameter_value] records
		 * are created when some piece of code gets stuck in a tight loop. If 
		 * this occurs, it is a bug that needs to be fixed, but we still want to
		 * avoid it because it will contaminate a customer's report server 
		 * database with zillions of jobs that the system will try to run.
		 */
		//TODO Should we sleep here for a minute or a few minutes to avoid having this job running too often?
		// But this might cause problems while trying to shutdown?
		// Perhaps we should record the last time this code ran using a field. Then, if the period is
		// too small, sleep for a little while?
		// private long lastRun = 0;  // System.currentTimeMillis();
		//Instead of sleeping, do nothing abd just let the method end. Only when 
		// at least a small time (1 minute?) has passed should we actually create
		// a Job - and then update "lastRun"!
	}

	public UUID getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(UUID subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	//	public SubscriptionService getSubscriptionService() {
	//		return subscriptionService;
	//	}
	//
	//	public void setSubscriptionService(SubscriptionService subscriptionService) {
	//		this.subscriptionService = subscriptionService;
	//	}

	public SubscriptionRepository getSubscriptionRepository() {
		return subscriptionRepository;
	}

	public void setSubscriptionRepository(SubscriptionRepository subscriptionRepository) {
		this.subscriptionRepository = subscriptionRepository;
	}

	public JobRepository getJobRepository() {
		return jobRepository;
	}

	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionScheduledJob [subscriptionId=");
		builder.append(subscriptionId);
		builder.append("]");
		return builder.toString();
	}
}
