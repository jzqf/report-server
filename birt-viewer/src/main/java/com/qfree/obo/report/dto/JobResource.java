package com.qfree.obo.report.dto;

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

import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

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
	private String email;

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
			 * they are not used for instance resources????????????????????????????????????????????????
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

			this.roleResource = new RoleResource(job.getRole(),
					uriInfo, newQueryParams, apiVersion);

			this.documentFormatResource = new DocumentFormatResource(job.getDocumentFormat(),
					uriInfo, newQueryParams, apiVersion);

			this.url = job.getUrl();
			this.fileName = job.getFileName();
			this.document = job.getDocument();
			this.encoded = job.getEncoded();
			this.reportRanAt = job.getReportRanAt();
			this.email = job.getEmail();
			this.reportEmailedAt = job.getReportEmailedAt();
			this.createdOn = job.getCreatedOn();

			this.jobParameterCollectionResource = new JobParameterCollectionResource(
					job, uriInfo, newQueryParams, apiVersion);
		}
	}

	//	public static List<JobResource> listFromDocumentFormat(DocumentFormat documentFormat, UriInfo uriInfo,
	//			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
	//
	//		if (documentFormat.getJobs() != null) {
	//			List<Job> jobs = documentFormat.getJobs();
	//			List<JobResource> jobResources = new ArrayList<>(jobs.size());
	//			for (Job job : jobs) {
	//				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
	//				//if (job.getActive() ||
	//				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
	//				//		ResourcePath.showAll(Job.class, showAll)) {
	//				jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
	//				//}
	//			}
	//			return jobResources;
	//		} else {
	//			return null;
	//		}
	//
	//	}
	//
	//	public static List<JobResource> listFromSubscription(Subscription subscription, UriInfo uriInfo,
	//			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
	//
	//		if (subscription.getJobs() != null) {
	//			List<Job> jobs = subscription.getJobs();
	//			List<JobResource> jobResources = new ArrayList<>(jobs.size());
	//			for (Job job : jobs) {
	//				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
	//				//if (job.getActive() ||
	//				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
	//				//		ResourcePath.showAll(Job.class, showAll)) {
	//				jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
	//				//}
	//			}
	//			return jobResources;
	//		} else {
	//			return null;
	//		}
	//
	//	}
	//
	//	public static List<JobResource> listFromRole(
	//			Role role,
	//			UriInfo uriInfo,
	//			Map<String, List<String>> queryParams,
	//			RestApiVersion apiVersion) {
	//
	//		if (role.getJobs() != null) {
	//
	//			/*
	//			 * Create a List of Job entities to return as REST resources. If the
	//			 * "offset" & "limit" query parameters are specified, we extract a
	//			 * sublist from the List role.getJobs(); otherwise, we use the whole
	//			 * list.
	//			 */
	//			List<Job> jobs = RestUtils.getPageOfList(role.getJobs(), queryParams);
	//
	//			List<JobResource> jobResources = new ArrayList<>(jobs.size());
	//			for (Job job : jobs) {
	//				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
	//				//if (job.getActive() ||
	//				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
	//				//		ResourcePath.showAll(Job.class, showAll)) {
	//
	//				// REMOVE PAGINATION QUERY PARAMS HERE AND PASS NULL FOR APIVERSION?
	//				// *ALSO* DO THIS IN JOBRESEOURCE CONSTRUCTOR AS I DO NOW ABOVE? NO SHOULD NOT BE NECESSARY?i DON'TKNOW. JUST TRY IT.
	//				// NEED TO WRITE:  RestUtils.queryParamsWithPaginationRemoved(queryParams)? MAYBE, BUT USE IT IN CONSTRUCTOR ABOVE IN super(...), NOT HERE?
	//				// BUT CAN STILL PASS APIVERSION AS NULL HERE?  HOW ABOUT, INSTEAD, SETTING APIVERSION=NULL IN ALL CONTROLLER METHODS AFTER IT IS USED???? THEN
	//				//		I DO NOT NEED TO SET IT TO NULL EVERYWHERE ELSE?  TEST FIRST WITH JUST RoleController. MAYBE NOT. 
	//				//		THIS WOULD INDICATE THAT APIVERSION SHOULD NOT BE A PARAMETER EVERYWHERE? WRONG. IT NEEDS TO BE A PARAMETER AT THE TOP LEVEL ONLY.
	//				// MAKE CORRESPONDING CHANGES ELSEWHERE.
	//				//jobResources.add(new JobResource(job, uriInfo, RestUtils.queryParamsWithPaginationRemoved(queryParams), null));			
	//				jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
	//				//}
	//			}
	//			return jobResources;
	//		} else {
	//			return null;
	//		}
	//
	//	}

	public static List<JobResource> listFromJobs(List<Job> jobs, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (jobs != null) {

			/*
			 * Create a List of Job entities to return as REST resources. If the
			 * "offset" & "limit" query parameters are specified, we extract a
			 * sublist from the List jobs; otherwise, we use the whole
			 * list.
			 */
			List<Job> pageOfJobs = RestUtils.getPageOfList(jobs, queryParams);

			List<JobResource> jobResources = new ArrayList<>(pageOfJobs.size());
			for (Job job : pageOfJobs) {
				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				//if (job.getActive() ||
				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
				//		ResourcePath.showAll(Job.class, showAll)) {

				// REMOVE PAGINATION QUERY PARAMS HERE AND PASS NULL FOR APIVERSION?
				// *ALSO* DO THIS IN JOBRESEOURCE CONSTRUCTOR AS I DO NOW ABOVE? NO SHOULD NOT BE NECESSARY?i DON'TKNOW. JUST TRY IT.
				// NEED TO WRITE:  RestUtils.queryParamsWithPaginationRemoved(queryParams)? MAYBE, BUT USE IT IN CONSTRUCTOR ABOVE IN super(...), NOT HERE?
				// BUT CAN STILL PASS APIVERSION AS NULL HERE?  HOW ABOUT, INSTEAD, SETTING APIVERSION=NULL IN ALL CONTROLLER METHODS AFTER IT IS USED???? THEN
				//		I DO NOT NEED TO SET IT TO NULL EVERYWHERE ELSE?  TEST FIRST WITH JUST RoleController. MAYBE NOT. 
				//		THIS WOULD INDICATE THAT APIVERSION SHOULD NOT BE A PARAMETER EVERYWHERE? WRONG. IT NEEDS TO BE A PARAMETER AT THE TOP LEVEL ONLY.
				// MAKE CORRESPONDING CHANGES ELSEWHERE.
				//jobResources.add(new JobResource(job, uriInfo, RestUtils.queryParamsWithPaginationRemoved(queryParams), null));			
				jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
				//}
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
		builder.append(document);
		builder.append(", encoded=");
		builder.append(encoded);
		builder.append(", jobStatusSetAt=");
		builder.append(jobStatusSetAt);
		builder.append(", reportRanAt=");
		builder.append(reportRanAt);
		builder.append(", email=");
		builder.append(email);
		builder.append(", reportEmailedAt=");
		builder.append(reportEmailedAt);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
