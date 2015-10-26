package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ConfigurationRepository;
import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.dto.ConfigurationCollectionResource;
import com.qfree.obo.report.dto.ConfigurationResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.ConfigurationService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

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
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Configuration> configurations = configurationRepository.findAll();
		List<ConfigurationResource> configurationResources = new ArrayList<>(configurations.size());
		for (Configuration configuration : configurations) {
			configurationResources.add(new ConfigurationResource(configuration, uriInfo, queryParams, apiVersion));
		}
		//		return configurationResources;
		return new ConfigurationCollectionResource(configurationResources, Configuration.class, uriInfo, queryParams,
				apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   
	 * Curl examples:
	 * 
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"paramName":"TEST_DATETIME","paramType":"DATETIME","datetimeValue":"1958-05-06T18:29:59.999Z"}' \
	 *   http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"paramName":"TEST_DATE","paramType":"DATE","dateValue":"1958-05-06"}' http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"paramName":"TEST_TIME","paramType":"TIME","timeValue":"18:59:59.999"}' http://localhost:8080/rest/configurations
	 * 
	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	 * Configuration to create is given:
	 * 
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"configurationId":"71b3e8ae-bba8-45b7-a85f-12546bcc95b2",\
	 *   "paramName":"TEST_DATETIME","paramType":"DATETIME","datetimeValue":"1958-05-06T18:29:59.999Z"}' \
	 *   http://localhost:8080/rest/configurations
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response create(
			ConfigurationResource configurationResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.debug("configurationResource = {}", configurationResource);

		Configuration configuration = configurationService
				.saveNewFromResource(configurationResource);

		logger.debug("configuration = {}", configuration);

		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Configuration.class);// Force primary resource
														// to be "expanded"
		// }

		ConfigurationResource newResource = new ConfigurationResource(configuration, uriInfo, queryParams,
				apiVersion);
		logger.debug("newResource = {}", newResource);

		return created(newResource);
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ConfigurationResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Configuration.class);
		}
		Configuration configuration = configurationRepository.findOne(id);
		RestUtils.ifNullThen404(configuration, Configuration.class, "configurationId", id.toString());
		ConfigurationResource configurationResource =
				new ConfigurationResource(configuration, uriInfo, queryParams, apiVersion);
		return configurationResource;
	}

}
