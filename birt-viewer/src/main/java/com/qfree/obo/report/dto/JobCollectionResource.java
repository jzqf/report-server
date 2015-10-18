package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobCollectionResource extends AbstractCollectionResource<JobResource> {

	private static final Logger logger = LoggerFactory.getLogger(JobCollectionResource.class);

	@XmlElement
	private List<JobResource> items;

	public JobCollectionResource() {
	}

	public JobCollectionResource(DocumentFormat documentFormat, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				JobResource.listFromDocumentFormat(documentFormat, uriInfo, queryParams, apiVersion),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, DocumentFormat.class, documentFormat.getDocumentFormatId(),
						null),
				ResourcePath.JOBS_PATH, uriInfo, queryParams, apiVersion);
	}

	public JobCollectionResource(Subscription subscription, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				JobResource.listFromSubscription(subscription, uriInfo, queryParams, apiVersion),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, Subscription.class, subscription.getSubscriptionId(), null),
				ResourcePath.JOBS_PATH, uriInfo, queryParams, apiVersion);
	}

	public JobCollectionResource(List<JobResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public JobCollectionResource(List<JobResource> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<JobResource> getItems() {
		return items;
	}

	public void setItems(List<JobResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobCollectionResource [href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}

}
