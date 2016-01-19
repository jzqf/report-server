package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import com.qfree.obo.report.db.AssetRepository;
import com.qfree.obo.report.domain.Asset;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.dto.AssetCollectionResource;
import com.qfree.obo.report.dto.AssetResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.AssetService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.ASSETS_PATH)
public class AssetController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

	private final AssetRepository assetRepository;
	private final AssetService assetService;

	@Autowired
	public AssetController(
			AssetRepository assetRepository,
			AssetService assetService) {
		this.assetRepository = assetRepository;
		this.assetService = assetService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/assets?expand=assets
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown (the Asset's "document" field has "fetch = FetchType.LAZY").
	 */
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public AssetCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Asset> assets = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Asset.class, showAll)) {
			assets = assetRepository.findByActiveTrue();
		} else {
			assets = assetRepository.findAll();
		}
		return new AssetCollectionResource(assets, Asset.class,
				uriInfo, queryParams, apiVersion);
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	//	 *   -iH "Accept: application/json;v=1" \-H "Content-Type: application/json" -d \
	//	 *   '{"name":"New Asset name","abbreviation":"NEWASSET","directory":"somedir",\"active":true}'\
	//	 *    http://localhost:8080/rest/assets
	//	 */
	//	@POST
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Transactional
	//	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	//	public Response create(
	//			AssetResource assetResource,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		Asset asset = assetService.saveNewFromResource(assetResource);
	//		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//		addToExpandList(expand, Asset.class); // Force primary resource to be "expanded"
	//		//	}
	//		AssetResource resource = new AssetResource(asset, uriInfo, queryParams, apiVersion);
	//		return created(resource);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/assets/272199f9-d407-492f-a147-41a2b7d0cd02?expand=assets
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown (the Asset's "document" field has "fetch = FetchType.LAZY").
	 */
	@Path("/{id}")
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public AssetResource getById(
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
			addToExpandList(expand, Asset.class);
		}
		Asset asset = assetRepository.findOne(id);
		RestUtils.ifNullThen404(asset, Asset.class, "assetId", id.toString());
		AssetResource assetResource = new AssetResource(asset, uriInfo, queryParams, apiVersion);
		return assetResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X PUT -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"filename":"new file name.png",\
	 *   "assetTree":{"assetTreeId": "7f9d0216-48d7-49ba-b043-ec48db03c938"},\
	 *   "assetType": {"assetTypeId": "1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8"},\
	 *   "document": {"documentId": "fbed70fe-c9b9-4b80-b827-f9e2f05ba91f"}}' \
	 *   http://localhost:8080/rest/assets/1c72d7d7-87a7-4980-89f4-4590b1fe7a09
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response updateById(
			AssetResource assetResource,
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
		 * Retrieve Asset entity to be updated.
		 */
		Asset asset = assetRepository.findOne(id);
		RestUtils.ifNullThen404(asset, Asset.class, "assetId", id.toString());

		/*
		 * Treat attributes of parameterGroupResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * parameterGroup entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		if (assetResource.getFilename() == null) {
			assetResource.setFilename(asset.getFilename());
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the current value stored for
		 * the Asset entity.
		 */
		assetResource.setAssetId(asset.getAssetId());
		assetResource.setCreatedOn(asset.getCreatedOn());

		/*
		 * Save updated entity.
		 */
		asset = assetService.saveExistingFromResource(assetResource);

		return Response.status(Response.Status.OK).build();
	}

}
