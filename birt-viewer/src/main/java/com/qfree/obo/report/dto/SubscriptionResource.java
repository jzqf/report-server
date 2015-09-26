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
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

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
	private String cronSchedule;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date runOnceAt;

	@XmlElement
	private String email;

	@XmlElement
	private String description;

	@XmlElement(name = "subscriptionParameters")
	private SubscriptionParameterCollectionResource subscriptionParameterCollectionResource;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public SubscriptionResource() {
	}

	public SubscriptionResource(Subscription subscription, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

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

			this.cronSchedule = subscription.getCronSchedule();
			this.runOnceAt = subscription.getRunOnceAt();
			this.email = subscription.getEmail();
			this.description = subscription.getDescription();
			this.active = subscription.getActive();
			this.createdOn = subscription.getCreatedOn();
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
						RestUtils.FILTER_INACTIVE_RECORDS == false ||
						ResourcePath.showAll(Subscription.class, showAll)) {
					subscriptionResources.add(
							new SubscriptionResource(subscription, uriInfo, queryParams, apiVersion));
				}
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

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public Date getRunOnceAt() {
		return runOnceAt;
	}

	public void setRunOnceAt(Date runOnceAt) {
		this.runOnceAt = runOnceAt;
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
		builder.append(", cronSchedule=");
		builder.append(cronSchedule);
		builder.append(", runOnceAt=");
		builder.append(runOnceAt);
		builder.append(", email=");
		builder.append(email);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
