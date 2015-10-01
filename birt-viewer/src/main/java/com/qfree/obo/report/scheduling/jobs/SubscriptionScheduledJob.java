package com.qfree.obo.report.scheduling.jobs;

import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.SubscriptionRepository;

/*
 * The scope of this bean is "prototype" because a new instance is required for
 * each value assigned to subscriptionId.
 * 
 * QUESTION: Is it possible for more than one instance of this class to be
 * created for the same subscription? Could this happen if a ridiculously
 * frequent schedule was set and the code run from this class ran for longer
 * than the repeat time? If so, we might need to be extra careful about making
 * everything thread-safe. Can I just make the affected methods "synchronized"?
 */
@Component
@Scope(value = "prototype")
public class SubscriptionScheduledJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduledJob.class);

	private UUID subscriptionId;
	private SubscriptionRepository subscriptionRepository;

	private JobRepository jobRepository;

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		logger.info("subscriptionId = {}", subscriptionId);
		logger.info("subscriptionRepository.findOne(subscriptionId) = {}",
				subscriptionRepository.findOne(subscriptionId));
		logger.info("jobRepository.count() = {}", jobRepository.count());

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
