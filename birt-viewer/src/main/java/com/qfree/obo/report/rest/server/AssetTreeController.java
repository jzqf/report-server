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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.AssetTreeRepository;
import com.qfree.obo.report.domain.AssetTree;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.dto.AssetTreeCollectionResource;
import com.qfree.obo.report.dto.AssetTreeResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.AssetTreeService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.ASSETTREES_PATH)
public class AssetTreeController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetTreeController.class);

	private final AssetTreeRepository assetTreeRepository;
	private final AssetTreeService assetTreeService;

	@Autowired
	public AssetTreeController(
			AssetTreeRepository assetTreeRepository,
			AssetTreeService assetTreeService) {
		this.assetTreeRepository = assetTreeRepository;
		this.assetTreeService = assetTreeService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/assetTrees?expand=assetTrees
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public AssetTreeCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<AssetTree> assetTrees = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(AssetTree.class, showAll)) {
			assetTrees = assetTreeRepository.findByActiveTrue();
		} else {
			assetTrees = assetTreeRepository.findAll();
		}
		return new AssetTreeCollectionResource(assetTrees, AssetTree.class,
				uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" \-H "Content-Type: application/json" -d \
	 *   '{"name":"New AssetTree name","abbreviation":"NEWASSETTREE","directory":"somedir",\"active":true}'\
	 *    http://localhost:8080/rest/assetTrees
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response create(
			AssetTreeResource assetTreeResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		AssetTree assetTree = assetTreeService.saveNewFromResource(assetTreeResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, AssetTree.class); // Force primary resource to be "expanded"
		//	}
		AssetTreeResource resource = new AssetTreeResource(assetTree, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/assetTrees/272199f9-d407-492f-a147-41a2b7d0cd02?expand=assetTrees
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public AssetTreeResource getById(
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
			addToExpandList(expand, AssetTree.class);
		}
		AssetTree assetTree = assetTreeRepository.findOne(id);
		RestUtils.ifNullThen404(assetTree, AssetTree.class, "assetTreeId", id.toString());
		AssetTreeResource assetTreeResource = new AssetTreeResource(assetTree, uriInfo, queryParams, apiVersion);
		return assetTreeResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X PUT -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"name":"Q-Free (modified)","abbreviation":"QFREE-MOD","directory":"qfree-mod","active":true}' \
	 *   http://localhost:8080/rest/assetTrees/7f9d0216-48d7-49ba-b043-ec48db03c938
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response updateById(
			AssetTreeResource assetTreeResource,
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
		 * Retrieve AssetTree entity to be updated.
		 */
		AssetTree assetTree = assetTreeRepository.findOne(id);
		RestUtils.ifNullThen404(assetTree, AssetTree.class, "assetTreeId", id.toString());

		/*
		 * Treat attributes of parameterGroupResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * parameterGroup entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		if (assetTreeResource.getName() == null) {
			assetTreeResource.setName(assetTree.getName());
		}
		if (assetTreeResource.getAbbreviation() == null) {
			assetTreeResource.setAbbreviation(assetTree.getAbbreviation());
		}
		if (assetTreeResource.getDirectory() == null) {
			assetTreeResource.setDirectory(assetTree.getDirectory());
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the current value stored for
		 * the AssetTree entity.
		 */
		assetTreeResource.setAssetTreeId(assetTree.getAssetTreeId());
		assetTreeResource.setCreatedOn(assetTree.getCreatedOn());

		/*
		 * Save updated entity.
		 */
		assetTree = assetTreeService.saveExistingFromResource(assetTreeResource);

		return Response.status(Response.Status.OK).build();
	}

}
