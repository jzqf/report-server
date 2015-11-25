package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.qfree.obo.report.dto.JobResource;
import com.qfree.obo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.obo.report.util.DateUtils;
import com.qfree.obo.report.util.RestUtils;

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

	/**
	 * The {@link Subscription} for which this Job was created.
	 * 
	 * This may be null if the Job is not created from a {@link Subscription}.
	 */
	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	//@NotNull
	@JoinColumn(name = "subscription_id", nullable = true,
			foreignKey = @ForeignKey(name = "fk_job_subscription") ,
			columnDefinition = "uuid")
	private Subscription subscription;

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
			foreignKey = @ForeignKey(name = "fk_job_reportversion") ,
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
			foreignKey = @ForeignKey(name = "fk_job_role") ,
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
			foreignKey = @ForeignKey(name = "fk_job_documentformat") ,
			columnDefinition = "uuid")
	private DocumentFormat documentFormat;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "job_status_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_job_jobstatus") ,
			columnDefinition = "uuid")
	private JobStatus jobStatus;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "job_status_set_at", nullable = false)
	private Date jobStatusSetAt;

	/**
	 * Specified details that give more details related to the current value of
	 * jobStatus.
	 * 
	 * This should be considered read-only in the ReST API.
	 */
	@Column(name = "job_status_remarks", nullable = true, columnDefinition = "text")
	private String jobStatusRemarks;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Job will delete all of its JobParameter's.
	 */
	@OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
	private List<JobParameter> jobParameters;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "report_ran_at", nullable = true)
	private Date reportRanAt;

	/**
	 * E-mail address to which the rendered report will be sent.
	 * 
	 * This allows a subscription to be set up that delivers reports to other
	 * than a role's primary e-mail address stored with the Role entity.
	 */
	// @NotBlank
	@Column(name = "email_address", nullable = true, length = 160)
	private String emailAddress;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "report_emailed_at", nullable = true)
	private Date reportEmailedAt;

	/**
	 * The URL used to request the report from the report server, *excluding*
	 * the scheme (http:// or https://), server and TCP port. Hence, it really
	 * isn't a URL so this attribute should probably be renamed to something
	 * like: "urlPath", or "requestUrlPath or... Or perhaps this attribute
	 * should simply be eliminated? Time will tell.
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
	 * This includes the file extension, but no path information. This will
	 * normally be generated automatically from the report number, name, version
	 * and document format (for the extension).
	 */
	@Column(name = "file_name", nullable = true, length = 128)
	private String fileName;

	/**
	 * The response from the report server returned for a request for a report.
	 * <p>
	 * This is Base64-encoded if the response represents a binary document
	 * format. This can be determined by checking the value of {@link #encoded}.
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
			JobResource jobResource,
			JobStatus jobStatus,
			DocumentFormat documentFormat,
			ReportVersion reportVersion,
			Role role,
			Subscription subscription) {
		this(
				subscription,
				jobStatus,
				jobResource.getJobStatusRemarks(),
				reportVersion,
				role,
				documentFormat,
				jobResource.getEmailAddress(),
				jobResource.getUrl(),
				jobResource.getFileName(),
				jobResource.getDocument(),
				jobResource.getEncoded(),
				jobResource.getReportRanAt(),
				jobResource.getReportEmailedAt(),
				DateUtils.nowUtc());
	}

	public Job(
			Subscription subscription,
			JobStatus jobStatus,
			String jobStatusRemarks,
			ReportVersion reportVersion,
			Role role,
			DocumentFormat documentFormat,
			String emailAddress) {
		this(
				subscription,
				jobStatus,
				jobStatusRemarks,
				reportVersion,
				role,
				documentFormat,
				emailAddress,
				null,
				null,
				null,
				null,
				null,
				null,
				DateUtils.nowUtc());
	}

	public Job(
			Subscription subscription,
			JobStatus jobStatus,
			String jobStatusRemarks,
			ReportVersion report_version,
			Role role,
			DocumentFormat documentFormat,
			String emailAddress,
			String url,
			String fileName,
			String document,
			Boolean encoded,
			Date reportRanAt,
			Date reportEmailedAt,
			Date createdOn) {
		this.subscription = subscription;
		//this.jobStatus = jobStatus;
		setJobStatus(jobStatus); // so jobStatusSetAt will also be set
		this.jobStatusRemarks = jobStatusRemarks;
		this.reportVersion = report_version;
		this.role = role;
		this.documentFormat = documentFormat;
		this.url = url;
		this.fileName = fileName;
		this.document = document;
		this.encoded = encoded;
		this.reportRanAt = null;
		this.emailAddress = emailAddress;
		this.reportEmailedAt = null;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public Long getJobId() {
		return this.jobId;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public void setJobStatusRemarks(String jobStatusRemarks) {
		this.jobStatusRemarks = jobStatusRemarks;
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

	public JobStatus getJobStatus() {
		return this.jobStatus;
	}

	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
		this.jobStatusSetAt = DateUtils.nowUtc();
	}

	public String getJobStatusRemarks() {
		return jobStatusRemarks;
	}

	public Date getReportRanAt() {
		return reportRanAt;
	}

	public void setReportRanAt() {
		setReportRanAt(DateUtils.nowUtc());
	}

	public void setReportRanAt(Date reportRanAt) {
		this.reportRanAt = reportRanAt;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Date getReportEmailedAt() {
		return reportEmailedAt;
	}

	public void setReportEmailedAt() {
		setReportEmailedAt(DateUtils.nowUtc());
	}

	public void setReportEmailedAt(Date reportEmailedAt) {
		this.reportEmailedAt = reportEmailedAt;
	}

	public Date getJobStatusSetAt() {
		return jobStatusSetAt;
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

	public Date getCreatedOn() {
		return this.createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job [jobId=");
		builder.append(jobId);
		builder.append(", jobStatus=");
		builder.append(jobStatus);
		builder.append(", jobStatusRemarks=");
		builder.append(jobStatusRemarks);
		builder.append(", reportVersion=");
		builder.append(reportVersion);
		builder.append(", role=");
		builder.append(role);
		builder.append(", documentFormat=");
		builder.append(documentFormat);
		//	builder.append(", jobParameters=");
		//	builder.append(jobParameters);  // <- generates circular reference - do not uncomment
		builder.append(", url=");
		builder.append(url);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", document=");
		builder.append(String.format("<%s bytes>", (document != null) ? document.length() : 0));
		builder.append(", encoded=");
		builder.append(encoded);
		builder.append(", jobStatusSetAt=");
		builder.append(jobStatusSetAt);
		builder.append(", reportRanAt=");
		builder.append(reportRanAt);
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		builder.append(", reportEmailedAt=");
		builder.append(reportEmailedAt);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns a {@link List} of filtered {@link Job} entities given an
	 * unfiltered list and a set of filter conditions.
	 * 
	 * This method first sets up one list for each attribute on which the list
	 * of {@link Job} entities can be filtered on. Then the filtering is
	 * performed by a call to a generic static method.
	 * 
	 * @param unfilteredJobs
	 * @param filterConditions
	 * @return
	 * @throws ResourceFilterExecutionException
	 */
	public static List<Job> getFilteredJobs(List<Job> unfilteredJobs, List<List<Map<String, String>>> filterConditions)
			throws ResourceFilterExecutionException {
		if (filterConditions == null || filterConditions.size() == 0) {
			return unfilteredJobs; // no filtering
		}
		List<Object> jobStatusIds = new ArrayList<>(unfilteredJobs.size());
		List<Object> jobStatusAbbreviations = new ArrayList<>(unfilteredJobs.size());
		List<Object> jobCreatedOns = new ArrayList<>(unfilteredJobs.size());
		List<Object> jobIds = new ArrayList<>(unfilteredJobs.size());
		for (Job job : unfilteredJobs) {
			jobStatusIds.add(job.getJobStatus().getJobStatusId());
			jobStatusAbbreviations.add(job.getJobStatus().getAbbreviation());
			jobCreatedOns.add(job.getCreatedOn());
			jobIds.add(job.getJobId());
		}
		Map<String, List<Object>> filterableAttributes = new HashMap<>(4);
		/*
		 * Here, the Map keys used *must* agree with the filter attributes used
		 * in the value assigned to the "filter" query parameter in the resource
		 * URI.
		 */
		filterableAttributes.put("jobStatusId", jobStatusIds);
		filterableAttributes.put("jobStatusAbbreviation", jobStatusAbbreviations);
		filterableAttributes.put("createdOn", jobCreatedOns);
		filterableAttributes.put("jobId", jobIds);
		/*
		 * The list must be ordered in case pagination is used for the 
		 * collection resource created from list of filtered entities. The only
		 * sensible order is chronological order. Since the Job entities has a 
		 * Long primary key, we could also sort by id.
		 */
		Comparator<Job> chronological = (Job job1, Job job2) -> job1.getCreatedOn().compareTo(job2.getCreatedOn());

		return RestUtils.filterEntities(unfilteredJobs, filterConditions, filterableAttributes, chronological,
				Job.class);
	}

}
