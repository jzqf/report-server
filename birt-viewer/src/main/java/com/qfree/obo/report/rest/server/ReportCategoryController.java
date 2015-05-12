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

import com.qfree.obo.report.db.ReportCategoryRepository;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.dto.ReportCategoryResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ReportCategoryService;

@Component
@Path(ResourcePath.REPORTCATEGORIES_PATH)
public class ReportCategoryController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryController.class);

	private final ReportCategoryRepository reportCategoryRepository;
	private final ReportCategoryService reportCategoryService;

	//	private final ReportCategoryService reportCategoryService;

	@Autowired
	public ReportCategoryController(
			ReportCategoryRepository reportCategoryRepository,
			ReportCategoryService reportCategoryService) {
		this.reportCategoryRepository = reportCategoryRepository;
		this.reportCategoryService = reportCategoryService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8081/report-server/rest/reportCategories
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReportCategoryResource> getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<ReportCategory> reportCategories = reportCategoryRepository.findByActiveTrue();
		List<ReportCategoryResource> reportCategoryResources = new ArrayList<>(reportCategories.size());
		for (ReportCategory reportCategory : reportCategories) {
			reportCategoryResources.add(new ReportCategoryResource(reportCategory, uriInfo, expand, apiVersion));
		}
		return reportCategoryResources;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X POST -d \
	 *   '{"abbreviation":"RCABBREV","description":"ReportCategory description",\
	 *   "active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *   http://localhost:8080/rest/reportCategories
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			ReportCategoryResource reportCategoryResource,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ReportCategory reportCategory = reportCategoryService.saveNewFromResource(reportCategoryResource);
		List<String> expand = newExpandList(ReportCategory.class);	// Force primary resource to be "expanded"
		ReportCategoryResource resource = new ReportCategoryResource(reportCategory, uriInfo, expand, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8081/report-server/rest/reportCategories/7a482694-51d2-42d0-b0e2-19dd13bbbc64
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportCategoryResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		addToExpandList(expand, ReportCategory.class);	// Force primary resource to be "expanded"
		ReportCategory reportCategory = reportCategoryRepository.findOne(id);
		RestUtils.ifNullThen404(reportCategory, ReportCategory.class, "reportCategoryId", id.toString());
		ReportCategoryResource reportCategoryResource =
				new ReportCategoryResource(reportCategory, uriInfo, expand, apiVersion);
		return reportCategoryResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X PUT -d \
	 *   '{"abbreviation":"QFREE-MOD","description":"Q-Free internal (modified)","active":false}' \
	 *   http://localhost:8080/rest/reportCategories/bb2bc482-c19a-4c19-a087-e68ffc62b5a0
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			ReportCategoryResource reportCategoryResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("apiVersion = {}", apiVersion);
		logger.debug("reportCategoryResource = {}", reportCategoryResource);

		/*
		 * Retrieve ReportCategory entity to be updated.
		 */
		ReportCategory reportCategory = reportCategoryRepository.findOne(id);
		RestUtils.ifNullThen404(reportCategory, ReportCategory.class, "reportCategoryId", id.toString());
		logger.debug("reportCategory (to be updated) = {}", reportCategory);
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportCategoryResource.setReportCategoryId(reportCategory.getReportCategoryId());
		reportCategoryResource.setCreatedOn(reportCategory.getCreatedOn());
		logger.debug("reportCategoryResource (adjusted) = {}", reportCategoryResource);
		/*
		 * Save updated entity.
		 */
		reportCategory = reportCategoryService.saveExistingFromResource(reportCategoryResource);
		logger.debug("reportCategory (after saveOrUpdateFromResource) = {}", reportCategory);
		return Response.status(Response.Status.OK).build();
	}

}
