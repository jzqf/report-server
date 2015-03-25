package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

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

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

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

	@OneToMany(targetEntity = RoleRole.class, fetch = FetchType.EAGER, mappedBy = "parentRole")
	private List<RoleRole> parentRoleRoles;

	@OneToMany(targetEntity = RoleRole.class, fetch = FetchType.EAGER, mappedBy = "childRole")
	private List<RoleRole> childRoleRoles;

	private Role() {
	}

	public Role(String encodedPassword, String username, Date createdOn, boolean loginRole) {
		this.createdOn = createdOn;
		this.loginRole = loginRole;
		this.username = username;
		this.encodedPassword = encodedPassword;
	}

	public UUID getRoleId() {
		return roleId;
	}

	public String getEncodedPassword() {
		return encodedPassword;
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

	public List<RoleRole> getChildRoleRoles() {
		return childRoleRoles;
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
