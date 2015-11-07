package com.qfree.obo.report.scheduling.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.exceptions.ReportingException;
import com.qfree.obo.report.service.JobService;

@Component
public class SubscriptionJobProcessorScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduledJob.class);

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private JobService jobService;

	//	@Autowired
	//	private BirtService birtService;

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

				job = jobService.setJobStatus(job.getJobId(), JobStatus.RUNNING_ID);

				/*
				 * We bypass the report rendering step if it look like it was 
				 * already performed. This bypass will happen only if "job" was
				 * left in the state "RUNNING" and it was re-QUEUED elsewhere in 
				 * order so that it will be fully processed. A newly created
				 * Job always has document==null, reportRanAt==null, ....
				 */
				//				if (job.getDocument() == null) {
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
				 *      on the current object ("self invocation") will bypass 
				 *      that proxy.
				 *      
				 * These requirements are satisfied by runAndRenderJob(...)
				 * since it is a method of the "jobService" object injected 
				 * above by Spring, and it is also annotated with 
				 * @Transactional.
				 */
				jobService.runAndRenderJob(job.getJobId());
				//				}

				//	job = jobService.setJobStatus(job.getJobId(), JobStatus.DELIVERING_ID);

				/*
				 * E-mail the rendered report document to the recipient.
				 * 
				 * jobService.emailJobDocument(...) must be a transactional 
				 * method for the same reasons given in the comment for the call
				 * to jobService.runAndRenderJob(...) above.
				 */
				jobService.emailJobDocument(job.getJobId());

				if (1 == 1) {
					throw new ReportingException("#1: Exception thrown to test transaction behviour.");
				}
				/*
				 * 
				 */
				logger.info("Setting status to \"COMPLETED\"");
				JobStatus jobStatus_COMPLETED = jobStatusRepository.findOne(JobStatus.COMPLETED_ID);
				job.setJobStatus(jobStatus_COMPLETED);
				job = jobRepository.save(job); // probably not necessary, but it cannot hurt

			} catch (ReportingException e) {
				logger.error("Exception thrown processing Job with Id {}:\n{}", job.getJobId(), e);
				JobStatus jobStatus_FAILED = jobStatusRepository.findOne(JobStatus.FAILED_ID);
				logger.info("Setting status to \"{}\"", jobStatus_FAILED.toString());
				job.setJobStatus(jobStatus_FAILED);
				job.setJobStatusRemarks(e.getMessage());
				logger.info("Saving job");
				job = jobRepository.save(job); // probably not necessary, but it cannot hurt
			}

		}
	}

	//	/**
	//	 * Runs the BIRT report associated with the {@link Job} that is specified by
	//	 * its id.
	//	 * 
	//	 * <p>
	//	 * If no exception is thrown, the {@link Job} is updated to hold the
	//	 * document, time it was run and other details.
	//	 * 
	//	 * @param jobId
	//	 * @throws ReportingException
	//	 */
	//	@Transactional //(propagation = Propagation.REQUIRES_NEW)
	//	public void runAndRenderJob(Long jobId) throws ReportingException {
	//
	//		//	logger.info("Setting status to \"RUNNING\"");
	//		//	JobStatus jobStatus_RUNNING = jobStatusRepository.findOne(JobStatus.RUNNING_ID);
	//		//	job.setJobStatus(jobStatus_RUNNING);
	//		//	job = jobRepository.save(job); // probably not necessary, but it cannot hurt
	//
	//		Job job = jobRepository.findOne(jobId);
	//		if (job == null) {
	//			throw new ReportingException("No Job found for jobId = " + jobId);
	//		}
	//
	//		logger.info("Processing job = {}", job);
	//		logger.info("format = {}", job.getSubscription().getDocumentFormat().getBirtFormat());
	//
	//		/*
	//		 * Create a map of Object arrays to pass the report parameter 
	//		 * values to BirtService.runAndRender(...). The map keys will
	//		 * be the report parameter names and the Object arrays will 
	//		 * contain the report parameter values. These arrays should
	//		 * contain only a single value for single-valued report
	//		 * parameters, but can contain any number of values for multi-
	//		 * valued parameters.
	//		 */
	//		Map<String, Object[]> parameterValueArrays = new HashMap<>();
	//		for (JobParameter jobParameter : job.getJobParameters()) {
	//			int numValues = jobParameter.getJobParameterValues().size();
	//			Object[] parameterValues = new Object[numValues];
	//			parameterValueArrays.put(jobParameter.getReportParameter().getName(), parameterValues);
	//			int i = -1;
	//			for (JobParameterValue jobParameterValue : jobParameter.getJobParameterValues()) {
	//				i++;
	//				switch (jobParameter.getReportParameter().getDataType()) {
	//				case IParameterDefn.TYPE_STRING:
	//					parameterValues[i] = jobParameterValue.getStringValue();
	//					break;
	//				case IParameterDefn.TYPE_FLOAT:
	//					parameterValues[i] = jobParameterValue.getFloatValue();
	//					break;
	//				case IParameterDefn.TYPE_DECIMAL:
	//					/*
	//					 * Assume that we can treat parameters of data type
	//					 * "decimal" as floats. This may not be so, but we 
	//					 * will give this a try.
	//					 */
	//					parameterValues[i] = jobParameterValue.getFloatValue();
	//					break;
	//				case IParameterDefn.TYPE_DATE_TIME:
	//					parameterValues[i] = jobParameterValue.getDatetimeValue();
	//					break;
	//				case IParameterDefn.TYPE_BOOLEAN:
	//					parameterValues[i] = jobParameterValue.getBooleanValue();
	//					break;
	//				case IParameterDefn.TYPE_INTEGER:
	//					parameterValues[i] = jobParameterValue.getIntegerValue();
	//					break;
	//				case IParameterDefn.TYPE_DATE:
	//					parameterValues[i] = jobParameterValue.getDateValue();
	//					break;
	//				case IParameterDefn.TYPE_TIME:
	//					parameterValues[i] = jobParameterValue.getTimeValue();
	//					break;
	//				default:
	//					String errorMessage = String.format("No support for report parameter data type \"%s\"",
	//							jobParameter.getReportParameter().getDataType());
	//					throw new UntreatedCaseException(errorMessage);
	//				}
	//			}
	//			logger.info("Parameter \"{}\" values: {}", jobParameter.getReportParameter().getName(),
	//					parameterValues);
	//		}
	//
	//		logger.info("parameterValueArrays = {}", parameterValueArrays);
	//
	//		/*
	//		 * The filename job.getReportVersion().getFileName() should end
	//		 * with ".rptdesign". Ifso, we strip off that extension and then
	//		 * add the appropriate extension for the document format that
	//		 * was chosen.
	//		 */
	//		String outputFileNameBase = job.getReportVersion().getFileName();
	//		int lastIndexOfDot = outputFileNameBase.lastIndexOf(".");
	//		if (lastIndexOfDot >= 0) {
	//			outputFileNameBase = outputFileNameBase.substring(0, lastIndexOfDot);
	//		}
	//		logger.info("outputFileNameBase = {}", outputFileNameBase);
	//		String outputFileName = outputFileNameBase + "." + job.getDocumentFormat().getFileExtension();
	//		logger.info("outputFileName = {}", outputFileName);
	//
	//		String tempDir = System.getProperty("java.io.tmpdir");
	//		Path outputFileNamePath = Paths.get(tempDir, outputFileName);
	//		logger.info("outputFileNamePath.toString() = {}", outputFileNamePath.toString());
	//
	//		byte[] renderedReportBytes = null;
	//		if (USE_BYTE_STREAM) {
	//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	//
	//			try {
	//				birtService.runAndRender(
	//						job.getReportVersion().getRptdesign(),
	//						parameterValueArrays,
	//						job.getSubscription().getDocumentFormat().getBirtFormat(),
	//						null,
	//						outputStream);
	//
	//				renderedReportBytes = outputStream.toByteArray();
	//				logger.info("outputBytes.length = {}", renderedReportBytes.length);
	//				//	/*
	//				//	 * This is only for testing purposes. There is no reason to
	//				//	 * write a document to disk 
	//				//	 */
	//				//		Files.write(outputFileNamePath, renderedReportBytes);
	//				//	} catch (IOException e) {
	//				//		logger.error("An exception was thrown writing file: ", e);
	//			} catch (BirtException e) {
	//				throw new ReportingException("Error running report: " + e.getMessage(), e);
	//			}
	//
	//		} else {
	//			try {
	//				birtService.runAndRender(
	//						job.getReportVersion().getRptdesign(),
	//						parameterValueArrays,
	//						job.getSubscription().getDocumentFormat().getBirtFormat(),
	//						outputFileNamePath.toString(),
	//						null);
	//
	//				/*
	//				 * Load the document that was created into a byte array.
	//				 */
	//				renderedReportBytes = Files.readAllBytes(outputFileNamePath);
	//			} catch (IOException e) {
	//				String errorMessage = String.format("Error loading document \"%s\": %s",
	//						outputFileNamePath.toString(), e.getMessage());
	//				throw new ReportingException(errorMessage, e);
	//			} catch (BirtException e) {
	//				throw new ReportingException("Error running report: " + e.getMessage(), e);
	//			}
	//		}
	//
	//		/*
	//		 * Store rendered document in Job entity.
	//		 */
	//		if (job.getDocumentFormat().getBinaryData()) {
	//			/*
	//			 * The bytes need encoded using Base64 using  the ISO-8859-1
	//			 * charset.
	//			 */
	//			String renderedReportBase64 = Base64.getEncoder().encodeToString(renderedReportBytes);
	//			job.setDocument(renderedReportBase64);
	//			job.setEncoded(true);
	//		} else {
	//			/*
	//			 * The bytes can be encoded as a String. We assume here that
	//			 * UTF-8 will work.
	//			 */
	//			String renderedReportString = new String(renderedReportBytes, StandardCharsets.UTF_8);
	//			job.setDocument(renderedReportString);
	//			job.setEncoded(false);
	//		}
	//		job.setFileName(outputFileName);
	//	}

	//	/**
	//	 * E-mails the rendered report associated with the {@link Job} passed to
	//	 * this method.
	//	 * 
	//	 * <p>
	//	 * If no exception is thrown, the {@link Job} is updated to hold details
	//	 * regarding the delivery of the document.
	//	 * 
	//	 * @param job
	//	 * @throws ReportingException
	//	 */
	//	private void emailJob(Job job) throws ReportingException {
	//
	//		//TODO  RUNNING -> ??????????????????????????????????????????????????????????????????????????????
	//		//		logger.info("Setting status to \"RUNNING\"");
	//		//		JobStatus jobStatus_RUNNING = jobStatusRepository.findOne(JobStatus.RUNNING_ID);
	//		//		job.setJobStatus(jobStatus_RUNNING);
	//		//		job = jobRepository.save(job); // probably not necessary, but it cannot hurt
	//
	//		logger.info("E-mailing job = {}", job);
	//
	//		//			try {
	//		//
	//		//			} catch (BirtException e) {
	//		//				throw new ReportingException("Error running report: " + e.getMessage(), e);
	//		//			}
	//
	//		/*
	//		 * Set details associated with the delivery.
	//		 */
	//		//		job...
	//	}

}