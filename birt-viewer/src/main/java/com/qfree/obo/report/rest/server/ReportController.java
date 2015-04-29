package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.REPORTS_PATH)
public class ReportController extends AbstractBaseController {

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
	public List<ReportResource> getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		//		logger.info("expand.size() = {}", expand.size());
		//		for (String e : expand) {
		//			logger.info("  expand: = {}", e);
		//		}

		List<Report> reports = reportRepository.findByActiveTrue();
		List<ReportResource> reportResources = new ArrayList<>();
		for (Report report : reports) {
			reportResources.add(new ReportResource(report, uriInfo, expand));
		}
		return reportResources;
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		String expandParam = ResourcePath.forEntity(Report.class).getExpandParam();
		if (!expand.contains(expandParam)) {
			expand.add(expandParam);	// Always expand primary resource.
		}

		//		logger.info("expand.size() = {}", expand.size());
		//		for (String e : expand) {
		//			logger.info("  expand: = {}", e);
		//		}

		Report report = reportRepository.findOne(id);
		ReportCategory reportCategory = report.getReportCategory();
		ReportResource reportResource = new ReportResource(report, uriInfo, expand);

		return reportResource;
	}

}
