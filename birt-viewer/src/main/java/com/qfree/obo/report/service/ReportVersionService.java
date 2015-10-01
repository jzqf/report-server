package com.qfree.obo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.rest.server.RestUtils;

@Component
@Transactional
public class ReportVersionService {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionService.class);

	private final ReportVersionRepository reportVersionRepository;
	private final ReportRepository reportRepository;

	@Autowired
	public ReportVersionService(
			ReportVersionRepository reportVersionRepository,
			ReportRepository reportRepository) {
		this.reportVersionRepository = reportVersionRepository;
		this.reportRepository = reportRepository;
	}

	@Transactional
	public ReportVersion saveNewFromResource(ReportVersionResource reportVersionResource) {
		RestUtils.ifNewResourceIdNotNullThen403(reportVersionResource.getReportVersionId(), ReportVersion.class,
				"reportVersionId", reportVersionResource.getReportVersionId());
		return saveOrUpdateFromResource(reportVersionResource);
	}

	@Transactional
	public ReportVersion saveExistingFromResource(ReportVersionResource reportVersionResource) {
		return saveOrUpdateFromResource(reportVersionResource);
	}

	@Transactional
	public ReportVersion saveOrUpdateFromResource(ReportVersionResource reportVersionResource) {
		logger.debug("reportVersionResource = {}", reportVersionResource);

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the ReportResource from reportVersionResource. We assume
		 * here that the reportId attribute of this object is set to
		 * the id of the Report that will we associated with the ReportVersion 
		 * entity that is be saved/created below. It is not necessary for
		 * any on the other ReportResource attributes to have non-null values.
		 * 
		 * If reportId is not provided here, we throw a custom exception. 
		 */
		ReportResource reportResource = reportVersionResource.getReportResource();
		logger.debug("reportResource = {}", reportResource);
		UUID reportId = null;
		if (reportResource != null) {
			reportId = reportResource.getReportId();
		}
		logger.debug("reportId = {}", reportId);
		Report report = null;
		if (reportId != null) {
			report = reportRepository.findOne(reportId);
			RestUtils.ifNullThen404(report, Report.class, "reportId", reportId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_REPORTVERSION_REPORT_NULL, ReportVersion.class, "reportId");
		}

		ReportVersion reportVersion = new ReportVersion(reportVersionResource, report);
		logger.debug("reportVersion = {}", reportVersion);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * ReportVersion.
		 */
		reportVersion = reportVersionRepository.save(reportVersion);
		logger.debug("reportVersion (created/updated) = {}", reportVersion);

		return reportVersion;
	}

	/**
	 * Returns the a value to assign to the "versionCode" field of the next
	 * Report Version for a specified Report. This will be the maximum value
	 * of "versionCode" for all Report Versions linked to the specified Report,
	 * incremented by one.
	 * 
	 * @param report
	 * @return
	 */
	@Transactional
	public Integer nextVersionCode(Report report) {
		Integer maxVersionCode = reportVersionRepository.maxVersionCodeForReport(report);
		return (maxVersionCode != null ? maxVersionCode : 0) + 1;
		//		return reportVersionRepository.maxVersionCodeForReport(report) + 1;
	}

	/**
	 * Returns the a value to assign to the "versionCode" field of the next
	 * Report Version for a specified Report. This will be the maximum value
	 * of "versionCode" for all Report Versions linked to the specified Report,
	 * incremented by one.
	 * 
	 * @param report
	 * @return
	 */
	@Transactional
	public Integer nextVersionCode(UUID reportId) {
		Report report = reportRepository.findOne(reportId);
		return nextVersionCode(report);
	}
}
