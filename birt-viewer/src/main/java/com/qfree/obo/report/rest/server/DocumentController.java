package com.qfree.obo.report.rest.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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

import org.eclipse.birt.core.exception.BirtException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentRepository;
import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Document;
import com.qfree.obo.report.dto.DocumentCollectionResource;
import com.qfree.obo.report.dto.DocumentResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;
import com.qfree.obo.report.service.ReportParameterService;
import com.qfree.obo.report.service.ReportSyncService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.DOCUMENTS_PATH)
public class DocumentController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

	private final DocumentRepository documentRepository;
	//	private final DocumentService documentService;
	private final ReportRepository reportRepository;
	private final ReportSyncService reportSyncService;
	private final ReportParameterService reportParameterService;

	@Autowired
	public DocumentController(
			DocumentRepository documentRepository,
			//			DocumentService documentService,
			ReportRepository reportRepository,
			ReportSyncService reportSyncService,
			ReportParameterService reportParameterService) {
		this.documentRepository = documentRepository;
		//		this.documentService = documentService;
		this.reportRepository = reportRepository;
		this.reportSyncService = reportSyncService;
		this.reportParameterService = reportParameterService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X -iH "Accept: application/json;v=1" GET http://localhost:8080/rest/documents
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating Document.getReportParameters().
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public DocumentCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Document> documents = null;
		documents = documentRepository.findAll();
		return new DocumentCollectionResource(documents, Document.class,
				uriInfo, queryParams, apiVersion);
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	//	 *   '{"report":{"reportId":"702d5daa-e23d-4f00-b32b-67b44c06d8f6"},\
	//	 *   "fileName":"400-SomeReport_v3.9.rptdesign",\
	//	 *   "rptdesign":"<?xml version=\"1.0\" encoding=\"UTF-8\"?><report></report>",\
	//	 *   "versionName":"3.9","versionCode":7,"active":true}' \
	//	 *   http://localhost:8080/rest/documents
	//	 * 
	//	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	//	 * being thrown when evaluating Document.getDocuments(), as well
	//	 * as potentially other lazy-evaluated attributes..
	//	 */
	//	@POST
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Transactional
	//	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	//	public Response createByPost(
	//			DocumentResource documentResource,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final ServletContext servletContext,
	//			@Context final UriInfo uriInfo) throws IOException, BirtException, RptdesignOpenFromStreamException {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		RestUtils.ifAttrNullOrBlankThen403(documentResource.getRptdesign(), Document.class, "rptdesign");
	//		RestUtils.ifAttrNullOrBlankThen403(documentResource.getFileName(), Document.class, "fileName");
	//		RestUtils.ifAttrNullOrBlankThen403(documentResource.getVersionName(), Document.class, "versionName");
	//
	//		RestUtils.ifNotValidXmlThen403(documentResource.getRptdesign(),
	//				String.format("The report definition '%s' is not valid XML", documentResource.getFileName()),
	//				Document.class, "rptdesign", null);
	//
	//		Document document = documentService.saveNewFromResource(documentResource);
	//		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//		addToExpandList(expand, Document.class);// Force primary resource
	//														// to be "expanded"
	//		// }
	//
	//		/*
	//		 * Write uploaded rptdesign file to the file system of the report 
	//		 * server, overwriting a file with the same name if one exists.
	//		 */
	//		java.nio.file.Path rptdesignFilePath = reportSyncService.writeRptdesignFile(document,
	//				servletContext.getRealPath(""));
	//
	//		DocumentResource newDocumentResource = new DocumentResource(document, uriInfo, queryParams,
	//				apiVersion);
	//
	//		return created(newDocumentResource);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   1. $ mvn clean spring-boot:run
	 *   
	 *   2. Open the following URL in a web browser:
	 *   
	 *         http://localhost:8080/upload_document.html
	 *   
	 *   3: Select a document from the file system and then click the button
	 *      labeled "Upload".
	 * 
	 * Only *one* of reportId and reportName need be defined, as long as a 
	 * they can be used to locate a parent Report. If both are defined, reportId
	 * will be used.
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response createByUpload(
			@FormDataParam("documentfile") InputStream uploadedInputStream,
			@FormDataParam("documentfile") FormDataContentDisposition fileDetail,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final ServletConfig servletConfig,
			@Context final UriInfo uriInfo) throws BirtException, RptdesignOpenFromStreamException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.debug("servletContext.getContextPath() = {}", servletContext.getContextPath());
		logger.debug("servletContext.getRealPath(\"\") = {}", servletContext.getRealPath(""));
		logger.debug("servletContext.getRealPath(\"/\") = {}", servletContext.getRealPath("/"));

		Enumeration<String> servletContextinitParamEnum = servletContext.getInitParameterNames();
		while (servletContextinitParamEnum.hasMoreElements()) {
			String servletContextInitParamName = servletContextinitParamEnum.nextElement();
			logger.debug("servletContextInitParamName = {}", servletContextInitParamName);
		}
		/*
		 * This returns null if this application is run via:
		 *  
		 *     mvn clean spring-boot:run
		 */
		logger.debug("BIRT_VIEWER_WORKING_FOLDER = {}", servletContext.getInitParameter("BIRT_VIEWER_WORKING_FOLDER"));

		Enumeration<String> servletConfigInitParamEnum = servletConfig.getInitParameterNames();
		while (servletConfigInitParamEnum.hasMoreElements()) {
			String servletConfigInitParamName = servletConfigInitParamEnum.nextElement();
			logger.debug("servletConfigInitParamName = {}", servletConfigInitParamName);
		}
		logger.debug("javax.ws.rs.Application = {}", servletConfig.getInitParameter("javax.ws.rs.Application"));

		RestUtils.ifAttrNullOrBlankThen403(uploadedInputStream, Document.class, "documentfile");

		DocumentResource documentResource = null;
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
			logger.info("documentContent.length = {}", documentContent.length);

			//	StringBuilder stringBuilder = new StringBuilder();
			//	try (BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedInputStream))) {
			//		String line;
			//		while ((line = reader.readLine()) != null) {
			//			stringBuilder.append(line);
			//		}
			//	}
			//	String contentHexEncoded = stringBuilder.toString();

			//	RestUtils.ifAttrNullOrBlankThen403(content, Document.class, "content");

			Document document = new Document(documentContent);
			document = documentRepository.save(document);
			logger.info("document = {}", document);
			if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
				addToExpandList(expand, Document.class);
			}

			//			/*
			//			 * Write uploaded rptdesign file to the file system of the report 
			//			 * server, overwriting a file with the same name if one exists.
			//			 */
			//			//			ReportUtils.writeRptdesignFile(document, servletContext.getRealPath(""));
			//			java.nio.file.Path rptdesignFilePath = reportSyncService.writeRptdesignFile(document,
			//					servletContext.getRealPath(""));

			documentResource = new DocumentResource(document, uriInfo, queryParams, apiVersion);
			logger.debug("documentResource = {}", documentResource);

		} catch (IOException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_FILE_UPLOAD, e);
		}

		return created(documentResource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/documents/2d01c072-5d9e-44f9-bfe3-b785d9a6bb0d?expand=documents
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public DocumentResource getById(
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
			addToExpandList(expand, Document.class);
		}
		Document document = documentRepository.findOne(id);
		RestUtils.ifNullThen404(document, Document.class, "documentId", id.toString());
		DocumentResource documentResource = new DocumentResource(document,
				uriInfo, queryParams, apiVersion);
		return documentResource;
	}

	/**
	 * This endpoint returns the file content for a specified {@link Document}
	 * entity.
	 * 
	 * <p>
	 * A representation of this document is stored in the
	 * {@link Document#content} field.
	 * <p>
	 * This endpoint can be tested with:
	 * 
	 * <pre>
	 * <code>$ mvn clean spring-boot:run
	 * $ curl -X GET -iH "Accept: application/pdf;v=1" \
	 * http://localhost:8080/rest/documents/2d01c072-5d9e-44f9-bfe3-b785d9a6bb0d/content</code>
	 * </pre>
	 * 
	 * @param id
	 * @param acceptHeader
	 * @param uriInfo
	 * @return
	 */
	@Path("/{id}/content")
	@GET
	@Transactional
	/*
	 * MediaType.APPLICATION_JSON seems to be needed here in order to be able
	 * to return a RestErrorResource from DocumentContentOutputStream if a problem
	 * is encountered.
	 */
	//@Produces({ MediaType.APPLICATION_JSON })
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public Response getDocumentContentByDocumentId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			//	@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			//	@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		//	Map<String, List<String>> queryParams = new HashMap<>();
		//	queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		//	queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Document document = documentRepository.findOne(id);
		RestUtils.ifNullThen404(document, Document.class, "documentId", id.toString());

		return Response.status(Response.Status.OK)
				.entity(new DocumentContentOutputStream(document))
				//.type(document.getDocumentFormat().getInternetMediaType())
				//.header("content-disposition", String.format("attachment; filename = \"%s\"", document.getFileName()))
				/*
				 * No filename is specified here. Chrome will open a "Save File"
				 * dialog with a filename of "content.html" filled in. This can
				 * be overridden with whichever name you like.
				 */
				.header("content-disposition", "attachment")
				.build();
	}
}