package com.qfree.obo.report.rest.server;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.UuidCustomType;
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
	/* 
	 * This is just for a transition period until we have better/different
	 * role management implemented.
	 */
	public static final boolean returnAllReportsForEachRole = true;

	private final RoleRepository roleRepository;
	private final RoleService roleService;
	private final ReportRepository reportRepository;

	@Autowired
	public RoleController(
			RoleRepository roleRepository,
			RoleService roleService,
			ReportRepository reportRepository) {
		this.roleRepository = roleRepository;
		this.roleService = roleService;
		this.reportRepository = reportRepository;
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
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		// List<Role> roles = roleRepository.findByActiveTrue();
		List<Role> roles = roleRepository.findAll();
		List<RoleResource> roleResources = new ArrayList<>(roles.size());
		for (Role role : roles) {
			roleResources.add(new RoleResource(role, uriInfo, queryParams, apiVersion));
		}
		//		return roleResources;
		return new RoleCollectionResource(roleResources, Role.class, uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"username":"bozoc,"fullName":"Bozo the clown","encodedPassword":"asdf=","loginRole":true}' \
	 *   http://localhost:8080/rest/roles
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			RoleResource roleResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Role role = roleService.saveNewFromResource(roleResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Role.class);  // Force primary resource to be "expanded"
		//	}
		RoleResource resource = new RoleResource(role, uriInfo, queryParams, apiVersion);
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
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Role.class);
		}
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		RoleResource roleResource = new RoleResource(role, uriInfo, queryParams, apiVersion);
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
	 * to. This does NOT include transitive access rights that are associated 
	 * with ancestor (parents, grandparents, ...) roles. Each Report will have 
	 * zero or more ReportVersions.
	 * 
	 * @param id
	 * @param acceptHeader
	 * @param expand
	 * @param showAll
	 * @param uriInfo
	 * @return
	 */
	@Path("/{id}/reports")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public ReportCollectionResource getReportsForRole(
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
			addToExpandList(expand, Role.class);
		}
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());

		List<Report> reports = new ArrayList<>(0);
		if (returnAllReportsForEachRole == true) {
			if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Report.class, showAll)) {
				reports = reportRepository.findByActiveTrue();
			} else {
				reports = reportRepository.findAll();
			}
		} else {
			/*
			 * The H2 database does not support recursive CTE expressions, so it is 
			 * necessary to run different code if the database is not PostgreSQL.
			 * This only affects integration tests, because only PostreSQL is used
			 * in production. 
			 */
			if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {

				/*
				 * roleRepository.findReportsByRoleIdRecursive(...) is a Spring Data
				 * JPA repository method that uses a *native* SQL query. It appears 
				 * that Hibernate has a problem treating the UUID data type for this
				 * environment, whether objects of that type appear in the SELECT
				 * result set or if an object of that type is used as a named 
				 * parameter in the query. To get around this limitation, this 
				 * repository method is specially written to deal with String
				 * representations of the UUID data type, instead of the UUID type
				 * itself. The chosen implementation:
				 * 
				 *   1. Takes as its first parameter, the String representation of
				 *   	the Role for which to locate Report's.
				 *   
				 *   2.	Returns a list of Report id's for the Report's to be 
				 *   	returned, but this id's are represented as String objects, 
				 *   	not as UUID objects.
				 * 
				 * As a result of this treatment, there are two extra steps required
				 * to create the list of Report's to return:
				 * 
				 *   1. Create a list of UUID objects from the list of UUID Strings.
				 *   
				 *   2.	Use Spring Data's provided "findAll" repository method to 
				 *   	return a list of Report's given the list of UUID's.
				 */
				List<String> stringUuids = null;
				if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Report.class, showAll)) {
					stringUuids = roleRepository.findReportsByRoleIdRecursive(role.getRoleId().toString(), true);
				} else {
					stringUuids = roleRepository.findReportsByRoleIdRecursive(role.getRoleId().toString(), false);
				}
				if (stringUuids != null && stringUuids.size() > 0) {
					logger.debug("Number of stringUuids (recursive) = {}", stringUuids.size());
					/*
					 * Create List of UUID's of the Report's that the endpoint will return.
					 */
					List<UUID> uuids = new ArrayList<>(stringUuids.size());
					for (String stringUuid : stringUuids) {
						logger.debug("stringUuid (recursive) = {}", stringUuid);
						uuids.add(UUID.fromString(stringUuid));
					}
					reports = reportRepository.findAll(uuids);
				}

			} else {
				/*
				 * This code returns only those Reports associated with RoleReport
				 * entities that are *directly* linked to the Role role. This does *not*
				 * include Reports associated with RoleReport entities that are linked 
				 * to ancestors (parents, gransparents, ...) of Role role.
				 */
				if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Report.class, showAll)) {
					reports = roleRepository.findActiveReportsByRoleId(role.getRoleId());
				} else {
					reports = roleRepository.findReportsByRoleId(role.getRoleId());
				}
			}
		}

		logger.debug("Number of reports = {}", reports.size());
		for (Report report : reports) {
			logger.debug("report = {}", report.getName());
		}

		List<ReportResource> reportResources = new ArrayList<>(reports.size());
		for (Report report : reports) {
			reportResources.add(new ReportResource(report, uriInfo, queryParams, apiVersion));
		}
		//		return reportResources;
		return new ReportCollectionResource(reportResources, Report.class, uriInfo, queryParams, apiVersion);
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

		/*
		 * Retrieve Role entity to be updated.
		 */
		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		roleResource.setRoleId(role.getRoleId());
		roleResource.setCreatedOn(role.getCreatedOn());
		/*
		 * Save updated entity.
		 */
		role = roleService.saveExistingFromResource(roleResource);
		return Response.status(Response.Status.OK).build();
	}

}