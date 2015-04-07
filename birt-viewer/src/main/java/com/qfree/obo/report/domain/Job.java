package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

/**
 * The persistent class for the "job" database table.
 * 
 * Instances/rows specify a log of reports that have been run/delivered.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "job", schema = "reporting")
//@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_id", unique = true, nullable = false)
	private Long jobId;
	//	@Type(type = "uuid-custom")
	//	//	@Type(type = "pg-uuid")
	//	@GeneratedValue(generator = "uuid2")
	//	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	//	@Column(name = "job_id", unique = true, nullable = false,
	//			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	//	private UUID jobId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	//	@JoinColumn(name = "report_id", nullable = false,
	//			foreignKey = @ForeignKey(name = "fk_job_report"),
	//			columnDefinition = "uuid")
	//	private Report report;
	@JoinColumn(name = "report_version_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_job_report"),
			columnDefinition = "uuid")
	private ReportVersion reportVersion;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_job_role"),
			columnDefinition = "uuid")
	private Role role;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Job will delete all of its JobParameterValue's.
	 */
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
	private List<JobParameterValue> jobParameterValues;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Job() {
	}

	public Job(ReportVersion report_version, Role role) {
		this(report_version, role, new Date());
	}

	public Job(ReportVersion report_version, Role role, Date createdOn) {
		this.reportVersion = report_version;
		this.role = role;
		this.createdOn = createdOn;
	}

	public Long getJobId() {
		return this.jobId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public ReportVersion getReportVersion() {
		return this.reportVersion;
	}

	public void setReportVersion(ReportVersion report_version) {
		this.reportVersion = report_version;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<JobParameterValue> getJobParameterValues() {
		return jobParameterValues;
	}

	public void setJobParameterValues(List<JobParameterValue> jobParameterValues) {
		this.jobParameterValues = jobParameterValues;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job [jobId=");
		builder.append(jobId);
		builder.append(", report_version=");
		builder.append(reportVersion);
		builder.append(", role=");
		builder.append(role);
		builder.append("]");
		return builder.toString();
	}

}
