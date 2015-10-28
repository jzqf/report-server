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
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterValueCollectionResource
		extends AbstractCollectionResourceXXXXXX<SubscriptionParameterValueResource, SubscriptionParameterValue> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterValueCollectionResource.class);

	@XmlElement
	private List<SubscriptionParameterValueResource> items;

	public SubscriptionParameterValueCollectionResource() {
	}

	public SubscriptionParameterValueCollectionResource(
			SubscriptionParameter subscriptionParameter,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				subscriptionParameter.getSubscriptionParameterValues(),
				SubscriptionParameterValue.class,
				AbstractBaseResource.createHref(uriInfo, SubscriptionParameter.class,
						subscriptionParameter.getSubscriptionParameterId(), null),
				ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public SubscriptionParameterValueCollectionResource(
			List<SubscriptionParameterValue> subscriptionParameterValues,
			Class<SubscriptionParameterValue> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				subscriptionParameterValues,
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
			this.items = SubscriptionParameterValueResource
					.subscriptionParameterValueResourceListPageFromSubscriptionParameterValues(
							subscriptionParameterValues, uriInfo, queryParams, apiVersion);
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
