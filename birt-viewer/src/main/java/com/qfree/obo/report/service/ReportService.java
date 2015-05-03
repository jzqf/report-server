package com.qfree.obo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportCategoryRepository;
import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.dto.ReportCategoryResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.exceptions.ReportingException;

@Component
@Transactional
public class ReportService {

	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	private final ReportRepository reportRepository;

	private final ReportCategoryRepository reportCategoryRepository;

	@Autowired
	public ReportService(ReportRepository reportRepository, ReportCategoryRepository reportCategoryRepository) {
		this.reportRepository = reportRepository;
		this.reportCategoryRepository = reportCategoryRepository;
	}

	@Transactional
	public Report saveOrUpdateFromResource(ReportResource reportResource) {
		logger.info("reportResource = {}", reportResource);

		/*
		 * Extract ReportCategoryResource from reportResource. We will assume
		 * here that the reportCategoryId attribute of the object is set to
		 * the id of the ReportCategory that
		 */
		ReportCategoryResource reportCategoryResource = reportResource.getReportCategoryResource();
		logger.info("reportCategoryResource = {}", reportCategoryResource);
		
		UUID reportCategoryId = reportCategoryResource.getReportCategoryId();
		logger.info("reportCategoryId = {}", reportCategoryId);
		ReportCategory reportCategory=null;
		if(reportCategoryId!=null){
			reportCategory = reportCategoryRepository.findOne(reportCategoryId);
		} else {
			//TODO Create custom exception for this case? Write custom exception mapper.
			throw new ReportingException("reportCategoryId is null");
		}

		/*
		 * Create Report entity instance from the supplied ReportResource 
		 * instance.
		 */
		Report report = new Report(
				reportResource.getReportId(),
				reportCategory,
				reportResource.getName(),
				reportResource.getNumber(),
				reportResource.isActive(),
				reportResource.getCreatedOn());
		logger.info("report = {}", report);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * Report.
		 */
		report = reportRepository.save(report);
		logger.info("report (created/updated) = {}", report);

		return report;
	}
}
