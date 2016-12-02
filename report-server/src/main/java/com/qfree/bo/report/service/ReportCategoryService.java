package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportCategoryRepository;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.dto.ReportCategoryResource;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class ReportCategoryService {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryService.class);

	private final ReportCategoryRepository reportCategoryRepository;

	@Autowired
	public ReportCategoryService(ReportCategoryRepository reportCategoryRepository) {
		this.reportCategoryRepository = reportCategoryRepository;
	}

	@Transactional
	public ReportCategory saveNewFromResource(ReportCategoryResource reportCategoryResource) {
		RestUtils.ifNewResourceIdNotNullThen403(reportCategoryResource.getReportCategoryId(), ReportCategory.class,
				"reportCategoryId", reportCategoryResource.getReportCategoryId());
		return saveOrUpdateFromResource(reportCategoryResource);
	}

	@Transactional
	public ReportCategory saveExistingFromResource(ReportCategoryResource reportCategoryResource) {
		return saveOrUpdateFromResource(reportCategoryResource);
	}

	@Transactional
	public ReportCategory saveOrUpdateFromResource(ReportCategoryResource reportCategoryResource) {
		logger.debug("reportCategoryResource = {}", reportCategoryResource);

		ReportCategory reportCategory = new ReportCategory(reportCategoryResource);
		logger.debug("reportCategory = {}", reportCategory);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * ReportCategory.
		 */
		reportCategory = reportCategoryRepository.save(reportCategory);
		logger.debug("reportCategory (created/updated) = {}", reportCategory);

		return reportCategory;
	}
}
