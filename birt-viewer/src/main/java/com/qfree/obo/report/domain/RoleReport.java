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
 * The persistent class for the "role_report" database table.
 * 
 * Instances/rows specify which reports are available to each role.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role_report", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "role_id", "report_id" },
				name = "uc_rolereport_role_report") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class RoleReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_report_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleRoleId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_rolereport_role"),
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_rolereport_report"),
			columnDefinition = "uuid")
	private Report report;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private RoleReport() {
	}

	public RoleReport(Role role, Report report) {
		this(role, report, new Date());
	}

	public RoleReport(Role role, Report report, Date createdOn) {
		this.role = role;
		this.report = report;
		this.createdOn = createdOn;
	}

	public UUID getRoleRoleId() {
		return this.roleRoleId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public Report getReport() {
		return this.report;
	}

	public void setReport(Report report) {
		this.report = report;
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
		builder.append("RoleReport [role=");
		builder.append(role);
		builder.append(", report=");
		builder.append(report);
		builder.append("]");
		return builder.toString();
	}

}
