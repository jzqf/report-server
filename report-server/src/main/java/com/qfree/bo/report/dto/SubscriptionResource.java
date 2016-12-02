package com.qfree.bo.report.dto;

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

import com.qfree.bo.report.domain.Subscription;
import com.qfree.bo.report.service.SubscriptionService;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

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
	private String emailAddress;

	@XmlElement
	private String description;

	@XmlElement
	private String customReportParameter1_name;

	@XmlElement
	private String customReportParameter1_value;

	@XmlElement
	private String customReportParameter2_name;

	@XmlElement
	private String customReportParameter2_value;

	@XmlElement
	private String customReportParameter3_name;

	@XmlElement
	private String customReportParameter3_value;

	@XmlElement
	private String customReportParameter4_name;

	@XmlElement
	private String customReportParameter4_value;

	@XmlElement
	private String customReportParameter5_name;

	@XmlElement
	private String customReportParameter5_value;

	@XmlElement
	private String customReportParameter6_name;

	@XmlElement
	private String customReportParameter6_value;

	@XmlElement
	private String customReportParameter7_name;

	@XmlElement
	private String customReportParameter7_value;

	@XmlElement
	private String customReportParameter8_name;

	@XmlElement
	private String customReportParameter8_value;

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

			/*
			 * We do not have access to an AuthorityService bean here, so we
			 * just pass null here. As a result, the RoleResource 
			 * constructed here will not have a "directAuthorities" or an 
			 * "allAuthorities" attribute (they will be null).
			 */
			this.roleResource = new RoleResource(subscription.getRole(), null,
					uriInfo, newQueryParams, apiVersion);

			this.documentFormatResource = new DocumentFormatResource(subscription.getDocumentFormat(),
					uriInfo, newQueryParams, apiVersion);

			this.deliveryCronSchedule = subscription.getDeliveryCronSchedule();
			this.deliveryTimeZoneId = subscription.getDeliveryTimeZoneId();
			this.deliveryDatetimeRunAt = subscription.getDeliveryDatetimeRunAt();
			this.emailAddress = subscription.getEmailAddress();
			this.description = subscription.getDescription();
			this.customReportParameter1_name = subscription.getCustomReportParameter1_name();
			this.customReportParameter1_value = subscription.getCustomReportParameter1_value();
			this.customReportParameter2_name = subscription.getCustomReportParameter2_name();
			this.customReportParameter2_value = subscription.getCustomReportParameter2_value();
			this.customReportParameter3_name = subscription.getCustomReportParameter3_name();
			this.customReportParameter3_value = subscription.getCustomReportParameter3_value();
			this.customReportParameter4_name = subscription.getCustomReportParameter4_name();
			this.customReportParameter4_value = subscription.getCustomReportParameter4_value();
			this.customReportParameter5_name = subscription.getCustomReportParameter5_name();
			this.customReportParameter5_value = subscription.getCustomReportParameter5_value();
			this.customReportParameter6_name = subscription.getCustomReportParameter6_name();
			this.customReportParameter6_value = subscription.getCustomReportParameter6_value();
			this.customReportParameter7_name = subscription.getCustomReportParameter7_name();
			this.customReportParameter7_value = subscription.getCustomReportParameter7_value();
			this.customReportParameter8_name = subscription.getCustomReportParameter8_name();
			this.customReportParameter8_value = subscription.getCustomReportParameter8_value();
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

	public static List<SubscriptionResource> subscriptionResourceListPageFromSubscriptions(
			List<Subscription> subscriptions,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (subscriptions != null) {

			/*
			 * The Subscription has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * Subscription entities below. Either:
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
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				subscriptionResources
						.add(new SubscriptionResource(subscription, uriInfo, queryParamsWOPagination, apiVersion));
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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustomReportParameter1_name() {
		return customReportParameter1_name;
	}

	public void setCustomReportParameter1_name(String customReportParameter1_name) {
		this.customReportParameter1_name = customReportParameter1_name;
	}

	public String getCustomReportParameter1_value() {
		return customReportParameter1_value;
	}

	public void setCustomReportParameter1_value(String customReportParameter1_value) {
		this.customReportParameter1_value = customReportParameter1_value;
	}

	public String getCustomReportParameter2_name() {
		return customReportParameter2_name;
	}

	public void setCustomReportParameter2_name(String customReportParameter2_name) {
		this.customReportParameter2_name = customReportParameter2_name;
	}

	public String getCustomReportParameter2_value() {
		return customReportParameter2_value;
	}

	public void setCustomReportParameter2_value(String customReportParameter2_value) {
		this.customReportParameter2_value = customReportParameter2_value;
	}

	public String getCustomReportParameter3_name() {
		return customReportParameter3_name;
	}

	public void setCustomReportParameter3_name(String customReportParameter3_name) {
		this.customReportParameter3_name = customReportParameter3_name;
	}

	public String getCustomReportParameter3_value() {
		return customReportParameter3_value;
	}

	public void setCustomReportParameter3_value(String customReportParameter3_value) {
		this.customReportParameter3_value = customReportParameter3_value;
	}

	public String getCustomReportParameter4_name() {
		return customReportParameter4_name;
	}

	public void setCustomReportParameter4_name(String customReportParameter4_name) {
		this.customReportParameter4_name = customReportParameter4_name;
	}

	public String getCustomReportParameter4_value() {
		return customReportParameter4_value;
	}

	public void setCustomReportParameter4_value(String customReportParameter4_value) {
		this.customReportParameter4_value = customReportParameter4_value;
	}

	public String getCustomReportParameter5_name() {
		return customReportParameter5_name;
	}

	public void setCustomReportParameter5_name(String customReportParameter5_name) {
		this.customReportParameter5_name = customReportParameter5_name;
	}

	public String getCustomReportParameter5_value() {
		return customReportParameter5_value;
	}

	public void setCustomReportParameter5_value(String customReportParameter5_value) {
		this.customReportParameter5_value = customReportParameter5_value;
	}

	public String getCustomReportParameter6_name() {
		return customReportParameter6_name;
	}

	public void setCustomReportParameter6_name(String customReportParameter6_name) {
		this.customReportParameter6_name = customReportParameter6_name;
	}

	public String getCustomReportParameter6_value() {
		return customReportParameter6_value;
	}

	public void setCustomReportParameter6_value(String customReportParameter6_value) {
		this.customReportParameter6_value = customReportParameter6_value;
	}

	public String getCustomReportParameter7_name() {
		return customReportParameter7_name;
	}

	public void setCustomReportParameter7_name(String customReportParameter7_name) {
		this.customReportParameter7_name = customReportParameter7_name;
	}

	public String getCustomReportParameter7_value() {
		return customReportParameter7_value;
	}

	public void setCustomReportParameter7_value(String customReportParameter7_value) {
		this.customReportParameter7_value = customReportParameter7_value;
	}

	public String getCustomReportParameter8_name() {
		return customReportParameter8_name;
	}

	public void setCustomReportParameter8_name(String customReportParameter8_name) {
		this.customReportParameter8_name = customReportParameter8_name;
	}

	public String getCustomReportParameter8_value() {
		return customReportParameter8_value;
	}

	public void setCustomReportParameter8_value(String customReportParameter8_value) {
		this.customReportParameter8_value = customReportParameter8_value;
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
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		builder.append(", description=");
		builder.append(description);
		builder.append(", customReportParameter1_name=");
		builder.append(customReportParameter1_name);
		builder.append(", customReportParameter1_value=");
		builder.append(customReportParameter1_value);
		builder.append(", customReportParameter2_name=");
		builder.append(customReportParameter2_name);
		builder.append(", customReportParameter2_value=");
		builder.append(customReportParameter2_value);
		builder.append(", customReportParameter3_name=");
		builder.append(customReportParameter3_name);
		builder.append(", customReportParameter3_value=");
		builder.append(customReportParameter3_value);
		builder.append(", customReportParameter4_name=");
		builder.append(customReportParameter4_name);
		builder.append(", customReportParameter4_value=");
		builder.append(customReportParameter4_value);
		builder.append(", customReportParameter5_name=");
		builder.append(customReportParameter5_name);
		builder.append(", customReportParameter5_value=");
		builder.append(customReportParameter5_value);
		builder.append(", customReportParameter6_name=");
		builder.append(customReportParameter6_name);
		builder.append(", customReportParameter6_value=");
		builder.append(customReportParameter6_value);
		builder.append(", customReportParameter7_name=");
		builder.append(customReportParameter7_name);
		builder.append(", customReportParameter7_value=");
		builder.append(customReportParameter7_value);
		builder.append(", customReportParameter8_name=");
		builder.append(customReportParameter8_name);
		builder.append(", customReportParameter8_value=");
		builder.append(customReportParameter8_value);
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
