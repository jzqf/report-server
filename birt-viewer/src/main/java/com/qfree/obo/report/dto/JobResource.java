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

import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(JobResource.class);

	@XmlElement
	private Long jobId;

	@XmlElement(name = "jobStatus")
	private JobStatusResource jobStatusResource;

	@XmlElement
	private String jobStatusRemarks;

	@XmlElement(name = "subscription")
	private SubscriptionResource subscriptionResource;

	@XmlElement(name = "reportVersion")
	private ReportVersionResource reportVersionResource;

	@XmlElement(name = "role")
	private RoleResource roleResource;

	@XmlElement(name = "documentFormat")
	private DocumentFormatResource documentFormatResource;

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

			this.jobId = job.getJobId();
			
			this.jobStatusResource = new JobStatusResource(job.getJobStatus(),
					uriInfo, newQueryParams, apiVersion);
			this.jobStatusRemarks = jobStatusRemarks;

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
			this.createdOn = job.getCreatedOn();

			this.jobParameterCollectionResource = new JobParameterCollectionResource(
					job, uriInfo, newQueryParams, apiVersion);
		}
	}

	public static List<JobResource> listFromDocumentFormat(DocumentFormat documentFormat, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (documentFormat.getJobs() != null) {
			List<Job> jobs = documentFormat.getJobs();
			List<JobResource> jobResources = new ArrayList<>(jobs.size());
			for (Job job : jobs) {
				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				//if (job.getActive() ||
				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
				//		ResourcePath.showAll(Job.class, showAll)) {
				jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
				//}
			}
			return jobResources;
		} else {
			return null;
		}

	}

	public static List<JobResource> listFromSubscription(Subscription subscription, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (subscription.getJobs() != null) {
			List<Job> jobs = subscription.getJobs();
			List<JobResource> jobResources = new ArrayList<>(jobs.size());
			for (Job job : jobs) {
				//List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				//if (job.getActive() ||
				//		RestUtils.FILTER_INACTIVE_RECORDS == false ||
				//		ResourcePath.showAll(Job.class, showAll)) {
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

	public JobStatusResource getJobStatusResource() {
		return jobStatusResource;
	}

	public void setJobStatusResource(JobStatusResource jobStatusResource) {
		this.jobStatusResource = jobStatusResource;
	}

	public String getJobStatusRemarks() {
		return jobStatusRemarks;
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
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
