package com.qfree.obo.report.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.service.ConfigurationService;

@Component
@Path("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	private final ConfigurationService configurationService;

	@Autowired
	public TestController(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

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
		configurationService.set(ParamName.TEST_STRING, "Default value for ParamName.TEST_STRING");
		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
		String stringParam = null;
		if (stringValueDefaultObject instanceof String) {
			stringParam = (String) stringValueDefaultObject;
		}
		logger.info("apiVersion = {}", apiVersion);
		return stringParam;
	}

	@GET
	@Path("/state")
	@Produces("text/plain")
	public String state(@HeaderParam("Accept") String acceptHeader) {
		String apiVersion = ReST.extractAPIVersion(acceptHeader);
		logger.info("apiVersion = {}", apiVersion);
		return "/test/state";
	}

}
