package com.qfree.obo.report.domain;

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

import com.qfree.obo.report.util.DateUtils;

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
	 *     Deleting a ReportParameter will delete all of its 
	 *     RoleParameterValue's.
	 */
	@OneToMany(mappedBy = "roleParameter", cascade = CascadeType.ALL)
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

}
