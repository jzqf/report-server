package com.qfree.obo.report.rest.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.eclipse.birt.core.exception.BirtException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.AssetRepository;
import com.qfree.obo.report.db.AssetTreeRepository;
import com.qfree.obo.report.db.AssetTypeRepository;
import com.qfree.obo.report.db.DocumentRepository;
import com.qfree.obo.report.domain.Asset;
import com.qfree.obo.report.domain.AssetTree;
import com.qfree.obo.report.domain.AssetType;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Document;
import com.qfree.obo.report.dto.AssetCollectionResource;
import com.qfree.obo.report.dto.AssetResource;
import com.qfree.obo.report.dto.DocumentResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;
import com.qfree.obo.report.service.AssetService;
import com.qfree.obo.report.service.AssetSyncService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.ASSETS_PATH)
public class AssetController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

	private final AssetRepository assetRepository;
	private final AssetService assetService;
	private final AssetSyncService assetSyncService;
	private final AssetTreeRepository assetTreeRepository;
	private final AssetTypeRepository assetTypeRepository;
	private final DocumentRepository documentRepository;

	@Autowired
	public AssetController(
			AssetRepository assetRepository,
			AssetService assetService,
			AssetSyncService assetSyncService,
			AssetTreeRepository assetTreeRepository,
			AssetTypeRepository assetTypeRepository,
			DocumentRepository documentRepository) {
		this.assetRepository = assetRepository;
		this.assetService = assetService;
		this.assetSyncService = assetSyncService;
		this.assetTreeRepository = assetTreeRepository;
		this.assetTypeRepository = assetTypeRepository;
		this.documentRepository = documentRepository;
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

	/**
	 * This endpoint creates a new {@link Asset}.
	 * 
	 * It accepts a JSON object that represents an {@link AssetResource} that
	 * has been POSTed to this enbdpoint's URI.
	 * 
	 * @param assetResource
	 * @param acceptHeader
	 * @param expand
	 * @param showAll
	 * @param uriInfo
	 * @return
	 */
	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" \-H "Content-Type: application/json" -d \
	 *   '{"filename":"success.gif",\
	 *   "assetTree":{"assetTreeId":"7f9d0216-48d7-49ba-b043-ec48db03c938"},\
	 *   "assetType":{"assetTypeId":"1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8"},\
	 *   "document":{"content":"R0lGODlhEAAQAKIAAAAA/z9/v3+fv6bK8J+/v////wAAAAAAACH5BAEAAAUALAAAAAAQABAAAAMpWLrc/jASEFkAo6pLdLmZJmDeAAjQgBZmkALYAEbjeWow7NUhvnrAYAIAOw=="}}' \
	 *   http://localhost:8080/rest/assets
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response createByPostJson(
			AssetResource assetResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		RestUtils.ifAttrNullOrBlankThen403(assetResource.getFilename(), Asset.class, "filename");
		RestUtils.ifAttrNullThen403(assetResource.getAssetTreeResource(), Asset.class, "assetTree");
		RestUtils.ifAttrNullThen403(assetResource.getAssetTypeResource(), Asset.class, "assetType");

		/*
		 * Extract the Base64-encoded document content from assetResource and
		 * decode it into a byte[] that can be used to create a new Document
		 * entity. 
		 */
		DocumentResource documentResource = assetResource.getDocumentResource();
		byte[] documentContent = null;
		if (documentResource != null) {
			if (documentResource.getContent() != null) {
				documentContent = Base64.getDecoder().decode(documentResource.getContent());
			} else {
				throw new RestApiException(RestError.FORBIDDEN_CREATE_ASSET_NO_DOCUMENT_CONTENT);
			}
		} else {
			throw new RestApiException(RestError.FORBIDDEN_CREATE_ASSET_NO_DOCUMENT_CONTENT);
		}

		/*
		 * Create new Document entity.
		 */
		Document document = new Document(documentContent);
		document = documentRepository.save(document);

		/*
		 * documentResource is used below to set a value for documentId in 
		 * assetResource. It is only necessary for the DocumentResource to have
		 * a value for its documentId attribute.
		 */
		documentResource = new DocumentResource();
		documentResource.setDocumentId(document.getDocumentId());

		/*
		 * Create a new Asset entity that is linked to the new Document that was
		 * just created.
		 */
		assetResource.setDocumentResource(documentResource);
		Asset asset = assetService.saveNewFromResource(assetResource);

		/*
		 * Write uploaded asset file to the file system of the report 
		 * server, overwriting a file with the same name if one exists.
		 */
		java.nio.file.Path assetFilePath = assetSyncService.writeAssetFile(asset, servletContext.getRealPath(""));

		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Asset.class); // Force primary resource to be "expanded"
		//	}
		AssetResource resource = new AssetResource(asset, uriInfo, queryParams, apiVersion);
		return created(resource);
	}

	/**
	 * This endpoint creates a new {@link Asset} entity using
	 * multipart/form-data.
	 * 
	 * It can be used as the "action" for an HTML form that performs a file
	 * upload of the asset document.
	 * 
	 * @param filename
	 * @param assetTreeId
	 * @param assetTypeId
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @param acceptHeader
	 * @param expand
	 * @param showAll
	 * @param uriInfo
	 * @return
	 * @throws BirtException
	 * @throws RptdesignOpenFromStreamException
	 */
	/*
	 * This endpoint can be tested with:
	 * 
	 *   1. $ mvn clean spring-boot:run
	 *   
	 *   2. Open the following URL in a web browser:
	 *   
	 *         http://localhost:8080/upload_asset.html
	 *   
	 *   3. Provide values for:
	 *   
	 *      a. filename
	 *      b. AssetTree id (UUID)
	 *      c. AssetType id (UUID)
	 *   
	 *   4. Select a document from the file system.
	 *   
	 *   5.	Click the button labeled "Upload".
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response createByUpload(
			@FormDataParam("filename") String filename,
			@FormDataParam("assetTreeId") UUID assetTreeId,
			@FormDataParam("assetTypeId") UUID assetTypeId,
			@FormDataParam("documentfile") InputStream uploadedInputStream,
			@FormDataParam("documentfile") FormDataContentDisposition fileDetail,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			//@Context final ServletConfig servletConfig,
			@Context final UriInfo uriInfo) throws BirtException, RptdesignOpenFromStreamException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		//	logger.debug("servletContext.getContextPath() = {}", servletContext.getContextPath());
		//	logger.debug("servletContext.getRealPath(\"\") = {}", servletContext.getRealPath(""));
		//	logger.debug("servletContext.getRealPath(\"/\") = {}", servletContext.getRealPath("/"));
		//
		//	Enumeration<String> servletContextinitParamEnum = servletContext.getInitParameterNames();
		//	while (servletContextinitParamEnum.hasMoreElements()) {
		//		String servletContextInitParamName = servletContextinitParamEnum.nextElement();
		//		logger.debug("servletContextInitParamName = {}", servletContextInitParamName);
		//	}
		//	/*
		//	 * This returns null if this application is run via:
		//	 *  
		//	 *     mvn clean spring-boot:run
		//	 */
		//	logger.debug("BIRT_VIEWER_WORKING_FOLDER = {}", servletContext.getInitParameter("BIRT_VIEWER_WORKING_FOLDER"));
		//
		//	Enumeration<String> servletConfigInitParamEnum = servletConfig.getInitParameterNames();
		//	while (servletConfigInitParamEnum.hasMoreElements()) {
		//		String servletConfigInitParamName = servletConfigInitParamEnum.nextElement();
		//		logger.debug("servletConfigInitParamName = {}", servletConfigInitParamName);
		//	}
		//	logger.debug("javax.ws.rs.Application = {}", servletConfig.getInitParameter("javax.ws.rs.Application"));

		RestUtils.ifAttrNullOrBlankThen403(filename, null, "filename");
		RestUtils.ifAttrNullThen403(assetTreeId, null, "assetTreeId");
		RestUtils.ifAttrNullThen403(assetTypeId, null, "assetTypeId");

		RestUtils.ifAttrNullThen403(uploadedInputStream, null, "uploadedInputStream");

		AssetResource assetResource = null;
		try {

			/*
			 * Read from the InputStream that represents the uploaded file to
			 * a ByteArrayOutputStream that is used to create a byte[] object
			 * that is stored in a new Document entity.
			 */
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = uploadedInputStream.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, read);
			}
			baos.flush();
			byte[] documentContent = baos.toByteArray();

			Document document = new Document(documentContent);
			document = documentRepository.save(document);
			if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
				addToExpandList(expand, Document.class);
			}
			RestUtils.ifAttrNullThen403(document.getDocumentId(), Document.class, "documentId");

			/*
			 * Make sure that assetTreeId & assetTypeId correspond to existing
			 * AssetTree & AssetType entities.
			 */
			AssetTree assetTree = assetTreeRepository.findOne(assetTreeId);
			RestUtils.ifNullThen404(assetTree, AssetTree.class, "assetTreeId", assetTreeId.toString());
			AssetType assetType = assetTypeRepository.findOne(assetTypeId);
			RestUtils.ifNullThen404(assetType, AssetType.class, "assetTypeId", assetTypeId.toString());

			Asset asset = new Asset(assetTree, assetType, document, filename, true);
			asset = assetRepository.save(asset);

			/*
			 * Write uploaded asset file to the file system of the report 
			 * server, overwriting a file with the same name if one exists.
			 */
			java.nio.file.Path assetFilePath = assetSyncService.writeAssetFile(asset, servletContext.getRealPath(""));

			if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
				addToExpandList(expand, Asset.class);
			}
			assetResource = new AssetResource(asset, uriInfo, queryParams, apiVersion);

		} catch (IOException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_FILE_UPLOAD, e);
		}

		return created(assetResource);
	}

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
	 *   "assetType": {"assetTypeId": "1e7ddbbc-8b40-4373-bfc5-6e6d3d5964d8"}}' \
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
		 * Construct a DocumentResource to specify the CURRENTLY
		 * selected Document.
		 */
		UUID currentDocumentId = asset.getDocument().getDocumentId();
		DocumentResource documentResource = new DocumentResource();
		documentResource.setDocumentId(currentDocumentId);
		assetResource.setDocumentResource(documentResource);

		/*
		 * Save updated entity.
		 */
		asset = assetService.saveExistingFromResource(assetResource);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X DELETE -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/assets/9e3ad41a-e026-43b6-89e1-5201deeee7f0
	 */
	@Path("/{id}")
	@DELETE
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public AssetResource deleteById(
			//public Response updateById(
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
		 * Retrieve Asset entity to be deleted.
		 */
		Asset asset = assetRepository.findOne(id);
		logger.debug("asset to be deleted = {}", asset);
		RestUtils.ifNullThen404(asset, Asset.class, "assetId", id.toString());

		/*
		 * Retrieve Asset entity's Document entity to be deleted.
		 */
		Document document = asset.getDocument();
		logger.debug("document to be deleted = {}", document);

		/*
		 * If the Asset entity is successfully deleted, it is 
		 * returned as the entity body so it is clear to the caller precisely
		 * which entity was deleted. Here, the resource to be returned is 
		 * created before the entity is deleted.
		 */
		addToExpandList(expand, Asset.class);
		addToExpandList(expand, AssetTree.class);
		addToExpandList(expand, AssetType.class);
		addToExpandList(expand, Document.class);
		AssetResource assetResource = new AssetResource(asset, uriInfo, queryParams, apiVersion);
		logger.debug("assetResource = {}", assetResource);

		/*
		 * Delete Asset entity. "document" should always be non-null. It should
		 * only be null of something went wrong somewhere at some point. But we
		 * do not want to throw an exception or it will be impossible to delete
		 * the asset.
		 */
		if (document != null) {
			assetRepository.delete(asset);
			logger.info("asset (after deletion) = {}", asset);
		} else {
			logger.warn("document is null. This should not be possible.");
		}

		/*
		 * Delete Asset entity's Document entity.
		 */
		documentRepository.delete(document);
		logger.info("document (after deletion) = {}", document);

		/*
		 * Delete the asset from the file system of the report server. This 
		 * assumes that the Asset entity "asset" is not cleared or set to null
		 * when it is deleted above.
		 */
		java.nio.file.Path assetFilePath = assetSyncService.deleteAssetFile(asset, servletContext.getRealPath(""));

		//	/*
		//	 * Confirm that the entity was, indeed, deleted. asset here
		//	 * should be null. Currently, I don't do anything based on this check.
		//	 * I assume that the delete call above with throw some sort of 
		//	 * RuntimeException if that happens, or some other exception will be
		//	 * thrown by the back-end database (PostgreSQL) code when the 
		//	 * transaction is eventually committed. I don't have the time to look 
		//	 * into this at the moment.
		//	 */
		//	asset = assetRepository.findOne(assetResource.getAssetId());
		//	logger.info("asset (after find()) = {}", asset); // asset is null here

		//return Response.status(Response.Status.OK).build();
		return assetResource;
	}

}
