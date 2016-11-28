package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qfree.obo.report.SecurityConfig;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.exceptions.ResourceFilterExecutionException;
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

	/**
	 * Primary key of the built-in "reportadmin" Role.
	 * 
	 * This Role should never be deleted, but if it does, it should be recreated
	 * with this id. This role should probably not be made inactive or disabled,
	 * either, but there might arise special conditions that will perhaps make
	 * this desirable.
	 */
	public static final UUID ADMIN_ROLE_ID = UUID.fromString("54aa1d35-f67d-47e6-8bea-cadd6085796e");

	/**
	 * Primary key of the built-in "qfree-reportserver-admin" Role.
	 * 
	 * This Role should never be deleted, but if it does, it should be recreated
	 * with this id.
	 */
	public static final UUID QFREE_ADMIN_ROLE_ID = UUID.fromString("10ab3537-0b12-44fa-a27b-6cf1aac14282");
	public static final String QFREE_ADMIN_ROLE_NAME = "qfree-reportserver-admin";

	/**
	 * Primary key of the built-in "reportserver-restadmin" Role.
	 * 
	 * This Role should never be deleted, but if it does, it should be recreated
	 * with this id.
	 */
	public static final UUID QFREE_REST_ADMIN_ROLE_ID = UUID.fromString("689833f9-e55c-4eaf-aba6-79f8b1d1a058");
	public static final String QFREE_REST_ADMIN_ROLE_NAME = "reportserver-restadmin";

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
	 * A hashed version of the Role's password. The encoding algorithm is set by
	 * the {@link PasswordEncoder} bean that can be obtained by Spring
	 * {@literal @}{@link Autowired} DI. This {@link PasswordEncoder} bean is
	 * configured in {@link SecurityConfig}.
	 */
	//@NotBlank
	@Column(name = "encoded_password", nullable = true, length = 64)
	private String encodedPassword;

	/**
	 * E-mail address to associate with the role. This can be used to
	 * automatically set the emailAddress address for a newly created report
	 * subscription.
	 */
	// @NotBlank
	@Column(name = "email_address", nullable = true, length = 160)
	private String emailAddress;

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
	 *     Deleting a Role will delete all of its RoleAuthority's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RoleAuthority> roleAuthorities;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its Subscription's.
	 */
	@OrderBy("createdOn ASC")
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Subscription> subscriptions;

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
	@OrderBy("jobId ASC")
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Job> jobs;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its Configuration's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<Configuration> configurations;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Role() {
	}

	public Role(RoleResource roleResource) {
		this(
				roleResource.getRoleId(),
				roleResource.getEncodedPassword(),
				roleResource.getUsername(),
				roleResource.getFullName(),
				roleResource.isLoginRole(),
				roleResource.getEmailAddress(),
				roleResource.getTimeZoneId(),
				roleResource.getEnabled(),
				roleResource.getActive(),
				roleResource.getCreatedOn());
	}

	public Role(String username) {
		this(
				null,
				null,
				username,
				null,
				true,
				null,
				null,
				true,
				true,
				null);
	}

	public Role(
			UUID roleId,
			String encodedPassword,
			String username,
			String fullName,
			Boolean loginRole,
			String emailAddress,
			String timeZoneId,
			Boolean enabled,
			Boolean active,
			Date createdOn) {
		this.roleId = roleId;
		this.loginRole = loginRole;
		this.username = username;
		this.fullName = fullName;
		this.encodedPassword = encodedPassword;
		this.emailAddress = emailAddress;
		this.timeZoneId = timeZoneId;
		this.enabled = (enabled != null) ? enabled : true;
		this.active = (active != null) ? active : true;
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

	public List<RoleAuthority> getRoleAuthorities() {
		return roleAuthorities;
	}

	public void setRoleAuthorities(List<RoleAuthority> roleAuthorities) {
		this.roleAuthorities = roleAuthorities;
	}

	/**
	 * Returns all Subscription entities linked to the Role, whether the
	 * associated ReportVersion is active or not.
	 * 
	 * @return
	 */
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	/**
	 * Returns only Subscription entities linked to the Role, where the
	 * associated ReportVersion is active.
	 * 
	 * TODO Add argument: List<List<Map<String, String>>> filterConditions so
	 * that we can filter on enabled/disabled and active/inactive.
	 * 
	 * @return
	 */
	public List<Subscription> getSubscriptionsForActiveReportVersions() {
		List<Subscription> subscriptionsForActiveReportVersions = new ArrayList<>();
		for (Subscription subscription : subscriptions) {
			if (subscription.getReportVersion().isActive()) {
				subscriptionsForActiveReportVersions.add(subscription);
			}
		}
		return subscriptionsForActiveReportVersions;
	}

	public void setSubscriptions(List<Subscription> roleSubscriptions) {
		this.subscriptions = roleSubscriptions;
	}

	public List<RoleParameter> getRoleParameters() {
		return roleParameters;
	}

	public void setRoleParameters(List<RoleParameter> roleParameters) {
		this.roleParameters = roleParameters;
	}

	/**
	 * Returns {@link List} of {@link Job} entities associated with the
	 * {@link Role}.
	 * 
	 * Filtering can be performed on the set of all {@link Job} entities
	 * associated with the {@link Role}.
	 * 
	 * @param filterConditions
	 * @return
	 * @throws ResourceFilterExecutionException
	 */
	public List<Job> getJobs(List<List<Map<String, String>>> filterConditions) throws ResourceFilterExecutionException {
		return Job.getFilteredJobs(getJobs(), filterConditions);
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
