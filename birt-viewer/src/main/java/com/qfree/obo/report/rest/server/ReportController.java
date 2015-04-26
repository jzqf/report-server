package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.resource.AbstractResource;
import com.qfree.obo.report.resource.ReportResource;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@Component
@Path(AbstractResource.REPORTS_PATH)
public class ReportController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	private final ReportRepository reportRepository;

	//	private final ReportCategoryRepository reportCategoryRepository;

	//	private final ReportService reportService;

	@Autowired
	public ReportController(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReportResource> list(
			@HeaderParam("Accept") String acceptHeader,
			@Context UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		// These will be detached entities,right? Perhaps this is not what I want!!!
		// IF THIS DOES NOT WORK, I WILL NEED TO DO EVERYTHING IN A @Transactional SERVICE METHOD!?
		List<Report> reports = reportRepository.findByActiveTrue();

		logger.info("reports.size() = {}", reports.size());

		List<ReportResource> reportResources = new ArrayList<>();
		for (Report report : reports) {
			logger.info("report = {}", report);
			reportResources.add(new ReportResource(uriInfo, report));
		}
		return reportResources;
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportResource getOne(
			@PathParam("id") UUID id,
			@HeaderParam("Accept") String acceptHeader,
			@Context UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.info("id = {}", id);

		// These will be detached entities,right? Perhaps this is not what I want!!!
		// IF THIS DOES NOT WORK, I WILL NEED TO DO EVERYTHING IN A @Transactional SERVICE METHOD!?
		// OR CAN I ANNOTATE THIS METHOD WITH @	Transactional ?????????????????????????????????????
		Report report = reportRepository.findOne(id);
		logger.info("report = {}", report);

		logger.info("About to call report.getReportCategory(). Will this trigger a lazy init exception? If not, perhaps after I try to access its attributes?");
		ReportCategory reportCategory = report.getReportCategory();
		logger.info("report.getReportCategory() ={}", reportCategory);
		logger.info("About to call reportCategory.getReportCategoryId(). Will this trigger a lazy init exception?");
		logger.info("reportCategory.getReportCategoryId() = {}", reportCategory.getReportCategoryId());
		logger.info("About to call reportCategory.getReportCategoryId(). Will this trigger a lazy init exception?");
		logger.info("reportCategory.getReportCategoryId() = {}", reportCategory.getDescription());

		ReportResource reportResource = new ReportResource(uriInfo, report);
		logger.info("reportResource = {}", reportResource);

		return reportResource;
	}

}
