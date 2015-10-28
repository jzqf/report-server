package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionCollectionResource extends AbstractCollectionResource<SubscriptionResource> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionCollectionResource.class);

	@XmlElement
	private List<SubscriptionResource> items;

	public SubscriptionCollectionResource() {
	}

	public SubscriptionCollectionResource(
			DocumentFormat documentFormat,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				documentFormat.getSubscriptions(),
				//				SubscriptionResource.listFromDocumentFormat(documentFormat, uriInfo, queryParams, apiVersion),
				Subscription.class,
				AbstractBaseResource.createHref(uriInfo, DocumentFormat.class, documentFormat.getDocumentFormatId(),
						null),
				ResourcePath.SUBSCRIPTIONS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public SubscriptionCollectionResource(
			Role role,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				role.getSubscriptions(),
				//				SubscriptionResource.listFromRole(role, uriInfo, queryParams, apiVersion),
				Subscription.class,
				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
				ResourcePath.SUBSCRIPTIONS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public SubscriptionCollectionResource(
			List<Subscription> subscriptions,
			//			List<SubscriptionResource> items,
			Class<?> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				subscriptions,
				//				items,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	//TODO ELMINATE THIS CONSTRUCTOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!111
	//	public SubscriptionCollectionResource(
	//			List<SubscriptionResource> items,
	//			Class<?> entityClass,
	//			String baseResourceUri,
	//			String collectionPath,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//
	//		super(
	//				items,
	//				entityClass,
	//				baseResourceUri,
	//				collectionPath,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//
	//		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
	//		if (ResourcePath.expand(entityClass, expand)) {
	//			this.items = items;
	//		}
	//	}

	public SubscriptionCollectionResource(
			List<Subscription> subscriptions,
			Class<?> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				//	SubscriptionResource.subscriptionResourceListPageFromSubscriptions(
				//			subscriptions, uriInfo, queryParams, apiVersion), // can be set to null here
				999999,
				subscriptions,
				entityClass,
				baseResourceUri,
				collectionPath,
				uriInfo,
				queryParams,
				apiVersion);

		//		init(subscriptions, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			/*
			 * We pass null for apiVersion since the version used in the 
			 * original request does not necessarily apply here.
			 */
			apiVersion = null;
			this.items = SubscriptionResource.subscriptionResourceListPageFromSubscriptions(
					subscriptions, uriInfo, queryParams, apiVersion);
		}
	}

	public List<SubscriptionResource> getItems() {
		return items;
	}

	public void setItems(List<SubscriptionResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionCollectionResource [href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}

}
