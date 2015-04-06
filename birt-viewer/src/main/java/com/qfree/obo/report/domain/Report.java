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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * The persistent class for the "report" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Report implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "report_id", unique = true, nullable = false)
	//	private Long reportId;
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_category_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_report_reportcategory"),
			columnDefinition = "uuid")
	private ReportCategory reportCategory;

	@Column(name = "name", nullable = false, length = 80)
	private String name;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its Subscription's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<ReportVersion> reportVersions;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its ReportParameter's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<ReportParameter> reportParameters;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its RoleReport's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<RoleReport> roleReports;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its Subscription's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<Subscription> reportSubscriptions;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its Job's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<Job> jobs;

	// Works for H2, but not PostgreSQL:
	//	@Column(name = "rptdesign", nullable = false, columnDefinition = "clob")
	/* This works for PostgreSQL but not for H2. With H2, it seems that if you 
	 * create a column of type "text", then the column that is created is  
	 * actually given the H2 type "clob". This means that validating the schema 
	 * via hbm2ddl.auto=validate will fail because Hibernate expects a column of 
	 * type "text" but it sees, instead, a column of type "clob". This causes
	 * the validation to fail. Hence, the schema cannotbe validated during unit
	 * testing with the embedded H2 database unless we temporarily set 
	 * columnDefinition = "clob".
	 */
	@Column(name = "rptdesign", nullable = false, columnDefinition = "text")
	private String rptdesign;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Report() {
	}

	public Report(ReportCategory reportCategory, String name, String rptdesign) {
		this(reportCategory, name, rptdesign, new Date());
	}

	public Report(ReportCategory reportCategory, String name, String rptdesign, Date createdOn) {
		this.reportCategory = reportCategory;
		this.name = name;
		this.rptdesign = rptdesign;
		this.createdOn = createdOn;
	}

	public Report(UUID reportId, ReportCategory reportCategory, String name, Date createdOn) {
		this.reportId = reportId;
		this.reportCategory = reportCategory;
		this.name = name;
		this.createdOn = createdOn;
	}

	public UUID getReportId() {
		return this.reportId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public ReportCategory getReportCategory() {
		return this.reportCategory;
	}

	public void setReportCategory(ReportCategory reportCategory) {
		this.reportCategory = reportCategory;
	}

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}

	public List<RoleReport> getRoleReports() {
		return roleReports;
	}

	public void setRoleReports(List<RoleReport> roleReports) {
		this.roleReports = roleReports;
	}

	public List<Subscription> getReportSubscriptions() {
		return reportSubscriptions;
	}

	public void setReportSubscriptions(List<Subscription> reportSubscriptions) {
		this.reportSubscriptions = reportSubscriptions;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public String getRptdesign() {
		return rptdesign;
	}

	public void setRptdesign(String rptdesign) {
		this.rptdesign = rptdesign;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [reportId=");
		builder.append(reportId);
		builder.append(", reportCategory=");
		builder.append(reportCategory);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
