package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportCategoryCollectionResource extends AbstractCollectionResource<ReportCategoryResource> {
	//public class ReportCategoryCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryCollectionResource.class);

	@XmlElement
	private List<ReportCategoryResource> items;

	public ReportCategoryCollectionResource() {
	}

	public ReportCategoryCollectionResource(List<ReportCategoryResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, queryParams, apiVersion);  // if class extends AbstractCollectionResource<ReportResource>
		//super(entityClass, null, uriInfo, queryParams, apiVersion);  // if class extends AbstractBaseResource

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<ReportCategoryResource> getItems() {
		return items;
	}

	public void setItems(List<ReportCategoryResource> items) {
		this.items = items;
	}

}
