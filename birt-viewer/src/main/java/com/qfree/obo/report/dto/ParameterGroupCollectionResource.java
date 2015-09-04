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
public class ParameterGroupCollectionResource extends AbstractCollectionResource<ParameterGroupResource> {

	private static final Logger logger = LoggerFactory.getLogger(ParameterGroupCollectionResource.class);

	@XmlElement
	private List<ParameterGroupResource> items;

	public ParameterGroupCollectionResource() {
	}

	public ParameterGroupCollectionResource(List<ParameterGroupResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<ParameterGroupResource> getItems() {
		return items;
	}

	public void setItems(List<ParameterGroupResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterGroupCollectionResource [href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}

}
