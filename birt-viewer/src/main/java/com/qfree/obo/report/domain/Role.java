package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "role" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }, name = "uc_role_username") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Role implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleId;

	@NotNull
	@Column(name = "login_role", nullable = false)
	private Boolean loginRole;

	@NotBlank
	@Column(name = "username", nullable = false, length = 32)
	private String username;

	@Column(name = "full_name", nullable = true, length = 32)
	private String fullName;

	/**
	 * Base64 encoding of SHA-1 digest of salted password.
	 */
	@NotBlank
	@Column(name = "encoded_password", nullable = false, length = 32)
	private String encodedPassword;

	/**
	 * E-mail address to associate with the role. This can be used to
	 * automatically set the email address for a newly created report
	 * subscription.
	 */
	// @NotBlank
	@Column(name = "email", nullable = true, length = 160)
	private String email;

	/**
	 * Used to associate a default time zone with the Role, e.g., for use with a
	 * cron schedule for a new Subscription.
	 * 
	 * This string must be a legal value to pass to
	 * {@link java.time.ZoneId#of(String)}.
	 */
	@Column(name = "time_zone_id", nullable = true, length = 80)
	private String timeZoneId;

	@OneToMany(targetEntity = RoleRole.class, mappedBy = "parentRole")
	private List<RoleRole> parentRoleRoles;

	@OneToMany(targetEntity = RoleRole.class, mappedBy = "childRole")
	private List<RoleRole> childRoleRoles;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its RoleReport's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RoleReport> roleReports;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its Subscription's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Subscription> roleSubscriptions;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its RoleParameter's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RoleParameter> roleParameters;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its Job's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Job> jobs;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its Configuration's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Configuration> configurations;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Role() {
	}

	//	public Role(String encodedPassword, String username, String fullName, Boolean loginRole) {
	//		this(
	//				null,
	//				encodedPassword,
	//				username,
	//				fullName,
	//				loginRole,
	//				null,
	//				null,
	//				DateUtils.nowUtc());
	//	}

	//	public Role(
	//			String encodedPassword,
	//			String username,
	//			String fullName,
	//			Boolean loginRole,
	//			String email,
	//			String timeZoneId,
	//			Date createdOn) {
	//		this(
	//				null,
	//				encodedPassword,
	//				username,
	//				fullName,
	//				loginRole,
	//				email,
	//				timeZoneId,
	//				createdOn);
	//	}

	public Role(RoleResource roleResource) {
		this(
				roleResource.getRoleId(),
				roleResource.getEncodedPassword(),
				roleResource.getUsername(),
				roleResource.getFullName(),
				roleResource.isLoginRole(),
				roleResource.getEmail(),
				roleResource.getTimeZoneId(),
				roleResource.getCreatedOn());
	}

	public Role(
			UUID roleId,
			String encodedPassword,
			String username,
			String fullName,
			Boolean loginRole,
			String email,
			String timeZoneId,
			Date createdOn) {
		this.roleId = roleId;
		this.loginRole = loginRole;
		this.username = username;
		this.fullName = fullName;
		this.encodedPassword = encodedPassword;
		this.email = email;
		this.timeZoneId = timeZoneId;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getRoleId() {
		return roleId;
	}

	public String getEncodedPassword() {
		return encodedPassword;
	}

	public void setEncodedPassword(String encodedPassword) {
		this.encodedPassword = encodedPassword;
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

	public List<RoleRole> getParentRoleRoles() {
		return parentRoleRoles;
	}

	public void setParentRoleRoles(List<RoleRole> parentRoleRoles) {
		this.parentRoleRoles = parentRoleRoles;
	}

	public List<RoleRole> getChildRoleRoles() {
		return childRoleRoles;
	}

	public void setChildRoleRoles(List<RoleRole> childRoleRoles) {
		this.childRoleRoles = childRoleRoles;
	}

	public List<RoleReport> getRoleReports() {
		return roleReports;
	}

	public void setRoleReports(List<RoleReport> roleReports) {
		this.roleReports = roleReports;
	}

	public List<Subscription> getRoleSubscriptions() {
		return roleSubscriptions;
	}

	public void setRoleSubscriptions(List<Subscription> roleSubscriptions) {
		this.roleSubscriptions = roleSubscriptions;
	}

	public List<RoleParameter> getRoleParameters() {
		return roleParameters;
	}

	public void setRoleParameters(List<RoleParameter> roleParameters) {
		this.roleParameters = roleParameters;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [roleId=");
		builder.append(roleId);
		builder.append(", loginRole=");
		builder.append(loginRole);
		builder.append(", username=");
		builder.append(username);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", encodedPassword=");
		builder.append(encodedPassword);
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
