package com.qfree.bo.report.rest.server;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

import com.qfree.bo.report.ApplicationConfig;

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
public class TestControllerTests {

	private static final Logger logger = LoggerFactory.getLogger(TestControllerTests.class);

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
	//	@DirtiesContext
	@Transactional(readOnly = true)
	public void testApiVersionGet() {
		/* This is the default version for the endpoint "test/api_version" 
		 * using HTTP GET.
		 */
		String defaultVersion = "2";
		Response response;

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("1"));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";application&v=1")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("1"));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=5")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("5"));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";application&v=5")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("5"));

		/*
		 * The version specifier "v" is case-insensitive.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";V=5")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("5"));

		/*
		 * If no version is specified, the default should be returned.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		/*
		 * Version 99 does not exist. The default version will be used.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=99")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";application&v=99")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		/*
		 * "vv=4" is not the correct syntax to specify the version. The default 
		 * version will be used.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";vv=4")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";application&vv=4")
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));
	}

	@Test
	//	@DirtiesContext
	@Transactional(readOnly = true)
	public void testApiVersionPost() {
		/* This is the default version for the endpoint "test/api_version" 
		 * using HTTP POST.
		 */
		String defaultVersion = "3";
		Response response;
		Form form = new Form();

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("1"));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=2")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("2"));

		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=5")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("5"));

		/*
		 * The version specifier "v" is case-insensitive.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";V=5")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("5"));

		/*
		 * If no version is specified, the default should be returned.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN)
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		/*
		 * Version 99 does not exist. The default version will be used.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=99")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));

		/*
		 * "vv=4" is not the correct syntax to specify the version. The default 
		 * version will be used.
		 */
		response = webTarget.path("test/api_version")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";vv=4")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(defaultVersion));
	}

	@Test
	//	@DirtiesContext
	@Transactional(readOnly = true)
	public void testFormPostProduceText() {
		Form form = new Form();
		String param1 = "param1 value";
		String param2 = "param2 value";
		form.param("param1", param1);
		form.param("param2", param2);

		Response response;

		response = webTarget.path("test/form")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("(" + param1 + ", " + param2 + "): v1"));

		response = webTarget.path("test/form")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=99")  // default version is "v2"
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is("(" + param1 + ", " + param2 + "): v2"));
	}

	@Test
	//	@DirtiesContext	//	@Ignore
	@Transactional(readOnly = true)
	public void testGetTestStringParamDefault() {
		String expected = "Meaning of life";
		Response response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(expected));
	}

	@Test
	@Ignore
	@DirtiesContext
	@Transactional
	public void testGetPostGetTestStringParamDefault() {
		String expectedInitialValue = "Meaning of life";
		String newValue = "New default value for ParamName.TEST_STRING";
		Response response;

		/*
		 * "GET" current default value
		 */
		response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(expectedInitialValue));

		/*
		 * "POST" new default value
		 */
		Form form = new Form();
		form.param("paramValue", newValue);
		response = webTarget.path("test/string_param_default")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1")
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(newValue));

		/*
		 * "GET" updated default value
		 */
		response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(newValue));
	}

	@Test
	@DirtiesContext
	@Transactional
	public void testGetPutGetTestStringParamDefault() {
		String expectedInitialValue = "Meaning of life";
		String newValue = "New default value for ParamName.TEST_STRING";
		Response response;

		/*
		 * "GET" current default value
		 */
		response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(expectedInitialValue));

		/*
		 * "PUT" new default value
		 */
		response = webTarget.path("test/string_param_default")
				.request()
				.header("Accept", MediaType.TEXT_PLAIN + ";v=1")
				.put(Entity.entity(newValue, MediaType.TEXT_PLAIN_TYPE));
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(newValue));

		/*
		 * "GET" updated default value
		 */
		response = webTarget
				.path("test/string_param_default")
				.request(MediaType.TEXT_PLAIN_TYPE)
				.get();
		assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
		assertThat(response.readEntity(String.class), is(newValue));
	}


	//TODO Test also setting Integer/Long values, Boolean values, date, time datetime, ...


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
	//		assertThat(response.getStatus(),is (Response.Status.METHOD_NOT_ALLOWED.getStatusCode()));
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
	//		assertThat(callResponse.getStatus(), is(Response.Status.OK.getStatusCode()));
	//		assertThat(callResponse.readEntity(STRING_SET_RETURN_TYPE), is(expectedResponse));
	//	}
	//
	//	/**
	//	 * Manage response. Check that the status code is correct (so we know the call worked), and then check that the
	//	 * value returned is as expected.
	//	 */
	//	private void checkAddCallResponse(final Response responseWrapper, final boolean expectedResponse) {
	//		assertThat(responseWrapper.getStatus(), is(Response.Status.OK.getStatusCode()));
	//		assertThat(responseWrapper.readEntity(BOOLEAN_RETURN_TYPE), is(expectedResponse));
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
