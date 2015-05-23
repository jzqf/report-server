package com.qfree.obo.report.rest.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportParameterCollectionResource;
import com.qfree.obo.report.dto.ReportVersionCollectionResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ReportVersionService;

@Component
@Path(ResourcePath.REPORTVERSIONS_PATH)
public class ReportVersionController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionController.class);

	private final ReportVersionRepository reportVersionRepository;

	private final ReportVersionService reportVersionService;

	@Autowired
	public ReportVersionController(ReportVersionRepository reportVersionRepository,
			ReportVersionService reportVersionService) {
		this.reportVersionRepository = reportVersionRepository;
		this.reportVersionService = reportVersionService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET http://localhost:8080/rest/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportParameters().
	 */
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//	public List<ReportVersionResource> getList(
	public ReportVersionCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<ReportVersion> reportVersions = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS) {
			reportVersions = reportVersionRepository.findByActiveTrue();
		} else {
			reportVersions = reportVersionRepository.findAll();
		}
		List<ReportVersionResource> reportVersionResources = new ArrayList<>(reportVersions.size());
		for (ReportVersion reportVersion : reportVersions) {
			reportVersionResources.add(new ReportVersionResource(reportVersion, uriInfo, expand, apiVersion));
		}
		//		return reportVersionResources;
		return new ReportVersionCollectionResource(reportVersionResources, ReportVersion.class, uriInfo, expand,
				apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X POST -d \
	 *   '{"report":{"reportId":"702d5daa-e23d-4f00-b32b-67b44c06d8f6"},\
	 *   "rptdesign":"Not a valid rptdesign, but this cannot be null",\
	 *   "versionName":"3.9","versionCode":17,"active":true}' \
	 *   http://localhost:8080/rest/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportVersions(), as well
	 * as potentially other lazy-evaluated attributes..
	 */
	@Transactional
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			ReportVersionResource reportVersionResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("reportVersionResource = {}", reportVersionResource);

		ReportVersion reportVersion = reportVersionService.saveNewFromResource(reportVersionResource);
		logger.debug("reportVersion = {}", reportVersion);
		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, ReportVersion.class);
		}
		ReportVersionResource resource = new ReportVersionResource(reportVersion, uriInfo, expand, apiVersion);
		logger.debug("resource = {}", resource);
		return created(resource);
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createByUpload(
			@FormDataParam("first_name") String firstName,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) throws IOException {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		StringBuilder rptdesignStringBuilder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedInputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				rptdesignStringBuilder.append(line);
			}
		}
		logger.info("firstName = {}", firstName);
		logger.info("rptdesignStringBuilder.toString() = {}", rptdesignStringBuilder.toString());

		//logger.info("rptdesignFile = {}");

		ReportVersionResource uploadedReportVersionResource = new ReportVersionResource();	// REPLACE WITH REAL CODE.  THIS IS ONLY TO AVOID AN ERROR
		logger.debug("uploadedReportVersionResource = {}", uploadedReportVersionResource);

		ReportVersion reportVersion = reportVersionService.saveNewFromResource(uploadedReportVersionResource);
		logger.debug("reportVersion = {}", reportVersion);
		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, ReportVersion.class);
		}
		ReportVersionResource resource = new ReportVersionResource(reportVersion, uriInfo, expand, apiVersion);
		logger.debug("resource = {}", resource);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d?expand=reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportParameters(), as well
	 * as potentially other lazy-evaluated attributes..
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportVersionResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("expand = {}", expand);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, ReportVersion.class);
		}
		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		ReportVersionResource reportVersionResource = new ReportVersionResource(reportVersion,
				uriInfo, expand, apiVersion);
		return reportVersionResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X PUT -d \
	 *   '{"report":{"reportId":"702d5daa-e23d-4f00-b32b-67b44c06d8f6"},\
	 *   "rptdesign":"Not a valid rptdesign, but this cannot be null",\
	 *   "versionName":"1.1.1","versionCode":2,"active":false}' \
	 *   http://localhost:8080/rest/reportVersions/bbd23109-e1e9-404e-913d-32150d8fd92f
	 *   
	 * This updates the ReportVersion with UUID 
	 * bbd23109-e1e9-404e-913d-32150d8fd92f with the following changes:
	 * 
	 * report:			Do not change - keep same parent report
	 * rptdesign:							-> "Not a valid rptdesign, but this cannot be null"
	 * versionName:		"1.1"				-> "1.1.1"
	 * versionCode:		2					-> 2
	 * active:			true				-> false
	 * 
	 * @Transactional may be needed here to avoid future exceptions of the type
	 * org.hibernate.LazyInitializationException being thrown when evaluating 
	 * lazy-evaluated attributes. It is not actually needed as this comment is
	 * being written.
	 */
	@Transactional
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			ReportVersionResource reportVersionResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("apiVersion = {}", apiVersion);
		logger.debug("reportVersionResource = {}", reportVersionResource);

		/*
		 * Retrieve ReportVersion entity to be updated.
		 */
		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		logger.debug("reportVersion (to be updated) = {}", reportVersion);
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportVersionResource.setReportVersionId(reportVersion.getReportVersionId());
		reportVersionResource.setCreatedOn(reportVersion.getCreatedOn());
		logger.debug("reportVersionResource (adjusted) = {}", reportVersionResource);
		/*
		 * Save updated entity.
		 */
		reportVersion = reportVersionService.saveExistingFromResource(reportVersionResource);
		logger.debug("reportVersion (after saveOrUpdateFromResource) = {}", reportVersion);
		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the ReportParameter's associated with a single ReportVersion that
	 * is specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d/reportParameters?expand=reportParameters
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.get...()?
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.REPORTPARAMETERS_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportParameterCollectionResource getReportParametersByReportVersionId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> extraQueryParams = new HashMap<>();
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("expand = {}", expand);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Report.class);
		}
		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		return new ReportParameterCollectionResource(reportVersion, uriInfo, expand, extraQueryParams, apiVersion);
	}
}
