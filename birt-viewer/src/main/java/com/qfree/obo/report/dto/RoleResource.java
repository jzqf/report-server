package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
	@XmlJavaTypeAdapter(DateAdapter.class)
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
	public RoleResource(Role role, UriInfo uriInfo, List<String> expand,
			RestApiVersion apiVersion) {

		super(Role.class, role.getRoleId(), uriInfo, expand, apiVersion);
		logger.debug("After super(Role.class, ...  this = {}", this);

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
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append("]");
		return builder.toString();
	}

}
