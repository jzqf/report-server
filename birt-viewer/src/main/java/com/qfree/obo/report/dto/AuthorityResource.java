package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) // so we can annotate fields with @XmlTransient
public class AuthorityResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID authorityId;

	@XmlElement
	private String name;

	//	@XmlElement
	//	private List<RoleAuthority> roleAuthorities;

	/**
	 * We omit the "active" field from AuthorityResource object returned by the
	 * ReST API because
	 * 
	 * <ol>
	 * <li>There is nothing the client can do with this value, anyway.</li>
	 * <li>Inactive {@link Authority} objects are never returned by the ReST
	 * API. In this way, authorities can be deactivated in a report server
	 * installation evne if they are currently granted to roles. There is no
	 * good reason to expose this behaviour by returning this value in the ReST
	 * API.</li>
	 * </ol>
	 */
	//	@XmlElement
	@XmlTransient // Do not serialize this field - requires @XmlAccessorType(XmlAccessType.FIELD)
	private Boolean active;

	//	@XmlElement
	@XmlTransient // Do not serialize this field - requires @XmlAccessorType(XmlAccessType.FIELD)
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public AuthorityResource() {
	}

	public AuthorityResource(Authority authority, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(Authority.class, authority.getAuthorityId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Authority.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			this.authorityId = authority.getAuthorityId();
			this.name = authority.getName();
			this.active = authority.isActive();
			this.createdOn = authority.getCreatedOn();

			logger.debug("authority = {}", authority);

			//		this.roleAuthorities = authority.getRoleAuthorities();
		}
	}

	public static List<AuthorityResource> authorityResourceListPageFromAuthorities(List<Authority> authorities,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (authorities != null) {

			/*
			 * The Authority has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * Authority entities below. Either:
			 * 
			 *   1. Filter the list "authorities" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "authorities" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Authority entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "authorities"; 
			 * otherwise, we use the whole list.
			 */
			List<Authority> pageOfAuthorities = RestUtils.getPageOfList(authorities, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<AuthorityResource> authorityResources = new ArrayList<>(pageOfAuthorities.size());
			for (Authority authority : pageOfAuthorities) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				authorityResources.add(new AuthorityResource(authority, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return authorityResources;
		} else {
			return null;
		}
	}

	public UUID getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(UUID authorityId) {
		this.authorityId = authorityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthorityResource [authorityId=");
		builder.append(authorityId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
