package com.qfree.obo.report.dto;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportVersionCollectionResource extends AbstractCollectionResource<ReportVersionResource> {
	//public class ReportVersionCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionCollectionResource.class);

	@XmlElement
	private List<ReportVersionResource> items;

	public ReportVersionCollectionResource() {
	}

	public ReportVersionCollectionResource(List<ReportVersionResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportVersionResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		if (expand.contains(expandParam)) {
			this.items = items;
		}
	}

	public List<ReportVersionResource> getItems() {
		return items;
	}

	public void setItems(List<ReportVersionResource> items) {
		this.items = items;
	}

}
