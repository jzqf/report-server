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

		/*
		 * Although this job is triggered periodically according its trigger 
		 * settings, it is *also* triggered manually from:
		 * 
		 *     SubscriptionScheduledJob.run()
		 * 
		 * so that this method will almost immediately run to process the Job
		 * created by SubscriptionScheduledJob.run(), instead of waiting for 
		 * this method to be run according to its trigger settings. 
		 * 
		 * If we do *not* sleep here a short while, this code here seems to run
		 * before the transaction that wraps SubscriptionScheduledJob.run() is
		 * committed. As a result, we will not see the new Job here. Testing has 
		 * shown that waiting here for a mere one second is enough to allow that
		 * transaction to complete. This is not guaranteed to work because it
		 * depends on non-deterministic multitasking details of the underlying 
		 * Java VM. But if this method does *not* see the new Job, all it means
		 * is that we must wait for the next scheduled run of this method (or 
		 * for SubscriptionScheduledJob.run() to run for another subscription, 
		 * since that will also trigger this method).
		 */
		try {
			Thread.sleep(1L * 1000L);
		} catch (InterruptedException e) {
			// Do nothing
		}

		logger.info("********** Write me!");
		logger.info("jobRepository.count() = {}", jobRepository.count());

	}

}