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
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobParameterResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterResource.class);

	@XmlElement
	private Long jobParameterId;

	@XmlElement(name = "job")
	private JobResource jobResource;

	@XmlElement(name = "reportParameter")
	private ReportParameterResource reportParameterResource;

	@XmlElement(name = "jobParameterValues")
	private JobParameterValueCollectionResource jobParameterValuesCollectionResource;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public JobParameterResource() {
	}

	public JobParameterResource(JobParameter jobParameter, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(JobParameter.class, jobParameter.getJobParameterId(), uriInfo,
				queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(JobParameter.class).getExpandParam();
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
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

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

			this.jobParameterId = jobParameter.getJobParameterId();

			this.jobResource = new JobResource(jobParameter.getJob(),
					uriInfo, newQueryParams, apiVersion);
			this.reportParameterResource = new ReportParameterResource(jobParameter.getReportParameter(),
					uriInfo, newQueryParams, apiVersion);

			this.jobParameterValuesCollectionResource = new JobParameterValueCollectionResource(
					jobParameter, uriInfo, newQueryParams, apiVersion);

			this.createdOn = jobParameter.getCreatedOn();
		}
	}

	public static List<JobParameterResource> listFromJob(Job job,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		if (job.getJobParameters() != null) {
			List<JobParameter> jobParameters = job.getJobParameters();
			List<JobParameterResource> jobParameterResources = new ArrayList<>(
					jobParameters.size());
			for (JobParameter jobParameter : jobParameters) {
				// List<String> showAll =
				// queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				// if (jobParameter.isActive() ||
				// RestUtils.FILTER_INACTIVE_RECORDS == false ||
				// ResourcePath.showAll(JobParameter.class,
				// showAll)) {
				jobParameterResources.add(
						new JobParameterResource(jobParameter, uriInfo, queryParams, apiVersion));
				// }
			}
			return jobParameterResources;
		} else {
			return null;
		}
	}

	public Long getJobParameterId() {
		return jobParameterId;
	}

	public void setJobParameterId(Long jobParameterId) {
		this.jobParameterId = jobParameterId;
	}

	public JobResource getJobResource() {
		return jobResource;
	}

	public void setJobResource(JobResource jobResource) {
		this.jobResource = jobResource;
	}

	public ReportParameterResource getReportParameterResource() {
		return reportParameterResource;
	}

	public void setReportParameterResource(ReportParameterResource reportParameterResource) {
		this.reportParameterResource = reportParameterResource;
	}

	public JobParameterValueCollectionResource getJobParameterValuesCollectionResource() {
		return jobParameterValuesCollectionResource;
	}

	public void setJobParameterValuesCollectionResource(
			JobParameterValueCollectionResource jobParameterValuesCollectionResource) {
		this.jobParameterValuesCollectionResource = jobParameterValuesCollectionResource;
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
		builder.append("JobParameterResource [jobParameterId=");
		builder.append(jobParameterId);
		builder.append(", jobResource=");
		builder.append(jobResource);
		builder.append(", reportParameterResource=");
		builder.append(reportParameterResource);
		builder.append(", jobParameterValuesCollectionResource=");
		builder.append(jobParameterValuesCollectionResource);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
