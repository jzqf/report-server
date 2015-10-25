package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobParameterValueCollectionResource
		extends AbstractCollectionResource<JobParameterValueResource> {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterValueCollectionResource.class);

	@XmlElement
	private List<JobParameterValueResource> items;

	public JobParameterValueCollectionResource() {
	}

	public JobParameterValueCollectionResource(JobParameter jobParameter, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				JobParameterValueResource.listFromJobParameter(jobParameter, uriInfo, queryParams, apiVersion),
				JobParameterValue.class,
				AbstractBaseResource.createHref(uriInfo, JobParameter.class, jobParameter.getJobParameterId(), null),
				ResourcePath.JOBPARAMETERVALUES_PATH, uriInfo, queryParams, apiVersion);
	}

	public JobParameterValueCollectionResource(
			List<JobParameterValueResource> items,
			Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public JobParameterValueCollectionResource(
			List<JobParameterValueResource> items,
			Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<JobParameterValueResource> getItems() {
		return items;
	}

	public void setItems(List<JobParameterValueResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobParameterValueCollectionResource [items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}
}
