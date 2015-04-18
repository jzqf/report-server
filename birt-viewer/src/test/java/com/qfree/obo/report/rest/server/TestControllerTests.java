package com.qfree.obo.report.rest.server;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
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

import com.qfree.obo.report.PersistenceConfigTestEnv;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PersistenceConfigTestEnv.class)
@WebIntegrationTest("server.port=0")
/*
 * These integration tests can modify the test database via the ReST call via
 * the HTTP connection to the embedded server that receives the request. They
 * may "dirty" trhe application context in other ways as well. This causes
 * other unit/integration tests in other test classes to fail when using an H2
 * embedded database. The @DirtiesContext annotation here tells Spring to reset 
 * the application context after all tests in the class. In addition, 
 * @DirtiesContext on each test method resets the application context after each
 * test.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TestControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(TestControllerTests.class);

	@Value("${local.server.port}")
	private int port;

	private static Client client = null;
	private WebTarget webTarget = null;

	@BeforeClass
	public static void setUpBeforeClass() {
		System.out.println("@BeforeClass: Setting up JAX-RS Client...");
		client = ClientBuilder.newBuilder()
				//	.register(JsonProcessingFeature.class)
				//	.property(JsonGenerator.PRETTY_PRINTING, true)
				.build();
	}

	@Before
	public void setUp() {
		/*
		 * This WebTarget can be used below in each test to derive a target that
		 * is specific to a particular resource associated with the test. This
		 * cannot go in @BeforeClass method because that is a static method and
		 * "port" is an instance variable.
		 */
		this.webTarget = client.target("http://localhost:" + port + "/rest");
	}

	@Test
	//	@Ignore
	//	@DirtiesContext
	@Transactional
	public void testGetTest() {
		Response response = webTarget
				.path("test")
				.request(MediaType.TEXT_PLAIN_TYPE)
				//	.request(MediaType.APPLICATION_JSON_TYPE);
				//	.header("some-header", "some-value");
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("/test"));
	}

	@Test
	//	@Ignore
	@DirtiesContext
	@Transactional
	public void testGetTestStringParamDefault() {
		Response response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("Default value for ParamName.TEST_STRING"));
	}

	//	/**
	//	 * Used to specify the format expected for the return type of the add call.
	//	 * 
	//	 * TODO Should I make these static?
	//	 */
	//	private final GenericType<Boolean> BOOLEAN_RETURN_TYPE = new GenericType<Boolean>() {
	//	};
	//	private final GenericType<Set<String>> STRING_SET_RETURN_TYPE = new GenericType<Set<String>>() {
	//	};

	//	/**
	//	 * Makes multiple calls to {@link SetResource#addSingle(javax.ws.rs.core.UriInfo, String)} and checks that the
	//	 * return type is correct.
	//	 */
	//	@Test
	//	public void repeatedAddCalls() throws InvalidRequestException {
	//		checkAddCallResponse(performAddCall("MyTest1"), true);
	//		checkAddCallResponse(performAddCall("MyTest1"), false);
	//		checkAddCallResponse(performAddCall("MyTest2"), true);
	//		checkAddCallResponse(performAddCall("MyTest3"), true);
	//		checkAddCallResponse(performAddCall("MyTest2"), false);
	//	}
	//
	//	/**
	//	 * Makes a call to {@link SetResource#addMultiple(Set)} and checks that the items were added.
	//	 */
	//	@Test
	//	public void addMultipleSingleCall() throws InvalidRequestException {
	//		final Set<String> valuesToStore = new HashSet<String>(Arrays.asList("a", "n", "g", "u", "s", "m", "a", "c"));
	//
	//		final Entity<Set<String>> requestBody = Entity.entity(valuesToStore, MediaType.APPLICATION_JSON_TYPE);
	//		target("set/add").request(MediaType.APPLICATION_JSON_TYPE).put(requestBody);
	//
	//		checkGetCallResponse(performGetCall(), valuesToStore);
	//	}
	//
	//	/**
	//	 * Attempt to call <tt>add</tt> but without the required parameter.
	//	 */
	//	@Test
	//	public void addCallNullParameter() {
	//		final Response response = target("set/add").request(MediaType.APPLICATION_JSON_TYPE).get();
	//		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
	//	}
	//
	//	/**
	//	 * Make calls to {@link SetResource#getAll()} and checks that the returned values are as expected.
	//	 */
	//	@Test
	//	public void getCallEmptySet() {
	//		checkGetCallResponse(performGetCall(), new HashSet<String>());
	//	}
	//
	//	@Test
	//	public void getCallSingleValue() {
	//		final Set<String> valuesToStore = new HashSet<String>();
	//
	//		addValuesAndCheckStored(valuesToStore);
	//	}
	//
	//	@Test
	//	public void getCallManyValues() {
	//		final Set<String> valuesToStore = new HashSet<String>(Arrays.asList("a", "n", "g", "u", "s", "m", "a", "c"));
	//		addValuesAndCheckStored(valuesToStore);
	//	}
	//
	//	/**
	//	 * Add the given values to the {@link SetResource} and then check that {@link SetResource#getAll()} returns those
	//	 * values as expected.
	//	 */
	//	private void addValuesAndCheckStored(final Set<String> valuesToStore) {
	//		addValues(valuesToStore);
	//		checkGetCallResponse(performGetCall(), valuesToStore);
	//	}
	//
	//	/**
	//	 * Add the given values to the store by calling the {@link SetResource#addSingle(javax.ws.rs.core.UriInfo, String)}
	//	 * method.
	//	 */
	//	private void addValues(final Set<String> valuesToStore) {
	//		for (final String value : valuesToStore) {
	//			checkAddCallResponse(performAddCall(value), true);
	//		}
	//	}
	//
	//	/**
	//	 * Check that the response to {@link SetResource#getAll()} is as expected.
	//	 */
	//	private void checkGetCallResponse(final Response callResponse, final Set<String> expectedResponse) {
	//		assertEquals(Response.Status.OK.getStatusCode(), callResponse.getStatus());
	//		assertEquals(expectedResponse, callResponse.readEntity(STRING_SET_RETURN_TYPE));
	//	}
	//
	//	/**
	//	 * Manage response. Check that the status code is correct (so we know the call worked), and then check that the
	//	 * value returned is as expected.
	//	 */
	//	private void checkAddCallResponse(final Response responseWrapper, final boolean expectedResponse) {
	//		assertEquals(Response.Status.OK.getStatusCode(), responseWrapper.getStatus());
	//		assertEquals(expectedResponse, responseWrapper.readEntity(BOOLEAN_RETURN_TYPE));
	//	}
	//
	//	/**
	//	 * Execute the {@link SetResource#addSingle(javax.ws.rs.core.UriInfo, String)} call.
	//	 */
	//	private Response performAddCall(final String value) {
	//		final String pathToCall = "set/add/" + value;
	//		return target(pathToCall).request(MediaType.APPLICATION_JSON_TYPE).get();
	//	}
	//
	//	/**
	//	 * Execute the {@link SetResource#addSingle(javax.ws.rs.core.UriInfo, String)} call.
	//	 */
	//	private Response performGetCall() {
	//		final String pathToCall = "set/get";
	//		return target(pathToCall).request(MediaType.APPLICATION_JSON_TYPE).get();
	//	}
}
