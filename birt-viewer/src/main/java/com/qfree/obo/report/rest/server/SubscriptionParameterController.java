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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.RoleParameterRepository;
import com.qfree.obo.report.db.SubscriptionParameterRepository;
import com.qfree.obo.report.domain.RoleParameter;
import com.qfree.obo.report.domain.RoleParameterValue;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.SubscriptionParameterResource;
import com.qfree.obo.report.dto.SubscriptionParameterValueCollectionResource;
import com.qfree.obo.report.dto.SubscriptionParameterValueResource;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.SUBSCRIPTIONPARAMETERS_PATH)
public class SubscriptionParameterController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterController.class);

	private final SubscriptionParameterRepository subscriptionParameterRepository;
	private final RoleParameterRepository roleParameterRepository;
	//	private final SubscriptionParameterValueRepository subscriptionParameterValueRepository;
	//	private final SubscriptionRepository subscriptionRepository;
	//	private final ReportParameterRepository reportParameterRepository;

	@Autowired
	public SubscriptionParameterController(
			SubscriptionParameterRepository subscriptionParameterRepository,
			RoleParameterRepository roleParameterRepository
	//SubscriptionParameterValueRepository subscriptionParameterValueRepository,
	//SubscriptionRepository subscriptionRepository,
	//ReportParameterRepository reportParameterRepository
	) {
		this.subscriptionParameterRepository = subscriptionParameterRepository;
		this.roleParameterRepository = roleParameterRepository;
		//	this.subscriptionParameterValueRepository = subscriptionParameterValueRepository;
		//	this.subscriptionRepository = subscriptionRepository;
		//	this.reportParameterRepository = reportParameterRepository;
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	//	 *   http://localhost:8080/rest/subscriptionParameters?expand=subscriptionParameters
	//	 * 
	//	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	//	 * being thrown.
	//	 */
	//	@Transactional
	//	@GET
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public SubscriptionParameterCollectionResource getList(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		List<SubscriptionParameter> subscriptionParameters = null;
	//		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(SubscriptionParameter.class, showAll)) {
	//			subscriptionParameters = subscriptionParameterRepository.findByActiveTrue();
	//		} else {
	//			subscriptionParameters = subscriptionParameterRepository.findAll();
	//		}
	//		List<SubscriptionParameterResource> subscriptionParameterResources = new ArrayList<>(
	//				subscriptionParameters.size());
	//		for (SubscriptionParameter subscriptionParameter : subscriptionParameters) {
	//			subscriptionParameterResources
	//					.add(new SubscriptionParameterResource(subscriptionParameter, uriInfo, queryParams, apiVersion));
	//		}
	//		return new SubscriptionParameterCollectionResource(subscriptionParameterResources, SubscriptionParameter.class,
	//				uriInfo, queryParams, apiVersion);
	//	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   
	//	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d '{\
	//	 *   "reportVersion":{"reportVersionId":"afd8777f-b6a2-4cb8-8dc2-887c47af3644"},\
	//	 *   "role":{"roleId":"b85fd129-17d9-40e7-ac11-7541040f8627"},\
	//	 *   "documentFormat":{"documentFormatId":"30800d77-5fdd-44bc-94a3-1502bd307c1d"}}' \
	//	 *   http://localhost:8080/rest/subscriptionParameters
	//	 *   
	//	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	//	 * being thrown when evaluating entity fields that represent related 
	//	 * entities or entity collections. In addition, we create here multiple
	//	 * record in the database from different tables and we want everything 
	//	 * wrapped in a transaction so that we create everything or nothing.
	//	 */
	//	@Transactional
	//	@POST
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public Response create(
	//			SubscriptionParameterResource subscriptionParameterResource,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		SubscriptionParameter subscriptionParameter = subscriptionParameterService
	//				.saveNewFromResource(subscriptionParameterResource);
	//
	//		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//		addToExpandList(expand, SubscriptionParameter.class); // Force primary resource to be "expanded"
	//		addToExpandList(expand, SubscriptionParameterValue.class); // Also force children to be "expanded"
	//		// }
	//		SubscriptionParameterResource resource = new SubscriptionParameterResource(subscriptionParameter, uriInfo,
	//				queryParams,
	//				apiVersion);
	//
	//		return created(resource);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/subscriptionParameters/c7f1d394-9814-4ede-bb01-2700187d79ca
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionParameterResource getById(
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
			addToExpandList(expand, SubscriptionParameter.class);
		}
		SubscriptionParameter subscriptionParameter = subscriptionParameterRepository.findOne(id);
		RestUtils.ifNullThen404(subscriptionParameter, SubscriptionParameter.class, "subscriptionParameterId",
				id.toString());

		SubscriptionParameterResource subscriptionParameterResource = new SubscriptionParameterResource(
				subscriptionParameter, uriInfo, queryParams, apiVersion);

		return subscriptionParameterResource;
	}

	/*
	 * There are no updateable fields of SubscriptionParameter that represent 
	 * quantities that represent columns of the [subscription_parameter] 
	 * database table. Nevertheless, this PUT endpoint still "updates" a
	 * SubscriptionParameterResource. The SubscriptionParameterResource should
	 * have a "subscriptionParameterValues" attribute which deserializes into
	 * a SubscriptionParameterValueCollectionResource. The 
	 * SubscriptionParameterValueResource objects in its "items" field are used
	 * to set the SubscriptionParameterValue entities that are related to the
	 * SubscriptionParameter associated with the SubscriptionParameterResource
	 * provided to this endpoint. If there are currently other
	 * SubscriptionParameterValue entities related to this 
	 * SubscriptionParameter, they are deleted.
	 * 
	 * Hence, this SubscriptionParameter PUT endpoint actually acts on
	 * SubscriptionParameterValue entities. It updates the 
	 * [subscription_parameter_value] table. I think that its behaviour should
	 * be considered idempotent, so PUT is the appropriate HTTP method.
	 * 
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"subscriptionParameterValues": {"items": [{"integerValue":123},{"integerValue":500},{"integerValue":999}]}}' \
	 *   http://localhost:8080/rest/subscriptionParameters/5f8036cc-cfc3-4e3f-99f4-0069238c152c
	 *   
	 * This "updates" the SubscriptionParameter with UUID 5f8036cc-cfc3-4e3f-99f4-0069238c152c
	 * so that it has 3 linked SubscriptionParameterValue entities. These 3
	 * SubscriptionParameterValue entities specify the integer values :
	 * 
	 *   integerValue = 123
	 *   integerValue = 500
	 *   integerValue = 999
	 * 
	 * Note: The format for specifying values for the "datetimeValue" attribute
	 *       depends on a configuration parameter. Currently, this parameter is:
	 *       DatetimeReportParameterDateTimeAdapter.DATETIME_REPORT_PARAMETERS_NO_TIMEZONE = true
	 *       This means that "datetimeValue" attribute values should be expressed
	 *       as "local datetime values", e.g.,
	 *       
	 *       "datetimeValue":"2016-09-24T22:30:00.000"
	 *       
	 *       This requires that the query associated with the report is written
	 *       to assume a particular time zone.
	 *       
	 *       If, however, this configuration parameter is changed to false, use:
	 *       
	 *       "datetimeValue":"2016-09-24T22:30:00.000Z"
	 *       
	 *       This requires that the query associated with the report is written
	 *       to assume that the datetime value is expressed relative to GMT/UTC.
	 *       
	 *       Note that these two datetime values represent different instants in
	 *       time, unless the server's time zone happens to be GMT/UTC.
	 *   
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionParameterValueCollectionResource updateSubscriptionParameterValuesBySubscriptionParameterId(
			SubscriptionParameterResource subscriptionParameterResource,
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

		logger.debug("id = {}", id);
		logger.debug("subscriptionParameterResource = {}", subscriptionParameterResource);

		/*
		 * TODO Move most of the code here to saveExistingFromResource(...)?
		 * 
		 * To do this,I would need to pass another parameter to 
		 * saveExistingFromResource(...) so that that method has access to the
		 * entity being updated, subscriptionParameter. So we could pass either 
		 * "id" or subscriptionParameter or ...
		 */

		/*
		 * Retrieve SubscriptionParameter entity to be updated.
		 */
		SubscriptionParameter subscriptionParameter = subscriptionParameterRepository.findOne(id);
		RestUtils.ifNullThen404(subscriptionParameter, SubscriptionParameter.class, "subscriptionParameterId",
				id.toString());

		/*
		 * All of the modifications to subscriptionParameterResource below are
		 * not really necessary because we do not use this object update and 
		 * save the associated SubscriptionParameter entity in this method. But
		 * in case we do do this one day, I have kept the code, but commented it
		 * out.
		 */

		//	/*
		//	 * Treat attributes of subscriptionParameterResource that are effectively
		//	 * required. These attributes can be omitted in the PUT data, but in
		//	 * that case they are then set here to the CURRENT values from the
		//	 * subscriptionParameter entity. These are that attributes that are required,
		//	 * but if their value does not need to be changed, they do not need to 
		//	 * be included in the PUT data.
		//	 */
		//	// There are currently no required attributes.
		//
		//	/*
		//	 * The values for the following attributes cannot be changed. These
		//	 * attributes should not appear in the PUT data, but if any do, their
		//	 * values will not be used because they will be overridden here by
		//	 * forcing their values to be the same as the CURRENT value stored for
		//	 * the subscriptionParameter entity.
		//	 */
		//	subscriptionParameterResource.setSubscriptionParameterId(subscriptionParameter.getSubscriptionParameterId());
		//	subscriptionParameterResource.setCreatedOn(subscriptionParameter.getCreatedOn());
		//	/*
		//	 * Construct a SubscriptionResource to specify the CURRENTLY
		//	 * selected Subscription. We also check that an existing Subscription
		//	 * entity exists for "subscriptionParameter".
		//	 */
		//	UUID currentSubscriptionId = subscriptionParameter.getSubscription().getSubscriptionId();
		//	Subscription subscription = subscriptionRepository.findOne(currentSubscriptionId);
		//	RestUtils.ifNullThen404(subscription, Subscription.class, "subscriptionId",
		//			currentSubscriptionId.toString());
		//	SubscriptionResource subscriptionResource = new SubscriptionResource();
		//	subscriptionResource.setSubscriptionId(currentSubscriptionId);
		//	subscriptionParameterResource.setSubscriptionResource(subscriptionResource);
		//	/*
		//	 * Construct a ReportParameterResource to specify the CURRENTLY selected ReportParameter. We 
		//	 * also check that an existing ReportParameter entity exists for "subscriptionParameter".
		//	 */
		//	UUID currentReportParameterId = subscriptionParameter.getReportParameter().getReportParameterId();
		//	ReportParameter reportParameter = reportParameterRepository.findOne(currentReportParameterId);
		//	RestUtils.ifNullThen404(reportParameter, ReportParameter.class, "reportParameterId",
		//			currentReportParameterId.toString());
		//	ReportParameterResource reportParameterResource = new ReportParameterResource();
		//	reportParameterResource.setReportParameterId(currentReportParameterId);
		//	subscriptionParameterResource.setReportParameterResource(reportParameterResource);

		/*
		 * Process the subscriptionParameterResource payload:
		 */
		SubscriptionParameterValueCollectionResource subscriptionParameterValueCollectionResourcePayload = subscriptionParameterResource
				.getSubscriptionParameterValuesCollectionResource();
		if (subscriptionParameterValueCollectionResourcePayload != null) {
			List<SubscriptionParameterValueResource> subscriptionParameterValueResourcesFromPayload = subscriptionParameterValueCollectionResourcePayload
					.getItems();
			if (subscriptionParameterValueResourcesFromPayload != null
					&& subscriptionParameterValueResourcesFromPayload.size() > 0) {

				/*
				 * If multivalued=true for the report parameter, there can be 
				 * more than one SubscriptionParameterValueResource object; 
				 * otherwise, there can be a maximum of only one.
				 */
				if (subscriptionParameterValueResourcesFromPayload.size() > 1
						&& !subscriptionParameter.getReportParameter().getMultivalued()) {
					String errorMessage = String.format(
							"%s values provided for the single-valued report parameter '%s'",
							subscriptionParameterValueResourcesFromPayload.size(),
							subscriptionParameter.getReportParameter().getName());
					throw new RestApiException(RestError.FORBIDDEN_MULTIPLE_VALUES_FOR_PARAM, errorMessage,
							SubscriptionParameterValue.class);
				}

				/*
				 * Delete the current selection of SubscriptionParameterValue
				 * entities associated with the SubscriptionParameter.
				 */
				if (subscriptionParameter.getSubscriptionParameterValues() != null &&
						subscriptionParameter.getSubscriptionParameterValues().size() > 0) {
					logger.debug("Deleting {} SubscriptionParameterValue entities in list: {}",
							subscriptionParameter.getSubscriptionParameterValues().size(),
							subscriptionParameter.getSubscriptionParameterValues());
					/*
					* The one-to-many link from the SubscriptionParameter entity
					* class to the SubscriptionParameterValue entity class has
					* "cascade = CascadeType.ALL". This means that in order to
					* delete individual SubscriptionParameterValue entities, it 
					* is necessary to first remove them from the list
					* subscriptionParameter.getSubscriptionParameterValues().
					* But this relation also has "orphanRemoval = true", so when 
					* I remove the SubscriptionParameterValue entities from
					* the list here with "clear()", JPA will also ensure that
					* they get deleted from the underlying database.
					* 
					* So it is not necessary to also execute:
					* 
					* subscriptionParameterValueRepository.delete(subscriptionParameter.getSubscriptionParameterValues());
					*/
					subscriptionParameter.getSubscriptionParameterValues().clear();
				} else {
					logger.debug("No SubscriptionParameterValue entities to be deleted.");
				}

				/*
				 * Defensive programming so that I know this attribute is an
				 * empty list below and not null.
				 */
				if (subscriptionParameter.getSubscriptionParameterValues() == null) {
					subscriptionParameter.setSubscriptionParameterValues(new ArrayList<SubscriptionParameterValue>());
				}

				/*
				 * Create a new selection of SubscriptionParameterValue entities
				 * entities from the list of SubscriptionParameterValueResource
				 * objects provided in the PUT data.
				 */
				for (SubscriptionParameterValueResource subscriptionParameterValueResource : subscriptionParameterValueResourcesFromPayload) {

					logger.debug("subscriptionParameterValueResource (for creating SubscriptionParameterValue) = {}",
							subscriptionParameterValueResource);
					/*
					 * This transfers the attributes of 
					 * subscriptionParameterValueResource to the newly
					 * constructed SubscriptionParameterValue.
					 */
					SubscriptionParameterValue subscriptionParameterValue = new SubscriptionParameterValue(
							subscriptionParameter, subscriptionParameterValueResource);
					logger.debug("subscriptionParameterValue (to persist) = {}", subscriptionParameterValue);

					/*
					 * Check that the data type of the supplied values agree 
					 * with the report parameter data type. This large block
					 * of code does not assign any values - it just checks 
					 * that the necessary attributes of 
					 * subscriptionParameterValueResource are not null, 
					 * depending on the data type of the repory parameter.
					 */
					switch (subscriptionParameter.getReportParameter().getDataType()) {
					case IParameterDefn.TYPE_ANY:
						/*
						 * Will this case occur? We treat it here, nevertheless.
						 */
						if (subscriptionParameterValueResource.getBooleanValue() == null &&
								subscriptionParameterValueResource.getDateValue() == null &&
								subscriptionParameterValueResource.getDatetimeValue() == null &&
								subscriptionParameterValueResource.getFloatValue() == null &&
								subscriptionParameterValueResource.getIntegerValue() == null &&
								subscriptionParameterValueResource.getStringValue() == null &&
								subscriptionParameterValueResource.getTimeValue() == null) {
							String errorMessage = "One of the attributes must be not null: 'booleanValue', 'dateValue', 'datetimeValue', 'floatValue', 'integerValue', 'stringValue', 'timeValue'";
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL, errorMessage,
									SubscriptionParameterValue.class);
						}
						break;
					case IParameterDefn.TYPE_STRING:
						if (subscriptionParameterValueResource.getStringValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "stringValue");
						}
						break;
					case IParameterDefn.TYPE_FLOAT:
						if (subscriptionParameterValueResource.getFloatValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "floatValue");
						}
						break;
					case IParameterDefn.TYPE_DECIMAL:
						/*
						 * Assume that we can treat parameters of data type
						 * "decimal" as floats. This may not be so, but we will
						 * give this a try.
						 */
						if (subscriptionParameterValueResource.getFloatValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "floatValue");
						}
						break;
					case IParameterDefn.TYPE_DATE_TIME:
						if (subscriptionParameterValue.getYearNumber() != null ||
								subscriptionParameterValue.getYearsAgo() != null ||
								subscriptionParameterValue.getMonthNumber() != null ||
								subscriptionParameterValue.getMonthsAgo() != null ||
								subscriptionParameterValue.getWeeksAgo() != null ||
								subscriptionParameterValue.getDaysAgo() != null ||
								subscriptionParameterValue.getDayOfWeekNumber() != null ||
								subscriptionParameterValue.getDayOfMonthNumber() != null ||
								(subscriptionParameterValue.getDayOfWeekInMonthOrdinal() != null &&
										subscriptionParameterValue.getDayOfWeekInMonthNumber() != null)) {
							/*
							 * OK, we will be computing a dynamic value for the datetime.
							 */
						} else {
							/*
							 * We are *not* computing a dynamic value for the datetime,
							 * so there must be a static value assigned to the 
							 * attribute "datetimeValue".
							 */
							if (subscriptionParameterValueResource.getDatetimeValue() == null) {
								throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
										SubscriptionParameterValue.class, "datetimeValue");
							}
						}
						break;
					case IParameterDefn.TYPE_BOOLEAN:
						if (subscriptionParameterValueResource.getBooleanValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "booleanValue");
						}
						break;
					case IParameterDefn.TYPE_INTEGER:
						if (subscriptionParameterValueResource.getIntegerValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "integerValue");
						}
						break;
					case IParameterDefn.TYPE_DATE:
						if (subscriptionParameterValue.getYearNumber() != null ||
								subscriptionParameterValue.getYearsAgo() != null ||
								subscriptionParameterValue.getMonthNumber() != null ||
								subscriptionParameterValue.getMonthsAgo() != null ||
								subscriptionParameterValue.getWeeksAgo() != null ||
								subscriptionParameterValue.getDaysAgo() != null ||
								subscriptionParameterValue.getDayOfWeekNumber() != null ||
								subscriptionParameterValue.getDayOfMonthNumber() != null ||
								(subscriptionParameterValue.getDayOfWeekInMonthOrdinal() != null &&
										subscriptionParameterValue.getDayOfWeekInMonthNumber() != null)) {
							/*
							 * OK, we will be computing a dynamic value for the date.
							 */
						} else {
							/*
							 * We are *not* computing a dynamic value for the date,
							 * so there must be a static value assigned to the 
							 * attribute "datetimeValue".
							 */
							if (subscriptionParameterValueResource.getDateValue() == null) {
								throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
										SubscriptionParameterValue.class, "dateValue");
							}
						}
						break;
					case IParameterDefn.TYPE_TIME:
						if (subscriptionParameterValueResource.getTimeValue() == null) {
							throw new RestApiException(RestError.FORBIDDEN_ATTRIBUTE_NULL,
									SubscriptionParameterValue.class, "timeValue");
						}
						break;
					default:
						String errorMessage = String.format(
								"subscriptionParameter.getReportParameter().getDataType() = %s",
								subscriptionParameter.getReportParameter().getDataType());
						throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_UNTREATED_CASE, errorMessage,
								SubscriptionParameterValue.class);
					}

					subscriptionParameter.getSubscriptionParameterValues().add(subscriptionParameterValue);
				}

				/*
				 * Save updated entity. This should create in the database the 
				 * SubscriptionParameterValue entities that were added to the
				 * list: subscriptionParameter.getSubscriptionParameterValues().
				 */
				subscriptionParameter = subscriptionParameterRepository.save(subscriptionParameter);

				/*
				 * Use the list of new SubscriptionParameterValue entities just
				 * created to update the RoleParameterValue entities that are
				 * used to record "last used" values. 
				 * 
				 * If no matching RoleParameter entity exists, we create a new
				 * one.
				 * 
				 * If a matching RoleParameter entity does exist, we re-used it,
				 * but all of its existing related RoleParameterValue entities 
				 * will be deleted (because they will be replaced with the new
				 * RoleParameterValue entities we will create here).
				 */
				// TODO Test this query thoroughly!
				RoleParameter roleParameter = roleParameterRepository.findByRoleAndReportParameter(
						subscriptionParameter.getSubscription().getRole().getRoleId(),
						subscriptionParameter.getReportParameter().getReportParameterId());
				if (roleParameter == null) {
					/*
					 * Since no matching RoleParameter entity exists, we create 
					 * a new one here.
					 */
					roleParameter = new RoleParameter(
							subscriptionParameter.getSubscription().getRole(),
							subscriptionParameter.getReportParameter());
					if (roleParameter.getRoleParameterValues() == null) {
						roleParameter.setRoleParameterValues(new ArrayList<RoleParameterValue>());
					}
				} else {
					if (roleParameter.getRoleParameterValues() == null) {
						logger.info(
								"roleParameter.getRoleParameterValues()==null for EXISTING RoleParameter. MUST CREATE EMPTY LIST !!!!!!!!!!!!!!!!!!!!!!!!!!");
						roleParameter.setRoleParameterValues(new ArrayList<RoleParameterValue>());
					} else {
						logger.info("roleParameter.getRoleParameterValues()!=null for EXISTING RoleParameter");
					}
				}
				/*
				 * The one-to-many link from the RoleParameter entity class to 
				 * the RoleParameterValue entity class has; 
				 * 
				 *   "orphanRemoval = true" , 
				 *   
				 * so when I remove any existing RoleParameterValue entities 
				 * from the list here with "clear()", JPA will also ensure that
				 * they get deleted from the underlying database.
				 */
				roleParameter.getRoleParameterValues().clear();
				/*
				 * Now add one new RoleParameterValue entity to the list
				 * roleParameter.getRoleParameterValues() for each 
				 * SubscriptionParameterValue created above. These will act as
				 * role-specific "last used" values for the report parameter.
				 */
				for (SubscriptionParameterValue subscriptionParameterValue : subscriptionParameter
						.getSubscriptionParameterValues()) {
					roleParameter.getRoleParameterValues()
							.add(new RoleParameterValue(roleParameter, subscriptionParameterValue));
				}
				/*
				 * Save updated entity. This should create in the database the 
				 * RoleParameterValue entities that were added to the list:
				 * roleParameter.getRoleParameterValues().
				 */
				roleParameter = roleParameterRepository.save(roleParameter);

			} else {
				/*
				 * If required=true for the report parameter, there must be at
				 * *least* one SubscriptionParameterValueResource. However, we
				 * do not bother to check the value of "required" here, and we
				 * do not throw an exception if required=true and there is not
				 * at least one SubscriptionParameterValueResource. But neither
				 * do we delete any current SubscriptionParameterValue entities.
				 * So, if no SubscriptionParameterValueResource entities are
				 * provided, we do nothing, which will preserve the current
				 * selection of current SubscriptionParameterValue entities, 
				 * if there are any.
				 */
				logger.warn("No SubscriptionParameterValueResource objects provided in PUT data.");
			}
		}

		logger.debug("subscriptionParameter = {}", subscriptionParameter);

		/*
		 * Save updated entity.
		 * 
		 * 
		 */
		//subscriptionParameter = subscriptionParameterService.saveExistingFromResource(subscriptionParameterResource);

		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, SubscriptionParameterValue.class); // Force primary resource to be "expanded"
		// }
		SubscriptionParameterValueCollectionResource subscriptionParameterValueCollectionResource = new SubscriptionParameterValueCollectionResource(
				subscriptionParameter, uriInfo, queryParams, apiVersion);

		return subscriptionParameterValueCollectionResource;
	}

	/*
	 * Return the SubscriptionParameterValue entities associated with a single 
	 * SubscriptionParameter that is specified by its id. This endpoint can be tested 
	 * with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/subscriptionParameters/c7f1d394-9814-4ede-bb01-2700187d79ca/subscriptionParameterValues
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionParameterValueCollectionResource getSubscriptionParameterValuesBySubscriptionParameterId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		SubscriptionParameter subscriptionParameter = subscriptionParameterRepository.findOne(id);
		RestUtils.ifNullThen404(subscriptionParameter, SubscriptionParameter.class, "subscriptionParameterId",
				id.toString());
		return new SubscriptionParameterValueCollectionResource(subscriptionParameter, uriInfo, queryParams,
				apiVersion);
	}
}
