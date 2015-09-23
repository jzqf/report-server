package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentFormatRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.db.SubscriptionRepository;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.dto.SubscriptionCollectionResource;
import com.qfree.obo.report.dto.SubscriptionParameterValueCollectionResource;
import com.qfree.obo.report.dto.SubscriptionResource;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.SubscriptionService;

@Component
@Path(ResourcePath.SUBSCRIPTIONS_PATH)
public class SubscriptionController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

	private final SubscriptionRepository subscriptionRepository;
	private final SubscriptionService subscriptionService;
	private final DocumentFormatRepository documentFormatRepository;
	private final ReportVersionRepository reportVersionRepository;
	private final RoleRepository roleRepository;

	@Autowired
	public SubscriptionController(
			SubscriptionRepository subscriptionRepository,
			SubscriptionService subscriptionService,
			DocumentFormatRepository documentFormatRepository,
			ReportVersionRepository reportVersionRepository,
			RoleRepository roleRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.subscriptionService = subscriptionService;
		this.documentFormatRepository = documentFormatRepository;
		this.reportVersionRepository = reportVersionRepository;
		this.roleRepository = roleRepository;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/subscriptions?expand=subscriptions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<Subscription> subscriptions = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Subscription.class, showAll)) {
			subscriptions = subscriptionRepository.findByActiveTrue();
		} else {
			subscriptions = subscriptionRepository.findAll();
		}
		List<SubscriptionResource> subscriptionResources = new ArrayList<>(subscriptions.size());
		for (Subscription subscription : subscriptions) {
			subscriptionResources.add(new SubscriptionResource(subscription, uriInfo, queryParams, apiVersion));
		}
		return new SubscriptionCollectionResource(subscriptionResources, Subscription.class, uriInfo, queryParams,
				apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d '{\
	 *   "reportVersion":{"reportVersionId":"afd8777f-b6a2-4cb8-8dc2-887c47af3644"},\
	 *   "role":{"roleId":"b85fd129-17d9-40e7-ac11-7541040f8627"},\
	 *   "documentFormat":{"documentFormatId":"30800d77-5fdd-44bc-94a3-1502bd307c1d"}}' \
	 *   http://localhost:8080/rest/subscriptions
	 *   
	 *   This will throw an exception because we are attempting to create a new
	 *   Subscription with active=true:
	 *   
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d '{\
	 *   "reportVersion":{"reportVersionId":"afd8777f-b6a2-4cb8-8dc2-887c47af3644"},\
	 *   "role":{"roleId":"b85fd129-17d9-40e7-ac11-7541040f8627"},\
	 *   "documentFormat":{"documentFormatId":"30800d77-5fdd-44bc-94a3-1502bd307c1d"},"active":true}' \
	 *   http://localhost:8080/rest/subscriptions
	 *   
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating entity fields that represent related 
	 * entities or entity collections. In addition, we create here multiple
	 * record in the database from different tables and we want everything 
	 * wrapped in a transaction so that we create everything or nothing.
	 */
	@Transactional
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			SubscriptionResource subscriptionResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Subscription subscription = subscriptionService.saveNewFromResource(subscriptionResource);

		/*
		 * Create one SubscriptionParameterValue for each ReportParameter that
		 * is related to the specified ReportVersion for this new Subscription.
		 * These SubscriptionParameterValue objects will be used by the
		 * reporting user to specify values for the report parameters, or to 
		 * specify how the values will be set when the report is run.
		 * 
		 * subscription.getReportVersion() will return a non-null ReportVersion
		 * here because this is enforced when 
		 * subscriptionService.saveNewFromResource(...) is called above (it 
		 * enforces that the Subscription that is created has a many-to-one
		 * relation to a ReportVersion.
		 * 
		 * TODO Should this code be moved somewhere else, e.g., subscriptionService.saveNewFromResource(...)?
		 * I will leave it here for now.
		 */
		List<ReportParameter> reportParameters = subscription.getReportVersion().getReportParameters();
		logger.info("reportParameters = {}", reportParameters);
		for (ReportParameter reportParameter : reportParameters) {
			//SubscriptionParameterValue subscriptionParameterValue = new SubscriptionParameterValue(subscription,
			//		reportParameter);
			/*
			 * Check if there exists one or more RoleParameterValue entities for
			 * this Role / ReportParameter combination. There can be more than 
			 * one if the parameter is multi-valued. If so, we can use it (or
			 * possibly them) to set the value for one or more 
			 * SubscriptionParameterValue entities that we create here.
			 */
		}



		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Subscription.class); // Force primary resource to be "expanded"
		addToExpandList(expand, SubscriptionParameterValue.class); // Also force children to be "expanded"
		// }
		SubscriptionResource resource = new SubscriptionResource(subscription, uriInfo, queryParams, apiVersion);

		return created(resource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/subscriptions/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating subscription.get...s().
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionResource getById(
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
			addToExpandList(expand, Subscription.class);
		}
		Subscription subscription = subscriptionRepository.findOne(id);
		RestUtils.ifNullThen404(subscription, Subscription.class, "subscriptionId", id.toString());
		SubscriptionResource subscriptionResource = new SubscriptionResource(subscription, uriInfo, queryParams,
				apiVersion);
		return subscriptionResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"reportCategory":{"reportCategoryId":"72d7cb27-1770-4cc7-b301-44d39ccf1e76"},\
	 *   "name":"Test Report #04 (modified by PUT)","number":1400,"sortOrder":1400,"active":false}' \
	 *   http://localhost:8080/rest/subscriptions/702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 *   
	 * This updates the subscription with UUID 702d5daa-e23d-4f00-b32b-67b44c06d8f6
	 * with the following changes:
	 * 
	 * report category:	"Q-Free internal"	-> "Traffic"
	 * name:			"Test Report #04"	-> "Test Report #04 (modified by PUT)"
	 * number:			400					-> 1400
	 * sortOrder:		400					-> 1400
	 * active:			true				-> false
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateById(
			SubscriptionResource subscriptionResource,
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
		 * TODO Move most of the code here to saveExistingFromResource(...)?
		 * 
		 * To do this,I would need to pass another parameter to 
		 * saveExistingFromResource(...) so that that method has access to the
		 * entity being updated, subscription. So we could pass either 
		 * "id" or subscription or ...
		 */

		/*
		 * Retrieve Subscription entity to be updated.
		 */
		Subscription subscription = subscriptionRepository.findOne(id);
		RestUtils.ifNullThen404(subscription, Subscription.class, "subscriptionId", id.toString());

		/*
		 * Treat attributes of subscriptionResource that are effectively
		 * required. These attributes can be omitted in the PUT data, but in
		 * that case they are then set here to the CURRENT values from the
		 * subscription entity. These are that attributes that are required,
		 * but if their value does not need to be changed, they do not need to 
		 * be included in the PUT data.
		 */
		// ALLOW email==null:
		// if (subscriptionResource.getEmail() == null) {
		// subscriptionResource.setEmail(subscription.getEmail());
		// }
		if (subscriptionResource.getActive() == null) {
			subscriptionResource.setActive(subscription.getActive());
		}
		if(subscriptionResource.getDocumentFormatResource()==null){
			/*
			 * Construct a DocumentFormatResource to specify the CURRENTLY
			 * selected DocumentFormat.
			 */
			UUID currentDocumentFormatId = subscription.getDocumentFormat().getDocumentFormatId();
			DocumentFormatResource documentFormatResource = new DocumentFormatResource();
			documentFormatResource.setDocumentFormatId(currentDocumentFormatId);
			subscriptionResource.setDocumentFormatResource(documentFormatResource);
		}
		/*
		 * Ensure that a DocumentFormat entity exists corresponding to
		 * subscriptionResource.getDocumentFormatResource():
		 */
		UUID documentFormatId = subscriptionResource.getDocumentFormatResource().getDocumentFormatId();
		if (documentFormatId != null) {
			DocumentFormat documentFormat = documentFormatRepository.findOne(documentFormatId);
			RestUtils.ifNullThen404(documentFormat, DocumentFormat.class, "documentFormatId",
					documentFormatId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_DOCUMENTFORMAT_NULL,
					Subscription.class, "documentFormatId");
		}

		/*
		 * The values for the following attributes cannot be changed. These
		 * attributes should not appear in the PUT data, but if any do, their
		 * values will not be used because they will be overridden here by
		 * forcing their values to be the same as the CURRENT value stored for
		 * the subscription entity.
		 */
		subscriptionResource.setSubscriptionId(subscription.getSubscriptionId());
		subscriptionResource.setCreatedOn(subscription.getCreatedOn());
		/*
		 * Construct a ReportVersionResource to specify the CURRENTLY
		 * selected ReportVersion. We also check that an existing ReportVersion
		 * entity exists for "subscription".
		 */
		if (subscription.getReportVersion() != null) {
			UUID currentReportVersionId = subscription.getReportVersion().getReportVersionId();
			ReportVersion reportVersion = reportVersionRepository.findOne(currentReportVersionId);
			RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId",
					currentReportVersionId.toString());
			ReportVersionResource reportVersionResource = new ReportVersionResource();
			reportVersionResource.setReportVersionId(currentReportVersionId);
			subscriptionResource.setReportVersionResource(reportVersionResource);
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_REPORTVERSION_NULL,
					Subscription.class, "reportVersionId");
		}
		/*
		 * Construct a RoleResource to specify the CURRENTLY selected Role. We 
		 * also check that an existing Role entity exists for "subscription".
		 */
		if (subscription.getRole() != null) {
			UUID currentRoleId = subscription.getRole().getRoleId();
			Role role = roleRepository.findOne(currentRoleId);
			RestUtils.ifNullThen404(role, Role.class, "roleId", currentRoleId.toString());
			RoleResource roleResource = new RoleResource();
			roleResource.setRoleId(currentRoleId);
			subscriptionResource.setRoleResource(roleResource);
		} else {
			throw new RestApiException(RestError.FORBIDDEN_SUBSCRIPTION_ROLE_NULL,
					Subscription.class, "roleId");
		}

		/*
		 * Save updated entity.
		 */
		subscription = subscriptionService.saveExistingFromResource(subscriptionResource);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the SubscriptionParameterValues associated with a single 
	 * Subscription that is specified by its id. This endpoint can be tested 
	 * with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/subscriptions/c7f1d394-9814-4ede-bb01-2700187d79ca/subscriptionParameterValues
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionParameterValueCollectionResource getSubscriptionParameterValuesBySubscriptionId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Subscription subscription = subscriptionRepository.findOne(id);
		RestUtils.ifNullThen404(subscription, Subscription.class, "subscriptionId", id.toString());
		return new SubscriptionParameterValueCollectionResource(subscription, uriInfo, queryParams, apiVersion);
	}
}
