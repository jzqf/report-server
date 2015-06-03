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

import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.dto.ReportCollectionResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RoleCollectionResource;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.RoleService;

@Component
@Path(ResourcePath.ROLES_PATH)
public class RoleController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

	private final RoleRepository roleRepository;
	private final RoleService roleService;

	@Autowired
	public RoleController(
			RoleRepository roleRepository,
			RoleService roleService) {
		this.roleRepository = roleRepository;
		this.roleService = roleService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/roles
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//	public List<RoleResource> getList(
	//	public CollectionResource<RoleResource> getList(
	public RoleCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		// List<Role> roles = roleRepository.findByActiveTrue();
		List<Role> roles = roleRepository.findAll();
		List<RoleResource> roleResources = new ArrayList<>(roles.size());
		for (Role role : roles) {
			roleResources.add(new RoleResource(role, uriInfo, expand, apiVersion));
		}
		//		return roleResources;
		return new RoleCollectionResource(roleResources, Role.class, uriInfo, expand, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"username":"Bozo","fullName":"Bozo the clown","encodedPassword":"asdf=","loginRole":true}' \
	 *   http://localhost:8080/rest/roles
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			RoleResource roleResource,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Role role = roleService.saveNewFromResource(roleResource);
		List<String> expand = newExpandList(Role.class);	// Force primary resource to be "expanded"
		RoleResource resource = new RoleResource(role, uriInfo, expand, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/roles/b85fd129-17d9-40e7-ac11-7541040f8627
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RoleResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Role.class);
		}
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		RoleResource roleResource = new RoleResource(role, uriInfo, expand, apiVersion);
		return roleResource;
	}

	/*
	 * This endpoint can be tested with (note that "&" is escaped here as "\&"
	 * so it will not be treated specially by the bash shell):
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/roles/b85fd129-17d9-40e7-ac11-7541040f8627/reports\
	 *   ?expand=reports\&expand=reportVersions\&expand=rptdesign
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	/**
	 * Return all Report instances that the Role with a specified id has access
	 * to. Each Report will have zero or more ReportVersions 
	 * 
	 * @param id
	 * @param acceptHeader
	 * @param expand
	 * @param uriInfo
	 * @return
	 */
	@Transactional
	@Path("/{id}/reports")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportCollectionResource getReportsForRole(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Role.class);
		}
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());

		/*
		 * TODO Add a query parameter to disable filtering on *active* for Report's?
		 * 		How about ...&nofilter=active or ... What if we want to see only
		 * 		active Report's but unfiltered ReportVersion's (active or not)?
		 */
		List<Report> reports = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS) {
			reports = roleRepository.findActiveReportsByRoleId(role.getRoleId());
		} else {
			reports = roleRepository.findReportsByRoleId(role.getRoleId());
		}
		List<ReportResource> reportResources = new ArrayList<>(reports.size());
		for (Report report : reports) {
			reportResources.add(new ReportResource(report, uriInfo, expand, apiVersion));
		}
		//		return reportResources;
		return new ReportCollectionResource(reportResources, Report.class, uriInfo, expand, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"username":"baaa (modified)","fullName":"Mr. baaa","encodedPassword":"qwerty=","loginRole":true}' \
	 *   http://localhost:8080/rest/roles/0db97c2a-fb78-464a-a0e7-8d25f6003c14
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			RoleResource roleResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		logger.debug("apiVersion = {}", apiVersion);
		logger.debug("roleResource = {}", roleResource);

		/*
		 * Retrieve Role entity to be updated.
		 */
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		logger.debug("role (to be updated) = {}", role);
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		roleResource.setRoleId(role.getRoleId());
		roleResource.setCreatedOn(role.getCreatedOn());
		logger.debug("roleResource (adjusted) = {}", roleResource);
		/*
		 * Save updated entity.
		 */
		role = roleService.saveExistingFromResource(roleResource);
		logger.debug("role (after saveOrUpdateFromResource) = {}", role);
		return Response.status(Response.Status.OK).build();
	}

}
