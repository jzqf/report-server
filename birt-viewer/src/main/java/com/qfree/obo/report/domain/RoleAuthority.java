package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
 * The persistent class for the "role_authority" database table.
 * 
 * Instances/rows specify which authorities are assigned to each role.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role_authority", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "role_id", "authority_id" },
				name = "uc_roleauthority_role_authority") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class RoleAuthority implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_authority_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleAuthorityId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_roleauthority_role") ,
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "authority_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_roleauthority_authority") ,
			columnDefinition = "uuid")
	private Authority authority;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private RoleAuthority() {
	}

	public RoleAuthority(Role role, Authority authority) {
		this(role, authority, DateUtils.nowUtc());
	}

	public RoleAuthority(Role role, Authority authority, Date createdOn) {
		this.role = role;
		this.authority = authority;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getRoleAuthorityId() {
		return this.roleAuthorityId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public Authority getAuthority() {
		return this.authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleAuthority [roleAuthorityId=");
		builder.append(roleAuthorityId);
		builder.append(", role=");
		builder.append(role);
		builder.append(", authority=");
		builder.append(authority);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
