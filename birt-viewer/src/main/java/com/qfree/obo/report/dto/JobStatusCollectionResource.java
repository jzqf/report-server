package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobStatusCollectionResource extends AbstractCollectionResource<JobStatusResource> {
	//public class JobStatusCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(JobStatusCollectionResource.class);

	@XmlElement
	private List<JobStatusResource> items;

	public JobStatusCollectionResource() {
	}

	public JobStatusCollectionResource(List<JobStatusResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<JobStatusResource> getItems() {
		return items;
	}

	public void setItems(List<JobStatusResource> items) {
		this.items = items;
	}

}
