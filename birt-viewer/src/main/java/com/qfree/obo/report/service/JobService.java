package com.qfree.obo.report.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentFormatRepository;
import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.dto.JobResource;
import com.qfree.obo.report.dto.JobStatusResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.dto.SubscriptionResource;
import com.qfree.obo.report.exceptions.ReportingException;
import com.qfree.obo.report.exceptions.UntreatedCaseException;
import com.qfree.obo.report.util.DateUtils;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class JobService {

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	private static final boolean USE_BYTE_STREAM = true;

	private final JobRepository jobRepository;
	private final JobStatusRepository jobStatusRepository;
	private final DocumentFormatRepository documentFormatRepository;
	private final ReportVersionRepository reportVersionRepository;
	private final RoleRepository roleRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final BirtService birtService;
	private final EmailService emailService;

	@Autowired
	public JobService(
			JobRepository jobRepository,
			JobStatusRepository jobStatusRepository,
			DocumentFormatRepository documentFormatRepository,
			ReportVersionRepository reportVersionRepository,
			RoleRepository roleRepository,
			SubscriptionRepository subscriptionRepository,
			BirtService birtService,
			EmailService emailService) {
		this.jobRepository = jobRepository;
		this.jobStatusRepository = jobStatusRepository;
		this.documentFormatRepository = documentFormatRepository;
		this.reportVersionRepository = reportVersionRepository;
		this.roleRepository = roleRepository;
		this.subscriptionRepository = subscriptionRepository;
		this.birtService = birtService;
		this.emailService = emailService;
	}

	@Transactional
	public Job saveNewFromResource(JobResource jobResource) {

		RestUtils.ifNewResourceIdNotNullThen403(jobResource.getJobId(), Job.class, "jobId", jobResource.getJobId());

		// /*
		// * Enforce NOT NULL constraints.
		// */
		// RestUtils.ifAttrNullThen403(jobResource.getEmailAddress(), Job.class, "emailAddress");

		return saveOrUpdateFromResource(jobResource);
	}

	@Transactional
	public Job saveExistingFromResource(JobResource jobResource) {
		return saveOrUpdateFromResource(jobResource);
	}

	@Transactional
	public Job saveOrUpdateFromResource(JobResource jobResource) {
		logger.debug("jobResource = {}", jobResource);

		/*
		 * Retrieve the JobStatusResource from jobResource.
		 * 
		 * IMPORTANT:
		 * 
		 * We assume here that the jobStatusId attribute of this object is
		 * set to the id of the JobStatus that will we associated with the
		 * Job entity that is be saved/created below. It is not necessary for 
		 * any of the other JobStatusResource attributes to have non-null 
		 * values.
		 * 
		 * If jobStatusId is not provided here, we throw a custom 
		 * exception. 
		 */
		UUID jobStatusId = null;
		JobStatus jobStatus = null;
		JobStatusResource jobStatusResource = jobResource.getJobStatusResource();
		if (jobStatusResource != null) {
			jobStatusId = jobStatusResource.getJobStatusId();
		}
		RestUtils.ifAttrNullThen403(jobStatusId, JobStatus.class, "jobStatusId");
		jobStatus = jobStatusRepository.findOne(jobStatusId);
		RestUtils.ifNullThen404(jobStatus, JobStatus.class, "jobStatusId", jobStatusId.toString());

		/*
		 * Retrieve the DocumentFormatResource from jobResource.
		 * 
		 * IMPORTANT:
		 * 
		 * We assume here that the documentFormatId attribute of this object is
		 * set to the id of the DocumentFormat that will we associated with the
		 * Job entity that is be saved/created below. It is not necessary for 
		 * any of the other DocumentFormatResource attributes to have non-null 
		 * values.
		 * 
		 * If documentFormatId is not provided here, we throw a custom 
		 * exception. 
		 */
		UUID documentFormatId = null;
		DocumentFormat documentFormat = null;
		DocumentFormatResource documentFormatResource = jobResource.getDocumentFormatResource();
		if (documentFormatResource != null) {
			documentFormatId = documentFormatResource.getDocumentFormatId();
		}
		//	if (documentFormatId != null) {
		//		documentFormat = documentFormatRepository.findOne(documentFormatId);
		//		RestUtils.ifNullThen404(documentFormat, DocumentFormat.class, "documentFormatId",
		//				documentFormatId.toString());
		//	} else {
		//		throw new RestApiException(RestError.FORBIDDEN_JOB_DOCUMENTFORMAT_NULL, Job.class, "documentFormatId");
		//	}		
		RestUtils.ifAttrNullThen403(documentFormatId, DocumentFormat.class, "documentFormatId");
		documentFormat = documentFormatRepository.findOne(documentFormatId);
		RestUtils.ifNullThen404(documentFormat, DocumentFormat.class, "documentFormatId", documentFormatId.toString());

		/*
		 * Retrieve the ReportVersionResource from jobResource.
		 * 
		 * IMPORTANT:
		 * 
		 * We assume here that the reportVersionId attribute of this object is
		 * set to the id of the ReportVersion that will we associated with the
		 * Job entity that is be saved/created below. It is not necessary for 
		 * any of the other ReportVersionResource attributes to have non-null 
		 * values.
		 * 
		 * If reportVersionId is not provided here, we throw a custom exception. 
		 */
		UUID reportVersionId = null;
		ReportVersion reportVersion = null;
		ReportVersionResource reportVersionResource = jobResource.getReportVersionResource();
		if (reportVersionResource != null) {
			reportVersionId = reportVersionResource.getReportVersionId();
		}
		//	if (reportVersionId != null) {
		//		reportVersion = reportVersionRepository.findOne(reportVersionId);
		//		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId",
		//				reportVersionId.toString());
		//	} else {
		//		throw new RestApiException(RestError.FORBIDDEN_JOB_REPORTVERSION_NULL,
		//				Job.class, "reportVersionId");
		//	}
		RestUtils.ifAttrNullThen403(reportVersionId, ReportVersion.class, "reportVersionId");
		reportVersion = reportVersionRepository.findOne(reportVersionId);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", reportVersionId.toString());

		/*
		 * Retrieve the RoleResource from jobResource.
		 * 
		 * IMPORTANT:
		 * 
		 * We assume here that the roleId attribute of this object is
		 * set to the id of the Role that will we associated with the
		 * Job entity that is be saved/created below. It is not necessary for 
		 * any of the other RoleResource attributes to have non-null values.
		 * 
		 * If roleId is not provided here, we throw a custom 
		 * exception. 
		 */
		UUID roleId = null;
		Role role = null;
		RoleResource roleResource = jobResource.getRoleResource();
		if (roleResource != null) {
			roleId = roleResource.getRoleId();
		}
		//	if (roleId != null) {
		//		role = roleRepository.findOne(roleId);
		//		RestUtils.ifNullThen404(role, Role.class, "roleId",
		//				roleId.toString());
		//	} else {
		//		throw new RestApiException(RestError.FORBIDDEN_JOB_ROLE_NULL,
		//				Job.class, "roleId");
		//	}
		RestUtils.ifAttrNullThen403(roleId, Role.class, "roleId");
		role = roleRepository.findOne(roleId);
		RestUtils.ifNullThen404(role, Role.class, "roleId", roleId.toString());

		/*
		 * Retrieve the SubscriptionResource from jobResource.
		 * 
		 * IMPORTANT:
		 * 
		 * We assume here that the subscriptionId attribute of this object is
		 * set to the id of the Subscription that will we associated with the
		 * Job entity that is be saved/created below. It is not necessary for 
		 * any of the other SubscriptionResource attributes to have non-null 
		 * values.
		 * 
		 * Note: It is legal for jobResource.getSubscriptionResource() to be 
		 *       null here, i.e., a Job does *not* need to be linked to a
		 *       Subscription. But *if* a non-null SubscriptionResource object
		 *       is provided by jobResource.getSubscriptionResource() then we 
		 *       do insist that its "subscriptionId" attribute is set to the
		 *       id of an existing Subscription. If not, we throw a custom 
		 *       exception. 
		 */
		UUID subscriptionId = null;
		Subscription subscription = null;
		SubscriptionResource subscriptionResource = jobResource.getSubscriptionResource();
		if (subscriptionResource != null) {
			subscriptionId = subscriptionResource.getSubscriptionId();
			RestUtils.ifAttrNullThen403(subscriptionId, Subscription.class, "subscriptionId");
			subscription = subscriptionRepository.findOne(subscriptionId);
			RestUtils.ifNullThen404(subscription, Subscription.class, "subscriptionId", subscriptionId.toString());
		}

		Job job = new Job(jobResource, jobStatus, documentFormat, reportVersion, role, subscription);
		logger.debug("job = {}", job);

		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * Job.
		 */
		job = jobRepository.save(job);
		logger.debug("job (created/updated) = {}", job);

		return job;
	}

	/**
	 * Sets the status of the specified Job to the specified JobStatus.
	 * 
	 * @param jobId
	 * @param jobStatusId
	 * @return
	 * @throws ReportingException
	 */
	@Transactional
	public Job setJobStatus(Long jobId, UUID jobStatusId, String jobStatusRemarks) throws ReportingException {
		Job job = jobRepository.findOne(jobId);
		if (job == null) {
			throw new ReportingException("No Job found for jobId = " + jobId);
		}
		JobStatus jobStatus = jobStatusRepository.findOne(jobStatusId);
		logger.info("Setting status to \"{}\", remarks to {}", jobStatus.toString(), jobStatusRemarks);
		job.setJobStatus(jobStatus);
		job.setJobStatusRemarks(jobStatusRemarks);
		job = jobRepository.save(job); // probably not necessary, but it cannot hurt
		return job;
	}

	/**
	 * Runs the BIRT report associated with the {@link Job} that is specified by
	 * its id.
	 * 
	 * <p>
	 * If no exception is thrown, the {@link Job} is updated to hold the
	 * document, time it was run and other details.
	 * 
	 * @param jobId
	 * @throws ReportingException
	 */
	@Transactional //(propagation = Propagation.REQUIRES_NEW)
	public void runAndRenderJob(Long jobId) throws ReportingException {

		//	logger.info("Setting status to \"RUNNING\"");
		//	JobStatus jobStatus_RUNNING = jobStatusRepository.findOne(JobStatus.RUNNING_ID);
		//	job.setJobStatus(jobStatus_RUNNING);
		//	job = jobRepository.save(job); // probably not necessary, but it cannot hurt

		Job job = jobRepository.findOne(jobId);
		if (job == null) {
			throw new ReportingException("No Job found for jobId = " + jobId);
		}

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
		job.setReportRanAt();
	}

	/**
	 * E-mails the rendered report associated with the {@link Job} that is
	 * specified by its id.
	 * 
	 * <p>
	 * If no exception is thrown, the {@link Job} is updated to hold details
	 * regarding the delivery of the document.
	 * 
	 * @param jobId
	 * @throws ReportingException
	 */
	@Transactional
	public void emailJobDocument(Long jobId) throws ReportingException {

		Job job = jobRepository.findOne(jobId);
		if (job == null) {
			throw new ReportingException("No Job found for jobId = " + jobId);
		}
		logger.info("E-mailing job = {}", job);

		/*
		 * This Classloader is used for loading the e-mail subject and body
		 * templates below.
		 */
		ClassLoader classLoader = getClass().getClassLoader();

		/*
		 * Load e-mail subject template from classpath. The resource name 
		 * provided is relative to this Eclipse project's src/main/resources/
		 * directory.
		 */
		File emailSubjectTemplateFile = new File(
				classLoader.getResource("templates/job_delivery_email_subject.txt").getFile());
		Path emailSubjectTemplatePath = emailSubjectTemplateFile.toPath();
		logger.debug("emailSubjectTemplatePath = {}", emailSubjectTemplatePath);
		String emailSubjectTemplateText = null;
		try {
			emailSubjectTemplateText = new String(Files.readAllBytes(emailSubjectTemplatePath),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new ReportingException("Error loading e-mail subject template from classpath", e);
		}
		logger.info("emailSubjectTemplateText = {}", emailSubjectTemplateText);

		/*
		 * Load e-mail body template from classpath. The resource name 
		 * provided is relative to this Eclipse project's src/main/resources/
		 * directory.
		 */
		File emailMsgBodyTemplateFile = new File(
				classLoader.getResource("templates/job_delivery_email_body.txt").getFile());
		Path emailMsgBodyTemplatePath = emailMsgBodyTemplateFile.toPath();
		logger.debug("emailMsgBodyTemplatePath = {}", emailMsgBodyTemplatePath);
		String emailMsgBodyTemplateText = null;
		try {
			emailMsgBodyTemplateText = new String(Files.readAllBytes(emailMsgBodyTemplatePath),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new ReportingException("Error loading e-mail body template from classpath", e);
		}
		logger.info("emailMsgBodyTemplateText = {}", emailMsgBodyTemplateText);

		/*
		 * Convert the document stored in the field Job.document into a byte
		 * array. If the document is currently Base64 encoded, it is decoded
		 * here.
		 */
		byte[] documentBytes;
		if (job.getEncoded()) {
			try {
				documentBytes = Base64.getDecoder().decode(job.getDocument());
			} catch (IllegalArgumentException e) {
				throw new ReportingException("Document cannot be decoded for jobId = " + jobId, e);
			}
		} else {
			documentBytes = job.getDocument().getBytes(StandardCharsets.UTF_8);
		}

		/*
		 * These are the arguments that can be interpolated into the email
		 * subject and message body templates.
		 */
		Object[] messageArguments = new Object[7];
		messageArguments[0] = job.getReportVersion().getReport().getName();
		messageArguments[1] = job.getReportVersion().getReport().getNumber();
		messageArguments[2] = job.getReportVersion().getFileName(); // rptdesign filename
		messageArguments[3] = job.getReportVersion().getVersionName();
		messageArguments[4] = job.getFileName(); // rendered report filename
		/*
		 * Adjust job.getReportRanAt() to be relative to the time zone where the
		 * report server is located. Currnently, we make no attempt to express
		 * the datetime in the time zone of the report user or e-mail recipient
		 * because we do not know what that time zone is (we would need to add
		 * support for that). 
		 * 
		 * job.getReportRanAt() holds the datetime relative to UTC. If we do not
		 * adjust it here, it will be wrong unless the
		 */
		messageArguments[5] = DateUtils.entityTimestampToServerTimezoneDate(job.getReportRanAt());
		messageArguments[6] = documentBytes.length;

		String subject = new MessageFormat(emailSubjectTemplateText, Locale.getDefault()).format(messageArguments);
		String msgBody = new MessageFormat(emailMsgBodyTemplateText, Locale.getDefault()).format(messageArguments);
		logger.info("subject = {}", subject);
		logger.info("msgBody = {}", msgBody);

		try {
			emailService.sendEmail(
					job.getEmailAddress(),
					subject,
					msgBody,
					documentBytes,
					job.getDocumentFormat().getInternetMediaType(),
					job.getFileName());
		} catch (MessagingException e) {
			throw new ReportingException("Error sending e-mail: " + e.getMessage(), e);
		}

		/*
		 * Set details associated with the delivery.
		 */
		job.setReportEmailedAt();

	}

}
