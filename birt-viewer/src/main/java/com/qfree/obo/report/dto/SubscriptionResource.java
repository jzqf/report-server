package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.service.SubscriptionService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID subscriptionId;

	@XmlElement(name = "reportVersion")
	private ReportVersionResource reportVersionResource;

	@XmlElement(name = "role")
	private RoleResource roleResource;

	@XmlElement(name = "documentFormat")
	private DocumentFormatResource documentFormatResource;

	@XmlElement
	private String deliveryCronSchedule;

	@XmlElement
	private String deliveryTimeZoneId;

	@XmlElement
	@XmlJavaTypeAdapter(SubscriptionRunAtDateTimeAdapter.class)
	private Date deliveryDatetimeRunAt;

	@XmlElement
	private String email;

	@XmlElement
	private String description;

	@XmlElement(name = "subscriptionParameters")
	private SubscriptionParameterCollectionResource subscriptionParameterCollectionResource;

	@XmlElement
	private Boolean enabled;

	@XmlElement(name = "schedulingStatus")
	private SchedulingStatusResource schedulingStatusResource;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public SubscriptionResource() {
	}

	public SubscriptionResource(Subscription subscription,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		/*
		 * Note on subscriptionService=null here:
		 * 
		 *   null is passed here for subscriptionService. The result of this is
		 *   that the SubscriptionResource that is constructed, will have
		 *   schedulingStatusResource=null, i.e., there will be not information 
		 *   about the status of the scheduling for this Subscription. 
		 *   
		 *   The reason why this constructor exists is that SubscriptionResource
		 *   objects can be constructed in a chain-like fashion when other
		 *   resource types are created. For example, when a 
		 *   DocumentFormatResource is created, it's
		 *   "subscriptionCollectionResource"field (if expanded) will hold a
		 *   potentially large number of SubscriptionCollectionResource objects.
		 *   Here are two points that relate to this "chained" instantiation of
		 *   SubscriptionCollectionResource objects:
		 *   
		 *   1. I am not convinced it is worth the effort to construct a 
		 *      potentially large number of SchedulingStatusResource instances,
		 *      each of which requires several calls to the SchedulerFactoryBean
		 *      or its underlying Quartz Scheduler.
		 *      
		 *   2.	I will need to make extra effort to make a SubscriptionService
		 *      bean available everywhere that this SubscriptionResource
		 *      constructor is called in this chained fashion. While this would
		 *      not be a technically difficult thing to do, it seems messy and
		 *      might not be worth the effort.
		 *      
		 *  If, one day, a use case appears where it is necessary to have the
		 *  schedulingStatusResource field filled out for *every*
		 *  SubscriptionResource that is constructed, even when in a chained 
		 *  fashion as I describe above, then we must simply eliminate this
		 *  constructor and make sure we always provide a SubscriptionService
		 *  to the remaining constructor that requires one.
		 */
		this(subscription, null, uriInfo, queryParams, apiVersion);
	}

	public SubscriptionResource(Subscription subscription, SubscriptionService subscriptionService,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(Subscription.class, subscription.getSubscriptionId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Subscription.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			this.subscriptionId = subscription.getSubscriptionId();

			this.reportVersionResource = new ReportVersionResource(subscription.getReportVersion(),
					uriInfo, newQueryParams, apiVersion);

			this.roleResource = new RoleResource(subscription.getRole(),
					uriInfo, newQueryParams, apiVersion);

			this.documentFormatResource = new DocumentFormatResource(subscription.getDocumentFormat(),
					uriInfo, newQueryParams, apiVersion);

			this.deliveryCronSchedule = subscription.getDeliveryCronSchedule();
			this.deliveryTimeZoneId = subscription.getDeliveryTimeZoneId();
			this.deliveryDatetimeRunAt = subscription.getDeliveryDatetimeRunAt();
			this.email = subscription.getEmail();
			this.description = subscription.getDescription();
			this.enabled = subscription.getEnabled();
			this.active = subscription.getActive();
			this.createdOn = subscription.getCreatedOn();

			/*
			 * Interrogate the Quartz scheduler to obtain details about the 
			 * scheduling state of the Subscription. This information is 
			 * returned as a SchedulingStatusResource object.
			 */
			if (subscriptionService != null) {
				this.schedulingStatusResource = subscriptionService.getSchedulingStatusResource(subscription);
			}

			this.subscriptionParameterCollectionResource = new SubscriptionParameterCollectionResource(
					subscription, uriInfo, newQueryParams, apiVersion);
		}
	}

	public static List<SubscriptionResource> listFromDocumentFormat(DocumentFormat documentFormat, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (documentFormat.getSubscriptions() != null) {
			List<Subscription> subscriptions = documentFormat.getSubscriptions();
			List<SubscriptionResource> subscriptionResources = new ArrayList<>(subscriptions.size());
			for (Subscription subscription : subscriptions) {
				List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				if (subscription.getActive() ||
						RestUtils.FILTER_INACTIVE_RECORDS == false
						|| ResourcePath.showAll(Subscription.class, showAll)) {
					subscriptionResources.add(new SubscriptionResource(subscription, uriInfo, queryParams, apiVersion));
				}
			}
			return subscriptionResources;
		} else {
			return null;
		}

	}

	public static List<SubscriptionResource> listFromRole(Role role, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (role.getSubscriptions() != null) {
			List<Subscription> subscriptions = role.getSubscriptions();
			List<SubscriptionResource> subscriptionResources = new ArrayList<>(subscriptions.size());
			for (Subscription subscription : subscriptions) {
				List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				if (subscription.getActive() ||
						RestUtils.FILTER_INACTIVE_RECORDS == false
						|| ResourcePath.showAll(Subscription.class, showAll)) {
					subscriptionResources.add(new SubscriptionResource(subscription, uriInfo, queryParams, apiVersion));
				}
			}
			return subscriptionResources;
		} else {
			return null;
		}

	}

	public static List<SubscriptionResource> subscriptionResourceListPageFromSubscriptions(
			List<Subscription> subscriptions,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (subscriptions != null) {

			/*
			 * The Subscription entity does not have an "active" field, but if 
			 * it did and if we wanted to return REST resources that correspond
			 * to only active entities, it would be necessary to do one of two 
			 * things *before* we extract a page of Subscription entities below.
			 * Either:
			 * 
			 *   1. Filter the list "subscriptions" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "subscriptions" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Subscription entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "subscriptions"; 
			 * otherwise, we use the whole list.
			 */
			List<Subscription> pageOfSubscriptions = RestUtils.getPageOfList(subscriptions, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<SubscriptionResource> subscriptionResources = new ArrayList<>(pageOfSubscriptions.size());
			for (Subscription subscription : pageOfSubscriptions) {
				/*
				 * We cannot filter out inactive entities here because then the
				 * page size will be variable. Instead, it is necessary to 
				 * filter out inactive entities *before* "pageOfSubscriptions"
				 * is created. I will leave these lines below that are commented
				 * out as a reminder of what can be done if there is no paging.
				 */
				//List<String> showAll = newQueryParams.get(ResourcePath.SHOWALL_QP_KEY);
				//if (subscription.getActive() ||
				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
				//		ResourcePath.showAll(Subscription.class, showAll)) {
				subscriptionResources
						.add(new SubscriptionResource(subscription, uriInfo, queryParamsWOPagination, apiVersion));
				//}
			}
			return subscriptionResources;
		} else {
			return null;
		}
	}

	public UUID getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(UUID subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public ReportVersionResource getReportVersionResource() {
		return reportVersionResource;
	}

	public void setReportVersionResource(ReportVersionResource reportVersionResource) {
		this.reportVersionResource = reportVersionResource;
	}

	public RoleResource getRoleResource() {
		return roleResource;
	}

	public void setRoleResource(RoleResource roleResource) {
		this.roleResource = roleResource;
	}

	public DocumentFormatResource getDocumentFormatResource() {
		return documentFormatResource;
	}

	public void setDocumentFormatResource(DocumentFormatResource documentFormatResource) {
		this.documentFormatResource = documentFormatResource;
	}

	public String getDeliveryCronSchedule() {
		return deliveryCronSchedule;
	}

	public void setDeliveryCronSchedule(String deliveryCronSchedule) {
		this.deliveryCronSchedule = deliveryCronSchedule;
	}

	public String getDeliveryTimeZoneId() {
		return deliveryTimeZoneId;
	}

	public void setDeliveryTimeZoneId(String deliveryTimeZoneId) {
		this.deliveryTimeZoneId = deliveryTimeZoneId;
	}

	public Date getDeliveryDatetimeRunAt() {
		return deliveryDatetimeRunAt;
	}

	public void setDeliveryDatetimeRunAt(Date deliveryDatetimeRunAt) {
		this.deliveryDatetimeRunAt = deliveryDatetimeRunAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public SchedulingStatusResource getSchedulingStatusResource() {
		return schedulingStatusResource;
	}

	public void setSchedulingStatusResource(SchedulingStatusResource schedulingStatusResource) {
		this.schedulingStatusResource = schedulingStatusResource;
	}

	public SubscriptionParameterCollectionResource getSubscriptionParameterCollectionResource() {
		return subscriptionParameterCollectionResource;
	}

	public void setSubscriptionParameterCollectionResource(
			SubscriptionParameterCollectionResource subscriptionParameterCollectionResource) {
		this.subscriptionParameterCollectionResource = subscriptionParameterCollectionResource;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionResource [subscriptionId=");
		builder.append(subscriptionId);
		builder.append(", reportVersionResource=");
		builder.append(reportVersionResource);
		builder.append(", roleResource=");
		builder.append(roleResource);
		builder.append(", documentFormatResource=");
		builder.append(documentFormatResource);
		builder.append(", deliveryCronSchedule=");
		builder.append(deliveryCronSchedule);
		builder.append(", deliveryTimeZoneId=");
		builder.append(deliveryTimeZoneId);
		builder.append(", deliveryDatetimeRunAt=");
		builder.append(deliveryDatetimeRunAt);
		builder.append(", email=");
		builder.append(email);
		builder.append(", description=");
		builder.append(description);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", schedulingStatusResource=");
		builder.append(schedulingStatusResource);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
