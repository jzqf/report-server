package com.qfree.bo.report.rest.server;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.ReportCategoryRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.domain.ReportCategory;
import com.qfree.bo.report.dto.ReportCategoryCollectionResource;
import com.qfree.bo.report.dto.ReportCategoryResource;
import com.qfree.bo.report.dto.ResourcePath;
import com.qfree.bo.report.service.ReportCategoryService;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.REPORTCATEGORIES_PATH)
public class ReportCategoryController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryController.class);

	private final ReportCategoryRepository reportCategoryRepository;
	private final ReportCategoryService reportCategoryService;

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
	 *   http://localhost:8080/rest/reportCategories
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_CATEGORIES + "')")
	public ReportCategoryCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<ReportCategory> reportCategories = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(ReportCategory.class, showAll)) {
			reportCategories = reportCategoryRepository.findByActiveTrue();
		} else {
			reportCategories = reportCategoryRepository.findAll();
		}
		return new ReportCategoryCollectionResource(reportCategories, ReportCategory.class,
				uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"abbreviation":"RCABBREV","description":"ReportCategory description",\
	 *   "active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *   http://localhost:8080/rest/reportCategories
	 * 
	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	 * ReportCategory to create is given:
	 * 
	 *	curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *	'{"reportCategoryId":"71b3e8ae-bba8-45b7-a85f-12546bcc95b2",\
	 *	'"abbreviation":"RCABBREV","description":"ReportCategory description",\
	 *	'"active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *	http://localhost:8080/rest/reportCategories
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_CATEGORIES + "')")
	public Response create(
			ReportCategoryResource reportCategoryResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ReportCategory reportCategory = reportCategoryService.saveNewFromResource(reportCategoryResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, ReportCategory.class);  // Force primary resource to be "expanded"
		//	}
		ReportCategoryResource resource = new ReportCategoryResource(reportCategory, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportCategories/7a482694-51d2-42d0-b0e2-19dd13bbbc64
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_CATEGORIES + "')")
	public ReportCategoryResource getById(
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
			addToExpandList(expand, ReportCategory.class);
		}
		ReportCategory reportCategory = reportCategoryRepository.findOne(id);
		RestUtils.ifNullThen404(reportCategory, ReportCategory.class, "reportCategoryId", id.toString());
		ReportCategoryResource reportCategoryResource =
				new ReportCategoryResource(reportCategory, uriInfo, queryParams, apiVersion);
		return reportCategoryResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"abbreviation":"QFREE-MOD","description":"Q-Free internal (modified)","active":false}' \
	 *   http://localhost:8080/rest/reportCategories/bb2bc482-c19a-4c19-a087-e68ffc62b5a0
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_CATEGORIES + "')")
	public Response updateById(
			ReportCategoryResource reportCategoryResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve ReportCategory entity to be updated.
		 */
		ReportCategory reportCategory = reportCategoryRepository.findOne(id);
		RestUtils.ifNullThen404(reportCategory, ReportCategory.class, "reportCategoryId", id.toString());
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportCategoryResource.setReportCategoryId(reportCategory.getReportCategoryId());
		reportCategoryResource.setCreatedOn(reportCategory.getCreatedOn());
		/*
		 * Save updated entity.
		 */
		reportCategory = reportCategoryService.saveExistingFromResource(reportCategoryResource);
		return Response.status(Response.Status.OK).build();
	}

}
