package com.qfree.obo.report.scheduling.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionJobProcessorScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduledJob.class);

	/**
	 * Runs periodically to process outstanding Job entities.
	 */
	public void run() {

		logger.info("********** Write me! Sleeping for 0s **********");
		//		try {
		//			Thread.sleep(30L * 1000L);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		//		logger.info("Running again");

	}

}