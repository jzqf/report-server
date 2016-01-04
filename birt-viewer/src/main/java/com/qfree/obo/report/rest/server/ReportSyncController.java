package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.ReportSyncService;
import com.qfree.obo.report.util.ReportUtils;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path("reportSyncs")
public class ReportSyncController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportSyncController.class);

	private final ReportRepository reportRepository;

	private final ReportSyncService reportSyncService;

	//	private final ReportService reportService;

	@Autowired
	public ReportSyncController(
			ReportRepository reportRepository, ReportSyncService reportSyncService) {
		this.reportRepository = reportRepository;
		this.reportSyncService = reportSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: text/plain;v=1" http://localhost:8080/rest/reportSyncs/availablePermits
	 * 
	 */
	@Path("/availablePermits")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_FILESYNCING + "')")
	public int getAvailablePermits(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		return ReportUtils.reportSyncSemaphore.availablePermits();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -X POST http://localhost:8080/rest/reportSyncs
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_FILESYNCING + "')")
	public ReportSyncResource syncReportsWithFileSystem(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		return reportSyncService.syncReportsWithFileSystem(servletContext, uriInfo, queryParams, apiVersion);
	}
}
