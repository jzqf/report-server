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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "job_parameter" database table.
 * 
 * Instances/rows represent report parameters for a job, and they are used to
 * associate one or more values with a parameter.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "job_parameter", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "job_id", "report_parameter_id" },
				name = "uc_jobparameter_job_parameter") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class JobParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_parameter_id", unique = true, nullable = false)
	private Long jobParameterId;
	//	@Id
	//	@Type(type = "uuid-custom")XXXXXXXXXXXXXXX
	//	//	@Type(type = "pg-uuid")
	//	@GeneratedValue(generator = "uuid2")
	//	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	//	@Column(name = "job_parameter_id", unique = true, nullable = false,
	//			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	//	private UUID jobParameterId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "job_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_jobparameter_job") )
	//			columnDefinition = "uuid")
	private Job job;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_jobparameter_reportparameter") ,
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a JobParameter will delete all of its 
	 *     JobParameterValue's. In order to delete individual
	 *     JobParameterValue' entities, it is necessary to first
	 *     remove them from this list.
	 *     
	 * orphanRemoval = true:
	 *     DO NOT CHANGE THIS SETTING. Removing JobParameterValue entities from
	 *     the list "JobParameterValues" will automatically delete the 
	 *     corresponding rows from the [sjob_parameter_values] underlying 
	 *     database table.
	 */
	@OneToMany(mappedBy = "jobParameter", cascade = CascadeType.ALL)
	private List<JobParameterValue> jobParameterValues;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private JobParameter() {
	}

	public JobParameter(Job job, ReportParameter reportParameter) {
		this(job, reportParameter, DateUtils.nowUtc());
	}

	public JobParameter(Job job, ReportParameter reportParameter, Date createdOn) {
		this.job = job;
		this.reportParameter = reportParameter;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public List<JobParameterValue> getJobParameterValues() {
		return jobParameterValues;
	}

	public void setJobParameterValues(List<JobParameterValue> jobParameterValues) {
		this.jobParameterValues = jobParameterValues;
	}

	public Long getJobParameterId() {
		return jobParameterId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobParameter [jobParameterId=");
		builder.append(jobParameterId);
		builder.append(", job=");
		builder.append(job);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		//	builder.append(", jobParameterValues=");
		//	builder.append(jobParameterValues);  // <- generates circular reference - do not uncomment
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
