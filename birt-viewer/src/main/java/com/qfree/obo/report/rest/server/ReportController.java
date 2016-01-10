package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.dto.ReportCollectionResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.dto.ReportVersionCollectionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.ReportService;
import com.qfree.obo.report.service.ReportSyncService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.REPORTS_PATH)
public class ReportController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	private final ReportRepository reportRepository;
	private final ReportService reportService;
	private final ReportSyncService reportSyncService;

	@Autowired
	public ReportController(
			ReportRepository reportRepository,
			ReportService reportService,
			ReportSyncService reportSyncService) {
		this.reportRepository = reportRepository;
		this.reportService = reportService;
		this.reportSyncService = reportSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/reports?expand=reports
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_USE_RESTAPI + "') and "
			+ "hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			//@Context SecurityContext sc, // javax.ws.rs.core.SecurityContext
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		//	logger.info("sc = {}", sc);
		//	if (sc != null) {
		//		logger.info("sc.getUserPrincipal() = {}", sc.getUserPrincipal());
		//		if (sc.getUserPrincipal() != null) {
		//			logger.info("sc.getUserPrincipal().getName() = {}", sc.getUserPrincipal().getName());
		//		}
		//	}

		List<Report> reports = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Report.class, showAll)) {
			reports = reportRepository.findByActiveTrue();
		} else {
			reports = reportRepository.findAll();
		}
		return new ReportCollectionResource(reports, Report.class, uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 *   "name":"Report name (created by POST)","number":666,"active":true}' \
	 *   http://localhost:8080/rest/reports
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_USE_RESTAPI + "') and ("
			+ "hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')" +
			" or hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "'))")
	public Response create(
			ReportResource reportResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Report report = reportService.saveNewFromResource(reportResource);
		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Report.class);// Force primary resource to be "expanded"
		// }
		ReportResource resource = new ReportResource(report, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Report.class);
		}
		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());
		ReportResource reportResource = new ReportResource(report, uriInfo, queryParams, apiVersion);
		return reportResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 *   "name":"Test Report #04 (modified by PUT)","number":1400,"sortOrder":1400,"active":false}' \
	 *   http://localhost:8080/rest/reports/702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 *   
	 * This updates the report with UUID 702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 * with the following changes:
	 * 
	 * report category:	"Q-Free internal"	-> "Traffic"
	 * name:			"Test Report #04"	-> "Test Report #04 (modified by PUT)"
	 * number:			400					-> 1400
	 * sortOrder:		400					-> 1400
	 * active:			true				-> false
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_USE_RESTAPI + "') and "
			+ "hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public Response updateById(
			ReportResource reportResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve Report entity to be updated.
		 */
		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());

		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportResource.setReportId(report.getReportId());
		reportResource.setCreatedOn(report.getCreatedOn());

		/*
		 * If the "sortOrder" was not specified in 
		 * reportResource, we use the current value stored in report.
		 */
		if (reportResource.getSortOrder() == null) {
			reportResource.setSortOrder(report.getSortOrder());
		}

		/*
		 * Save updated entity.
		 */
		report = reportService.saveExistingFromResource(reportResource);

		/*
		 * Synchronize "rptdesign" files in the report server's file system with
		 * the "rptdesign" definitions stored in the report server's database.
		 * This may create or delete files if the report's "active" attribute 
		 * has been modified.
		 */
		ReportSyncResource reportSyncResource = reportSyncService.syncReportsWithFileSystem(servletContext, uriInfo,
				queryParams, apiVersion);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the ReportVersions associated with a single Report that is 
	 * specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.REPORTVERSIONS_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportVersionCollectionResource getReportVersionsByReportId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());
		return new ReportVersionCollectionResource(report, uriInfo, queryParams, apiVersion);
	}
}
