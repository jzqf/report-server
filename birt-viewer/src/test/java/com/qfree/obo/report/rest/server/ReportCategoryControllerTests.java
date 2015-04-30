package com.qfree.obo.report.rest.server;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.dto.ReportCategoryResource;
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
public class ReportCategoryControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryControllerTests.class);

	//	@Autowired
	//	private ConfigurationService configurationService;

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
		 * AbstractResource.REPORTCATEGORIES_PATH  using HTTP POST.
		 */
		String defaultVersion = "1";

		String newAbbreviation = "NEWRCABBREV";
		String newDescription = "New report category description";
		Response response;

		ReportCategoryResource reportCategoryResource = new ReportCategoryResource();
		reportCategoryResource.setAbbreviation(newAbbreviation);
		reportCategoryResource.setDescription(newDescription);
		reportCategoryResource.setActive(true);
		//		logger.info("reportCategoryResource = {}", reportCategoryResource);

		response = webTarget.path(ResourcePath.REPORTCATEGORIES_PATH)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersion)
				.post(Entity.entity(reportCategoryResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

		/*
		 * The HTTP "Location" header should have been set in the response. It
		 * should contain the URI of the resource created. The resource at this
		 * URI is loaded below for additional tests.
		 */
		MultivaluedMap<String, Object> headers = response.getHeaders();
		//		logger.info("headers = {}", headers);
		List<Object> createdEntityLocations = headers.get("Location");
		assertThat(createdEntityLocations, is(not(nullValue())));
		assertThat(createdEntityLocations.size(), is(greaterThan(0)));

		ReportCategoryResource responseEntity = response.readEntity(ReportCategoryResource.class);
		assertThat(responseEntity, is(not(nullValue())));
		assertThat(responseEntity.getAbbreviation(), is(newAbbreviation));
		assertThat(responseEntity.getDescription(), is(newDescription));
		assertThat(responseEntity.getActive(), is(true));
		/*
		 * Assert that the "CreatedOn" datetime is within 5 minutes of the
		 * current time in this process. Ideally,they should be much, much
		 * closer, but this at least is a sanity check that the "CreatedOn"
		 * datetime is actually getting set. We don't want this to fail unless
		 * there is a significant difference; otherwise, this could cause 
		 * problems with continuous integration and automatic builds.
		 */
		//		logger.info(" DateUtils.nowUtc() = {}, responseEntity.getCreatedOn() = {}", DateUtils.nowUtc(), responseEntity.getCreatedOn());
		long millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - responseEntity.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		assertThat(responseEntity.getHref(), is(not(nullValue())));
		//		logger.info("responseEntity.getReportCategoryId() = {}", responseEntity.getReportCategoryId());
		/*
		 * We test that an id was generated, but we don't know what it will be,
		 * so it does make sense to check that it is "correct".
		 */
		assertThat(responseEntity.getReportCategoryId(), is(not(nullValue())));

		/*
		 * Load the ReportCategoryResource that was created. Its URI should have
		 * been returned in the HTTP "Location" header.
		 */
		String uriAsString;
		uriAsString = createdEntityLocations.get(0).toString();
		//		logger.info("uriAsString =) {}", uriAsString);
		response = client.target(uriAsString)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersion)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportCategoryResource resource = response.readEntity(ReportCategoryResource.class);
		//		logger.info("resource = {}", resource);

		/*
		 * Check that there is now a ReportCategory in the database 
		 * corresponding to the ReportCategoryResource.
		 */
	}

	@Test
	@DirtiesContext
	@Transactional
	public void testUpdateByPut() {
		/* This is the default version for the endpoint 
		 * AbstractResource.REPORTCATEGORIES_PATH  using HTTP POST.
		 */
		String defaultVersion = "1";

		/*
		 * Retails of ReportCategory to update.
		 */
		UUID uuidOfReportCategory = UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0");
		String currentAbbreviation = "QFREE";
		String currentDescription = "Q-Free internal";
		//		Date currentCreatedOn =

		String newAbbreviation = "NEWRCABBREV";
		String newDescription = "New report category description";
		Response response;

		ReportCategoryResource reportCategoryResource = new ReportCategoryResource();
		reportCategoryResource.setAbbreviation(newAbbreviation);
		reportCategoryResource.setDescription(newDescription);
		reportCategoryResource.setActive(true);
		//		logger.info("reportCategoryResource = {}", reportCategoryResource);

		response = webTarget.path(ResourcePath.REPORTCATEGORIES_PATH)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersion)
				.post(Entity.entity(reportCategoryResource, MediaType.APPLICATION_JSON_TYPE));
		assertThat(response.getStatus(), is(Response.Status.CREATED.getStatusCode()));

		/*
		 * The HTTP "Location" header should have been set in the response. It
		 * should contain the URI of the resource created. The resource at this
		 * URI is loaded below for additional tests.
		 */
		MultivaluedMap<String, Object> headers = response.getHeaders();
		//		logger.info("headers = {}", headers);
		List<Object> createdEntityLocations = headers.get("Location");
		assertThat(createdEntityLocations, is(not(nullValue())));
		assertThat(createdEntityLocations.size(), is(greaterThan(0)));

		ReportCategoryResource responseEntity = response.readEntity(ReportCategoryResource.class);
		assertThat(responseEntity, is(not(nullValue())));
		assertThat(responseEntity.getAbbreviation(), is(newAbbreviation));
		assertThat(responseEntity.getDescription(), is(newDescription));
		assertThat(responseEntity.getActive(), is(true));
		/*
		 * Assert that the "CreatedOn" datetime is within 5 minutes of the
		 * current time in this process. Ideally,they should be much, much
		 * closer, but this at least is a sanity check that the "CreatedOn"
		 * datetime is actually getting set. We don't want this to fail unless
		 * there is a significant difference; otherwise, this could cause 
		 * problems with continuous integration and automatic builds.
		 */
		//		logger.info(" DateUtils.nowUtc() = {}, responseEntity.getCreatedOn() = {}", DateUtils.nowUtc(), responseEntity.getCreatedOn());
		long millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - responseEntity.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));

		assertThat(responseEntity.getHref(), is(not(nullValue())));
		//		logger.info("responseEntity.getReportCategoryId() = {}", responseEntity.getReportCategoryId());
		/*
		 * We test that an id was generated, but we don't know what it will be,
		 * so it does make sense to check that it is "correct".
		 */
		assertThat(responseEntity.getReportCategoryId(), is(not(nullValue())));

		/*
		 * Load the ReportCategoryResource that was created. Its URI should have
		 * been returned in the HTTP "Location" header.
		 */
		String uriAsString;
		uriAsString = createdEntityLocations.get(0).toString();
		//		logger.info("uriAsString =) {}", uriAsString);
		response = client.target(uriAsString)
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + defaultVersion)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		ReportCategoryResource resource = response.readEntity(ReportCategoryResource.class);
		//		logger.info("resource = {}", resource);

		/*
		 * Check that there is now a ReportCategory in the database 
		 * corresponding to the ReportCategoryResource.
		 */
	}

}
