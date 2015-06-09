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
import com.qfree.obo.report.db.ReportParameterRepository;
import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportParameterResource;
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
public class ReportVersionControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionControllerTests.class);

	@Autowired
	ReportVersionRepository reportVersionRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	ReportParameterRepository reportParameterRepository;

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
		 * ReportVersionResource attributes for creating a new ReportVersion.
		 */
		String newFilename = "400-TestReport04_v2.1-CreatedByIntegrationTesting.rptdesign";
		String newRptdesign = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
				.append("<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">\n")
				.append("<property name=\"createdBy\">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>\n")
				.append("<property name=\"units\">in</property>\n")
				.append("<property name=\"iconFile\">/templates/blank_report.gif</property>\n")
				.append("<property name=\"bidiLayoutOrientation\">ltr</property>\n")
				.append("<property name=\"imageDPI\">96</property>\n")
				.append("<page-setup>\n")
				.append("<simple-master-page name=\"Simple MasterPage\" id=\"2\"/>\n")
				.append("</page-setup>\n")
				.append("<body>\n")
				.append("<label id=\"3\">\n")
				.append("<text-property name=\"text\">Report created by HTTP POST</text-property>\n")
				.append("</label>\n")
				.append("</body>\n")
				.append("</report>").toString();
		String newVersionName = "2.1";
		Integer newVersionCode = 4;
		Boolean newActive = true;
		Date newCreatedOn = null;	// this will be filled in when the ReportVersion is created
		/*
		 * IMPORTANT:
		 * 
		 * This ReportResource instance needs only its "id" field to be defined.
		 * That is the only ReportResource field that needs to be defined when 
		 * creating a new ReportVersion from a ReportVersionResouce.
		 */
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		ReportResource newReportResource = new ReportResource();
		newReportResource.setReportId(uuidOfReport04);

		ReportVersionResource reportVersionResource = new ReportVersionResource();
		reportVersionResource.setReportResource(newReportResource);
		reportVersionResource.setFileName(newFilename);
		reportVersionResource.setRptdesign(newRptdesign);
		reportVersionResource.setVersionName(newVersionName);
		reportVersionResource.setVersionCode(newVersionCode);
		//	private List<ReportParameter> reportParameters;
		//	private List<Subscription> reportSubscriptions;
		reportVersionResource.setActive(newActive);
		reportVersionResource.setCreatedOn(newCreatedOn);
		logger.debug("reportVersionResource = {}", reportVersionResource);

		Response response;

		/*
		 * IMPORTANT:
		 * 
		 * It is necessary here to specify that "report" be expanded; otherwise, 
		 * responseEntity.getReportResource().getReportId() will
		 * be null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though the POST will still correctly create 
		 * the new ReportVersion object. The same basic reason applies to all of
		 * the other "expand" parameters,
		 */
		response = webTarget
				// .path(ResourcePath.REPORTS_PATH)
				.path(ResourcePath.forEntity(ReportVersion.class).getPath())
				// See comments above why these query parameters are necessary here:
				.queryParam(ResourcePath.EXPAND_QP_NAME,
						ResourcePath.REPORTVERSION_EXPAND_PARAM,
						ResourcePath.RPTDESIGN_EXPAND_PARAM,
						ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTPARAMETER_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPost)
				.post(Entity.entity(reportVersionResource, MediaType.APPLICATION_JSON_TYPE));
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

		ReportVersionResource responseEntity = response.readEntity(ReportVersionResource.class);
		logger.debug("responseEntity = {}", responseEntity);
		assertThat(responseEntity, is(not(nullValue())));
		assertThat(responseEntity.getReportResource(), is(not(nullValue())));
		assertThat(responseEntity.getReportResource().getReportId(),
				is(uuidOfReport04));
		assertThat(responseEntity.getRptdesign(), is(newRptdesign));
		assertThat(responseEntity.getRptdesign(), is(newRptdesign));
		assertThat(responseEntity.getVersionName(), is(newVersionName));
		assertThat(responseEntity.getVersionCode(), is(newVersionCode));
		assertThat(responseEntity.isActive(), is(newActive));
		assertThat(responseEntity.getHref(), is(not(nullValue())));
		assertThat(responseEntity.getMediaType(), is(not(nullValue())));
		/*
		 * We test that an id was generated, but we don't know what it will be,
		 * so it does make sense to check that it is "correct".
		 */
		assertThat(responseEntity.getReportVersionId(), is(not(nullValue())));
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
		 * Retrieve the ReportVersionResource associated with the ReportVersion that was just 
		 * created. Its URI should have been returned in the HTTP "Location" 
		 * header.
		 * 
		 * uriAsString will include the query parameters:
		 * "?expand=reportVersion&expand=report" because these are the query
		 * parameters specified for the POST above. Hence, the 
		 * ReportResource resource.getReportResource() will be
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
		ReportVersionResource resource = response.readEntity(ReportVersionResource.class);
		logger.debug("resource = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getReportResource().getReportId(),
				is(uuidOfReport04));
		assertThat(resource.getRptdesign(), is(newRptdesign));
		assertThat(resource.getVersionName(), is(newVersionName));
		assertThat(resource.getVersionCode(), is(newVersionCode));
		assertThat(resource.isActive(), is(newActive));
		assertThat(resource.getHref(), is(not(nullValue())));
		assertThat(resource.getMediaType(), is(not(nullValue())));
		//		assertThat(responseEntity.getReportVersionId(), is(responseEntity.getReportVersionId()));
		millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - resource.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		/*
		 * Check that there is now a ReportVersion in the database 
		 * corresponding to the ReportVersionResource.
		 */
		assertThat(resource.getReportVersionId(), is(not(nullValue())));
		ReportVersion newReportVersion = reportVersionRepository.findOne(resource.getReportVersionId());
		assertThat(newReportVersion, is(not(nullValue())));
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
		 * Details of the ReportVersion to fetch (from test-data.sql).
		 */
		UUID uuidOfReport04Version2 = UUID.fromString("80b14b11-45c7-4a05-99ed-972050f2338f");
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		UUID reportParameterUuid_1 = UUID.fromString("458c5619-5f0e-4218-b0b0-ae02f2174be0");
		UUID reportParameterUuid_2 = UUID.fromString("fc7bcff1-2234-460f-a0cf-d832d5933eaf");
		UUID reportParameterUuid_3 = UUID.fromString("48583e0f-490b-46f3-9fa5-d35dc3ffdc90");
		UUID reportParameterUuid_4 = UUID.fromString("e7d61d7d-c2d2-477c-9abb-0d56d172f392");
		UUID reportParameterUuid_5 = UUID.fromString("bcab7172-a063-46ba-857d-2acaa5e498df");
		UUID reportParameterUuid_6 = UUID.fromString("72478fe3-cb7a-4df9-b8e1-fc05b71195c5");
		UUID reportParameterUuid_7 = UUID.fromString("be77549d-bf1a-4aee-b9aa-78ba633a8358");
		String filename = "400-TestReport04_v0.6.rptdesign";
		String versionName = "0.6";
		Integer versionCode = 2;
		Boolean active = true;
		Date createdOn = DateUtils.dateUtcFromIso8601String("2015-05-06T16:59:00.000Z");

		/*
		 * Retrieve Report that should be linked to the ReportVersion.
		 */
		Report report = reportRepository.findOne(uuidOfReport04);
		assertThat(report, is(not(nullValue())));

		/*
		 * Retrieve the ReportParameter's that should be linked to the ReportVersion.
		 */
		ReportParameter reportParameter_1 = reportParameterRepository.findOne(reportParameterUuid_1);
		ReportParameter reportParameter_2 = reportParameterRepository.findOne(reportParameterUuid_2);
		ReportParameter reportParameter_3 = reportParameterRepository.findOne(reportParameterUuid_3);
		ReportParameter reportParameter_4 = reportParameterRepository.findOne(reportParameterUuid_4);
		ReportParameter reportParameter_5 = reportParameterRepository.findOne(reportParameterUuid_5);
		ReportParameter reportParameter_6 = reportParameterRepository.findOne(reportParameterUuid_6);
		ReportParameter reportParameter_7 = reportParameterRepository.findOne(reportParameterUuid_7);
		assertThat(reportParameter_1, is(not(nullValue())));
		assertThat(reportParameter_2, is(not(nullValue())));
		assertThat(reportParameter_3, is(not(nullValue())));
		assertThat(reportParameter_4, is(not(nullValue())));
		assertThat(reportParameter_5, is(not(nullValue())));
		assertThat(reportParameter_6, is(not(nullValue())));
		assertThat(reportParameter_7, is(not(nullValue())));
		//List<ReportParameter> expectedReportParameters = new ArrayList<>();
		//expectedReportParameters.add(reportParameter_1);
		//expectedReportParameters.add(reportParameter_2);

		/*
		 * Retrieve the ReportVersion that will also be retrieved below by the HTTP
		 * GET ReST request.
		 */
		ReportVersion reportVersion = reportVersionRepository.findOne(uuidOfReport04Version2);
		assertThat(reportVersion, is(not(nullValue())));
		assertThat(reportVersion.getReport(), is(report));
		assertThat(reportVersion.getFileName(), is(filename));
		assertThat(reportVersion.getVersionName(), is(versionName));
		assertThat(reportVersion.getVersionCode(), is(versionCode));
		assertThat(reportVersion.isActive(), is(active));
		assertThat(DateUtils.entityTimestampToNormalDate(reportVersion.getCreatedOn()), is(createdOn));
		assertThat(reportVersion.getReportParameters(), is(not(nullValue())));
		assertThat(reportVersion.getReportParameters(), IsCollectionWithSize.hasSize(7));
		assertThat(reportVersion.getReportParameters(), hasSize(7));
		assertThat(reportVersion.getReportParameters(), IsCollectionContaining.hasItems(reportParameter_1,
				reportParameter_2, reportParameter_3, reportParameter_4, reportParameter_5, reportParameter_6,
				reportParameter_7));

		/*
		 * Create ReportResource from report so we can test that
		 * the ReportVersionResource retrieved by the fetch contains this 
		 * ReportResource.
		 */

		Response response;
		String path;

		path = Paths.get(ResourcePath.forEntity(ReportVersion.class).getPath(), uuidOfReport04Version2.toString())
				.toString();

		/*
		 * Retrieve the ReportVersionResource via HTTP GET.
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "report" for the 
		 * same reason as it is needed above for the "POST" request; otherwise, 
		 * reportResource.getReportResource().getReportId() will
		 * be null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though this GET will still correctly 
		 * retrieve the ReportVersion object. So this is necessary only for this 
		 * integration test to execute successfully.
		 * 
		 * For the same reason, the "reportParameter" "expand" query parameter is
		 * specified so that the attributes of the ReportParameterResource objects
		 * are filled in (so we can use the in asserts).
		 */
		response = webTarget.path(path)
				// See comments above why these query parameters are necessary here:
				.queryParam(ResourcePath.EXPAND_QP_NAME,
						ResourcePath.REPORTVERSION_EXPAND_PARAM,
						ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTPARAMETER_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportVersionResource reportVersionResource = response.readEntity(ReportVersionResource.class);
		assertThat(reportVersionResource, is(not(nullValue())));
		assertThat(reportVersionResource.getFileName(), is(filename));
		assertThat(reportVersionResource.getVersionName(), is(versionName));
		assertThat(reportVersionResource.getVersionCode(), is(versionCode));
		assertThat(reportVersionResource.isActive(), is(active));
		assertThat(DateUtils.entityTimestampToNormalDate(reportVersionResource.getCreatedOn()), is(createdOn));
		ReportResource reportResource = reportVersionResource.getReportResource();
		assertThat(reportResource, is(not(nullValue())));
		assertThat(reportResource.getReportId(), is(uuidOfReport04));
		assertThat(reportVersionResource.getReportParameters(), is(not(nullValue())));

		/*
		 * Assert that the id of each ReportParameterResource object returned in
		 * the ReportVersionResource reportVersionResource object agree with the id's that we
		 * know they should.
		 */
		/*
		 *  This is for the case where reportVersionResource.getReportParameters() is a 
		 *  List<ReportParameterResource> object:
		 */
		//List<UUID> reportParameterResourceUuids=new ArrayList<>(reportVersionResource.getReportParameters().size());
		//for (ReportParameterResource reportParameterResource : reportVersionResource.getReportParameters()) {
		//	reportParameterResourceUuids.add(reportParameterResource.getReportParameterId());
		//}
		/*
		 *  This is for the case where reportVersionResource.getReportParameters() is a 
		 *  ReportParameterCollectionResource object:
		 */
		assertThat(reportVersionResource.getReportParameters().getItems(), is(not(nullValue())));
		List<UUID> reportParameterResourceUuids = new ArrayList<>(reportVersionResource.getReportParameters()
				.getItems().size());
		for (ReportParameterResource reportParameterResource : reportVersionResource.getReportParameters().getItems()) {
			reportParameterResourceUuids.add(reportParameterResource.getReportParameterId());
		}
		assertThat(reportParameterResourceUuids, hasSize(7));
		assertThat(reportParameterResourceUuids,
				IsCollectionContaining.hasItems(reportParameterUuid_1, reportParameterUuid_2, reportParameterUuid_3,
						reportParameterUuid_4, reportParameterUuid_5, reportParameterUuid_6, reportParameterUuid_7));

		/*
		 * Attempt to fetch a ReportVersionResource using an id that does not exist. This
		 * should throw a "404" exception.
		 */
		UUID uuidOfNonExistentReportVersion = UUID.fromString("6c328253-0000-0000-0000-ef38197931b0");

		path = Paths.get(ResourcePath.forEntity(ReportVersion.class).getPath(),
				uuidOfNonExistentReportVersion.toString())
				.toString();

		/*
		 * Attempt to retrieve the nonexistent ReportVersionResource via HTTP GET.
		 */
		response = webTarget.path(path)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.NOT_FOUND.getStatusCode()));
		/*
		 * If an error occurs, then a RestErrorResource is returned as the 
		 * entity, not a ReportVersionResource.
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
		 * This is the ReportVersion to be updated.
		 */
		UUID uuidOfReport04Version1 = UUID.fromString("293abf69-1516-4e9b-84ae-241d25c13e8d");
		ReportVersion reportVersion = reportVersionRepository.findOne(uuidOfReport04Version1);
		assertThat(reportVersion, is(not(nullValue())));

		/*
		 * Details of the ReportVersion to update (from test-data.sql).
		 */
		// Report currentReport = reportVersion.getReport();
		// UUID currentReportUuid = reportVersion.getReport().getReportId();
		UUID currentReportUuid = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");

		String currentFilename = "400-TestReport04_v0.5.rptdesign";
		String currentRptdesign = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
				.append("<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">\n")
				.append("    <property name=\"createdBy\">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>\n")
				.append("    <property name=\"units\">in</property>\n")
				.append("    <property name=\"iconFile\">/templates/blank_report.gif</property>\n")
				.append("    <property name=\"bidiLayoutOrientation\">ltr</property>\n")
				.append("    <property name=\"imageDPI\">96</property>\n")
				.append("    <page-setup>\n")
				.append("        <simple-master-page name=\"Simple MasterPage\" id=\"2\"/>\n")
				.append("    </page-setup>\n")
				.append("</report>").toString();
		String currentVersionName = "0.5";
		Integer currentVersionCode = 1;
		boolean currentActive = true;
		Date currentCreatedOn = DateUtils.dateUtcFromIso8601String("2015-05-06T16:10:01.000Z");

		Report currentReport = reportRepository.findOne(currentReportUuid);
		assertThat(currentReport, is(not(nullValue())));
		assertThat(reportVersion.getReport(), is(currentReport));
		assertThat(reportVersion.getFileName(), is(currentFilename));
		assertThat(reportVersion.getRptdesign(), is(currentRptdesign));
		assertThat(reportVersion.getVersionName(), is(currentVersionName));
		assertThat(reportVersion.getVersionCode(), is(currentVersionCode));
		assertThat(reportVersion.isActive(), is(currentActive));
		assertThat(DateUtils.entityTimestampToNormalDate(reportVersion.getCreatedOn()), is(currentCreatedOn));

		/*
		 * New details that will be used to update the ReportVersion.
		 */
		UUID newReportUuid = currentReportUuid;  // do not link to different parent report
		String newFilename = "400-TestReport04_v0.5.1.rptdesign";
		Report newReport = reportRepository.findOne(newReportUuid);
		String newRptdesign = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
				.append("<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">\n")
				.append("<property name=\"createdBy\">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>\n")
				.append("<property name=\"units\">in</property>\n")
				.append("<property name=\"iconFile\">/templates/blank_report.gif</property>\n")
				.append("<property name=\"bidiLayoutOrientation\">ltr</property>\n")
				.append("<property name=\"imageDPI\">96</property>\n")
				.append("<page-setup>\n")
				.append("<simple-master-page name=\"Simple MasterPage\" id=\"2\"/>\n")
				.append("</page-setup>\n")
				.append("<body>\n")
				.append("<label id=\"3\">\n")
				.append("<text-property name=\"text\">Report updated by HTTP PUT</text-property>\n")
				.append("</label>\n")
				.append("</body>\n")
				.append("</report>").toString();
		String newVersionName = "0.6.1";	// we should probably never change the version name in practice
		Integer newVersionCode = 100;			// we should probably never change the version number in practice
		boolean newActive = false;

		/*
		 * Construct ReportResource for the new Report to assign
		 * to the ReportVersion.
		 * 
		 * IMPORTANT:
		 * 
		 * This ReportResource instance needs only its "id" field 
		 * to be defined. That is the only ReportResource field that 
		 * needs to be defined when updating a ReportVersion from a ReportVersionResouce.
		 */
		ReportResource newReportResource = new ReportResource();
		newReportResource.setReportId(newReportUuid);

		ReportVersionResource reportVersionResource = new ReportVersionResource();
		reportVersionResource.setReportResource(newReportResource);
		reportVersionResource.setFileName(newFilename);
		reportVersionResource.setRptdesign(newRptdesign);
		reportVersionResource.setVersionName(newVersionName);
		reportVersionResource.setVersionCode(newVersionCode);
		reportVersionResource.setActive(newActive);
		logger.debug("reportVersionResource = {}", reportVersionResource);

		/*
		 * It is important here to specify that "report" be expanded;
		 * otherwise, 
		 * resource.getReportResource().getReportId() will
		 * be null and the assert for it below will fail, even though the POST
		 * will still correctly create the new ReportVersion object.
		 */
		String path = Paths
				.get(ResourcePath.forEntity(ReportVersion.class).getPath(), uuidOfReport04Version1.toString())
				.toString();
		logger.debug("path = {}", path);
		Response response = webTarget.path(path)
				/*
				 * These "expand" query parameters will be necessary if this
				 * request ever returns a ReportVersionResource entity in the response
				 * for the same reason as it is necessary in the GET request 
				 * below - see the comments there for an explanation. It won't
				 * hurt to uncomment this line here now, but it is not 
				 * currently necessary to do this.
				 */
				// .queryParam(ResourcePath.EXPAND_QP_NAME, "reportVersion", "report")
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionPut)
				.put(Entity.entity(reportVersionResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));

		/*
		 * Retrieve the ReportVersionResource that was updated via HTTP PUT. 
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "report" for the 
		 * same reason as it is needed above for the "POST" request; otherwise, 
		 * resource.getReportResource().getReportId() will be 
		 * null, since the JSON object returned by the server will not have
		 * included an attribute/value pair for it. As a result, the assert for 
		 * it below will fail, even though the POST will still correctly create 
		 * the new ReportVersion object. Note that this is necessary for this 
		 * integration test to execute successfully; the ReportVersion will be updated
		 * successfully above by the "PUT" request regardless.
		 */
		response = webTarget.path(path)
				// See comments above why these query parameters are necessary here:
				.queryParam(ResourcePath.EXPAND_QP_NAME,
						ResourcePath.REPORTVERSION_EXPAND_PARAM,
						ResourcePath.RPTDESIGN_EXPAND_PARAM,
						ResourcePath.REPORT_EXPAND_PARAM,
						ResourcePath.REPORTCATEGORY_EXPAND_PARAM)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersionGet)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportVersionResource resource = response.readEntity(ReportVersionResource.class);
		logger.debug("resource (updated) = {}", resource);
		assertThat(resource, is(not(nullValue())));
		assertThat(resource.getReportResource(), is(not(nullValue())));
		assertThat(resource.getReportResource().getReportId(), is(newReportUuid));
		assertThat(resource.getFileName(), is(newFilename));
		assertThat(resource.getRptdesign(), is(newRptdesign));
		assertThat(resource.getVersionName(), is(newVersionName));
		assertThat(resource.getVersionCode(), is(newVersionCode));
		assertThat(resource.isActive(), is(newActive));
		assertThat(DateUtils.entityTimestampToNormalDate(resource.getCreatedOn()), is(currentCreatedOn));

		/*
		 * Check that the ReportVersion entity was updated properly. We cannot
		 * simply use "reportVersionRepository.findOne(uuidOfReportVersion)" 
		 * to load the ReportVersion entity because it was updated in the 
		 * Jersey server thread that received the PUT request from above. Hence,
		 * the EntityManager that is managing this thread does not know about
		 * the update and will simply return the old (un-updated) entity. 
		 * Therefore, we need to tell the EntityManager to refresh its copy
		 * before it returns it.
		 * 
		 * Will return old (un-updated) ReportVersion entity:
		 * 
		 * reportVersion = reportVersionRepository.findOne(uuidOfReportVersion);
		 */
		reportVersion = reportVersionRepository.refresh(reportVersion);
		logger.debug("reportVersion (refeshed) = {}", reportVersion);
		assertThat(reportVersion, is(not(nullValue())));
		assertThat(reportVersion.getReport(), is(newReport));
		assertThat(reportVersion.getFileName(), is(newFilename));
		assertThat(reportVersion.getRptdesign(), is(newRptdesign));
		assertThat(reportVersion.getVersionName(), is(newVersionName));
		assertThat(reportVersion.getVersionCode(), is(newVersionCode));
		assertThat(reportVersion.isActive(), is(newActive));
		assertThat(DateUtils.entityTimestampToNormalDate(reportVersion.getCreatedOn()), is(currentCreatedOn));
	}

}
