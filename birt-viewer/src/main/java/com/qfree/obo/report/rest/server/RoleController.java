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
import com.qfree.obo.report.dto.JobCollectionResource;
import com.qfree.obo.report.dto.ReportCollectionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.RoleCollectionResource;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.dto.SubscriptionCollectionResource;
import com.qfree.obo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.obo.report.exceptions.ResourceFilterParseException;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.service.RoleService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

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
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET http://localhost:8080/rest/roles?expand=roles
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RoleCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Role> roles = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Role.class, showAll)) {
			roles = roleRepository.findByActiveTrue();
		} else {
			roles = roleRepository.findAll();
		}
		return new RoleCollectionResource(roles, Role.class, uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"username":"bozoc","fullName":"Bozo the clown","unencodedPassword":"iambozo","loginRole":true,\
	 *   "enabled":true,"email_address":"bozo@circus.net","timeZoneId":"CET"}' http://localhost:8080/rest/roles
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
		addToExpandList(expand, Role.class); // Force primary resource to be "expanded"
		//	}
		RoleResource resource = new RoleResource(role, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/roles/b85fd129-17d9-40e7-ac11-7541040f8627
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RoleResource getByIdOrUsername(
			@PathParam("id") final String idOrUsername,
			//@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.info("idOrUsername = {}", idOrUsername);

		UUID id = null;
		String username = null;
		try {
			id = UUID.fromString(idOrUsername);
		} catch (IllegalArgumentException e) {
			/*
			 * idOrUsername does not represent a UUID, so we interpret it as a 
			 * username.
			 */
			username = idOrUsername;
		}

		Role role = null;
		if (id != null) {
			role = roleRepository.findOne(id);
			RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		} else {
			role = roleRepository.findByUsername(username);
			RestUtils.ifNullThen404(role, Role.class, "username", username);
		}

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Role.class);
		}
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
		return new ReportCollectionResource(reports, Report.class, uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   
	 *   $ curl -X PUT -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"username":"bozoc","fullName":Bozo the clown","loginRole":true,\
	 *   "email_address":"dumbo@circus.net","timeZoneId":"UTC"}' \
	 *   http://localhost:8080/rest/roles/f9a94054-c62b-464c-874c-a61d18530c87
	 * 
	 * This example updates the password that is used to *locally* authenticate
	 * the role:
	 * 
	 *   $ curl -X PUT -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"username":"bozoc","fullName":"Bozo the clown","loginRole":true,\
	 *   "unencodedPassword":"iambozo2","email_address":"dumbo@circus.net","timeZoneId":"UTC"}' \
	 *   http://localhost:8080/rest/roles/f9a94054-c62b-464c-874c-a61d18530c87
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
		 * Treat attributes of roleResource that are effectively required,
		 * meaning that the corresponding fields of Role cannot be null. These 
		 * attributes can be omitted in the PUT data, but in that case they are 
		 * then set here to the CURRENT values from the role entity. 
		 */
		if (roleResource.getActive() == null) {
			roleResource.setActive(role.getActive());
		}
		if (roleResource.getEnabled() == null) {
			roleResource.setEnabled(role.getEnabled());
		}
		/*
		 * If no value for "unencodedPassword" is specified in roleResource, we
		 * take this to mean that the current value for "encodedPassword" for
		 * corresponding Role should be kept (not cleared). There is no way for
		 * the caller of this endpoint to set "unencodedPassword" for 
		 * roleResource to the current value of the password because this is 
		 * unknown (it is not persisted and it cannot be recovered from its
		 * hashed value, "encodedPassword").
		 */
		if (roleResource.getUnencodedPassword() == null || roleResource.getUnencodedPassword().isEmpty()) {
			roleResource.setEncodedPassword(role.getEncodedPassword());
		} else {
			/*
			 * For this case, the encoded password for the role will be set in
			 * call to the "saveExistingFromResource" service method below.
			 * Nevertheless, to avoid an unlikely security breach, we clear out
			 * the encode password value here in case the caller is trying to 
			 * set it directly.
			 */
			roleResource.setEncodedPassword(null);
		}

		/*
		 * Save updated entity.
		 */
		role = roleService.saveExistingFromResource(roleResource);
		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the Job entities associated with a single Role that is 
	 * specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/roles/b85fd129-17d9-40e7-ac11-7541040f8627/jobs
	 * 
	 * Note:  This endpoint supports pagination.
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}" + ResourcePath.JOBS_PATH)
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public JobCollectionResource getJobsByRoleId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@QueryParam(ResourcePath.FILTER_QP_NAME) final List<String> filter,
			@QueryParam(ResourcePath.PAGE_OFFSET_QP_NAME) final List<String> pageOffset,
			@QueryParam(ResourcePath.PAGE_LIMIT_QP_NAME) final List<String> pageLimit,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		queryParams.put(ResourcePath.FILTER_QP_KEY, filter);
		RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		try {
			return new JobCollectionResource(role, uriInfo, queryParams, apiVersion);
		} catch (ResourceFilterExecutionException | ResourceFilterParseException e) {
			throw new RestApiException(RestError.FORBIDDEN_RESOURCE_FILTER_PROBLEM, e.getMessage(), e);
		}
	}

	/*
	 * Return the Subscription entities associated with a single Role that is 
	 * specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/roles/b85fd129-17d9-40e7-ac11-7541040f8627/subscriptions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}" + ResourcePath.SUBSCRIPTIONS_PATH)
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionCollectionResource getSubscriptionsByRoleId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@QueryParam(ResourcePath.PAGE_OFFSET_QP_NAME) final List<String> pageOffset,
			@QueryParam(ResourcePath.PAGE_LIMIT_QP_NAME) final List<String> pageLimit,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Role role = roleRepository.findOne(id);
		RestUtils.ifNullThen404(role, Role.class, "roleId", id.toString());
		return new SubscriptionCollectionResource(role, uriInfo, queryParams, apiVersion);
	}

}
