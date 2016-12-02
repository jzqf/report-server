package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.ParameterGroup;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ParameterGroupCollectionResource
		extends AbstractCollectionResource<ParameterGroupResource, ParameterGroup> {

	private static final Logger logger = LoggerFactory.getLogger(ParameterGroupCollectionResource.class);

	@XmlElement
	private List<ParameterGroupResource> items;

	public ParameterGroupCollectionResource() {
	}

	public ParameterGroupCollectionResource(
			List<ParameterGroup> parameterGroups,
			Class<ParameterGroup> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				parameterGroups,
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
			this.items = ParameterGroupResource.parameterGroupResourceListPageFromParameterGroups(
					parameterGroups, uriInfo, queryParams, apiVersion);
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
