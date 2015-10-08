package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterValueCollectionResource
		extends AbstractCollectionResource<SubscriptionParameterValueResource> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterValueCollectionResource.class);

	@XmlElement
	private List<SubscriptionParameterValueResource> items;

	public SubscriptionParameterValueCollectionResource() {
	}

	public SubscriptionParameterValueCollectionResource(SubscriptionParameter subscriptionParameter, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				SubscriptionParameterValueResource.listFromSubscriptionParameter(subscriptionParameter, uriInfo,
						queryParams,
						apiVersion),
				SubscriptionParameterValue.class,
				AbstractBaseResource.createHref(uriInfo, SubscriptionParameter.class,
						subscriptionParameter.getSubscriptionParameterId(),
						null),
				ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public SubscriptionParameterValueCollectionResource(
			List<SubscriptionParameterValueResource> items,
			Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public SubscriptionParameterValueCollectionResource(
			List<SubscriptionParameterValueResource> items,
			Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<SubscriptionParameterValueResource> getItems() {
		return items;
	}

	public void setItems(List<SubscriptionParameterValueResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionParameterValueCollectionResource [items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}
}