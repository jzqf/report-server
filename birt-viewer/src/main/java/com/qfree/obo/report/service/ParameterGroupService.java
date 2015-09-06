package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ParameterGroupRepository;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.dto.ParameterGroupResource;
import com.qfree.obo.report.rest.server.RestUtils;

@Component
@Transactional
public class ParameterGroupService {

	private static final Logger logger = LoggerFactory.getLogger(ParameterGroupService.class);

	private final ParameterGroupRepository parameterGroupRepository;

	@Autowired
	public ParameterGroupService(ParameterGroupRepository parameterGroupRepository) {
		this.parameterGroupRepository = parameterGroupRepository;
	}

	@Transactional
	public ParameterGroup saveNewFromResource(ParameterGroupResource parameterGroupResource) {
		RestUtils.ifNewResourceIdNotNullThen403(parameterGroupResource.getParameterGroupId(), ParameterGroup.class,
				"parameterGroupId", parameterGroupResource.getParameterGroupId());
		return saveOrUpdateFromResource(parameterGroupResource);
	}

	@Transactional
	public ParameterGroup saveExistingFromResource(ParameterGroupResource parameterGroupResource) {
		return saveOrUpdateFromResource(parameterGroupResource);
	}

	@Transactional
	public ParameterGroup saveOrUpdateFromResource(ParameterGroupResource parameterGroupResource) {
		logger.debug("parameterGroupResource = {}", parameterGroupResource);

		ParameterGroup parameterGroup = new ParameterGroup(parameterGroupResource);
		logger.debug("parameterGroup = {}", parameterGroup);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * ParameterGroup.
		 */
		parameterGroup = parameterGroupRepository.save(parameterGroup);
		logger.debug("parameterGroup (created/updated) = {}", parameterGroup);

		return parameterGroup;
	}
}
