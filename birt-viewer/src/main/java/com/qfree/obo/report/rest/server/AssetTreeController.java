package com.qfree.obo.report.rest.server;

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

import com.qfree.obo.report.db.AssetTreeRepository;
import com.qfree.obo.report.domain.AssetTree;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.dto.AssetTreeCollectionResource;
import com.qfree.obo.report.dto.AssetTreeResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.service.AssetSyncService;
import com.qfree.obo.report.service.AssetTreeService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.ASSETTREES_PATH)
public class AssetTreeController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetTreeController.class);

	/*
	 * This is the name of the direcotry within the "webcontent" directory 
	 * where the BIRT WebViewer stores its assets. We cannot create or modify
	 * an AssetTree entity with directory="birt"; otherwise, we can end up
	 * messing up the functionality needed for displaying reports with the BIRT
	 * "viewservlets".
	 */
	private static final String BIRT_VIEWER_ASSET_TREE = "birt";

	private final AssetTreeRepository assetTreeRepository;
	private final AssetTreeService assetTreeService;
	private final AssetSyncService assetSyncService;

	@Autowired
	public AssetTreeController(
			AssetTreeRepository assetTreeRepository,
			AssetTreeService assetTreeService,
			AssetSyncService assetSyncService) {
		this.assetTreeRepository = assetTreeRepository;
		this.assetTreeService = assetTreeService;
		this.assetSyncService = assetSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/report-server/rest/assetTrees?expand=assetTrees
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTREES + "')")
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
	 *    http://localhost:8080/report-server/rest/assetTrees
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTREES + "')")
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

		if (BIRT_VIEWER_ASSET_TREE.equals(assetTreeResource.getDirectory())) {
			/*
			 * We cannot create or modify an AssetTree entity with a directory
			 * that conflicts with the directory used by the BIRT WebViewer;
			 * otherwise, we can end up messing up the functionality needed for
			 * displaying reports with the BIRT "viewservlets".
			 */
			throw new RestApiException(RestError.FORBIDDEN_RESERVED_VALUE,
					RestError.FORBIDDEN_RESERVED_VALUE.getErrorMessage(),
					AssetTree.class, "directory", assetTreeResource.getDirectory());
		}

		/*
		 * The code below for renaming/moving the asset tree directory in the
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
		if (assetTreeResource.getDirectory().indexOf(directorySeparator) != -1) {
			throw new RestApiException(RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS,
					RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS.getErrorMessage(),
					AssetTree.class, "directory", assetTreeResource.getDirectory());
		}

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
	 *   http://localhost:8080/report-server/rest/assetTrees/272199f9-d407-492f-a147-41a2b7d0cd02?expand=assetTrees
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTREES + "')")
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
	 *   http://localhost:8080/report-server/rest/assetTrees/7f9d0216-48d7-49ba-b043-ec48db03c938
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_ASSETTREES + "')")
	public Response updateById(
			AssetTreeResource assetTreeResource,
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
		 * Retrieve AssetTree entity to be updated.
		 */
		AssetTree assetTree = assetTreeRepository.findOne(id);
		RestUtils.ifNullThen404(assetTree, AssetTree.class, "assetTreeId", id.toString());

		/*
		 * Create a shallow copy of the AssetTree before it is modified.
		 */
		AssetTree assetTreeBeforeUpdate = new AssetTree(assetTree);

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
		if (assetTreeResource.isActive() == null) {
			assetTreeResource.setActive(assetTree.isActive());
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

		if (BIRT_VIEWER_ASSET_TREE.equals(assetTreeResource.getDirectory())) {
			/*
			 * We cannot create or modify an AssetTree entity with a directory
			 * that conflicts with the directory used by the BIRT WebViewer;
			 * otherwise, we can end up messing up the functionality needed for
			 * displaying reports with the BIRT "viewservlets".
			 */
			throw new RestApiException(RestError.FORBIDDEN_RESERVED_VALUE,
					RestError.FORBIDDEN_RESERVED_VALUE.getErrorMessage(),
					AssetTree.class, "directory", assetTreeResource.getDirectory());
		}

		/*
		 * The code below for renaming/moving the asset tree directory in the
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
		if (assetTreeResource.getDirectory().indexOf(directorySeparator) != -1) {
			throw new RestApiException(RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS,
					RestError.FORBIDDEN_MULTIPLE_DIRECTORY_LEVELS.getErrorMessage(),
					AssetTree.class, "directory", assetTreeResource.getDirectory());
		}

		/*
		 * Save updated entity.
		 */
		assetTree = assetTreeService.saveExistingFromResource(assetTreeResource);
		
		/*
		 * If the AssetTree has just been made active/inactive, we do *not* 
		 * write/delete any assets to/from the file system. This is because we
		 * do *not* currently use the "active" field of AsetTree entities to 
		 * control whether Assets linked to it are synchronized with the file
		 * system or not.
		 * 
		 * The "active" field should only be used to show or hide the AssetTree
		 * in a UI where an AssetTree can be selected to associate with an
		 * Asset.
		 */

		/*
		 * If the "directory" field of the AssetTree has been modified, it is
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
		if (!assetTree.getDirectory().equals(assetTreeBeforeUpdate.getDirectory())) {
			java.nio.file.Path assetFilePath = assetSyncService.moveAssetTree(assetTreeBeforeUpdate, assetTree,
					servletContext.getRealPath(""));
		}

		return Response.status(Response.Status.OK).build();
	}

}
