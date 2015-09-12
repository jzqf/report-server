package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.qfree.obo.report.db.ParameterGroupRepository;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.dto.ParameterGroupResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ParameterGroupService;

@Component
@Path(ResourcePath.PARAMETERGROUPS_PATH)
public class ParameterGroupController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ParameterGroupController.class);

	private final ParameterGroupRepository parameterGroupRepository;
	private final ParameterGroupService parameterGroupService;

	@Autowired
	public ParameterGroupController(
			ParameterGroupRepository parameterGroupRepository,
			ParameterGroupService parameterGroupService) {
		this.parameterGroupRepository = parameterGroupRepository;
		this.parameterGroupService = parameterGroupService;
	}

	// /*
	// * This endpoint can be tested with:
	// *
	// * $ mvn clean spring-boot:run
	// * $ curl -i -H "Accept: application/json;v=1" -X GET \
	// * http://localhost:8080/rest/parameterGroups?expand=parameterGroups
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// // public List<ParameterGroupResource> getList(
	// public ParameterGroupCollectionResource getList(
	// @HeaderParam("Accept") final String acceptHeader,
	// @QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	// @QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	// @Context final UriInfo uriInfo) {
	// Map<String, List<String>> queryParams = new HashMap<>();
	// queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	// queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	// RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader,
	// RestApiVersion.v1);
	//
	// List<ParameterGroup> parameterGroups = null;
	// parameterGroups = parameterGroupRepository.findAll();
	// List<ParameterGroupResource> parameterGroupResources = new
	// ArrayList<>(parameterGroups.size());
	// for (ParameterGroup parameterGroup : parameterGroups) {
	// parameterGroupResources.add(new ParameterGroupResource(parameterGroup,
	// uriInfo, queryParams, apiVersion));
	// }
	// // return parameterGroupResources;
	// return new ParameterGroupCollectionResource(parameterGroupResources,
	// ParameterGroup.class, uriInfo,
	// queryParams, apiVersion);
	// }

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"name":"ParameterGroup name","promptText":"Group prompt text",\
	 *   "groupType":4, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *   http://localhost:8080/rest/parameterGroups
	 * 
	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	 * ParameterGroup to create is given:
	 * 
	 *	curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *	'{"name":"ParameterGroup name","promptText":"Group prompt text","groupType":4}' \
	 *	http://localhost:8080/rest/parameterGroups
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response create(
			ParameterGroupResource parameterGroupResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ParameterGroup parameterGroup = parameterGroupService.saveNewFromResource(parameterGroupResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, ParameterGroup.class);// Force primary resource to be "expanded"
		//	}
		
		ParameterGroupResource resource = new ParameterGroupResource(parameterGroup, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/parameterGroups/7a482694-51d2-42d0-b0e2-19dd13bbbc64\
	 *   ?expand=parameterGroups
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ParameterGroupResource getById(
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
			addToExpandList(expand, ParameterGroup.class);
		}
		ParameterGroup parameterGroup = parameterGroupRepository.findOne(id);
		RestUtils.ifNullThen404(parameterGroup, ParameterGroup.class, "parameterGroupId", id.toString());
		ParameterGroupResource parameterGroupResource = new ParameterGroupResource(parameterGroup, uriInfo, queryParams,
				apiVersion);
		return parameterGroupResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"name":"ParameterGroup name (modified)","promptText":"Group prompt text","groupType":4}' \
	 *   http://localhost:8080/rest/parameterGroups/bb2bc482-c19a-4c19-a087-e68ffc62b5a0
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response updateById(
			ParameterGroupResource parameterGroupResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve ParameterGroup entity to be updated.
		 */
		ParameterGroup parameterGroup = parameterGroupRepository.findOne(id);
		RestUtils.ifNullThen404(parameterGroup, ParameterGroup.class, "parameterGroupId", id.toString());

		/*
		 * Treat attributes of parameterGroupResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * parameterGroup entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		if (parameterGroupResource.getGroupType() == null) {
			parameterGroupResource.setGroupType(parameterGroup.getGroupType());
		}
		if (parameterGroupResource.getPromptText() == null) {
			parameterGroupResource.setPromptText(parameterGroup.getPromptText());
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the current value stored for
		 * the parameterGroup entity.
		 */
		parameterGroupResource.setParameterGroupId(parameterGroup.getParameterGroupId());
		parameterGroupResource.setName(parameterGroup.getName());
		parameterGroupResource.setCreatedOn(parameterGroup.getCreatedOn());

		/*
		 * Save updated entity.
		 */
		parameterGroup = parameterGroupService.saveExistingFromResource(parameterGroupResource);

		return Response.status(Response.Status.OK).build();
	}

}
