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
public class ReportCollectionResource extends AbstractCollectionResource<ReportResource> {
	//public class ReportCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportCollectionResource.class);

	@XmlElement
	private List<ReportResource> items;

	public ReportCollectionResource() {
	}

	public ReportCollectionResource(List<ReportResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, queryParams, apiVersion);  // if class extends AbstractCollectionResource<ReportResource>
		//super(entityClass, null, uriInfo, queryParams, apiVersion);  // if class extends AbstractBaseResource

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<ReportResource> getItems() {
		return items;
	}

	public void setItems(List<ReportResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportCollectionResource [href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}

}
