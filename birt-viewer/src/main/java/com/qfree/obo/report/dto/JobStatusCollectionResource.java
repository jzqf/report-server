package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class JobStatusCollectionResource extends AbstractCollectionResource<JobStatusResource, JobStatus> {

	private static final Logger logger = LoggerFactory.getLogger(JobStatusCollectionResource.class);

	@XmlElement
	private List<JobStatusResource> items;

	public JobStatusCollectionResource() {
	}

	public JobStatusCollectionResource(
			List<JobStatus> jobStatuses,
			Class<JobStatus> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				jobStatuses,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public JobStatusCollectionResource(
			List<JobStatus> jobStatuses,
			Class<JobStatus> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				jobStatuses,
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
			this.items = JobStatusResource.jobStatusResourceListPageFromJobStatuses(
					jobStatuses, uriInfo, queryParams, apiVersion);
		}
	}

	public List<JobStatusResource> getItems() {
		return items;
	}

	public void setItems(List<JobStatusResource> items) {
		this.items = items;
	}

}
