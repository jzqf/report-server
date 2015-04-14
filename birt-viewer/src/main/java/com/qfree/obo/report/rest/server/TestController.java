package com.qfree.obo.report.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.qfree.obo.report.configuration.Config;
import com.qfree.obo.report.configuration.Config.ParamName;

/*
 * TODO If I use a @Controller or @Component annotation here and pick up this controller by Spring component scanning:
 *     1. Create a marker interface and add it to the @ComponentScan annotation of the appropriate @Configuration classes.
 *     2. Check whether it is still necessary to register this class in JAXRSConfiguration.
 */
//@Controller
@Path("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Autowired
	Config config;

	@GET
	@Produces("text/plain")
	public String test(@HeaderParam("Accept") String acceptHeader) {
		String apiVersion = ReST.extractAPIVersion(acceptHeader);
		logger.info("apiVersion = {}", apiVersion);
		return "/test";
	}

	@GET
	@Path("/string_param_default")
	@Produces("text/plain")
	public String getTestStringParamDefault(@HeaderParam("Accept") String acceptHeader) {
		String apiVersion = ReST.extractAPIVersion(acceptHeader);
		config.set(ParamName.TEST_STRING, "Default value for ParamName.TEST_STRING");
		Object stringValueDefaultObject = config.get(ParamName.TEST_STRING);
		String stringParam = null;
		if (stringValueDefaultObject instanceof String) {
			stringParam = (String) stringValueDefaultObject;
		}
		logger.info("apiVersion = {}", apiVersion);
		logger.info("config = {}", config);
		return "Default value for TEST_STRING configuration parameter = \"" + stringParam + "\"";
	}

	@GET
	@Path("/state")
	@Produces("text/plain")
	public String state(@HeaderParam("Accept") String acceptHeader) {
		String apiVersion = ReST.extractAPIVersion(acceptHeader);
		return "/test/state";
	}

}
