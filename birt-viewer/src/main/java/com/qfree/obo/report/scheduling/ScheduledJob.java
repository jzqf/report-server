package com.qfree.obo.report.scheduling;

import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ScheduledJob extends QuartzJobBean{

	private static final Logger logger = LoggerFactory.getLogger(ScheduledJob.class);

	/*
	 * In a final implementation:
	 *   1.	This will be a UUID object that contains the id of a Subscription?
	 *   2.	We need to test that the Subscription still exists when this job runs.
	 *   	If it does not, we should try to cancel/delete this job so that it
	 *   	never runs again.
	 *   3.	Should we test if the Subscription is inactive when this job runs?
	 *   	If it is inactive, do the same as if the Subscription was deleted?
	 */
	private AnotherBean anotherBean; 

	private UUID subscriptionUuid;

	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		anotherBean.printAnotherMessage();
		logger.info("subscriptionUuid = {}", subscriptionUuid);
	}

	public void setAnotherBean(AnotherBean anotherBean) {
		this.anotherBean = anotherBean;
	}

	public UUID getSubscriptionUuid() {
		return subscriptionUuid;
	}

	public void setSubscriptionUuid(UUID subscriptionUuid) {
		this.subscriptionUuid = subscriptionUuid;
	}
}
