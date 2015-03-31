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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * The persistent class for the "role_role" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role_role", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "parent_role_id", "child_role_id" },
				name = "uc_role_role_parent_child") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class RoleRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_role_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleRoleId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "parent_role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_rolerole_parentrole"),
			columnDefinition = "uuid")
	private Role parentRole;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "child_role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_rolerole_childrole"),
			columnDefinition = "uuid")
	private Role childRole;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private RoleRole() {
	}

	public RoleRole(Role parentRole, Role childRole) {
		this(parentRole, childRole, new Date());
	}

	public RoleRole(Role parentRole, Role childRole, Date createdOn) {
		this.parentRole = parentRole;
		this.childRole = childRole;
		this.createdOn = createdOn;
	}

	public UUID getRoleRoleId() {
		return this.roleRoleId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public Role getParentRole() {
		return this.parentRole;
	}

	public void setParentRole(Role parentRole) {
		this.parentRole = parentRole;
	}

	public Role getChildRole() {
		return this.childRole;
	}

	public void setChildRole(Role childRole) {
		this.childRole = childRole;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleRole [parentRole=");
		builder.append(parentRole);
		builder.append(", childRole=");
		builder.append(childRole);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

	//TODO Create toString()
}
