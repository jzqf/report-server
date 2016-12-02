package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.service.AuthorityService;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class AuthorityCollectionResource extends AbstractCollectionResource<AuthorityResource, Authority> {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityCollectionResource.class);

	@XmlElement
	private List<AuthorityResource> items;

	public AuthorityCollectionResource() {
	}

	public AuthorityCollectionResource(
			Role role,
			boolean includeInheritedAuthorities,
			AuthorityService authorityService,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				includeInheritedAuthorities ?
						/*
						 * This returns all Authorities linked to the Role, 
						 * either directly or indirectly (If PostgreSQL is used
						 * - H2 does not support recursive CTEs):
						 */
						authorityService.getActiveAuthoritiesByRoleId(role.getRoleId())
						:
						/*
						 * This returns all Authorities linked *directly* to the
						 * Role. Role inheritance is *not* used:
						 */
						authorityService.getActiveAuthoritiesByRoleIdDirect(role.getRoleId()),
				Authority.class,
				AbstractBaseResource.createHref(uriInfo, Role.class, role.getRoleId(), null),
				ResourcePath.AUTHORITIES_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public AuthorityCollectionResource(
			List<Authority> authorities,
			Class<Authority> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				authorities,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public AuthorityCollectionResource(
			List<Authority> authorities,
			Class<Authority> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				authorities,
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
			this.items = AuthorityResource.authorityResourceListPageFromAuthorities(
					authorities, uriInfo, queryParams, apiVersion);
		}
	}

	public List<AuthorityResource> getItems() {
		return items;
	}

	public void setItems(List<AuthorityResource> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorityCollectionResource [items=");
		builder.append(items);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", limit=");
		builder.append(limit);
		builder.append(", size=");
		builder.append(size);
		builder.append(", first=");
		builder.append(first);
		builder.append(", previous=");
		builder.append(previous);
		builder.append(", next=");
		builder.append(next);
		builder.append(", last=");
		builder.append(last);
		builder.append(", href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append("]");
		return builder.toString();
	}

}
