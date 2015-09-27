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
import javax.validation.constraints.NotNull;

import com.qfree.obo.report.util.DateUtils;

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
	//@NotNull <-- No, this should not be used
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
	@NotNull
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
	@NotNull
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_job_role"),
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 * 
	 * The value of documentFormat.birtFormat should be assigned to the BIRT
	 * __format query parameter in the URL.
	 * 
	 * TODO How to determine documentFormat for a report run manually (not from a Subscription)?
	 */
	@NotNull
	@JoinColumn(name = "document_format_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_job_documentformat"),
			columnDefinition = "uuid")
	private DocumentFormat documentFormat;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Job will delete all of its JobParameter's.
	 */
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
	private List<JobParameter> jobParameters;

	/**
	 * The URL used to request the report from the report server.
	 * <p>
	 * This will include the name of the rptdesign document from the report 
	 * server's file system, the document format (&__format=...) and other
	 * details.
	 * <p>
	 * If this {@link Job} is created for a Subscription, it will include query 
	 * parameters for all of the report parameters. If this report is run 
	 * manually, the user will be prompted for the report parameter values when 
	 * the report is run so they will not appear in {@link #url}.
	 */
	@Column(name = "url", nullable = true, length = 1024)
	private String url;

	/**
	 * The name of the file to generate from the document stored in 
	 * {@link #document}.
	 * <p>
	 * This includes the file extension, but no path 
	 * information. This will normally be generated automatically from the 
	 * report number, name, version and document format (for the extension).
	 */
	@Column(name = "file_name", nullable = true, length = 128)
	private String fileName;

	/**
	 * The response from the report server returned for a request for a 
	 * report.
	 * <p>
	 * This is Base64-encoded if the response represents a binary document
	 * format. This can be determined by checking the value of 
	 * {@link #encoded}.
	 * <p>
	 * If this {@link Job} is created for a Subscription, the decoded value of 
	 * this field is delivered to the recipient's e-mail address as an 
	 * attachment.
	 */
	@Column(name = "document", nullable = true, columnDefinition = "text")
	private String document;

	/**
	 * true if {@link #document} is Base64 encoded.
	 */
	@Column(name = "encoded", nullable = true)
	private Boolean encoded;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Job() {
	}

	public Job(
			ReportVersion report_version,
			Role role,
			DocumentFormat documentFormat) {
		this(
				report_version,
				role,
				documentFormat,
				null,
				null,
				null,
				null,
				DateUtils.nowUtc());
	}

	public Job(
			ReportVersion report_version,
			Role role,
			DocumentFormat documentFormat,
			String url,
			String fileName,
			String document,
			Boolean encoded) {
		this(
				report_version,
				role,
				documentFormat,
				url,
				fileName,
				document,
				encoded,
				DateUtils.nowUtc());
	}

	public Job(
			ReportVersion report_version,
			Role role,
			DocumentFormat documentFormat,
			String url,
			String fileName,
			String document,
			Boolean encoded,
			Date createdOn) {
		this.reportVersion = report_version;
		this.role = role;
		this.documentFormat = documentFormat;
		this.url = url;
		this.fileName = fileName;
		this.document = document;
		this.encoded = encoded;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
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

	public DocumentFormat getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(DocumentFormat documentformat) {
		this.documentFormat = documentformat;
	}

	public List<JobParameter> getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(List<JobParameter> jobParameters) {
		this.jobParameters = jobParameters;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Boolean getEncoded() {
		return encoded;
	}

	public void setEncoded(Boolean encoded) {
		this.encoded = encoded;
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
