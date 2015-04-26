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

}
