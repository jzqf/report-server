package com.qfree.obo.report.rest.server;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qfree.obo.report.db.PersistenceConfigTestEnv;

/**
 * 
 * @author Jeffrey Zelt
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
//public class TestControllerTest extends JerseyTest {
public class TestControllerTest {

	private static final Logger logger = LoggerFactory.getLogger(TestControllerTest.class);

	//	@Autowired
	//	Config config;

	//	/**
	//	 * Used to specify the format expected for the return type of the add call.
	//	 * 
	//	 * TODO Should I make these static?
	//	 */
	//	private final GenericType<Boolean> BOOLEAN_RETURN_TYPE = new GenericType<Boolean>() {
	//	};
	//	private final GenericType<Set<String>> STRING_SET_RETURN_TYPE = new GenericType<Set<String>>() {
	//	};

	//	@Override
	//	protected Application configure() {
	//		//		return new JAXRSConfiguration();
	//		//		logger.info("config = {}", config);
	//
	//		ResourceConfig rc = new JAXRSConfiguration();
	//		/*
	//		 * For a reference on these settings see:
	//		 * 
	//		 * http://stackoverflow.com/questions/10607168/unit-testing-with-spring-and-the-jersey-test-framework
	//		 */
	//		//		rc.register(SpringLifecycleListener.class); // org.glassfish.jersey.server.spring.SpringLifecycleListener
	//		//		rc.register(RequestContextFilter.class);    // org.glassfish.jersey.server.spring.scope.RequestContextFilter
	//		//		rc.register(ContextLoaderListener.class);
	//		//		rc.property("contextConfigLocation", "");
	//		//		rc.property("contextConfigLocation", "classpath:XXXapplicationContext.xml");
	//		//		rc.property("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
	//		//		rc.property("contextConfigLocation", "com.qfree.obo.report.db.PersistenceConfigTestEnv");//TODO Derive this from the class object PersistenceConfigTestEnv.class!
	//		//		rc.property("javax.ws.rs.Application", "com.qfree.obo.report.rest.server.JAXRSConfiguration");
	//		//		rc.register(TestController.class);
	//		//		rc.register(Config.class);
	//		//		rc.registerInstances(config);
	//		//		rc.packages("com.qfree.obo.report.configuration");
	//		//				.property(
	//		//						"contextConfigLocation",
	//		//						"classpath:applicationContext.xml"
	//		//				);
	//		return rc;
	//
	//		//		ApplicationContext context = new AnnotationConfigApplicationContext(PersistenceConfigTestEnv.class);
	//		//		logger.info("Created ApplicationContext context.");
	//		//		TestController testController = context.getBean(TestController.class);
	//		//		logger.info("TestController testController bean.");
	//		//		Config config = context.getBean(Config.class);
	//		//		//		ResourceConfig rc = new JAXRSConfiguration().registerInstances(testController, config);
	//		//		ResourceConfig rc = new JAXRSConfiguration().registerInstances(testController);
	//		//		//		rc = rc.register(config);
	//		//		logger.info("Registered testController.");
	//		//		//		ResourceConfig rc = new JAXRSConfiguration().register(context);
	//		//		return rc;
	//
	//		//		return new ResourceConfig(TestController.class);
	//		//			return new JAXRSConfiguration(TestController.class);
	//		return new ResourceConfig(TestController.class);
	//	}

	@Test
	@Ignore
	public void testGetTest() {
		logger.info("Enter method.");
		//		//		//		final Response response = target("set/add").request(MediaType.APPLICATION_JSON_TYPE).get();
		//		final Response response = target("test").request(MediaType.TEXT_PLAIN).get();
		//		String responseString = response.readEntity(String.class);
		//		logger.info("responseString = {}", responseString);
		//		//		//		assertEquals(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatus());
		//		//
		//		assertThat(responseString, is("/test"));
	}

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
