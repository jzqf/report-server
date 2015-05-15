package com.qfree.obo.report.dto;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ConfigurationCollectionResource extends AbstractCollectionResource<ConfigurationResource> {
	//public class ConfigurationCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationCollectionResource.class);

	@XmlElement
	private List<ConfigurationResource> items;

	public ConfigurationCollectionResource() {
	}

	public ConfigurationCollectionResource(List<ConfigurationResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ConfigurationResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		if (expand.contains(expandParam)) {
			this.items = items;
		}
	}

	public List<ConfigurationResource> getItems() {
		return items;
	}

	public void setItems(List<ConfigurationResource> items) {
		this.items = items;
	}

}
