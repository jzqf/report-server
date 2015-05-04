package com.qfree.obo.report.rest.server;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.dto.ReportCategoryResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ResourcePath;
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
		 * It is necessary here to specify that "reportcategory" be expanded;
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
				.queryParam("expand", "report", "reportcategory")  // see comments above why this is necessary
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
		 * "?expand=report&expand=reportcategory" because these are the query
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
		assertThat(responseEntity.getReportId(), is(responseEntity.getReportId()));
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
		 * It is important here to specify that "reportcategory" be expanded;
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
				// .queryParam("expand", "report", "reportcategory")
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPut)
				.put(Entity.entity(reportResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));

		/*
		 * Retrieve the ReportResource that was updated via HTTP PUT. 
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "reportcategory" for the 
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
				.queryParam("expand", "report", "reportcategory")  // see comments above why this is necessary
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
