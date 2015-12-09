package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class RoleResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(RoleResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID roleId;

	@XmlElement
	private String username;

	@XmlElement
	private String fullName;

	@XmlElement
	private String encodedPassword;

	@XmlElement
	private Boolean loginRole;

	@XmlElement
	private String emailAddress;

	@XmlElement
	private String timeZoneId;

	@XmlElement
	private Boolean enabled;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public RoleResource() {
	}

	/**
	 * Create new {@link RoleResource} instance from a {@link Role} instance.
	 * 
	 * @param role
	 * @param uriInfo
	 * @param expand
	 * @param apiVersion
	 */
	public RoleResource(
			Role role,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(Role.class, role.getRoleId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Role.class).getExpandParam();
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
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

			this.roleId = role.getRoleId();
			this.username = role.getUsername();
			this.fullName = role.getFullName();
			this.encodedPassword = role.getEncodedPassword();
			this.loginRole = role.isLoginRole();
			this.emailAddress = role.getEmailAddress();
			this.timeZoneId = role.getTimeZoneId();
			this.enabled = role.getEnabled();
			this.active = role.getActive();
			this.createdOn = role.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public static List<RoleResource> roleResourceListPageFromRoles(
			List<Role> roles,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (roles != null) {

			/*
			 * The Report has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * Role entities below. Either:
			 * 
			 *   1. Filter the list "roles" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "roles" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Role entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "roles"; 
			 * otherwise, we use the whole list.
			 */
			List<Role> pageOfRoles = RestUtils.getPageOfList(roles, queryParams);

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

			List<RoleResource> roleResources = new ArrayList<>(pageOfRoles.size());
			for (Role role : pageOfRoles) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				roleResources.add(new RoleResource(role, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return roleResources;
		} else {
			return null;
		}
	}

	public UUID getRoleId() {
		return roleId;
	}

	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
	}

	public Boolean isLoginRole() {
		return loginRole;
	}

	public void setLoginRole(Boolean loginRole) {
		this.loginRole = loginRole;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getActive() {
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
		builder.append("RoleResource [roleId=");
		builder.append(roleId);
		builder.append(", username=");
		builder.append(username);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", encodedPassword=");
		builder.append(encodedPassword);
		builder.append(", loginRole=");
		builder.append(loginRole);
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		builder.append(", timeZoneId=");
		builder.append(timeZoneId);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
