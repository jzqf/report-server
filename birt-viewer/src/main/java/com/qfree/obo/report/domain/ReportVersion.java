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

/**
 * The persistent class for the "report_version" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report_version", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "report_id", "version_code" },
						name = "uc_reportversion_report_versioncode"),
				@UniqueConstraint(columnNames = { "report_id", "version_name" },
						name = "uc_reportversion_report_versioncode") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ReportVersion implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_version_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportVersionId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportversion_report"),
			columnDefinition = "uuid")
	private Report report;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its ReportParameter's.
	 */
	@OneToMany(mappedBy = "reportVersion", cascade = CascadeType.ALL)
	private List<ReportParameter> reportParameters;
	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its Subscription's.
	 */
	@OneToMany(mappedBy = "reportVersion", cascade = CascadeType.ALL)
	private List<Subscription> reportSubscriptions;

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

	/**
	 * A string value that represents the release version of the report as it 
	 * should be shown to users. The value is a string so that you can describe
	 * the report version as a <major>.<minor>.<point> string, or in any other
	 * form
	 */
	@Column(name = "version_name", nullable = false, length = 16)
	private String versionName;

	/**
	 * An integer value that represents the version of the ReportVersion, 
	 * relative to other versions for the same Report. The value is an integer 
	 * so that it can be used for ordering in a UI or for other numerical uses.
	 */
	@Column(name = "version_code", nullable = false)
	private Integer versionCode;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public ReportVersion() {
	}

	public ReportVersion(Report report, String rptdesign, String versionName, Integer versionCode, boolean active) {
		this(report, rptdesign, versionName, versionCode, active, new Date());
	}

	public ReportVersion(Report report, String rptdesign, String versionName, Integer versionCode, boolean active,
			Date createdOn) {
		this.report = report;
		this.rptdesign = rptdesign;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.active = active;
		this.createdOn = createdOn;
	}

	public UUID getReportVersionId() {
		return this.reportVersionId;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}

	public List<Subscription> getReportSubscriptions() {
		return reportSubscriptions;
	}

	public void setReportSubscriptions(List<Subscription> reportSubscriptions) {
		this.reportSubscriptions = reportSubscriptions;
	}

	public String getRptdesign() {
		return rptdesign;
	}

	public void setRptdesign(String rptdesign) {
		this.rptdesign = rptdesign;
	}

	public String getVersionName() {
		return this.versionName;
	}

	public void setVersionName(String name) {
		this.versionName = name;
	}

	public Integer getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Integer versionCode) {
		this.versionCode = versionCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportParameter [report=");
		builder.append(report);
		builder.append(", versionName=");
		builder.append(versionName);
		builder.append(", versionCode=");
		builder.append(versionCode);
		builder.append("]");
		return builder.toString();
	}

}