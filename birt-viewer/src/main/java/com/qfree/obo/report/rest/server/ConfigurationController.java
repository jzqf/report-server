package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.ConfigurationRepository;
import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.dto.ConfigurationCollectionResource;
import com.qfree.obo.report.dto.ConfigurationResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ConfigurationService;

@Component
@Path(ResourcePath.CONFIGURATIONS_PATH)
public class ConfigurationController extends AbstractBaseController {

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
	//	public List<ConfigurationResource> getList(
	public ConfigurationCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Configuration> configurations = configurationRepository.findAll();
		List<ConfigurationResource> configurationResources = new ArrayList<>(configurations.size());
		for (Configuration configuration : configurations) {
			configurationResources.add(new ConfigurationResource(configuration, uriInfo, expand, apiVersion));
		}
		//		return configurationResources;
		return new ConfigurationCollectionResource(configurationResources, Configuration.class, uriInfo, expand,
				apiVersion);
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ConfigurationResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		addToExpandList(expand, Configuration.class);	// Force primary resource to be "expanded"
		Configuration configuration = configurationRepository.findOne(id);
		RestUtils.ifNullThen404(configuration, Configuration.class, "configurationId", id.toString());
		ConfigurationResource configurationResource =
				new ConfigurationResource(configuration, uriInfo, expand, apiVersion);
		return configurationResource;
	}

}
