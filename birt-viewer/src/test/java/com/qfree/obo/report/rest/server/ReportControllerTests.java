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
import com.qfree.obo.report.db.ReportCategoryRepository;
import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportCategoryResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
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
public class ReportControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportControllerTests.class);

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

	@Autowired
	ReportVersionRepository reportVersionRepository;

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
	@DirtiesContext
	@Transactional
	public void testCreateByPost() {
		/* These the default versions for the endpoint 
		 * AbstractResource.REPORTS_PATH using HTTP POST and GET, respectively.
		 */
		String defaultVersionPost = "1";
		String defaultVersionGet = "1";
		
		/*
		 * ReportResource attributes for creating a new Report.
		 */
		String newName = "Report created from testCreateByPost()";
		Integer newNumber = 999;
		Boolean newActive = true;
		Date newCreatedOn = null;	// this will be filled in when the Report is created
		/*
		 * IMPORTANT:
		 * 
		 * This ReportCategoryResource instance needs only its "id" field 
		 * to be defined. That is the only ReportCategoryResource field that 
		 * needs to be defined when creating a new Report from a ReportResouce.
		 */
		UUID uuidOfQfreeInternalReportCategory = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");
		ReportCategoryResource newReportCategoryResource = new ReportCategoryResource();
		newReportCategoryResource.setReportCategoryId(uuidOfQfreeInternalReportCategory);

		ReportResource reportResource = new ReportResource();
		reportResource.setReportCategoryResource(newReportCategoryResource);
		reportResource.setName(newName);
		reportResource.setNumber(newNumber);
		//	private List<ReportVersion> reportVersions;
		//	private List<RoleReport> roleReports;
		reportResource.setActive(newActive);
		reportResource.setCreatedOn(newCreatedOn);
		logger.debug("reportResource = {}", reportResource);

		Response response;

		/*
		 * IMPORTANT:
		 * 
		 * It is necessary here to specify that "reportCategory" be expanded;
		 * otherwise, 
		 * responseEntity.getReportCategoryResource().getReportCategoryId() will
		 * be null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though the POST will still correctly create 
		 * the new Report object. 
		 */
		response = webTarget
				// .path(ResourcePath.REPORTS_PATH)
				.path(ResourcePath.forEntity(Report.class).getPath())
				// See comments above why these query parameters are necessary here:
				.queryParam("expand", ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPost)
				.post(Entity.entity(reportResource, MediaType.APPLICATION_JSON_TYPE));
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

		ReportResource responseEntity = response.readEntity(ReportResource.class);
		logger.debug("responseEntity = {}", responseEntity);
		assertThat(responseEntity, is(not(nullValue())));
		assertThat(responseEntity.getReportCategoryResource().getReportCategoryId(),
				is(uuidOfQfreeInternalReportCategory));
		assertThat(responseEntity.getName(), is(newName));
		assertThat(responseEntity.getNumber(), is(newNumber));
		assertThat(responseEntity.isActive(), is(newActive));
		assertThat(responseEntity.getHref(), is(not(nullValue())));
		assertThat(responseEntity.getMediaType(), is(not(nullValue())));
		/*
		 * We test that an id was generated, but we don't know what it will be,
		 * so it does make sense to check that it is "correct".
		 */
		assertThat(responseEntity.getReportId(), is(not(nullValue())));
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
		 * Retrieve the ReportResource associated with the Report that was just 
		 * created. Its URI should have been returned in the HTTP "Location" 
		 * header.
		 * 
		 * uriAsString will include the query parameters:
		 * "?expand=report&expand=reportCategory" because these are the query
		 * parameters specified for the POST above. Hence, the 
		 * ReportCategoryResource resource.getReportCategoryResource() will be
		 * expanded and contain all of its attributes, in particular its "id", 
		 * so it can be used in the assert below. 
		 */
		String uriAsString;
		uriAsString = createdEntityLocations.get(0).toString();
		logger.debug("uriAsString = {}", uriAsString);
		response = client.target(uriAsString)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportResource resource = response.readEntity(ReportResource.class);
		logger.debug("resource = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getReportCategoryResource().getReportCategoryId(),
				is(uuidOfQfreeInternalReportCategory));
		assertThat(resource.getName(), is(newName));
		assertThat(resource.getNumber(), is(newNumber));
		assertThat(resource.isActive(), is(newActive));
		assertThat(resource.getHref(), is(not(nullValue())));
		assertThat(resource.getMediaType(), is(not(nullValue())));
		//		assertThat(responseEntity.getReportId(), is(responseEntity.getReportId()));
		millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - resource.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		/*
		 * Check that there is now a Report in the database 
		 * corresponding to the ReportResource.
		 */
		assertThat(resource.getReportId(), is(not(nullValue())));
		Report newReport = reportRepository.findOne(resource.getReportId());
		assertThat(newReport, is(not(nullValue())));
	}

	@Test
	//	@DirtiesContext
	@Transactional
	public void testGetById() {
		/* 
		 * This is the default version for the endpoint 
		 * AbstractResource.REPORTS_PATH/{id} using HTTP GET.
		 */
		String defaultVersionGet = "1";

		/*
		 * Details of the Report to fetch (from test-data.sql).
		 */
		UUID uuidOfReport = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		UUID uuidOfReportCategory = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");
		UUID reportVersionUuid_1 = UUID.fromString("293abf69-1516-4e9b-84ae-241d25c13e8d");
		UUID reportVersionUuid_2 = UUID.fromString("80b14b11-45c7-4a05-99ed-972050f2338f");
		String name = "Report name #04";
		Integer number = 400;
		Boolean active = true;
		Date createdOn = DateUtils.dateUtcFromIso8601String("2014-03-25T12:15:00.000Z");

		/*
		 * Retrieve ReportCategory that should be linked to the Report.
		 */
		ReportCategory reportCategory = reportCategoryRepository.findOne(uuidOfReportCategory);
		assertThat(reportCategory, is(not(nullValue())));

		/*
		 * Retrieve the ReportVersion's that should be linked to the Report.
		 */
		ReportVersion reportVersion_1 = reportVersionRepository.findOne(reportVersionUuid_1);
		ReportVersion reportVersion_2 = reportVersionRepository.findOne(reportVersionUuid_2);
		assertThat(reportVersion_1, is(not(nullValue())));
		assertThat(reportVersion_2, is(not(nullValue())));
		//List<ReportVersion> expectedReportVersions = new ArrayList<>();
		//expectedReportVersions.add(reportVersion_1);
		//expectedReportVersions.add(reportVersion_2);

		/*
		 * Retrieve the Report that will also be retrieved below by the HTTP
		 * GET ReST request.
		 */
		Report report = reportRepository.findOne(uuidOfReport);
		assertThat(report, is(not(nullValue())));
		assertThat(report.getReportCategory(), is(reportCategory));
		assertThat(report.getName(), is(name));
		assertThat(report.getNumber(), is(number));
		assertThat(report.isActive(), is(active));
		assertThat(DateUtils.entityTimestampToNormalDate(report.getCreatedOn()), is(createdOn));
		assertThat(report.getReportVersions(), is(not(nullValue())));
		assertThat(report.getReportVersions(), IsCollectionWithSize.hasSize(2));
		assertThat(report.getReportVersions(), hasSize(2));
		assertThat(report.getReportVersions(), IsCollectionContaining.hasItems(reportVersion_1, reportVersion_2));

		/*
		 * Create ReportCategoryResource from reportCategory so we can test that
		 * the ReportResource retrieved by the fetch contains this 
		 * ReportCategoryResource.
		 */

		Response response;
		String path;

		path = Paths.get(ResourcePath.forEntity(Report.class).getPath(), uuidOfReport.toString())
				.toString();

		/*
		 * Retrieve the ReportResource via HTTP GET.
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "reportCategory" for the 
		 * same reason as it is needed above for the "POST" request; otherwise, 
		 * reportResource.getReportCategoryResource().getReportCategoryId() will
		 * be null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though this GET will still correctly 
		 * retrieve the Report object. So this is necessary only for this 
		 * integration test to execute successfully.
		 * 
		 * For the same reason, the "reportVersion" "expand" query parameter is
		 * specified so that the attributes of the ReportVersionResource objects
		 * are filled in (so we can use the in asserts).
		 */
		response = webTarget.path(path)
				// See comments above why these query parameters are necessary here:
				.queryParam("expand", ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTCATEGORY_EXPAND_PARAM,
						ResourcePath.REPORTVERSION_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportResource reportResource = response.readEntity(ReportResource.class);
		assertThat(reportResource, is(not(nullValue())));
		assertThat(reportResource.getName(), is(name));
		assertThat(reportResource.getNumber(), is(number));
		assertThat(reportResource.isActive(), is(active));
		assertThat(DateUtils.entityTimestampToNormalDate(reportResource.getCreatedOn()), is(createdOn));
		ReportCategoryResource reportCategoryResource = reportResource.getReportCategoryResource();
		assertThat(reportCategoryResource, is(not(nullValue())));
		assertThat(reportCategoryResource.getReportCategoryId(), is(uuidOfReportCategory));
		assertThat(reportResource.getReportVersions(), is(not(nullValue())));

		/*
		 * Assert that the id of each ReportVersionResource object returned in
		 * the ReportResource reportResource object agree with the id's that we
		 * know they should.
		 */
		/*
		 *  This is for the case where reportResource.getReportVersions() is a 
		 *  List<ReportVersionResource> object:
		 */
		//List<UUID> reportVersionResourceUuids=new ArrayList<>(reportResource.getReportVersions().size());
		//for (ReportVersionResource reportVersionResource : reportResource.getReportVersions()) {
		//	reportVersionResourceUuids.add(reportVersionResource.getReportVersionId());
		//}
		/*
		 *  This is for the case where reportResource.getReportVersions() is a 
		 *  ReportVersionCollectionResource object:
		 */
		assertThat(reportResource.getReportVersions().getItems(), is(not(nullValue())));
		List<UUID> reportVersionResourceUuids = new ArrayList<>(reportResource.getReportVersions().getItems().size());
		for (ReportVersionResource reportVersionResource : reportResource.getReportVersions().getItems()) {
			reportVersionResourceUuids.add(reportVersionResource.getReportVersionId());
		}
		assertThat(reportVersionResourceUuids, hasSize(2));
		assertThat(reportVersionResourceUuids,
				IsCollectionContaining.hasItems(reportVersionUuid_1, reportVersionUuid_2));

		/*
		 * Attempt to fetch a ReportResource using an id that does not exist. This
		 * should throw a "404" exception.
		 */
		UUID uuidOfNonExistentReport = UUID.fromString("6c328253-0000-0000-0000-ef38197931b0");

		path = Paths.get(ResourcePath.forEntity(Report.class).getPath(), uuidOfNonExistentReport.toString())
				.toString();

		/*
		 * Attempt to retrieve the nonexistent ReportResource via HTTP GET.
		 */
		response = webTarget.path(path)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
		/*
		 * If an error occurs, then a RestErrorResource is returned as the 
		 * entity, not a ReportResource.
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
	public void testUpdateByPut() {
		/* These the default versions for the endpoint 
		 * AbstractResource.REPORTS_PATH using HTTP PUT and GET, respectively.
		 */
		String defaultVersionPut = "1";
		String defaultVersionGet = "1";

		/*
		 * This is the Report to be updated.
		 */
		UUID uuidOfReport = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report = reportRepository.findOne(uuidOfReport);
		assertThat(report, is(not(nullValue())));

		/*
		 * Details of the Report to update (from test-data.sql).
		 * 
		 * currentReportCategory should correspond to the ReportCategory
		 * "Q-Free internal"
		 */
		// ReportCategory currentReportCategory = report.getReportCategory();
		// UUID currentReportCategoryUuid = report.getReportCategory().getReportCategoryId();
		UUID currentReportCategoryUuid = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");  // "Q-Free internal"
		ReportCategory currentReportCategory = reportCategoryRepository.findOne(currentReportCategoryUuid);
		String currentName = "Report name #04";
		Integer currentNumber = 400;
		boolean currentActive = true;
		Date currentCreatedOn = DateUtils.dateUtcFromIso8601String("2014-03-25T12:15:00.000Z");

		assertThat(currentReportCategory, is(not(nullValue())));
		assertThat(report.getReportCategory(), is(currentReportCategory));
		assertThat(report.getName(), is(currentName));
		assertThat(report.getNumber(), is(currentNumber));
		assertThat(report.isActive(), is(currentActive));
		assertThat(DateUtils.entityTimestampToNormalDate(report.getCreatedOn()), is(currentCreatedOn));

		/*
		 * New details that will be used to update the Report.
		 */
		UUID newReportCategoryUuid = UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76");  // "Traffic"
		ReportCategory newReportCategory = reportCategoryRepository.findOne(newReportCategoryUuid);
		String newName = "Report name #04 (modfifed by PUT)";
		Integer newNumber = 999;
		boolean newActive = false;

		/*
		 * Construct ReportCategoryResource for the new ReportCategory to assign
		 * to the Report.
		 * 
		 * IMPORTANT:
		 * 
		 * This ReportCategoryResource instance needs only its "id" field 
		 * to be defined. That is the only ReportCategoryResource field that 
		 * needs to be defined when updating a Report from a ReportResouce.
		 */
		ReportCategoryResource newReportCategoryResource = new ReportCategoryResource();
		newReportCategoryResource.setReportCategoryId(newReportCategoryUuid);

		ReportResource reportResource = new ReportResource();
		reportResource.setReportCategoryResource(newReportCategoryResource);
		reportResource.setName(newName);
		reportResource.setNumber(newNumber);
		reportResource.setActive(newActive);
		logger.debug("reportResource = {}", reportResource);

		/*
		 * It is important here to specify that "reportCategory" be expanded;
		 * otherwise, 
		 * resource.getReportCategoryResource().getReportCategoryId() will
		 * be null and the assert for it below will fail, even though the POST
		 * will still correctly create the new Report object.
		 */
		String path = Paths
				.get(ResourcePath.forEntity(Report.class).getPath(), uuidOfReport.toString())
				.toString();
		logger.debug("path = {}", path);
		Response response = webTarget.path(path)
				/*
				 * These "expand" query parameters will be necessary if this
				 * request ever returns a ReportResource entity in the response
				 * for the same reason as it is necessary in the GET request 
				 * below - see the comments there for an explanation. It won't
				 * hurt to uncomment this line here now, but it is not 
				 * currently necessary to do this.
				 */
				// .queryParam("expand", "report", "reportCategory")
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPut)
				.put(Entity.entity(reportResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));

		/*
		 * Retrieve the ReportResource that was updated via HTTP PUT. 
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "reportCategory" for the 
		 * same reason as it is needed above for the "POST" request; otherwise, 
		 * resource.getReportCategoryResource().getReportCategoryId() will be 
		 * null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though the POST will still correctly create 
		 * the new Report object. Note that this is necessary for this 
		 * integration test to execute successfully; the Report will be updated
		 * successfully above by the "PUT" request regardless.
		 */
		response = webTarget.path(path)
				// See comments above why these query parameters are necessary here:
				.queryParam("expand", ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportResource resource = response.readEntity(ReportResource.class);
		logger.debug("resource (updated) = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getReportCategoryResource().getReportCategoryId(), is(newReportCategoryUuid));
		assertThat(resource.getName(), is(newName));
		assertThat(resource.getNumber(), is(newNumber));
		assertThat(resource.isActive(), is(newActive));
		assertThat(DateUtils.entityTimestampToNormalDate(resource.getCreatedOn()), is(currentCreatedOn));

		/*
		 * Check that the Report entity was updated properly. We cannot
		 * simply use "reportRepository.findOne(uuidOfReport)" 
		 * to load the Report entity because it was updated in the 
		 * Jersey server thread that received the PUT request from above. Hence,
		 * the EntityManager that is managing this thread does not know about
		 * the update and will simply return the old (un-updated) entity. 
		 * Therefore, we need to tell the EntityManager to refresh its copy
		 * before it returns it.
		 * 
		 * Will return old (un-updated) Report entity:
		 * 
		 * report = reportRepository.findOne(uuidOfReport);
		 */
		report = reportRepository.refresh(report);
		logger.debug("report (refeshed) = {}", report);
		assertThat(report, is(not(nullValue())));
		assertThat(report.getReportCategory(), is(newReportCategory));
		assertThat(report.getName(), is(newName));
		assertThat(report.getNumber(), is(newNumber));
		assertThat(report.isActive(), is(newActive));
		assertThat(DateUtils.entityTimestampToNormalDate(report.getCreatedOn()), is(currentCreatedOn));
	}

}
