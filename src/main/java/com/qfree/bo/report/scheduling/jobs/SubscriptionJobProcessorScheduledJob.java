package com.qfree.bo.report.scheduling.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.bo.report.db.JobRepository;
import com.qfree.bo.report.db.JobStatusRepository;
import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.domain.JobStatus;
import com.qfree.bo.report.exceptions.ReportingException;
import com.qfree.bo.report.service.JobService;

@Component
public class SubscriptionJobProcessorScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduledJob.class);

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private JobService jobService;

	/**
	 * Runs periodically to process outstanding Job entities.
	 */
	//TODO Use "synchronized" here??? Just to be sure we do not process the same Job in two different threads?
	//     This may not be necessary, because I have used setConcurrent(false) in 
	//     SubscriptionJobProcessorScheduler.scheduleJob(...)
	//	@Transactional
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

		logger.info("");

		/*
		 * Since this run() method is not transactional, "queuedJobs" will be a 
		 * list of DETACHED Job entities. This is the desired behavior. It 
		 * allows changes to be persisted to Job entities below without forcing
		 * all earlier changes to be rolled back in the event that an exception
		 * is thrown. It also means that the status of each Job can be monitored
		 * from other processes as it is updated - changes to Job entities will
		 * be persisted and be visible to other processes after 
		 */
		List<Job> queuedJobs = jobRepository.findByJobStatusJobStatusId(JobStatus.QUEUED_ID);
		logger.info("{} queued Jobs to process", queuedJobs.size());

		for (Job job : queuedJobs) {

			try {

				job = jobService.setJobStatus(job.getJobId(), JobStatus.RUNNING_ID, null);

				/*
				 * We bypass the report rendering step if it looks like it was 
				 * already performed. This bypass will happen only if "job" was
				 * re-QUEUED elsewhere (probably because it was discovered that
				 * the Job was not fully processed). A newly created Job always
				 * has document==null, reportRanAt==null, ...
				 */
				if (job.getDocument() == null) {
					/*
					 * jobService.runAndRenderJob(...) must be a transactional 
					 * method for several reasons:
					 * 
					 *   1. To be able to rollback changes in the event of an
					 *   	exception being thrown.
					 *   
					 *   2.	To work with *attached* JPA objects so that 
					 *   	org.hibernate.LazyInitializationException exceptions
					 *   	are avoided.
					 *   
					 * In order for this method call to "runAndRenderJob" to be 
					 * transactional, there are two requirements:
					 * 
					 *   1. The "runAndRenderJob method must be annotated with
					 *      @Transactional.
					 *      
					 *   2. The "runAndRenderJob method must be a method of an 
					 *      object managed by Spring, but not a method of the 
					 *      current SubscriptionJobProcessorScheduledJob object. 
					 *      This is because Spring sets up proxy methods for 
					 *      @Transactional methods and calling a method directly 
					 *      on the current object ("self invocation") will 
					 *      bypass that proxy.
					 *      
					 * These requirements are satisfied by runAndRenderJob(...)
					 * since it is a method of the "jobService" object injected 
					 * above by Spring, and it is also annotated with 
					 * @Transactional.
					 */
					jobService.runAndRenderJob(job.getJobId());
				}

				job = jobService.setJobStatus(job.getJobId(), JobStatus.DELIVERING_ID, null);

				/*
				 * We attempt to e-mail the rendered document only if there is 
				 * an email address provided. It is conceivable that a Job was 
				 * created to run a report *without* the rendered report then
				 * being e-mailed. In such a case, the recipient must display
				 * the Job record and then download the rendered report document
				 * from the Job.
				 * 
				 * Current programming logic in this application tries to ensure
				 * that all enabled Subscription entities have  value for their
				 * "emilAddress" field. Since this value is assigned to a new
				 * Job entity's "emailAddress" field when a new Job is created
				 * in SubscriptionScheduledJob.run(), job.getEmailAddress()
				 * should, ideally, never be null here. But future changes to
				 * this application's logic/behaviour may change this; if so,
				 * we handle that case here.
				 */
				if (job.getEmailAddress() != null && !job.getEmailAddress().isEmpty()) {
					/*
					 * We bypass the report delivery step if it looks like it
					 * was already performed. This bypass will happen only if 
					 * it was re-QUEUED elsewhere (probably because it was 
					 * discovered that the Job was not fully processed). A newly
					 * created Job always has reportEmailedAt==null, ...
					 */
					if (job.getReportEmailedAt() == null) {
						/*
						 * E-mail the rendered report document to the recipient.
						 * 
						 * jobService.emailJobDocument(...) must be a 
						 * transactional method for the same reasons given in
						 * the comment for the call to 
						 * jobService.runAndRenderJob(...) above.
						 */
						jobService.emailJobDocument(job.getJobId());
					}
				}

				/*
				 * 
				 */
				job = jobService.setJobStatus(job.getJobId(), JobStatus.COMPLETED_ID, null);

			} catch (ReportingException e) {
				logger.error("Exception thrown processing Job with Id {}:\n{}", job.getJobId(), e);
				try {
					job = jobService.setJobStatus(job.getJobId(), JobStatus.FAILED_ID, e.getMessage());
				} catch (ReportingException e1) {
					logger.error("Exception thrown setting the status for a job: {}", e1.getMessage());
				}
			}

		}
	}
}