package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobCollectionResource extends AbstractCollectionResource<JobResource> {

	private static final Logger logger = LoggerFactory.getLogger(JobCollectionResource.class);

	@XmlElement
	private List<JobResource> items;

	public JobCollectionResource() {
	}

	//	public JobCollectionResource(
	//			DocumentFormat documentFormat,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				JobResource.listFromDocumentFormat(documentFormat, uriInfo, queryParams, apiVersion),
	//				Job.class,
	//				AbstractBaseResource.createHref(uriInfo, DocumentFormat.class, documentFormat.getDocumentFormatId(),
	//						null),
	//				ResourcePath.JOBS_PATH,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

	//	public JobCollectionResource(
	//			Subscription subscription,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				JobResource.listFromSubscription(subscription, uriInfo, queryParams, apiVersion),
	//				Job.class,
	//				AbstractBaseResource.createHref(uriInfo, Subscription.class, subscription.getSubscriptionId(), null),
	//				ResourcePath.JOBS_PATH,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

	//	public JobCollectionResource(
	//			Role role,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				JobResource.listFromRole(role, uriInfo, queryParams, apiVersion),
	//				Job.class,
	//				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
	//				ResourcePath.JOBS_PATH,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

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
			RestApiVersion apiVersion) {
		this(
				subscription.getJobs(),
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
			RestApiVersion apiVersion) {
		this(
				role.getJobs(),
				Job.class,
				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
				ResourcePath.JOBS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	//	public JobCollectionResource(
	//			List<Job> jobs,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				jobs,
	//				Job.class,
	//				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
	//				ResourcePath.JOBS_PATH,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

	public JobCollectionResource(
			List<Job> jobs,
			Class<?> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				JobResource.listFromJobs(jobs, uriInfo, queryParams, apiVersion), // can be set to null here
				entityClass,
				baseResourceUri,
				collectionPath,
				uriInfo,
				queryParams,
				apiVersion);

		this.offset = RestUtils.paginationOffsetQueryParam(queryParams);
		this.limit = RestUtils.paginationLimitQueryParam(queryParams);
		if (this.offset != null && this.limit != null) {

			this.size = jobs.size();

			/*
			 * Remember so I can restore below.
			 */
			List<String> currentPageOffset = queryParams.get(ResourcePath.PAGE_OFFSET_QP_KEY);

			List<String> pageOffset = new ArrayList<>();
			Integer offset;

			offset = 0;
			pageOffset.add(offset.toString());
			logger.info("pageOffset (first) = {}", pageOffset);
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
			logger.info("queryParams (first) = {}", queryParams);
			this.first = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);

			if (this.offset > 0) {
				/*
				 * There is at least one *previous* page that can be requested.
				 */
				offset = this.offset - this.limit;
				if (offset < 0) {
					offset = 0;
				}
				pageOffset.clear(); // reuse list
				pageOffset.add(offset.toString());
				logger.info("pageOffset (previous) = {}", pageOffset);
				queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
				logger.info("queryParams (previous) = {}", queryParams);
				this.previous = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
			}

			if (this.offset < this.size - this.limit) {
				/*
				 * There is at least one more page that can be requested.
				 */
				offset = this.offset + this.limit;
				pageOffset.clear(); // reuse list
				pageOffset.add(offset.toString());
				logger.info("pageOffset (next) = {}", pageOffset);
				queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
				logger.info("queryParams (next) = {}", queryParams);
				this.next = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
			}

			offset = (this.size / this.limit) * this.limit;
			if (offset.equals(this.size)) {
				/*
				 * If the size of the list of Job entities is an exact multiple
				 * of this.limit, then we reduce "offset" by this.limit; 
				 * otherwise, the "last" URI will have no items in it.
				 */
				offset -= this.limit;
			}
			if (offset < 0) {
				offset = 0;
			}
			pageOffset.clear(); // reuse list
			pageOffset.add(offset.toString());
			logger.info("pageOffset (last) = {}", pageOffset);
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
			logger.info("queryParams (last) = {}", queryParams);
			this.last = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);

			/*
			 * Restore.
			 */
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, currentPageOffset);
		}

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = JobResource.listFromJobs(jobs, uriInfo, queryParams, apiVersion);
		}

	}

	//	public JobCollectionResource(
	//			List<JobResource> items,
	//			Class<?> entityClass,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				items,
	//				entityClass,
	//				null,
	//				null,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

	//TODO ELIMINATE THIS CONSTRUCTOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//	public JobCollectionResource(
	//			List<JobResource> items,
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
