package com.qfree.obo.report.service;

import java.util.UUID;

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
import com.qfree.obo.report.rest.server.RestUtils;

@Component
@Transactional
public class JobService {

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	private final JobRepository jobRepository;
	private final JobStatusRepository jobStatusRepository;
	private final DocumentFormatRepository documentFormatRepository;
	private final ReportVersionRepository reportVersionRepository;
	private final RoleRepository roleRepository;
	private final SubscriptionRepository subscriptionRepository;

	@Autowired
	public JobService(
			JobRepository jobRepository,
			JobStatusRepository jobStatusRepository,
			DocumentFormatRepository documentFormatRepository,
			ReportVersionRepository reportVersionRepository,
			RoleRepository roleRepository,
			SubscriptionRepository subscriptionRepository) {
		this.jobRepository = jobRepository;
		this.jobStatusRepository = jobStatusRepository;
		this.documentFormatRepository = documentFormatRepository;
		this.reportVersionRepository = reportVersionRepository;
		this.roleRepository = roleRepository;
		this.subscriptionRepository = subscriptionRepository;
	}

	@Transactional
	public Job saveNewFromResource(JobResource jobResource) {

		RestUtils.ifNewResourceIdNotNullThen403(jobResource.getJobId(), Job.class, "jobId", jobResource.getJobId());

		// /*
		// * Enforce NOT NULL constraints.
		// */
		// RestUtils.ifAttrNullThen403(jobResource.getEmail(), Job.class, "email");

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
}
