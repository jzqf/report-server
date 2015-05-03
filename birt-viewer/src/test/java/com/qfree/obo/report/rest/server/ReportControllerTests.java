package com.qfree.obo.report.rest.server;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
		/* This is the default version for the endpoint 
		 * AbstractResource.REPORTS_PATH  using HTTP POST.
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
		 * This ReportCategoryResource instance has only its "id" field defined.
		 * That is the only field that needs to be defined when creating a new
		 * Report from a ReportResouce.
		 */
		UUID uuidOfQfreeInternalReportCategory = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");
		ReportCategoryResource newReportCategoryResource = new ReportCategoryResource();
		newReportCategoryResource.setReportCategoryId(uuidOfQfreeInternalReportCategory);
		//TODO DELETE THE FOLLOWING LINES: XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		//		ReportCategory reportCategory = reportCategoryRepository.findOne(uuidOfQfreeInternalReportCategory);
		//		assertThat(reportCategory, is(not(nullValue())));
		//		String reportCategoryDescription = reportCategory.getDescription();
		//		newReportCategoryResource.setDescription(reportCategoryDescription);

		ReportResource reportResource = new ReportResource();
		reportResource.setReportCategoryResource(newReportCategoryResource);
		reportResource.setName(newName);
		reportResource.setNumber(newNumber);
		//	private List<ReportVersion> reportVersions;
		//	private List<RoleReport> roleReports;
		reportResource.setActive(newActive);
		reportResource.setCreatedOn(newCreatedOn);

		logger.info("reportResource = {}", reportResource);

		Response response;

		/*
		 * It is important here to specify that "reportcategory" be expanded;
		 * otherwise, 
		 * responseEntity.getReportCategoryResource().getReportCategoryId() will
		 * be null and the assert for it below will fail, even though the POST
		 * will still correctly create the new Report object.
		 */
		//		response = webTarget.path(ResourcePath.REPORTCATEGORIES_PATH)
		response = webTarget
				.path(ResourcePath.forEntity(Report.class).getPath())
				.queryParam("expand", "report", "reportcategory")
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
		logger.info("responseEntity = {}", responseEntity);
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
		logger.info("uriAsString = {}", uriAsString);
		response = client.target(uriAsString)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportResource resource = response.readEntity(ReportResource.class);
		logger.info("resource = {}", resource);
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
	}

	//	@Test
	//	@DirtiesContext
	//	@Transactional
	//	public void testUpdateByPut() {
	//		/* 
	//		 * These are the default versions for the endpoint 
	//		 * AbstractResource.REPORTCATEGORIES_PATH/{id} using HTTP PUT and GET.
	//		 */
	//		String defaultVersionPut = "1";
	//		String defaultVersionGet = "1";
	//
	//		/*
	//		 * Details of the Report to update.
	//		 */
	//		UUID uuidOfReport = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");
	//		String currentAbbreviation = "QFREE";
	//		String currentDescription = "Q-Free internal";
	//		boolean currentActive = true;
	//		Date currentCreatedOn = DateUtils.dateUtcFromIso8601String("2015-05-30T22:00:00.000Z");
	//
	//		/*
	//		 * New details that will be used to update the Report.
	//		 */
	//		String newAbbreviation = "QFREEMODIFIED";
	//		String newDescription = "Q-Free internal (modified)";
	//		boolean newActive = false;
	//
	//		Report report = reportRepository.findOne(uuidOfReport);
	//		assertThat(report, is(not(nullValue())));
	//		assertThat(report.getAbbreviation(), is(currentAbbreviation));
	//		assertThat(report.getDescription(), is(currentDescription));
	//		assertThat(report.isActive(), is(currentActive));
	//		assertThat(DateUtils.entityTimestampToNormalDate(report.getCreatedOn()), is(currentCreatedOn));
	//
	//		ReportResource reportResource = new ReportResource();
	//		reportResource.setAbbreviation(newAbbreviation);
	//		reportResource.setDescription(newDescription);
	//		reportResource.setActive(newActive);
	//		logger.debug("reportResource = {}", reportResource);
	//
	//		String path = Paths
	//				.get(ResourcePath.forEntity(Report.class).getPath(), uuidOfReport.toString())
	//				.toString();
	//		logger.debug("path = {}", path);
	//		Response response = webTarget.path(path)
	//				.request()
	//				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPut)
	//				.put(Entity.entity(reportResource, MediaType.APPLICATION_JSON_TYPE));
	//		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
	//
	//		/*
	//		 * Retrieve the ReportResource that was updated via HTTP GET.
	//		 */
	//		response = webTarget.path(path)
	//				.request()
	//				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
	//				.get();
	//		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
	//		ReportResource resource = response.readEntity(ReportResource.class);
	//		logger.debug("resource (updated) = {}", resource);
	//		assertThat(resource, is(not(nullValue())));
	//		assertThat(resource.getAbbreviation(), is(newAbbreviation));
	//		assertThat(resource.getDescription(), is(newDescription));
	//		assertThat(resource.isActive(), is(newActive));
	//		assertThat(DateUtils.entityTimestampToNormalDate(resource.getCreatedOn()), is(currentCreatedOn));
	//
	//		/*
	//		 * Check that the Report entity was updated properly. We cannot
	//		 * simply use "reportRepository.findOne(uuidOfReport)" 
	//		 * to load the Report entity because it was updated in the 
	//		 * Jersey server thread that received the PUT request from above. Hence,
	//		 * the EntityManager that is managing this thread does not know about
	//		 * the update and will simply return the old (un-updated) entity. 
	//		 * Therefore, we need to tell the EntityManager to refresh its copy
	//		 * before it returns it.
	//		 * 
	//		 * Will return old (un-updated) Report entity:
	//		 * 
	//		 * report = reportRepository.findOne(uuidOfReport);
	//		 */
	//		report = reportRepository.refresh(report);
	//		logger.debug("report (refeshed) = {}", report);
	//		assertThat(report, is(not(nullValue())));
	//		assertThat(report.getAbbreviation(), is(newAbbreviation));
	//		assertThat(report.getDescription(), is(newDescription));
	//		assertThat(report.isActive(), is(newActive));
	//		assertThat(DateUtils.entityTimestampToNormalDate(report.getCreatedOn()), is(currentCreatedOn));
	//	}

}
