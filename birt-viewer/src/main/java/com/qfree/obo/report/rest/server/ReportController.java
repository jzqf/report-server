package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
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
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.UuidCustomType;
import com.qfree.obo.report.dto.ReportCollectionResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.dto.ReportVersionCollectionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RoleCollectionResource;
import com.qfree.obo.report.service.AuthorityService;
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
	private final RoleRepository roleRepository;
	private final AuthorityService authorityService;

	@Autowired
	public ReportController(ReportRepository reportRepository, ReportService reportService,
			ReportSyncService reportSyncService, RoleRepository roleRepository, AuthorityService authorityService) {
		this.reportRepository = reportRepository;
		this.reportService = reportService;
		this.reportSyncService = reportSyncService;
		this.roleRepository = roleRepository;
		this.authorityService = authorityService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 * $ mvn clean spring-boot:run 
	 * $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 * http://localhost:8080/rest/reports?expand=reports
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportCollectionResource getList(@HeaderParam("Accept") final String acceptHeader,
			// @Context SecurityContext sc, // javax.ws.rs.core.SecurityContext
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll, @Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		// logger.info("sc = {}", sc);
		// if (sc != null) {
		// logger.info("sc.getUserPrincipal() = {}", sc.getUserPrincipal());
		// if (sc.getUserPrincipal() != null) {
		// logger.info("sc.getUserPrincipal().getName() = {}",
		// sc.getUserPrincipal().getName());
		// }
		// }

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
	 * $ mvn clean spring-boot:run 
	 * $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	 * -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 * '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 * "name":"Report name (created by POST)","number":666,"active":true}' \
	 * http://localhost:8080/rest/reports
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')" + " or hasAuthority('"
			+ Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response create(ReportResource reportResource, @HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll, @Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Report report = reportService.saveNewFromResource(reportResource);
		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Report.class);// Force primary resource to be
												// "expanded"
		// }
		ReportResource resource = new ReportResource(report, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 * $ mvn clean spring-boot:run 
	 * $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 * http://localhost:8080/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportResource getById(@PathParam("id") final UUID id, @HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll, @Context final UriInfo uriInfo) {
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
	 * $ mvn clean spring-boot:run 
	 * $ curl -X PUT -u reportserver-restadmin:ReportServer*RESTADMIN \
	 * -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 * '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 * "name":"Test Report #04 (modified by PUT)","number":1400,"sortOrder":1400,"active":false}' \
	 * http://localhost:8080/rest/reports/702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 * 
	 * This updates the report with UUID 702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 * with the following changes:
	 * 
	 * report category: "Q-Free internal" -> "Traffic" name: "Test Report #04"
	 * -> "Test Report #04 (modified by PUT)" number: 400 -> 1400 sortOrder: 400
	 * -> 1400 active: true -> false
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public Response updateById(ReportResource reportResource, @PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext, @Context final UriInfo uriInfo) {
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
		 * If the "sortOrder" was not specified in reportResource, we use the
		 * current value stored in report.
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
	 * $ mvn clean spring-boot:run
	 * $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 * http://localhost:8080/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.REPORTVERSIONS_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
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

	/*
	 * Return the Roles that have been given access to a Report that is
	 * specified by its id. This access is specified via RoleReport entities.
	 * This endpoint can be tested with:
	 * 
	 * $ mvn clean spring-boot:run 
	 * $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 * http://localhost:8080/report-server/rest/reports/c7f1d394-9814-4ede-bb01-2700187d79ca/roles?expand=roles
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.ROLES_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public RoleCollectionResource getAuthorizedRolesByReportId(
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
		Report report = reportRepository.findOne(id);
		RestUtils.ifNullThen404(report, Report.class, "reportId", id.toString());

		List<Role> roles = new ArrayList<>(0);
		if (RoleController.ALLOW_ALL_REPORTS_FOR_EACH_ROLE) {
			if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Role.class, showAll)) {
				roles = roleRepository.findByActiveTrue();
			} else {
				roles = roleRepository.findAll();
			}
		} else {
			/*
			 * The H2 database does not support recursive CTE expressions, so it
			 * is necessary to run different code if the database is not
			 * PostgreSQL. This only affects integration tests, because only
			 * PostreSQL is used in production.
			 */
			if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {

				/*
				 * PostgreSQL:
				 * 
				 * reportRepository.findRolesByReportIdRecursive(...) is a
				 * Spring Data JPA repository method that uses a *native* SQL
				 * query. It appears that Hibernate has a problem treating the
				 * UUID data type for this environment, whether objects of that
				 * type appear in the SELECT result set or if an object of that
				 * type is used as a named parameter in the query. To get around
				 * this limitation, this repository method is specially written
				 * to deal with String representations of the UUID data type,
				 * instead of the UUID type itself. The chosen implementation:
				 *
				 * 1. Takes as its first parameter, the String representation of
				 * the Report for which to locate Role.
				 *
				 * 2. Returns a list of Role id's for the Role's to be returned,
				 * but this id's are represented as String objects, not as UUID
				 * objects.
				 *
				 * As a result of this treatment, there are two extra steps
				 * required to create the list of Role's to return:
				 *
				 * 1. Create a list of UUID objects from the list of UUID
				 * Strings.
				 *
				 * 2. Use Spring Data's provided "findAll" repository method to
				 * return a list of Role's given the list of UUID's.
				 */
				List<String> stringUuids = null;
				if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Role.class, showAll)) {
					stringUuids = reportRepository.findRolesByReportIdRecursive(report.getReportId().toString(), true);
				} else {
					stringUuids = reportRepository.findRolesByReportIdRecursive(report.getReportId().toString(), false);
				}
				if (stringUuids != null && stringUuids.size() > 0) {
					logger.info("Number of stringUuids (recursive) = {}", stringUuids.size());
					/*
					 * Create List of UUID's of the Role's that the endpoint
					 * will return.
					 */
					List<UUID> uuids = new ArrayList<>(stringUuids.size());
					for (String stringUuid : stringUuids) {
						logger.info("stringUuid (recursive) = {}", stringUuid);
						uuids.add(UUID.fromString(stringUuid));
					}
					roles = roleRepository.findAll(uuids);
				}

			} else {
				/*
				 * H2:
				 * 
				 * This code returns only those Roles associated with RoleReport
				 * entities that are *directly* linked to the Report report.
				 * This does not* include Roles associated with RoleReport
				 * entities that are linked to descendants (children
				 * grandchildren, ...) of these Roles.
				 */
				if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Report.class, showAll)) {
					roles = reportRepository.findActiveRolesByReportId(report.getReportId());
				} else {
					roles = reportRepository.findRolesByReportId(report.getReportId());
				}
			}
		}

		logger.info("Number of roles = {}", roles.size());
		for (Role role : roles) {
			logger.info("role = {}", role.getUsername());
		}
		return new RoleCollectionResource(roles, Role.class, authorityService, uriInfo, queryParams, apiVersion);
	}
}
