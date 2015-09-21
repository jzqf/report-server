package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentFormatRepository;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.dto.DocumentFormatCollectionResource;
import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.DocumentFormatService;

@Component
@Path(ResourcePath.DOCUMENTFORMATS_PATH)
public class DocumentFormatController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFormatController.class);

	private final DocumentFormatRepository documentFormatRepository;
	private final DocumentFormatService documentFormatService;

	@Autowired
	public DocumentFormatController(
			DocumentFormatRepository documentFormatRepository,
			DocumentFormatService documentFormatService) {
		this.documentFormatRepository = documentFormatRepository;
		this.documentFormatService = documentFormatService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/documentFormats?expand=documentFormats
	 *   
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating documentFormat.getSubscriptions() in
	 * SubscriptionResource.listFromDocumentFormat(...).
	 */
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentFormatCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<DocumentFormat> documentFormats = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(DocumentFormat.class, showAll)) {
			documentFormats = documentFormatRepository.findByActiveTrue();
		} else {
			documentFormats = documentFormatRepository.findAll();
		}
		List<DocumentFormatResource> documentFormatResources = new ArrayList<>(documentFormats.size());
		for (DocumentFormat documentFormat : documentFormats) {
			documentFormatResources.add(new DocumentFormatResource(documentFormat, uriInfo, queryParams, apiVersion));
		}
		return new DocumentFormatCollectionResource(documentFormatResources, DocumentFormat.class, uriInfo,
				queryParams, apiVersion);
	}

	// /*
	// * This endpoint can be tested with:
	// *
	// * $ mvn clean spring-boot:run
	// * $ curl -iH "Accept: application/json;v=1" -H "Content-Type:
	// application/json" -X POST -d \
	// * '{...,"active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	// * http://localhost:8080/rest/documentFormats
	// */
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @Transactional
	// public Response create(
	// DocumentFormatResource documentFormatResource,
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
	// DocumentFormat documentFormat =
	// documentFormatService.saveNewFromResource(documentFormatResource);
	// // if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	// addToExpandList(expand, DocumentFormat.class); // Force primary resource
	// // to be "expanded"
	// // }
	// DocumentFormatResource resource = new
	// DocumentFormatResource(documentFormat, uriInfo, queryParams, apiVersion);
	// return created(resource);
	// }

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8081/report-server/rest/documentFormats/7a482694-51d2-42d0-b0e2-19dd13bbbc64?expand=documentFormats
	 *   
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating documentFormat.getSubscriptions() in
	 * SubscriptionResource.listFromDocumentFormat(...).
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentFormatResource getById(
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
			addToExpandList(expand, DocumentFormat.class);
		}
		DocumentFormat documentFormat = documentFormatRepository.findOne(id);
		RestUtils.ifNullThen404(documentFormat, DocumentFormat.class, "documentFormatId", id.toString());
		DocumentFormatResource documentFormatResource = new DocumentFormatResource(documentFormat, uriInfo, queryParams,
				apiVersion);
		return documentFormatResource;
	}

	// /*
	// * This endpoint can be tested with:
	// *
	// * $ mvn clean spring-boot:run
	// * $ curl -iH "Accept: application/json;v=1" -H "Content-Type:
	// application/json" -X PUT -d \
	// * '{...,"active":false}' \
	// *
	// http://localhost:8080/rest/documentFormats/bb2bc482-c19a-4c19-a087-e68ffc62b5a0
	// */
	// @Path("/{id}")
	// @PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// @Transactional
	// public Response updateById(
	// DocumentFormatResource documentFormatResource,
	// @PathParam("id") final UUID id,
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
	// /*
	// * Retrieve DocumentFormat entity to be updated.
	// */
	// DocumentFormat documentFormat = documentFormatRepository.findOne(id);
	// RestUtils.ifNullThen404(documentFormat, DocumentFormat.class,
	// "documentFormatId", id.toString());
	// /*
	// * Ensure that the entity's "id" and "CreatedOn" are not changed.
	// */
	// documentFormatResource.setDocumentFormatId(documentFormat.getDocumentFormatId());
	// documentFormatResource.setCreatedOn(documentFormat.getCreatedOn());
	// /*
	// * Save updated entity.
	// */
	// documentFormat =
	// documentFormatService.saveExistingFromResource(documentFormatResource);
	// return Response.status(Response.Status.OK).build();
	// }

}
