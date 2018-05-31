package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.service.AuthorityService;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class RoleCollectionResource extends AbstractCollectionResource<RoleResource, Role> {

	private static final Logger logger = LoggerFactory.getLogger(RoleCollectionResource.class);

	@XmlElement
	private List<RoleResource> items;

	public RoleCollectionResource() {
	}

	public RoleCollectionResource(
			List<Role> roles,
			Class<Role> entityClass,
			AuthorityService authorityService,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				roles,
				entityClass,
				authorityService,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public RoleCollectionResource(
			List<Role> roles,
			Class<Role> entityClass,
			AuthorityService authorityService,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				roles,
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
			this.items = RoleResource.roleResourceListPageFromRoles(roles, authorityService,
					uriInfo, queryParams, apiVersion);
		}
	}

	public List<RoleResource> getItems() {
		return items;
	}

	public void setItems(List<RoleResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleCollectionResource [href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append(", items=");
		builder.append(items);
		builder.append("]");
		return builder.toString();
	}

}
