package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterCollectionResource
		extends AbstractCollectionResource<SubscriptionParameterResource> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterCollectionResource.class);

	@XmlElement
	private List<SubscriptionParameterResource> items;

	public SubscriptionParameterCollectionResource() {
	}

	public SubscriptionParameterCollectionResource(Subscription subscription, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				SubscriptionParameterResource.listFromSubscription(subscription, uriInfo,
						queryParams, apiVersion),
				SubscriptionParameter.class,
				AbstractBaseResource.createHref(uriInfo, SubscriptionParameter.class,
						subscription.getSubscriptionId(), null),
				ResourcePath.SUBSCRIPTIONPARAMETERS_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public SubscriptionParameterCollectionResource(
			List<SubscriptionParameterResource> items,
			Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public SubscriptionParameterCollectionResource(
			List<SubscriptionParameterResource> items,
			Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<SubscriptionParameterResource> getItems() {
		return items;
	}

	public void setItems(List<SubscriptionParameterResource> items) {
		this.items = items;
	}

}
