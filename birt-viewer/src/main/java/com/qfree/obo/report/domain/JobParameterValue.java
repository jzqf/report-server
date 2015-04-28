package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "job_parameter_value" database table.
 * 
 * Instances/rows represent last used values by a specified Job for a specified
 * ReportParameter. Multivalued ReportParameter's can have multiple 
 * JobParameterValue's for a single Job. Single-valued ReportParameter's may 
 * only have a single JobParameterValue for a single Job.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "job_parameter_value", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "job_id", "report_parameter_id", "string_value" },
						name = "uc_jobparametervalue_job_parameter_value") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class JobParameterValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_parameter_value_id", unique = true, nullable = false)
	private Long jobParameterValueId;
	//	@NotNull
	//	@Type(type = "uuid-custom")
	//	//	@Type(type = "pg-uuid")
	//	@GeneratedValue(generator = "uuid2")
	//	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	//	@Column(name = "job_parameter_value_id", unique = true, nullable = false,
	//			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	//	private UUID jobParameterValueId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "job_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_jobparametervalue_job"))
	//			columnDefinition = "uuid")
	private Job job;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_jobparametervalue_reportparameter"))
	//			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/**
	 * The value used for the specified report parameter by the specified job.
	 * role. This value cannot be null. Instead, if a value was not specified 
	 * for a parameter (or specified null), then no {@link JobParameterValue} 
	 * entity should be created for that job/parameter combination. This should 
	 * only be possible for report parameters with required=false.
	 * 
	 * The value is stored as text, regardless of its native data type.
	 */
	@Column(name = "string_value", nullable = false, length = 80)
	private String stringValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public JobParameterValue() {
	}

	public JobParameterValue(Job job, ReportParameter reportParameter, String stringValue) {
		this(job, reportParameter, stringValue, DateUtils.nowUtc());
	}

	public JobParameterValue(Job job, ReportParameter reportParameter, String stringValue, Date createdOn) {
		this.job = job;
		this.reportParameter = reportParameter;
		this.stringValue = stringValue;
		if (createdOn != null) {
			this.createdOn = createdOn;
		} else {
			this.createdOn = DateUtils.nowUtc();
		}
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Long getJobParameterValueId() {
		return this.jobParameterValueId;
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
		builder.append("JobParameterValue [job=");
		builder.append(job);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append("]");
		return builder.toString();
	}

}