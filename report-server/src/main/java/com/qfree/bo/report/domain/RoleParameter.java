package com.qfree.bo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.bo.report.util.DateUtils;

/**
 * The persistent class for the "role_parameter" database table.
 * 
 * Instances/rows are used to manage role-specific "last used" or default values
 * for report parameters.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "role_parameter", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "role_id", "report_parameter_id" },
				name = "uc_roleparameter_role_parameter") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class RoleParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "role_parameter_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID roleParameterId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_roleparameter_role") ,
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
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_roleparameter_reportparameter") ,
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a RoleParameter will delete all of its 
	 *     RoleParameterValue's. In order to delete individual
	 *     RoleParameterValue entities, it is necessary to first
	 *     remove them from this list.
	 *     
	 * orphanRemoval = true:
	 *     DO NOT CHANGE THIS SETTING. This is made use of in
	 *     SubscriptionParameterController.updateSubscriptionParameterValuesBySubscriptionParameterId(...)
	 *     In that method, RoleParameterValue entities are removed from
	 *     the list "roleParameterValues". Doing this deletes the 
	 *     rows from the [role_parameter_values] underlying database table.
	 */
	@OneToMany(mappedBy = "roleParameter", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RoleParameterValue> roleParameterValues;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private RoleParameter() {
	}

	public RoleParameter(Role role, ReportParameter reportParameter) {
		this(role, reportParameter, DateUtils.nowUtc());
	}

	public RoleParameter(Role role, ReportParameter reportParameter, Date createdOn) {
		this.role = role;
		this.reportParameter = reportParameter;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public List<RoleParameterValue> getRoleParameterValues() {
		return roleParameterValues;
	}

	public void setRoleParameterValues(List<RoleParameterValue> roleParameterValues) {
		this.roleParameterValues = roleParameterValues;
	}

	public UUID getRoleParameterId() {
		return roleParameterId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleParameter [roleParameterId=");
		builder.append(roleParameterId);
		builder.append(", roleId=");
		builder.append(role.getRoleId());
		builder.append(", reportParameterId=");
		builder.append(reportParameter.getReportParameterId());
		builder.append(", roleParameterValues=");
		builder.append(roleParameterValues);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
