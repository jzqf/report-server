package com.qfree.obo.report.scheduling.jobs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.exceptions.ReportingException;
import com.qfree.obo.report.exceptions.UntreatedCaseException;
import com.qfree.obo.report.service.BirtService;

@Component
public class SubscriptionJobProcessorScheduledJob {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionJobProcessorScheduledJob.class);

	private static final boolean USE_BYTE_STREAM = true;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private BirtService birtService;

	/**
	 * Runs periodically to process outstanding Job entities.
	 */
	//TODO Use "synchronized" here??? Just to be sure we do not process the same Job in two different threads?
	//     This may not be necessary, because I have used setConcurrent(false) in 
	//     SubscriptionJobProcessorScheduler.scheduleJob(...)
	@Transactional
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

		List<Job> queuedJobs = jobRepository.findByJobStatusJobStatusId(JobStatus.QUEUED_ID);
		logger.info("{} queued Jobs to process", queuedJobs.size());

		for (Job job : queuedJobs) {

			try {
				runAndRender(job);


				//TODO if error emailing document, set status to FAILED, set "Remarks field" and then save Job.

				//TODO Set status to COMPLETED here and save Job??????????????????????????????????????????????????????????????????????????????????????????????

			} catch (ReportingException e) {
				logger.error("Exception thrown processing Job with Id {}:\n{}", job.getJobId(), e);
				JobStatus jobStatus_FAILED = jobStatusRepository.findOne(JobStatus.FAILED_ID);
				logger.info("Setting status to \"FAILED\"");
				job.setJobStatus(jobStatus_FAILED);
				job.setJobStatusRemarks(e.getMessage());
				logger.info("Saving job");
				job = jobRepository.save(job); // probably not necessary, but it cannot hurt
			}

		}

	}

	/**
	 * Runs the BIRT report associated with the {@link Job} passed to this
	 * method.
	 * 
	 * <p>
	 * If no exception is thrown, the {@link Job} is updated to hold the
	 * document, time it was run and other details.
	 * 
	 * @param job
	 * @throws ReportingException
	 */
	private void runAndRender(Job job) throws ReportingException {

		JobStatus jobStatus_RUNNING = jobStatusRepository.findOne(JobStatus.RUNNING_ID);

		logger.info("Setting status to \"RUNNING\"");
		job.setJobStatus(jobStatus_RUNNING);
		job = jobRepository.save(job); // probably not necessary, but it cannot hurt

		logger.info("Processing job = {}", job);
		logger.info("format = {}", job.getSubscription().getDocumentFormat().getBirtFormat());

		/*
		 * Create a map of Object arrays to pass the report parameter 
		 * values to BirtService.runAndRender(...). The map keys will
		 * be the report parameter names and the Object arrays will 
		 * contain the report parameter values. These arrays should
		 * contain only a single value for single-valued report
		 * parameters, but can contain any number of values for multi-
		 * valued parameters.
		 */
		Map<String, Object[]> parameterValueArrays = new HashMap<>();
		for (JobParameter jobParameter : job.getJobParameters()) {
			int numValues = jobParameter.getJobParameterValues().size();
			Object[] parameterValues = new Object[numValues];
			parameterValueArrays.put(jobParameter.getReportParameter().getName(), parameterValues);
			int i = -1;
			for (JobParameterValue jobParameterValue : jobParameter.getJobParameterValues()) {
				i++;
				switch (jobParameter.getReportParameter().getDataType()) {
				case IParameterDefn.TYPE_STRING:
					parameterValues[i] = jobParameterValue.getStringValue();
					break;
				case IParameterDefn.TYPE_FLOAT:
					parameterValues[i] = jobParameterValue.getFloatValue();
					break;
				case IParameterDefn.TYPE_DECIMAL:
					/*
					 * Assume that we can treat parameters of data type
					 * "decimal" as floats. This may not be so, but we 
					 * will give this a try.
					 */
					parameterValues[i] = jobParameterValue.getFloatValue();
					break;
				case IParameterDefn.TYPE_DATE_TIME:
					parameterValues[i] = jobParameterValue.getDatetimeValue();
					break;
				case IParameterDefn.TYPE_BOOLEAN:
					parameterValues[i] = jobParameterValue.getBooleanValue();
					break;
				case IParameterDefn.TYPE_INTEGER:
					parameterValues[i] = jobParameterValue.getIntegerValue();
					break;
				case IParameterDefn.TYPE_DATE:
					parameterValues[i] = jobParameterValue.getDateValue();
					break;
				case IParameterDefn.TYPE_TIME:
					parameterValues[i] = jobParameterValue.getTimeValue();
					break;
				default:
					String errorMessage = String.format("No support for report parameter data type \"%s\"",
							jobParameter.getReportParameter().getDataType());
					throw new UntreatedCaseException(errorMessage);
				}
			}
			logger.info("Parameter \"{}\" values: {}", jobParameter.getReportParameter().getName(),
					parameterValues);
		}

		logger.info("parameterValueArrays = {}", parameterValueArrays);

		/*
		 * The filename job.getReportVersion().getFileName() should end
		 * with ".rptdesign". Ifso, we strip off that extension and then
		 * add the appropriate extension for the document format that
		 * was chosen.
		 */
		String outputFileNameBase = job.getReportVersion().getFileName();
		int lastIndexOfDot = outputFileNameBase.lastIndexOf(".");
		if (lastIndexOfDot >= 0) {
			outputFileNameBase = outputFileNameBase.substring(0, lastIndexOfDot);
		}
		logger.info("outputFileNameBase = {}", outputFileNameBase);
		String outputFileName = outputFileNameBase + "." + job.getDocumentFormat().getFileExtension();
		logger.info("outputFileName = {}", outputFileName);

		String tempDir = System.getProperty("java.io.tmpdir");
		Path outputFileNamePath = Paths.get(tempDir, outputFileName);
		logger.info("outputFileNamePath.toString() = {}", outputFileNamePath.toString());

		byte[] renderedReportBytes = null;
		if (USE_BYTE_STREAM) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			try {
				birtService.runAndRender(
						job.getReportVersion().getRptdesign(),
						parameterValueArrays,
						job.getSubscription().getDocumentFormat().getBirtFormat(),
						null,
						outputStream);

				renderedReportBytes = outputStream.toByteArray();
				logger.info("outputBytes.length = {}", renderedReportBytes.length);
				//	/*
				//	 * This is only for testing purposes. There is no reason to
				//	 * write a document to disk 
				//	 */
				//		Files.write(outputFileNamePath, renderedReportBytes);
				//	} catch (IOException e) {
				//		logger.error("An exception was thrown writing file: ", e);
			} catch (BirtException e) {
				throw new ReportingException("Error running report: " + e.getMessage(), e);
			}

		} else {
			try {
			birtService.runAndRender(
					job.getReportVersion().getRptdesign(),
					parameterValueArrays,
					job.getSubscription().getDocumentFormat().getBirtFormat(),
					outputFileNamePath.toString(),
					null);

			/*
			 * Load the document that was created into a byte array.
			 */
				renderedReportBytes = Files.readAllBytes(outputFileNamePath);
			} catch (IOException e) {
				String errorMessage = String.format("Error loading document \"%s\": %s",
						outputFileNamePath.toString(), e.getMessage());
				throw new ReportingException(errorMessage, e);
			} catch (BirtException e) {
				throw new ReportingException("Error running report: " + e.getMessage(), e);
			}
		}

		/*
		 * Store rendered document in Job entity.
		 */
		if (job.getDocumentFormat().getBinaryData()) {
			/*
			 * The bytes need encoded using Base64 using  the ISO-8859-1
			 * charset.
			 */
			String renderedReportBase64 = Base64.getEncoder().encodeToString(renderedReportBytes);
			job.setDocument(renderedReportBase64);
			job.setEncoded(true);
		} else {
			/*
			 * The bytes can be encoded as a String. We assume here that
			 * UTF-8 will work.
			 */
			String renderedReportString = new String(renderedReportBytes, StandardCharsets.UTF_8);
			job.setDocument(renderedReportString);
			job.setEncoded(false);
		}
		job.setFileName(outputFileName);
	}

}