package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.SelectionListValueRepository;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.SelectionListValueResource;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.SelectionListValueService;

@Component
@Path(ResourcePath.SELECTIONLISTVALUES_PATH)
public class SelectionListValueController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListValueController.class);

	private final SelectionListValueRepository selectionListValueRepository;
	private final SelectionListValueService selectionListValueService;

	@Autowired
	public SelectionListValueController(
			SelectionListValueRepository selectionListValueRepository,
			SelectionListValueService selectionListValueService) {
		this.selectionListValueRepository = selectionListValueRepository;
		this.selectionListValueService = selectionListValueService;
	}

	// /*
	// * This endpoint can be tested with:
	// *
	// * $ mvn clean spring-boot:run
	// * $ curl -i -H "Accept: application/json;v=1" -X GET \
	// *
	// http://localhost:8080/rest/selectionListValues?expand=selectionListValues
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// // public List<SelectionListValueResource> getList(
	// public SelectionListValueCollectionResource getList(
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
	// List<SelectionListValue> selectionListValues = null;
	// selectionListValues = selectionListValueRepository.findAll();
	// List<SelectionListValueResource> selectionListValueResources = new
	// ArrayList<>(selectionListValues.size());
	// for (SelectionListValue selectionListValue : selectionListValues) {
	// selectionListValueResources
	// .add(new SelectionListValueResource(selectionListValue, uriInfo,
	// queryParams, apiVersion));
	// }
	// // return selectionListValueResources;
	// return new
	// SelectionListValueCollectionResource(selectionListValueResources,
	// SelectionListValue.class, uriInfo,
	// queryParams, apiVersion);
	// }

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"reportParameter":{"reportParameterId":"3012de58-e668-41d5-8a9e-aa305ee79811"},\
	 *   "valueAssigned":"newvaluetoassign","valueDisplayed":"New displayed value",\
	 *   "orderIndex":5, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *   http://localhost:8080/rest/selectionListValues
	 * 
	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	 * SelectionListValue to create is given:
	 * 
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"selectionListValueId":"71b3e8ae-bba8-45b7-a85f-12546bcc95b2",\
	 *   "reportParameter":{"reportParameterId":"3012de58-e668-41d5-8a9e-aa305ee79811"},\
	 *   "valueAssigned":"newvaluetoassign","valueDisplayed":"New displayed value",\
	 *   "orderIndex":5, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	 *   http://localhost:8080/rest/selectionListValues
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response create(
			SelectionListValueResource selectionListValueResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		SelectionListValue selectionListValue = selectionListValueService
				.saveNewFromResource(selectionListValueResource);
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, SelectionListValue.class);// Force primary
															// resource to be
															// "expanded"
		//	}

		SelectionListValueResource resource = new SelectionListValueResource(selectionListValue, uriInfo, queryParams,
				apiVersion);
		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/selectionListValues/7a482694-51d2-42d0-b0e2-19dd13bbbc64\
	 *   ?expand=selectionListValues
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SelectionListValueResource getById(
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
			addToExpandList(expand, SelectionListValue.class);
		}
		SelectionListValue selectionListValue = selectionListValueRepository.findOne(id);
		RestUtils.ifNullThen404(selectionListValue, SelectionListValue.class, "selectionListValueId", id.toString());
		SelectionListValueResource selectionListValueResource = new SelectionListValueResource(selectionListValue,
				uriInfo, queryParams,
				apiVersion);
		return selectionListValueResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"valueAssigned":"valuetoassign-modified","valueDisplayed":"Displayed value - modified",\
	 *   "orderIndex":5}' \
	 *   http://localhost:8080/rest/selectionListValues/0b986fd2-6d6b-46c3-9be7-5c1831a563ca
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response updateById(
			SelectionListValueResource selectionListValueResource,
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
		 * TODO Move most of the code here to saveExistingFromResource(...)?
		 * 
		 * To do this,I would need to pass another parameter to 
		 * saveExistingFromResource(...) so that that method has access to the
		 * entity being updated, selectionListValue. So we could pass either 
		 * "id" or selectionListValue or ...
		 */

		/*
		 * Retrieve SelectionListValue entity to be updated.
		 */
		SelectionListValue selectionListValue = selectionListValueRepository.findOne(id);
		RestUtils.ifNullThen404(selectionListValue, SelectionListValue.class, "selectionListValueId", id.toString());

		/*
		 * Treat attributes of selectionListValueResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * selectionListValue entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		if (selectionListValueResource.getValueAssigned() == null) {
			selectionListValueResource.setValueAssigned(selectionListValue.getValueAssigned());
		}
		if (selectionListValueResource.getValueDisplayed() == null) {
			selectionListValueResource.setValueDisplayed(selectionListValue.getValueDisplayed());
		}
		if (selectionListValueResource.getOrderIndex() == null) {
			selectionListValueResource.setOrderIndex(selectionListValue.getOrderIndex());
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the CURRENT value stored for
		 * the selectionListValue entity.
		 */
		selectionListValueResource.setSelectionListValueId(selectionListValue.getSelectionListValueId());
		selectionListValueResource.setCreatedOn(selectionListValue.getCreatedOn());
		/*
		 * Construct a ReportParameterResource to specify the CURRENTLY
		 * selected ReportParameter.
		 */
		UUID currentReportParameterId = selectionListValue.getReportParameter().getReportParameterId();
		ReportParameterResource reportParameterResource = new ReportParameterResource();
		reportParameterResource.setReportParameterId(currentReportParameterId);
		selectionListValueResource.setReportParameterResource(reportParameterResource);

		/*
		 * Save updated entity.
		 */
		selectionListValue = selectionListValueService.saveExistingFromResource(selectionListValueResource);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X DELETE \
	 *   http://localhost:8080/rest/selectionListValues/0b986fd2-6d6b-46c3-9be7-5c1831a563ca
	 */
	@Path("/{id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public SelectionListValueResource deleteById(
			//public Response updateById(
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
		 * Retrieve SelectionListValue entity to be updated.
		 */
		SelectionListValue selectionListValue = selectionListValueRepository.findOne(id);
		logger.info("selectionListValue = {}", selectionListValue);
		RestUtils.ifNullThen404(selectionListValue, SelectionListValue.class, "selectionListValueId", id.toString());

		/*
		 * If the SelectionListValue entity is successfully deleted, it is 
		 * returned as the entity body so it is clear to the caller precisely
		 * which entity was deleted. Here, the resource to be returned is 
		 * created before the entity is deleted.
		 */
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, SelectionListValue.class);// Force primary resource to be "expanded"
		//	}
		SelectionListValueResource selectionListValueResource = new SelectionListValueResource(selectionListValue,
				uriInfo, queryParams, apiVersion);
		logger.info("selectionListValueResource = {}", selectionListValueResource);
		/*
		 * Delete entity.
		 */
		selectionListValueRepository.delete(selectionListValue);
		logger.info("selectionListValue (after deletion) = {}", selectionListValue);
		/*
		 * Confirm that the entity was, indeed, deleted. selectionListValue here
		 * should be null. Currently, I don't do anything based on this check.
		 * I assume that the delete call above with throw some sort of 
		 * RuntimeException if that happens, or some other exception will be
		 * thrown by the back-end databse (PostgreSQL) code when the transaction
		 * is eventually committed. I don't have the time to look into this at
		 * the moment, but I doubt that this deletion will encounter problems
		 * often, if ever, since it will never violate a foreign key constraint.
		 */
		selectionListValue = selectionListValueRepository.findOne(selectionListValueResource.getSelectionListValueId());
		logger.info("selectionListValue (after find()) = {}", selectionListValue);

		//return Response.status(Response.Status.OK).build();
		return selectionListValueResource;
	}
}
