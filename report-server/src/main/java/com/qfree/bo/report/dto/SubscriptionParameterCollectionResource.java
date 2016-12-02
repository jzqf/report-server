package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Subscription;
import com.qfree.bo.report.domain.SubscriptionParameter;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterCollectionResource
		extends AbstractCollectionResource<SubscriptionParameterResource, SubscriptionParameter> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterCollectionResource.class);

	@XmlElement
	private List<SubscriptionParameterResource> items;

	public SubscriptionParameterCollectionResource() {
	}

	public SubscriptionParameterCollectionResource(
			Subscription subscription,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				subscription.getSubscriptionParameters(),
				SubscriptionParameter.class,
				AbstractBaseResource.createHref(uriInfo, Subscription.class, subscription.getSubscriptionId(), null),
				ResourcePath.SUBSCRIPTIONPARAMETERS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public SubscriptionParameterCollectionResource(
			List<SubscriptionParameter> subscriptionParameters,
			Class<SubscriptionParameter> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				subscriptionParameters,
				entityClass,
				baseResourceUri,
				collectionPath,
				uriInfo,
				queryParams,
				apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			/*
			 * We pass null for apiVersion since the version used in the 
			 * original request does not necessarily apply here.
			 */
			apiVersion = null;
			this.items = SubscriptionParameterResource.subscriptionParameterResourceListPageFromSubscriptionParameters(
					subscriptionParameters, uriInfo, queryParams, apiVersion);
		}
	}

	public List<SubscriptionParameterResource> getItems() {
		return items;
	}

	public void setItems(List<SubscriptionParameterResource> items) {
		this.items = items;
	}

}
