package com.qfree.obo.report.scheduling.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.JobRepository;

@Component
public class SubscriptionJobProcessorScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduledJob.class);

	@Autowired
	private JobRepository jobRepository;

	/**
	 * Runs periodically to process outstanding Job entities.
	 */
	//TODO Use "synchronized" here??? Just to be sure we do not process the same Job in two different threads?
	public void run() {

		logger.info("********** Write me!");
		logger.info("jobRepository.count() = {}", jobRepository.count());

		//		try {
		//			Thread.sleep(30L * 1000L);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}
		//		logger.info("Running again");

	}

}