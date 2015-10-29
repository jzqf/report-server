package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ConfigurationCollectionResource
		extends AbstractCollectionResource<ConfigurationResource, Configuration> {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationCollectionResource.class);

	@XmlElement
	private List<ConfigurationResource> items;

	public ConfigurationCollectionResource() {
	}

	public ConfigurationCollectionResource(
			List<Configuration> configurations,
			Class<Configuration> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				configurations,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public ConfigurationCollectionResource(
			List<Configuration> configurations,
			Class<Configuration> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				configurations,
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
			this.items = ConfigurationResource.configurationResourceListPageFromConfigurations(
					configurations, uriInfo, queryParams, apiVersion);
		}
	}

	public List<ConfigurationResource> getItems() {
		return items;
	}

	public void setItems(List<ConfigurationResource> items) {
		this.items = items;
	}

}
