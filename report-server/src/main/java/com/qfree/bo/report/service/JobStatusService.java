package com.qfree.bo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.JobStatusRepository;
import com.qfree.bo.report.domain.JobStatus;
import com.qfree.bo.report.dto.JobStatusResource;
import com.qfree.bo.report.util.RestUtils;

@Component
@Transactional
public class JobStatusService {

	private static final Logger logger = LoggerFactory.getLogger(JobStatusService.class);

	private final JobStatusRepository jobStatusRepository;

	@Autowired
	public JobStatusService(JobStatusRepository jobStatusRepository) {
		this.jobStatusRepository = jobStatusRepository;
	}

	@Transactional
	public JobStatus saveNewFromResource(JobStatusResource jobStatusResource) {
		RestUtils.ifNewResourceIdNotNullThen403(jobStatusResource.getJobStatusId(), JobStatus.class,
				"jobStatusId", jobStatusResource.getJobStatusId());
		return saveOrUpdateFromResource(jobStatusResource);
	}

	@Transactional
	public JobStatus saveExistingFromResource(JobStatusResource jobStatusResource) {
		return saveOrUpdateFromResource(jobStatusResource);
	}

	@Transactional
	public JobStatus saveOrUpdateFromResource(JobStatusResource jobStatusResource) {
		logger.debug("jobStatusResource = {}", jobStatusResource);

		JobStatus jobStatus = new JobStatus(jobStatusResource);
		logger.debug("jobStatus = {}", jobStatus);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * JobStatus.
		 */
		jobStatus = jobStatusRepository.save(jobStatus);
		logger.debug("jobStatus (created/updated) = {}", jobStatus);

		return jobStatus;
	}
}
