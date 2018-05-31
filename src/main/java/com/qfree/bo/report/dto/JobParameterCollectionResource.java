package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.domain.JobParameter;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class JobParameterCollectionResource
		extends AbstractCollectionResource<JobParameterResource, JobParameter> {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterCollectionResource.class);

	@XmlElement
	private List<JobParameterResource> items;

	public JobParameterCollectionResource() {
	}

	public JobParameterCollectionResource(
			Job job,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				job.getJobParameters(),
				JobParameter.class,
				AbstractBaseResource.createHref(uriInfo, Job.class, job.getJobId(), null),
				ResourcePath.JOBPARAMETERS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	//	public JobParameterCollectionResource(
	//			List<JobParameter> jobParameters,
	//			Class<JobParameter> entityClass,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//		this(
	//				jobParameters,
	//				entityClass,
	//				null,
	//				null,
	//				uriInfo,
	//				queryParams,
	//				apiVersion);
	//	}

	public JobParameterCollectionResource(
			List<JobParameter> jobParameters,
			Class<JobParameter> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				jobParameters,
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
			this.items = JobParameterResource.jobParameterResourceListPageFromJobParameters(
					jobParameters, uriInfo, queryParams, apiVersion);
		}
	}

	public List<JobParameterResource> getItems() {
		return items;
	}

	public void setItems(List<JobParameterResource> items) {
		this.items = items;
	}

}
