package com.qfree.bo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class JobResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(JobResource.class);

	@XmlElement
	private Long jobId;

	@XmlElement(name = "subscription")
	private SubscriptionResource subscriptionResource;

	@XmlElement(name = "reportVersion")
	private ReportVersionResource reportVersionResource;

	@XmlElement(name = "role")
	private RoleResource roleResource;

	@XmlElement(name = "documentFormat")
	private DocumentFormatResource documentFormatResource;

	@XmlElement(name = "jobStatus")
	private JobStatusResource jobStatusResource;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date jobStatusSetAt;

	@XmlElement
	private String jobStatusRemarks;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date reportRanAt;

	@XmlElement
	private String emailAddress;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date reportEmailedAt;

	@XmlElement
	private String url;

	@XmlElement
	private String fileName;

	@XmlElement
	private String document;

	@XmlElement
	private Boolean encoded;

	@XmlElement(name = "jobParameters")
	private JobParameterCollectionResource jobParameterCollectionResource;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public JobResource() {
	}

	public JobResource(Job job, UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(Job.class, job.getJobId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Job.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			/*
			 * If there exists pagination query parameters, remove them because
			 * they are not used for instance resources, and they will not apply
			 * to any resource fields that are collection resources.
			 */
			newQueryParams.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			newQueryParams.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			this.jobId = job.getJobId();

			this.jobStatusResource = new JobStatusResource(job.getJobStatus(),
					uriInfo, newQueryParams, apiVersion);
			this.jobStatusSetAt = job.getJobStatusSetAt();
			this.jobStatusRemarks = job.getJobStatusRemarks();

			this.subscriptionResource = new SubscriptionResource(job.getSubscription(),
					uriInfo, newQueryParams, apiVersion);

			this.reportVersionResource = new ReportVersionResource(job.getReportVersion(),
					uriInfo, newQueryParams, apiVersion);

			/*
			 * We do not have access to an AuthorityService bean here, so we
			 * just pass null here. As a result, the RoleResource 
			 * constructed here will not have a "directAuthorities" or an 
			 * "allAuthorities" attribute (they will be null).
			 */
			this.roleResource = new RoleResource(job.getRole(), null,
					uriInfo, newQueryParams, apiVersion);

			this.documentFormatResource = new DocumentFormatResource(job.getDocumentFormat(),
					uriInfo, newQueryParams, apiVersion);

			this.url = job.getUrl();
			this.fileName = job.getFileName();
			//this.document = job.getDocument();
			this.document = String.format("<%s bytes>",
					(job.getDocument() != null) ? job.getDocument().length() : 0);
			this.encoded = job.getEncoded();
			this.reportRanAt = job.getReportRanAt();
			this.emailAddress = job.getEmailAddress();
			this.reportEmailedAt = job.getReportEmailedAt();
			this.createdOn = job.getCreatedOn();

			this.jobParameterCollectionResource = new JobParameterCollectionResource(
					job, uriInfo, newQueryParams, apiVersion);
		}
	}

	public static List<JobResource> jobResourceListPageFromJobs(
			List<Job> jobs,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (jobs != null) {

			/*
			 * The Job entity does not have an "active" field, but if it did and
			 * if we wanted to return REST resources that correspond to only
			 * active entities, it would be necessary to do one of two things
			 * *before* we extract a page of Job entities below. Either:
			 * 
			 *   1. Filter the list "jobs" here to eliminate inactive entities,
			 *      or:
			 *   
			 *   2. Ensure that the list "jobs" was passed to this method was 
			 *      *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Job entities to return as REST resources. If the
			 * "offset" & "limit" query parameters are specified, we extract a
			 * sublist of the List "jobs"; otherwise, we use the whole list.
			 */
			List<Job> pageOfJobs = RestUtils.getPageOfList(jobs, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<JobResource> jobResources = new ArrayList<>(pageOfJobs.size());
			for (Job job : pageOfJobs) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				jobResources.add(new JobResource(job, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return jobResources;
		} else {
			return null;
		}
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public void setJobStatusRemarks(String jobStatusRemarks) {
		this.jobStatusRemarks = jobStatusRemarks;
	}

	public ReportVersionResource getReportVersionResource() {
		return reportVersionResource;
	}

	public void setReportVersionResource(ReportVersionResource reportVersionResource) {
		this.reportVersionResource = reportVersionResource;
	}

	public RoleResource getRoleResource() {
		return roleResource;
	}

	public void setRoleResource(RoleResource roleResource) {
		this.roleResource = roleResource;
	}

	public DocumentFormatResource getDocumentFormatResource() {
		return documentFormatResource;
	}

	public void setDocumentFormatResource(DocumentFormatResource documentFormatResource) {
		this.documentFormatResource = documentFormatResource;
	}

	public JobStatusResource getJobStatusResource() {
		return jobStatusResource;
	}

	public void setJobStatusResource(JobStatusResource jobStatusResource) {
		this.jobStatusResource = jobStatusResource;
		/*
		 * No, do not set jobStatusSetAt here. This setter can be called when
		 * creating a JobResource from JSON data that has been POSTed or PUTed
		 * to a REST endpoint. Only in the Job setter "setJobStatusResource"
		 * should jobStatusSetAt be set.
		 */
		//this.jobStatusSetAt = DateUtils.nowUtc(); ? // Do not uncomment
	}

	public String getJobStatusRemarks() {
		return jobStatusRemarks;
	}

	public Date getJobStatusSetAt() {
		return jobStatusSetAt;
	}

	public void setJobStatusSetAt(Date jobStatusSetAt) {
		this.jobStatusSetAt = jobStatusSetAt;
	}

	public Date getReportRanAt() {
		return reportRanAt;
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

	public void setReportEmailedAt(Date reportEmailedAt) {
		this.reportEmailedAt = reportEmailedAt;
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

	public SubscriptionResource getSubscriptionResource() {
		return subscriptionResource;
	}

	public void setSubscriptionResource(SubscriptionResource subscriptionResource) {
		this.subscriptionResource = subscriptionResource;
	}

	public JobParameterCollectionResource getJobParameterCollectionResource() {
		return jobParameterCollectionResource;
	}

	public void setJobParameterCollectionResource(
			JobParameterCollectionResource jobParameterCollectionResource) {
		this.jobParameterCollectionResource = jobParameterCollectionResource;
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
		builder.append("JobResource [jobId=");
		builder.append(jobId);
		builder.append(", jobStatusResource=");
		builder.append(jobStatusResource);
		builder.append(", jobStatusRemarks=");
		builder.append(jobStatusRemarks);
		builder.append(", subscriptionResource=");
		builder.append(subscriptionResource);
		builder.append(", reportVersionResource=");
		builder.append(reportVersionResource);
		builder.append(", roleResource=");
		builder.append(roleResource);
		builder.append(", documentFormatResource=");
		builder.append(documentFormatResource);
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
}
