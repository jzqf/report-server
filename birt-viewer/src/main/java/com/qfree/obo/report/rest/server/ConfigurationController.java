package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.ConfigurationRepository;
import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.resource.AbstractResource;
import com.qfree.obo.report.resource.ConfigurationResource;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ConfigurationService;

@Component
@Path(AbstractResource.CONFIGURATIONS_PATH)
public class ConfigurationController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	private final ConfigurationRepository configurationRepository;
	private final ConfigurationService configurationService;

	@Autowired
	public ConfigurationController(
			ConfigurationRepository configurationRepository,
			ConfigurationService configurationService) {
		this.configurationRepository = configurationRepository;
		this.configurationService = configurationService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ConfigurationResource> list(
			@HeaderParam("Accept") String acceptHeader,
			@Context UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Configuration> configurations = configurationRepository.findAll();
		List<ConfigurationResource> configurationResources = new ArrayList<>();
		for (Configuration configuration : configurations) {
			configurationResources.add(new ConfigurationResource(uriInfo, configuration));
		}
		return configurationResources;
	}

	//	@GET
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public ConfigurationResource getConfiguration(@HeaderParam("Accept") String acceptHeader) {
	//		return new ConfigurationResource(
	//				null, new Date(), null, ParamName.TEST_STRING, "Some bloody value");
	//	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ConfigurationResource getOne(
			@PathParam("id") UUID id,
			@HeaderParam("Accept") String acceptHeader,
			@Context UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Configuration configuration = configurationRepository.findOne(id);
		ConfigurationResource configurationResource = new ConfigurationResource(uriInfo, configuration);
		return configurationResource;
	}

	//	@GET
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String test(@HeaderParam("Accept") String acceptHeader) {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		switch (apiVersion) {
	//		case v1:
	//			/*
	//			 * Code for API v1:
	//			 */
	//			break;
	//		default:
	//			/*
	//			 * Code for default API version as well as unrecognized version from 
	//			 * "Accept" header:
	//			 */
	//		}
	//		return "/test endpoint: API version " + apiVersion.getVersion();
	//	}
	//
	//	@GET
	//	@Path("/api_version")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String acceptHeaderApiVersionGet(@HeaderParam("Accept") String acceptHeader) {
	//		//		logger.info("acceptHeader = {}", acceptHeader);
	//		//		System.out.println("acceptHeaderApiVersionGet: acceptHeader = " + acceptHeader);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
	//		//		System.out.println("acceptHeaderApiVersionGet: apiVersion, apiVersion.getVersion() = "
	//		//				+ apiVersion + ", " + apiVersion.getVersion());
	//		return apiVersion.getVersion();
	//	}
	//
	//	@POST
	//	@Path("/api_version")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String acceptHeaderApiVersionPost(@HeaderParam("Accept") String acceptHeader) {
	//		//		logger.info("acceptHeader = {}", acceptHeader);
	//		//		System.out.println("acceptHeaderApiVersionPost: acceptHeader = " + acceptHeader);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v3);
	//		//		logger.info("apiVersion, apiVersion.getVersion() = {}, {}", apiVersion, apiVersion.getVersion());
	//		//		System.out.println("acceptHeaderApiVersionPost: apiVersion, apiVersion.getVersion() = "
	//		//				+ apiVersion + ", " + apiVersion.getVersion());
	//		return apiVersion.getVersion();
	//	}
	//
	//	@POST
	//	@Path("/form")
	//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String formPostProduceText(
	//			@HeaderParam("Accept") String acceptHeader,
	//			@FormParam("param1") String param1,
	//			@FormParam("param2") String param2) {
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		return "(" + param1 + ", " + param2 + "): " + apiVersion;
	//	}
	//
	//	@GET
	//	@Path("/string_param_default")
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String getTestStringParamDefault(@HeaderParam("Accept") String acceptHeader) {
	//		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		//		Object stringValueDefaultObject = configurationService.get(ParamName.TEST_STRING);
	//		//		String stringParam = null;
	//		//		if (stringValueDefaultObject instanceof String) {
	//		//			stringParam = (String) stringValueDefaultObject;
	//		//		}
	//		//		return stringParam;
	//		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	//	}
	//
	//	@GET
	//	@Path("/string_param_default")
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public String getTestStringParamDefaultAsJson(@HeaderParam("Accept") String acceptHeader) {
	//		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		String stringValue = configurationService.get(ParamName.TEST_STRING, null, String.class);
	//
	//
	//		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	//		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	//		//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	//
	//
	//		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	//	}
	//
	//	@POST
	//	@Path("/string_param_default")
	//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String postTestStringParamDefault(
	//			@HeaderParam("Accept") String acceptHeader,
	//			@FormParam("paramValue") String newParamValue) {
	//		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		/*
	//		 * Update parameter's default value.
	//		 */
	//		configurationService.set(ParamName.TEST_STRING, newParamValue);
	//		/*
	//		 * Return updated value.
	//		 */
	//		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	//	}
	//
	//	@PUT
	//	@Path("/string_param_default")
	//	@Consumes(MediaType.TEXT_PLAIN)
	//	@Produces(MediaType.TEXT_PLAIN)
	//	public String putTestStringParamDefault(
	//			@HeaderParam("Accept") String acceptHeader,
	//			String newParamValue) {
	//		//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v2);
	//		/*
	//		 * Update parameter's default value.
	//		 */
	//		configurationService.set(ParamName.TEST_STRING, newParamValue);
	//		/*
	//		 * Return updated value.
	//		 */
	//		return configurationService.get(ParamName.TEST_STRING, null, String.class);
	//	}
	//
	//	//TODO USE @PUT TO RETURN A JSON object?????????????????
	//
	//	//TODO USE @PUT TO accept a JSON object, e.g., a new Configuration and then later a new Role?
	//	//		Insert into DB and then RETURN A JSON object?????????????????

}
