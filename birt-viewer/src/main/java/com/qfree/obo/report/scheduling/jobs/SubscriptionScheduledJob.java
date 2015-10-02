package com.qfree.obo.report.scheduling.jobs;

import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.SubscriptionRepository;

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
 *    classes, etc.
 * 
 * 2. Use JobDetailFactoryBean to create a JobDetail: Create a 
 *    SpringBeanJobFactory that supports Spring's @Autowired dependency 
 *    injection. The Quartz scheduler can be configured to be aware of this 
 *    factory. Then @Autowired DI will just work in any Quartz-instantiated job 
 *    beans created from a JobDetailFactoryBean. This is the approach used in 
 *    this application.
 * 
 * 3. Use a MethodInvokingJobDetailFactoryBean to create a scheduled job: For
 *    this to work this SubscriptionScheduledJob class must be a prototype-
 *    scoped  Spring bean and the instance of this class that is passed to the
 *    MethodInvokingJobDetailFactoryBean must be created from an ObjectFactory 
 *    (so we get a new instance for each subscription job). This approach 
 *    produces a Quartz scheduled job bean that is fully Spring-managed (I 
 *    think).
 * 
 * Since this class is not managed by Spring, it makes no sense to annotate it   <-- MOVE BLOCK UP UNDER #2
 * with @Component. If it _were_ a Spring-managed class, it would probably need
 * to be given a scope of "prototype" because we need separate instances to
 * maintain the state, in particular the field "subscriptionId". But since this
 * class is not managed by Spring, it makes no sense to annotate it with @Scope,
 * either.
 */
// @Component <- No, this is not a Spring-managed bean
// @Scope(value = "prototype") <- No, this is not a Spring-managed bean
public class SubscriptionScheduledJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduledJob.class);

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private JobRepository jobRepository;

	private UUID subscriptionId;

	/*
	 * QUESTION: Is it possible for more than one instance of this class to be
	 * created for the same subscription? Could this happen if a ridiculously
	 * frequent schedule was set and the code run from this class ran for longer
	 * than the repeat time? If so, we might need to be extra careful about making
	 * everything thread-safe. If so, should I declare this method as 
	 * "synchronized"?
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("subscriptionId = {}", subscriptionId);

		/*
		 * TODO Inject a service ban instead of Repository beans so that the job will
		 * be transactional (annotate service method with @Transactional) and I 
		 * won't have to worry about lazy instaniation exceptions.
		 */
		logger.info("subscriptionRepository.findOne(subscriptionId) = {}",
				subscriptionRepository.findOne(subscriptionId));
		logger.info("jobRepository.count() = {}", jobRepository.count());

		//TODO After creating a Job, force the job processor to run with triggerJob(),

	}

	public UUID getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(UUID subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

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

}
