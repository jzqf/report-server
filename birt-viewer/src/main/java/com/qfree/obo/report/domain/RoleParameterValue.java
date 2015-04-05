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

/**
 * The persistent class for the "role_parameter_value" database table.
 * 
 * Instances/rows represent last used values by a specified Role for a specified
 * ReportParameter. Multivalued ReportParameter's can have multiple 
 * RoleParameterValue's for a single Role. Single-valued ReportParameter's may 
 * only have a single RoleParameterValue for a single Role.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role_parameter_value", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "role_id", "report_parameter_id", "string_value" },
						name = "uc_roleparametervalue_role_parameter_value") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class RoleParameterValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_parameter_value_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleParameterValueId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_role"),
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_roleparametervalue_reportparameter"),
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/**
	 * A "last used" value for the specified report parameter by the specified 
	 * role. This value cannot be null. Instead, if the role does not specify a 
	 * value for a parameter (or specified null), then no 
	 * {@link RoleParameterValue} entity should be created for that 
	 * role/parameter combination. This should only be possible for report 
	 * parameters with required=false.
	 * 
	 * The value is stored as text, regardless of its native data type.
	 */
	@Column(name = "string_value", nullable = false, length = 80)
	private String stringValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public RoleParameterValue() {
	}

	public RoleParameterValue(Role role, ReportParameter reportParameter, String stringValue) {
		this(role, reportParameter, stringValue, new Date());
	}

	public RoleParameterValue(Role role, ReportParameter reportParameter, String stringValue, Date createdOn) {
		this.role = role;
		this.reportParameter = reportParameter;
		this.stringValue = stringValue;
		this.createdOn = createdOn;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public UUID getRoleParameterValueId() {
		return this.roleParameterValueId;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String description) {
		this.stringValue = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleParameterValue [role=");
		builder.append(role);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append("]");
		return builder.toString();
	}

}