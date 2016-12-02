package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.DocumentFormat;
import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.Subscription;
import com.qfree.bo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.bo.report.exceptions.ResourceFilterParseException;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class JobCollectionResource extends AbstractCollectionResource<JobResource, Job> {

	private static final Logger logger = LoggerFactory.getLogger(JobCollectionResource.class);

	@XmlElement
	private List<JobResource> items;

	public JobCollectionResource() {
	}

	public JobCollectionResource(
			List<Job> jobs,
			Class<Job> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) throws ResourceFilterExecutionException, ResourceFilterParseException {
		this(
				//jobs,
				Job.getFilteredJobs(jobs, RestUtils.parseFilterQueryParams(queryParams)),
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public JobCollectionResource(
			DocumentFormat documentFormat,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				documentFormat.getJobs(),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, DocumentFormat.class, documentFormat.getDocumentFormatId(),
						null),
				ResourcePath.JOBS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public JobCollectionResource(
			Subscription subscription,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) throws ResourceFilterExecutionException, ResourceFilterParseException {
		this(
				//subscription.getJobs(),
				subscription.getJobs(RestUtils.parseFilterQueryParams(queryParams)),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, Subscription.class, subscription.getSubscriptionId(), null),
				ResourcePath.JOBS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public JobCollectionResource(
			Role role,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) throws ResourceFilterExecutionException, ResourceFilterParseException {
		this(
				//role.getJobs(),
				role.getJobs(RestUtils.parseFilterQueryParams(queryParams)),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
				ResourcePath.JOBS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public JobCollectionResource(
			List<Job> jobs,
			Class<Job> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				jobs,
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
			this.items = JobResource.jobResourceListPageFromJobs(jobs, uriInfo, queryParams, apiVersion);
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
		builder.append("JobCollectionResource [items=");
		builder.append(items);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", size=");
		builder.append(size);
		builder.append(", first=");
		builder.append(first);
		builder.append(", previous=");
		builder.append(previous);
		builder.append(", next=");
		builder.append(next);
		builder.append(", last=");
		builder.append(last);
		builder.append(", href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append("]");
		return builder.toString();
	}

}
