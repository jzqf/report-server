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
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.util.DateUtils;

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
						name = "uc_reportversion_report_versionname") })
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
	@NotNull
	@JoinColumn(name = "report_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportversion_report"),
			columnDefinition = "uuid")
	private Report report;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportVersion will delete all of its ReportParameter's.
	 */
	@OneToMany(mappedBy = "reportVersion", cascade = CascadeType.ALL)
	private List<ReportParameter> reportParameters;
	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportVersion will delete all of its Subscription's.
	 */
	@OneToMany(mappedBy = "reportVersion", cascade = CascadeType.ALL)
	private List<Subscription> reportSubscriptions;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportVersion will delete all of its Job's.
	 */
	@OneToMany(mappedBy = "reportVersion", cascade = CascadeType.ALL)
	private List<Job> jobs;

	/**
	 * The name of the file as uploaded from disk or written to disk. This must
	 * be preserved because some reports may refer to other reports by name.
	 */
	@NotBlank
	@Column(name = "file_name", nullable = false, length = 80)
	private String fileName;

	@NotBlank
	@Column(name = "rptdesign", nullable = false, columnDefinition = "text")
	private String rptdesign;

	/**
	 * A string value that represents the release version of the report as it 
	 * should be shown to users. The value is a string so that you can describe
	 * the report version as a <major>.<minor>.<point> string, or in any other
	 * chosen format.
	 */
	@NotBlank
	@Column(name = "version_name", nullable = false, length = 16)
	private String versionName;

	/**
	 * An integer value that represents the version of the ReportVersion, 
	 * relative to other versions for the same Report. The value is an integer 
	 * so that it can be used for ordering in a UI or for other numerical uses.
	 */
	@NotNull
	@Column(name = "version_code", nullable = false)
	private Integer versionCode;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public ReportVersion() {
	}

	public ReportVersion(
			Report report,
			String fileName,
			String rptdesign,
			String versionName,
			Integer versionCode,
			Boolean active,
			Date createdOn) {
		this(null, report, fileName, rptdesign, versionName, versionCode, active, createdOn);
	}

	public ReportVersion(Report report, String fileName, String rptdesign, String versionName, Integer versionCode,
			Boolean active) {
		this(null, report, fileName, rptdesign, versionName, versionCode, active, DateUtils.nowUtc());
	}

	public ReportVersion(ReportVersionResource reportVersionResource, Report report) {
		this(
				reportVersionResource.getReportVersionId(),
				report,
				reportVersionResource.getFileName(),
				reportVersionResource.getRptdesign(),
				reportVersionResource.getVersionName(),
				reportVersionResource.getVersionCode(),
				reportVersionResource.isActive(),
				reportVersionResource.getCreatedOn());
	}

	public ReportVersion(UUID reportVersionId, Report report, String fileName, String rptdesign,
			String versionName, Integer versionCode, Boolean active, Date createdOn) {
		this.reportVersionId = reportVersionId;
		this.report = report;
		this.fileName = fileName;
		this.rptdesign = rptdesign;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
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

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportVersion [reportVersionId=");
		builder.append(reportVersionId);
		builder.append(", report=");
		builder.append(report);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", rptdesign=");
		builder.append("<" + ((rptdesign != null) ? rptdesign.length() : 0) + " bytes>");
		builder.append(", versionName=");
		builder.append(versionName);
		builder.append(", versionCode=");
		builder.append(versionCode);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}


}