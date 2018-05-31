package com.qfree.bo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
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

import com.qfree.bo.report.db.AssetTypeRepository;
import com.qfree.bo.report.domain.AssetType;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.dto.AssetTypeCollectionResource;
import com.qfree.bo.report.dto.AssetTypeResource;
import com.qfree.bo.report.dto.ResourcePath;
import com.qfree.bo.report.dto.RestErrorResource.RestError;
import com.qfree.bo.report.exceptions.RestApiException;
import com.qfree.bo.report.service.AssetSyncService;
import com.qfree.bo.report.service.AssetTypeService;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.ASSETTYPES_PATH)
public class AssetTypeController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetTypeController.class);

	private final AssetTypeRepository assetTypeRepository;
	private final AssetTypeService assetTypeService;
	private final AssetSyncService assetSyncService;

	@Autowired
	public AssetTypeController(
			AssetTypeRepository assetTypeRepository,
			AssetTypeService assetTypeService,
			AssetSyncService assetSyncService) {
		this.assetTypeRepository = assetTypeRepository;
		this.assetTypeService = assetTypeService;
		this.assetSyncService = assetSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/report-server/rest/assetTypes?expand=assetTypes
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTYPES + "')")
	public AssetTypeCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<AssetType> assetTypes = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(AssetType.class, showAll)) {
			assetTypes = assetTypeRepository.findByActiveTrue();
		} else {
			assetTypes = assetTypeRepository.findAll();
		}
		return new AssetTypeCollectionResource(assetTypes, AssetType.class,
				uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"name":"New AssetType name","abbreviation":"NEWASSETTYPE","directory":"somedir","active":true}' \
	 *   http://localhost:8080/report-server/rest/assetTypes
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTYPES + "')")
	public Response create(
			AssetTypeResource assetTypeResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * The code below for renaming/moving the asset type directory in the
		 * report server's file system does not work if the new directory name
		 * contains a directory separator ("/" in Linux/Unix) and all of the 
		 * parent directories do not already exists, i.e., it will not create 
		 * new intermediate directories.
		 * 
		 * To avoid unexpected results and to keep things as simple as possible,
		 * this endpoint does not allow a directory separator at all in the
		 * directory name.
		 */
		String directorySeparator = System.getProperty("file.separator");
		logger.info("directorySeparator = {}", directorySeparator);
		if (assetTypeResource.getDirectory().indexOf(directorySeparator) != -1) {
			throw new RestApiException(RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS,
					RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS.getErrorMessage(),
					AssetType.class, "directory", assetTypeResource.getDirectory());
		}

		AssetType assetType = assetTypeService.saveNewFromResource(assetTypeResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, AssetType.class); // Force primary resource to be "expanded"
		//	}
		AssetTypeResource resource = new AssetTypeResource(assetType, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/report-server/rest/assetTypes/1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8?expand=assetTypes
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTYPES + "')")
	public AssetTypeResource getById(
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
			addToExpandList(expand, AssetType.class);
		}
		AssetType assetType = assetTypeRepository.findOne(id);
		RestUtils.ifNullThen404(assetType, AssetType.class, "assetTypeId", id.toString());
		AssetTypeResource assetTypeResource = new AssetTypeResource(assetType, uriInfo, queryParams, apiVersion);
		return assetTypeResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X PUT -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"name":"Image file (modified)","abbreviation":"IMAGE-MOD","directory":"images-mod","active":true}' \
	 *   http://localhost:8080/report-server/rest/assetTypes/1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTYPES + "')")
	public Response updateById(
			AssetTypeResource assetTypeResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve AssetType entity to be updated.
		 */
		AssetType assetType = assetTypeRepository.findOne(id);
		RestUtils.ifNullThen404(assetType, AssetType.class, "assetTypeId", id.toString());

		/*
		 * Create a shallow copy of the AssetType before it is modified.
		 */
		AssetType assetTypeBeforeUpdate = new AssetType(assetType);

		/*
		 * Treat attributes of parameterGroupResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * parameterGroup entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		if (assetTypeResource.getName() == null) {
			assetTypeResource.setName(assetType.getName());
		}
		if (assetTypeResource.getAbbreviation() == null) {
			assetTypeResource.setAbbreviation(assetType.getAbbreviation());
		}
		if (assetTypeResource.getDirectory() == null) {
			assetTypeResource.setDirectory(assetType.getDirectory());
		}
		if (assetTypeResource.isActive() == null) {
			assetTypeResource.setActive(assetType.isActive());
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the current value stored for
		 * the AssetType entity.
		 */
		assetTypeResource.setAssetTypeId(assetType.getAssetTypeId());
		assetTypeResource.setCreatedOn(assetType.getCreatedOn());

		/*
		 * The code below for renaming/moving the asset type directory in the
		 * report server's file system does not work if the new directory name
		 * contains a directory separator ("/" in Linux/Unix) and all of the 
		 * parent directories do not already exists, i.e., it will not create 
		 * new intermediate directories.
		 * 
		 * To avoid unexpected results and to keep things as simple as possible,
		 * this endpoint does not allow a directory separator at all in the
		 * directory name.
		 */
		String directorySeparator = System.getProperty("file.separator");
		logger.info("directorySeparator = {}", directorySeparator);
		if (assetTypeResource.getDirectory().indexOf(directorySeparator) != -1) {
			throw new RestApiException(RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS,
					RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS.getErrorMessage(),
					AssetType.class, "directory", assetTypeResource.getDirectory());
		}

		/*
		 * Save updated entity.
		 */
		assetType = assetTypeService.saveExistingFromResource(assetTypeResource);

		/*
		 * If the AssetType has just been made active/inactive, we do *not* 
		 * write/delete any assets to/from the file system. This is because we
		 * do *not* currently use the "active" field of AsetType entities to 
		 * control whether Assets linked to it are synchronized with the file
		 * system or not.
		 * 
		 * The "active" field should only be used to show or hide the AssetType
		 * in a UI where an AssetType can be selected to associate with an
		 * Asset.
		 */

		/*
		 * If the "directory" field of the AssetType has been modified, it is
		 * necessary to rename the directory in the file system, if it exists. 
		 * 
		 * This code will *NOT* work if the new directory name contains a 
		 * directory separator ("/") and all of the parent directories do not 
		 * already exists, i.e., it will not create new intermediate 
		 * directories.
		 * 
		 * To avoid this, the condition that "directory" does not contain
		 * multiple directory levels is enforced above.
		 * 
		 * It is best to use this endpoint for simply renaming an existing
		 * directory name.
		 */
		if (!assetType.getDirectory().equals(assetTypeBeforeUpdate.getDirectory())) {
			java.nio.file.Path assetFilePath = assetSyncService.moveAssetType(assetTypeBeforeUpdate, assetType,
					servletContext.getRealPath(""));
		}

		return Response.status(Response.Status.OK).build();
	}

}
