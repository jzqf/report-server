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
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

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
	private String email;

	@XmlElement
	private String timeZoneId;

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
	public RoleResource(Role role, UriInfo uriInfo, Map<String, List<String>> queryParams,
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
			this.email = role.getEmail();
			this.timeZoneId = role.getTimeZoneId();
			this.createdOn = role.getCreatedOn();
		}
		logger.debug("this = {}", this);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
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
		builder.append(", email=");
		builder.append(email);
		builder.append(", timeZoneId=");
		builder.append(timeZoneId);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
