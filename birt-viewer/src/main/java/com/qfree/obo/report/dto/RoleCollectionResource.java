package com.qfree.obo.report.dto;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class RoleCollectionResource extends AbstractCollectionResource<RoleResource> {
	//public class RoleCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(RoleCollectionResource.class);

	@XmlElement
	private List<RoleResource> items;

	public RoleCollectionResource() {
	}

	public RoleCollectionResource(List<RoleResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<RoleResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		if (expand.contains(expandParam)) {
			this.items = items;
		}
	}

	public List<RoleResource> getItems() {
		return items;
	}

	public void setItems(List<RoleResource> items) {
		this.items = items;
	}

}
