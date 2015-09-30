package com.qfree.obo.report.scheduling.jobs;

import java.util.UUID;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/*
 * The scope of this bean is "prototype" because a new instance is required for
 * each value assigned to subscriptionUuid.
 */
@Component
@Scope(value = "prototype")
public class SubscriptionScheduledJob extends QuartzJobBean{

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionScheduledJob.class);

	private UUID subscriptionUuid;

	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		logger.info("subscriptionUuid = {}", subscriptionUuid);

		logger.info("********** Write me! Sleeping for 0s **********");
		//		try {
		//			Thread.sleep(30L * 1000L);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		//		logger.info("Running again");
	}

	public UUID getSubscriptionUuid() {
		return subscriptionUuid;
	}

	public void setSubscriptionUuid(UUID subscriptionUuid) {
		this.subscriptionUuid = subscriptionUuid;
	}
}
