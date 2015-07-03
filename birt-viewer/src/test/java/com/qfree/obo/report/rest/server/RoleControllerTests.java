package com.qfree.obo.report.rest.server;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.UuidCustomType;
import com.qfree.obo.report.dto.ReportCollectionResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationConfig.class)
@WebIntegrationTest("server.port=0")
/*
 * These integration tests can modify the test database via the ReST call via
 * the HTTP connection to the embedded server that receives the request. They
 * may "dirty" the application context in other ways as well. This causes
 * other unit/integration tests in other test classes to fail when using an H2
 * embedded database. The @DirtiesContext annotation here tells Spring to reset 
 * the application context after all tests in the class. In addition, 
 * @DirtiesContext on each test method resets the application context after each
 * test.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RoleControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(RoleControllerTests.class);

	@Autowired
	RoleRepository roleRepository;

	@Value("${local.server.port}")
	private int port;

	private static Client client = null;
	private WebTarget webTarget = null;

	@BeforeClass
	public static void setUpBeforeClass() {
		client = ControllerTestUtils.setUpJaxRsClient();
	}

	@Before
	public void setUp() {
		this.webTarget = ControllerTestUtils.setUpWebTarget(client, port);
	}

	@Test
	//	@DirtiesContext
	@Transactional
	public void testGetById() {
		/* 
		 * This is the default version for the endpoint 
		 * AbstractResource.ROLES_PATH/{id} using HTTP GET.
		 */
		String defaultVersionGet = "1";

		/*
		 * Details of the Role to fetch (from test-data.sql).
		 */
		UUID uuidOfRole = UUID.fromString("6c328253-5fa6-4b11-8052-ef38197931b0");
		String currentUsername = "aaaa";
		String currentFullName = "";
		String currentEncodedPassword = "";
		Boolean currentLoginRole = false;
		Date currentCreatedOn = DateUtils.dateUtcFromIso8601String("2015-04-13T08:00:00.000Z");

		Role role = roleRepository.findOne(uuidOfRole);
		assertThat(role, is(not(nullValue())));
		assertThat(role.getUsername(), is(currentUsername));
		assertThat(role.getFullName(), is(currentFullName));
		assertThat(role.getEncodedPassword(), is(currentEncodedPassword));
		assertThat(role.isLoginRole(), is(currentLoginRole));
		assertThat(DateUtils.entityTimestampToNormalDate(role.getCreatedOn()), is(currentCreatedOn));

		Response response;
		String path;

		path = Paths
				.get(ResourcePath.forEntity(Role.class).getPath(), uuidOfRole.toString())
				.toString();
		logger.debug("path = {}", path);

		/*
		 * Retrieve the RoleResource via HTTP GET.
		 */
		response = webTarget.path(path)
				.queryParam(ResourcePath.EXPAND_QP_NAME, ResourcePath.ROLE_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		RoleResource roleResource = response.readEntity(RoleResource.class);
		logger.debug("roleResource = {}", roleResource);
		assertThat(roleResource, is(not(nullValue())));
		assertThat(roleResource.getRoleId(), is(uuidOfRole));
		assertThat(roleResource.getUsername(), is(currentUsername));
		assertThat(roleResource.getFullName(), is(currentFullName));
		assertThat(roleResource.getEncodedPassword(), is(currentEncodedPassword));
		assertThat(roleResource.isLoginRole(), is(currentLoginRole));
		assertThat(DateUtils.entityTimestampToNormalDate(roleResource.getCreatedOn()), is(currentCreatedOn));

		/*
		 * Attempt to fetch a RoleResource using an id that does not exist. This
		 * should throw a "404" exception.
		 */
		UUID uuidOfNonExistentRole = UUID.fromString("6c328253-0000-0000-0000-ef38197931b0");

		path = Paths
				.get(ResourcePath.forEntity(Role.class).getPath(), uuidOfNonExistentRole.toString())
				.toString();
		logger.debug("path (uuidOfNonExistentRole) = {}", path);

		/*
		 * Attempt to retrieve the nonexistent RoleResource via HTTP GET.
		 */
		response = webTarget.path(path)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
		/*
		 * If an error occurs, then a RestErrorResource is returned as the 
		 * entity, not a RoleResource.
		 */
		RestErrorResource restErrorResource = response.readEntity(RestErrorResource.class);
		logger.debug("restErrorResource = {}", restErrorResource);
		assertThat(restErrorResource, is(not(nullValue())));
		assertThat(restErrorResource.getHttpStatus(), is(404));
		assertThat(restErrorResource.getHttpStatus(),
				is(RestError.NOT_FOUND_RESOUCE.getResponseStatus().getStatusCode()));
		assertThat(restErrorResource.getErrorCode(), is(RestError.NOT_FOUND_RESOUCE.getErrorCode()));

	}

	@Test
	@DirtiesContext
	@Transactional
	public void testCreateByPost() {
		/* This is the default version for the endpoint 
		 * AbstractResource.ROLES_PATH using HTTP POST.
		 */
		String defaultVersionPost = "1";
		String defaultVersionGet = "1";

		String newUsername = "new-user-from-post";
		String newFullName = "Full Name of new-user-from-post";
		String newEncodedPassword = "8sV4cDCNyH9DLlkS1N5vjjInIbo=";  // Base64(SHA-1("newpassword"))
		Boolean newLoginRole = true;

		RoleResource roleResource = new RoleResource();
		roleResource.setUsername(newUsername);
		roleResource.setFullName(newFullName);
		roleResource.setEncodedPassword(newEncodedPassword);
		roleResource.setLoginRole(newLoginRole);
		logger.debug("roleResource = {}", roleResource);

		Response response;
		//		response = webTarget.path(ResourcePath.ROLES_PATH)
		response = webTarget.path(ResourcePath.forEntity(Role.class).getPath())
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPost)
				.post(Entity.entity(roleResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

		/*
		 * The HTTP "Location" header should have been set in the response. It
		 * should contain the URI of the resource created. The resource at this
		 * URI is loaded below for additional tests.
		 */
		MultivaluedMap<String, Object> headers = response.getHeaders();
		logger.debug("headers = {}", headers);
		List<Object> createdEntityLocations = headers.get("Location");
		assertThat(createdEntityLocations, is(not(nullValue())));
		assertThat(createdEntityLocations.size(), is(greaterThan(0)));

		RoleResource responseEntity = response.readEntity(RoleResource.class);
		assertThat(responseEntity, is(not(nullValue())));
		assertThat(responseEntity.getUsername(), is(newUsername));
		assertThat(responseEntity.getFullName(), is(newFullName));
		assertThat(responseEntity.getEncodedPassword(), is(newEncodedPassword));
		assertThat(responseEntity.isLoginRole(), is(newLoginRole));
		assertThat(responseEntity.getHref(), is(not(nullValue())));
		assertThat(responseEntity.getMediaType(), is(not(nullValue())));
		/*
		 * We test that an id was generated, but we don't know what it will be,
		 * so it does make sense to check that it is "correct".
		 */
		assertThat(responseEntity.getRoleId(), is(not(nullValue())));
		/*
		 * Assert that the "CreatedOn" datetime is within 5 minutes of the
		 * current time in this process. Ideally,they should be much, much
		 * closer, but this at least is a sanity check that the "CreatedOn"
		 * datetime is actually getting set. We don't want this to fail unless
		 * there is a significant difference; otherwise, this could cause 
		 * problems with continuous integration and automatic builds.
		 */
		logger.debug(" DateUtils.nowUtc() = {}, responseEntity.getCreatedOn() = {}",
				DateUtils.nowUtc(), responseEntity.getCreatedOn());
		long millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - responseEntity.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		/*
		 * Retrieve the RoleResource associated with the 
		 * Role that was just created. Its URI should have been 
		 * returned in the HTTP "Location" header.
		 */
		String uriAsString;
		uriAsString = createdEntityLocations.get(0).toString();
		logger.debug("uriAsString = {}", uriAsString);
		response = client.target(uriAsString)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		RoleResource resource = response.readEntity(RoleResource.class);
		logger.debug("resource = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getUsername(), is(newUsername));
		assertThat(resource.getFullName(), is(newFullName));
		assertThat(resource.getEncodedPassword(), is(newEncodedPassword));
		assertThat(resource.isLoginRole(), is(newLoginRole));
		assertThat(resource.getHref(), is(not(nullValue())));
		assertThat(resource.getMediaType(), is(not(nullValue())));
		assertThat(responseEntity.getRoleId(), is(responseEntity.getRoleId()));
		millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - resource.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		/*
		 * Check that there is now a Role in the database 
		 * corresponding to the RoleResource.
		 */
		assertThat(resource.getRoleId(), is(not(nullValue())));
		Role newRole = roleRepository.findOne(resource.getRoleId());
		assertThat(newRole, is(not(nullValue())));
	}

	@Test
	@DirtiesContext
	@Transactional
	public void testUpdateByPut() {
		/* 
		 * These are the default versions for the endpoint 
		 * AbstractResource.ROLES_PATH/{id} using HTTP PUT and GET.
		 */
		String defaultVersionPut = "1";
		String defaultVersionGet = "1";

		/*
		 * Details of the Role to update (from test-data.sql).
		 */
		UUID uuidOfRole = UUID.fromString("6c328253-5fa6-4b11-8052-ef38197931b0");
		String currentUsername = "aaaa";
		String currentFullName = "";
		String currentEncodedPassword = "";
		Boolean currentLoginRole = false;
		Date currentCreatedOn = DateUtils.dateUtcFromIso8601String("2015-04-13T08:00:00.000Z");

		/*
		 * New details that will be used to update the Role.
		 */
		String newUsername = "aaaa (modified by PUT)";
		String newFullName = "Full Name set by PUY";
		String newEncodedPassword = "8sV4cDCNyH9DLlkS1N5vjjInIbo=";  // Base64(SHA-1("newpassword"))
		Boolean newLoginRole = true;

		Role role = roleRepository.findOne(uuidOfRole);
		assertThat(role, is(not(nullValue())));
		assertThat(role.getUsername(), is(currentUsername));
		assertThat(role.getFullName(), is(currentFullName));
		assertThat(role.getEncodedPassword(), is(currentEncodedPassword));
		assertThat(role.isLoginRole(), is(currentLoginRole));
		assertThat(DateUtils.entityTimestampToNormalDate(role.getCreatedOn()), is(currentCreatedOn));

		RoleResource roleResource = new RoleResource();
		roleResource.setUsername(newUsername);
		roleResource.setFullName(newFullName);
		roleResource.setEncodedPassword(newEncodedPassword);
		roleResource.setLoginRole(newLoginRole);
		logger.debug("roleResource = {}", roleResource);

		String path = Paths
				.get(ResourcePath.forEntity(Role.class).getPath(), uuidOfRole.toString())
				.toString();
		logger.debug("path = {}", path);
		Response response = webTarget.path(path)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPut)
				.put(Entity.entity(roleResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));

		/*
		 * Retrieve the RoleResource that was updated via HTTP GET.
		 */
		response = webTarget.path(path)
				.queryParam(ResourcePath.EXPAND_QP_NAME, ResourcePath.ROLE_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		RoleResource resource = response.readEntity(RoleResource.class);
		logger.debug("resource (updated) = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getUsername(), is(newUsername));
		assertThat(resource.getFullName(), is(newFullName));
		assertThat(resource.getEncodedPassword(), is(newEncodedPassword));
		assertThat(resource.isLoginRole(), is(newLoginRole));
		assertThat(DateUtils.entityTimestampToNormalDate(resource.getCreatedOn()), is(currentCreatedOn));

		/*
		 * Check that the Role entity was updated properly. We cannot
		 * simply use "roleRepository.findOne(uuidOfRole)" 
		 * to load the Role entity because it was updated in the 
		 * Jersey server thread that received the PUT request from above. Hence,
		 * the EntityManager that is managing this thread does not know about
		 * the update and will simply return the old (un-updated) entity. 
		 * Therefore, we need to tell the EntityManager to refresh its copy
		 * before it returns it.
		 * 
		 * Will return old (un-updated) Role entity:
		 * 
		 * role = roleRepository.findOne(uuidOfRole);
		 */
		role = roleRepository.refresh(role);
		logger.debug("role (refeshed) = {}", role);
		assertThat(role, is(not(nullValue())));
		assertThat(role.getUsername(), is(newUsername));
		assertThat(role.getFullName(), is(newFullName));
		assertThat(role.getEncodedPassword(), is(newEncodedPassword));
		assertThat(role.isLoginRole(), is(newLoginRole));
		assertThat(DateUtils.entityTimestampToNormalDate(role.getCreatedOn()), is(currentCreatedOn));
	}

	@Test
	//	@DirtiesContext
	@Transactional
	public void testGetReportsForRole() {
		/* 
		 * This is the default version for the endpoint 
		 * ResourcePath.ROLES_PATH/{id}/ResourcePath.REPORTS_PATH using HTTP GET.
		 */
		String defaultApiVersion = "1";

		/*
		 * Details of the Role (from test-data.sql) for which Reports will be 
		 * fetched.
		 */
		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		String currentUsername = "aabb";

		UUID uuidOfReport01 = UUID.fromString("d65f3d9c-f67d-4beb-9936-9dfa19aa1407");	// active=true
		UUID uuidOfReport02 = UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca");	// active=true
		UUID uuidOfReport03 = UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d");	// active=false
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");	// active=true
		//	List<UUID> activeReportUuids = new ArrayList<>();
		//	activeReportUuids.add(uuidOfReport04);
		//	List<UUID> allReportUuids = new ArrayList<>();
		//	allReportUuids.add(uuidOfReport03);
		//	allReportUuids.add(uuidOfReport04);

		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(not(nullValue())));
		assertThat(role_aabb.getUsername(), is(currentUsername));

		Response response;
		String path;

		path = Paths
				.get(
						ResourcePath.forEntity(Role.class).getPath(),
						uuidOfRole_aabb.toString(),
						ResourcePath.forEntity(Report.class).getPath())
				.toString();
		logger.debug("path = {}", path);

		/*
		 * Retrieve the RoleResource via HTTP GET.
		 */
		response = webTarget.path(path)
				.queryParam(ResourcePath.EXPAND_QP_NAME,
						ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTVERSION_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultApiVersion)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportCollectionResource reportCollectionResource = response.readEntity(ReportCollectionResource.class);
		logger.debug("reportCollectionResource = {}", reportCollectionResource);
		assertThat(reportCollectionResource, is(not(nullValue())));
		assertThat(reportCollectionResource.getItems(), is(not(nullValue())));
		/*
		 * Note:	Since the H2 database does not support recursive CTE 
		 * 			expressions, the REST endpoint being tested here does not
		 * 			consider inheritance of report access for roles. This means
		 * 			that the tests here must be different for H2 and PostgreSQL.
		 */
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			/*
			 * With Role inheritance included, there are 4 Report's that are
			 * "enabled" for Role "aabb":
			 * 
			 * 		"Report name #01"
			 * 		"Report name #02"
			 * 		"Report name #03"  (inactive)
			 * 		"Report name #04"
			 * 
			 * However, report "Report name #03" is not active,so that leave 3
			 * "active" Report's.
			 * 
			 * With recursive CTE expression:
			 */
			if (RoleController.returnAllReportsForEachRole == false) {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(3));
				assertThat(reportCollectionResource.getItems(), hasSize(3));
			} else {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(4));
				assertThat(reportCollectionResource.getItems(), hasSize(4));
			}
			List<UUID> activeReportUuidsFromEndpoint = new ArrayList<>(reportCollectionResource.getItems().size());
			for (ReportResource reportResource : reportCollectionResource.getItems()) {
				activeReportUuidsFromEndpoint.add(reportResource.getReportId());
			}
			assertThat(activeReportUuidsFromEndpoint,
					IsCollectionContaining.hasItems(uuidOfReport01, uuidOfReport02, uuidOfReport04));
		} else {
			/*
			 * Without considering Role inheritance, there should only be a single
			 * *active* Report returned, "Report name #04". "Report name #03" is
			 * also linked to the Role with id uuidOfRole_aabb, but it is not 
			 * active.
			 * 
			 * Without recursive CTE expression:
			 */
			if (RoleController.returnAllReportsForEachRole == false) {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(1));
				assertThat(reportCollectionResource.getItems(), hasSize(1));
			} else {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(4));
				assertThat(reportCollectionResource.getItems(), hasSize(4));
			}
			List<UUID> activeReportUuidsFromEndpoint = new ArrayList<>(reportCollectionResource.getItems().size());
			for (ReportResource reportResource : reportCollectionResource.getItems()) {
				activeReportUuidsFromEndpoint.add(reportResource.getReportId());
			}
			assertThat(activeReportUuidsFromEndpoint, IsCollectionContaining.hasItems(uuidOfReport04));
		}

		/*
		 * Repeat, but this time turn off filtering on "active" for both Reports
		 * and ReportVersions.
		 */
		response = webTarget.path(path)
				.queryParam(ResourcePath.EXPAND_QP_NAME,
						ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTVERSION_EXPAND_PARAM)
				.queryParam(ResourcePath.SHOWALL_QP_NAME,
						ResourcePath.REPORT_SHOWALL_PARAM,
						ResourcePath.REPORTVERSION_SHOWALL_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultApiVersion)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		reportCollectionResource = response.readEntity(ReportCollectionResource.class);
		logger.debug("reportCollectionResource = {}", reportCollectionResource);
		assertThat(reportCollectionResource, is(not(nullValue())));
		assertThat(reportCollectionResource.getItems(), is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			/*
			 * With recursive CTE expression:
			 */
			if (RoleController.returnAllReportsForEachRole == false) {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(4));
				assertThat(reportCollectionResource.getItems(), hasSize(4));
			} else {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(6));
				assertThat(reportCollectionResource.getItems(), hasSize(6));
			}
			List<UUID> allReportUuidsFromEndpoint = new ArrayList<>(reportCollectionResource.getItems().size());
			for (ReportResource reportResource : reportCollectionResource.getItems()) {
				allReportUuidsFromEndpoint.add(reportResource.getReportId());
			}
			assertThat(allReportUuidsFromEndpoint,
					IsCollectionContaining.hasItems(uuidOfReport01, uuidOfReport02, uuidOfReport03, uuidOfReport04));
		} else {
			/*
			 * Without recursive CTE expression:
			 */
			if (RoleController.returnAllReportsForEachRole == false) {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(2));
				assertThat(reportCollectionResource.getItems(), hasSize(2));
			} else {
				assertThat(reportCollectionResource.getItems(), IsCollectionWithSize.hasSize(6));
				assertThat(reportCollectionResource.getItems(), hasSize(6));
			}
			List<UUID> allReportUuidsFromEndpoint = new ArrayList<>(reportCollectionResource.getItems().size());
			for (ReportResource reportResource : reportCollectionResource.getItems()) {
				allReportUuidsFromEndpoint.add(reportResource.getReportId());
			}
			assertThat(allReportUuidsFromEndpoint, IsCollectionContaining.hasItems(uuidOfReport03, uuidOfReport04));
		}

		/*
		 * Attempt to fetch a list of reports for a Role using an id that does 
		 * not exist. This should throw a "404" exception.
		 */
		UUID uuidOfNonExistentRole = UUID.fromString("6c328253-0000-0000-0000-ef38197931b0");

		path = Paths
				.get(
						ResourcePath.forEntity(Role.class).getPath(),
						uuidOfNonExistentRole.toString(),
						ResourcePath.forEntity(Report.class).getPath())
				.toString();

		/*
		 * Attempt to retrieve the list of reports via HTTP GET.
		 */
		response = webTarget.path(path)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultApiVersion)
				.get();
		assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
		/*
		 * If an error occurs, then a RestErrorResource is returned as the 
		 * entity, not a RoleResource.
		 */
		RestErrorResource restErrorResource = response.readEntity(RestErrorResource.class);
		logger.debug("restErrorResource = {}", restErrorResource);
		assertThat(restErrorResource, is(not(nullValue())));
		assertThat(restErrorResource.getHttpStatus(), is(404));
		assertThat(restErrorResource.getHttpStatus(),
				is(RestError.NOT_FOUND_RESOUCE.getResponseStatus().getStatusCode()));
		assertThat(restErrorResource.getErrorCode(), is(RestError.NOT_FOUND_RESOUCE.getErrorCode()));

	}

}
