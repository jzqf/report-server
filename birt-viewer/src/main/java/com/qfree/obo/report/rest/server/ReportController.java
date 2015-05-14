package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ReportService;

@Component
@Path(ResourcePath.REPORTS_PATH)
public class ReportController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	private final ReportRepository reportRepository;

	private final ReportService reportService;

	@Autowired
	public ReportController(ReportRepository reportRepository, ReportService reportService) {
		this.reportRepository = reportRepository;
		this.reportService = reportService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8081/report-server/rest/reports
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReportResource> getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Report> reports = reportRepository.findByActiveTrue();
		List<ReportResource> reportResources = new ArrayList<>(reports.size());
		for (Report report : reports) {
			reportResources.add(new ReportResource(report, uriInfo, expand, apiVersion));
		}
		return reportResources;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X POST -d \
	 *   '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 *   "name":"Report name (created by POST)","number":666,"active":true}' \
	 *   http://localhost:8080/rest/reports
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			ReportResource reportResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("reportResource = {}", reportResource);

		Report report = reportService.saveNewFromResource(reportResource);
		logger.debug("report = {}", report);
		addToExpandList(expand, Report.class);  // Force primary resource to be "expanded", if not already requested
		ReportResource resource = new ReportResource(report, uriInfo, expand, apiVersion);
		logger.debug("resource = {}", resource);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8081/report-server/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public ReportResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("expand = {}", expand);

		addToExpandList(expand, Report.class);	// Force primary resource to be "expanded"
		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());
		logger.debug("report = {}", report);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());
		ReportResource reportResource = new ReportResource(report, uriInfo, expand, apiVersion);
		logger.debug("reportResource = {}", reportResource);
		return reportResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X PUT -d \
	 *   '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},'\
	 *   '"name":"Report #04 (modified by PUT)","number":1400,"active":false}' \
	 *   http://localhost:8080/rest/reports/702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 *   
	 * This updates the report with UUID 702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 * with the following changes:
	 * 
	 * report category:	"Q-Free internal"	->	"Traffic"
	 * name:			"Report name #04"	-> "Report name #04 (modified by PUT)"
	 * number:			400					-> 1400
	 * active:			true				-> false
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			ReportResource reportResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("apiVersion = {}", apiVersion);
		logger.debug("reportResource = {}", reportResource);

		/*
		 * Retrieve Report entity to be updated.
		 */
		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());
		logger.debug("report (to be updated) = {}", report);
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportResource.setReportId(report.getReportId());
		reportResource.setCreatedOn(report.getCreatedOn());
		logger.debug("reportResource (adjusted) = {}", reportResource);
		/*
		 * Save updated entity.
		 */
		report = reportService.saveExistingFromResource(reportResource);
		logger.debug("report (after saveOrUpdateFromResource) = {}", report);
		return Response.status(Response.Status.OK).build();
	}

}
