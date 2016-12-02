package com.qfree.bo.report.rest.server;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.ConfigurationRepository;
import com.qfree.bo.report.db.RoleRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.domain.Configuration;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.Configuration.ParamName;
import com.qfree.bo.report.dto.ConfigurationCollectionResource;
import com.qfree.bo.report.dto.ConfigurationResource;
import com.qfree.bo.report.dto.ResourcePath;
import com.qfree.bo.report.dto.RoleResource;
import com.qfree.bo.report.service.ConfigurationService;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.CONFIGURATIONS_PATH)
public class ConfigurationController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

	private final ConfigurationRepository configurationRepository;
	private final RoleRepository roleRepository;
	private final ConfigurationService configurationService;

	@Autowired
	public ConfigurationController(
			ConfigurationRepository configurationRepository,
			RoleRepository roleRepository,
			ConfigurationService configurationService) {
		this.configurationRepository = configurationRepository;
		this.roleRepository = roleRepository;
		this.configurationService = configurationService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_PREFERENCES + "')")
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
		return new ConfigurationCollectionResource(configurations, Configuration.class,
				uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint with create a new Configuration if there is not already one
	 * that matches the specified values for:
	 * 
	 *   - paramName
	 *   - roleId
	 * 
	 * If there is no Configuration that matches these values, this endpoint
	 * creates a new Configuration. Note that it is legal for roleId to be null;
	 * this represents a *global* Configuration that can be overridden with a 
	 * role-specific one, if this is supported for paramName. 
	 * 
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   
	 * Curl examples:
	 * 
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"TEST_DATETIME","paramType":"DATETIME","datetimeValue":"1958-05-06T18:29:59.999Z"}' \
	 *   http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"TEST_DATE","paramType":"DATE","dateValue":"1958-05-06"}' http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"TEST_TIME","paramType":"TIME","timeValue":"18:59:59.999"}' http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"AUTHENTICATION_PROVIDER_URL","paramType":"STRING","stringValue":"http://www.apple.com"}' \
	 *   http://localhost:8080/rest/configurations
	 *   
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"AUTHENTICATION_PROVIDER_HTTP_METHOD","paramType":"STRING","stringValue":"HEAD"}' \
	 *   http://localhost:8080/rest/configurations
	 *   
	 * This should *update* the Configuration (not create a new Configuration)
	 * that was created by the previous curl command:
	 * 
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"AUTHENTICATION_PROVIDER_URL","paramType":"STRING","stringValue":"http://www.vg.no"}' \
	 *   http://localhost:8080/rest/configurations
	 * 
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"paramName":"AUTHENTICATION_PROVIDER_HTTP_METHOD","paramType":"STRING","stringValue":"GET"}' \
	 *   http://localhost:8080/rest/configurations
	 * 
	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	 * Configuration to create is given:
	 * 
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"configurationId":"71b3e8ae-bba8-45b7-a85f-12546bcc95b2",\
	 *   "paramName":"TEST_DATETIME","paramType":"DATETIME","datetimeValue":"1958-05-06T18:29:59.999Z"}' \
	 *   http://localhost:8080/rest/configurations
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_PREFERENCES + "')")
	public Response createOrUpdate(
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

		RestUtils.ifAttrNullThen403(configurationResource.getParamName(), Configuration.class, "paramName");
		//	RestUtils.ifAttrNullThen403(configurationResource.getParamType(), Configuration.class, "paramType");
		ParamName paramName = configurationResource.getParamName();
		//	ParamType paramType = configurationResource.getParamType();

		/*
		 * Retrieve the RoleResource specified by the configurationResource. 
		 * This may be null. However, if the RoleResource is not null, we assume
		 * here that the roleId attribute of this object is set to the id of the
		 * Role that will we associated with the Configuration entity that will
		 * be saved/created here. It is not necessary for any on the other 
		 * RoleResource attributes to have non-null values.
		 */
		Role role = null;
		RoleResource roleResource = configurationResource.getRoleResource();
		logger.debug("roleResource = {}", roleResource);
		if (roleResource != null) {
			UUID roleId = roleResource.getRoleId();
			logger.debug("roleId = {}", roleId);
			if (roleId != null) {
				role = roleRepository.findOne(roleId);
				RestUtils.ifNullThen404(role, Role.class, "roleId", roleId.toString());
			}
		}
		logger.debug("role = {}", role);

		Configuration configuration = null;
		if (role == null) {
			configuration = configurationRepository.findByParamName(paramName);
			logger.debug("role==null:  paramName = {}, configuration = {}", paramName, configuration);
		} else {
			/*
			 * Do *not* call findByParamName(paramName, role) here with 
			 * role=null. This will not work. See comments where this query is
			 * defined in ConfigurationRepository.
			 */
			configuration = configurationRepository.findByParamName(paramName, role);
		}

		if (configuration == null) {
			/*
			 * Create new Configuration.
			 */
			configuration = configurationService.saveNewFromResource(configurationResource);
			ConfigurationResource resource = new ConfigurationResource(configuration, uriInfo, queryParams, apiVersion);
			logger.debug("resource = {}", resource);
			return created(resource);
		} else {
			/*
			 * Update existing Configuration.
			 */
			configurationResource.setConfigurationId(configuration.getConfigurationId());
			configurationResource.setCreatedOn(configuration.getCreatedOn());
			configuration = configurationService.saveExistingFromResource(configurationResource);
			ConfigurationResource resource = new ConfigurationResource(configuration, uriInfo, queryParams, apiVersion);
			logger.debug("resource = {}", resource);
			return ok(resource);
		}
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_PREFERENCES + "')")
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
		ConfigurationResource configurationResource = new ConfigurationResource(configuration, uriInfo, queryParams,
				apiVersion);
		return configurationResource;
	}

}
