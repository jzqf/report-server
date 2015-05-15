package com.qfree.obo.report.dto;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportCategoryCollectionResource extends AbstractCollectionResource<ReportCategoryResource> {
	//public class ReportCategoryCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryCollectionResource.class);

	@XmlElement
	private List<ReportCategoryResource> items;

	public ReportCategoryCollectionResource() {
	}

	public ReportCategoryCollectionResource(List<ReportCategoryResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportCategoryResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		if (expand.contains(expandParam)) {
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
