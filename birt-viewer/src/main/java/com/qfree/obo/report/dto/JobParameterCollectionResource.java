package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class JobParameterCollectionResource
		extends AbstractCollectionResource<JobParameterResource> {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterCollectionResource.class);

	@XmlElement
	private List<JobParameterResource> items;

	public JobParameterCollectionResource() {
	}

	public JobParameterCollectionResource(Job job, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				JobParameterResource.listFromJob(job, uriInfo,
						queryParams, apiVersion),
				JobParameter.class,
				AbstractBaseResource.createHref(uriInfo, JobParameter.class,
						job.getJobId(), null),
				ResourcePath.JOBPARAMETERS_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public JobParameterCollectionResource(
			List<JobParameterResource> items,
			Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public JobParameterCollectionResource(
			List<JobParameterResource> items,
			Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<JobParameterResource> getItems() {
		return items;
	}

	public void setItems(List<JobParameterResource> items) {
		this.items = items;
	}

}
