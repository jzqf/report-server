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

import com.qfree.obo.report.domain.Subscription;
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
	private Date runOnceAt;

	@XmlElement
	private String email;

	@XmlElement
	private String description;

	// @XmlElement(name = "subscriptionParameterValues")
	// private SubscriptionParameterValueCollectionResource
	// subscriptionParameterValuesCollectionResource;

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
			// this.subscriptionParameterValuesCollectionResource = new
			// SubscriptionParameterValueCollectionResource(
			// subscription, uriInfo, newQueryParams, apiVersion);
		}
	}

}
