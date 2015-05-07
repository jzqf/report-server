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

	@Column(name = "login_role", nullable = false)
	private boolean loginRole;

	@Column(name = "username", nullable = false, length = 32)
	private String username;

	@Column(name = "full_name", nullable = true, length = 32)
	private String fullName;

	/**
	 * Base64 encoding of SHA-1 digest of salted password.
	 */
	@Column(name = "encoded_password", nullable = false, length = 32)
	private String encodedPassword;

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
	 *     Deleting a Role will delete all of its RoleParameterValue's.
	 */
	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	private List<RoleParameterValue> roleParameterValues;

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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Role() {
	}

	public Role(String encodedPassword, String username, String fullName, boolean loginRole) {
		this(encodedPassword, username, fullName, loginRole, DateUtils.nowUtc());
	}

	public Role(String encodedPassword, String username, String fullName, boolean loginRole, Date createdOn) {
		this(null, encodedPassword, username, fullName, loginRole, createdOn);
		//		this.loginRole = loginRole;
		//		this.username = username;
		//		this.encodedPassword = encodedPassword;
		//		if (createdOn != null) {
		//			this.createdOn = createdOn;
		//		} else {
		//			this.createdOn = DateUtils.nowUtc();
		//		}
	}

	public Role(UUID roleId, String encodedPassword, String username, String fullName, boolean loginRole, Date createdOn) {
		this.roleId = roleId;
		this.loginRole = loginRole;
		this.username = username;
		this.fullName = fullName;
		this.encodedPassword = encodedPassword;
		if (createdOn != null) {
			this.createdOn = createdOn;
		} else {
			this.createdOn = DateUtils.nowUtc();
		}
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

	public boolean isLoginRole() {
		return loginRole;
	}

	public void setLoginRole(boolean loginRole) {
		this.loginRole = loginRole;
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

	public List<RoleParameterValue> getRoleParameterValues() {
		return roleParameterValues;
	}

	public void setRoleParameterValues(List<RoleParameterValue> roleParameterValues) {
		this.roleParameterValues = roleParameterValues;
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
		builder.append("Role [loginRole=");
		builder.append(loginRole);
		builder.append(", username=");
		builder.append(username);
		builder.append("]");
		return builder.toString();
	}

}
