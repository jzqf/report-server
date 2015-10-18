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

import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class JobParameterValueResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterValueResource.class);

	@XmlElement
	private Long jobParameterValueId;

	@XmlElement(name = "jobParameter")
	private JobParameterResource jobParameterResource;

	@XmlElement
	private Boolean booleanValue;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date dateValue;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date datetimeValue;

	@XmlElement
	private Double floatValue;

	@XmlElement
	private Integer integerValue;

	@XmlElement
	private String stringValue;

	@XmlElement
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date timeValue;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public JobParameterValueResource() {
	}

	public JobParameterValueResource(JobParameterValue jobParameterValue, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(JobParameterValue.class, jobParameterValue.getJobParameterValueId(), uriInfo,
				queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(JobParameterValue.class).getExpandParam();
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

			this.jobParameterValueId = jobParameterValue.getJobParameterValueId();

			this.jobParameterResource = new JobParameterResource(
					jobParameterValue.getJobParameter(),
					uriInfo, newQueryParams, apiVersion);

			this.booleanValue = jobParameterValue.getBooleanValue();
			this.dateValue = jobParameterValue.getDateValue();
			this.datetimeValue = jobParameterValue.getDatetimeValue();
			this.floatValue = jobParameterValue.getFloatValue();
			this.integerValue = jobParameterValue.getIntegerValue();
			this.stringValue = jobParameterValue.getStringValue();
			this.timeValue = jobParameterValue.getTimeValue();
			this.createdOn = jobParameterValue.getCreatedOn();
		}
	}

	public static List<JobParameterValueResource> listFromJobParameter(JobParameter jobParameter,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		if (jobParameter.getJobParameterValues() != null) {
			List<JobParameterValue> jobParameterValues = jobParameter
					.getJobParameterValues();
			List<JobParameterValueResource> jobParameterValueResources = new ArrayList<>(
					jobParameterValues.size());
			for (JobParameterValue jobParameterValue : jobParameterValues) {
				// List<String> showAll =
				// queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				// if (jobParameterValue.isActive() ||
				// RestUtils.FILTER_INACTIVE_RECORDS == false ||
				// ResourcePath.showAll(JobParameterValue.class,
				// showAll)) {
				jobParameterValueResources.add(
						new JobParameterValueResource(jobParameterValue, uriInfo, queryParams, apiVersion));
				// }
			}
			return jobParameterValueResources;
		} else {
			return null;
		}
	}

	public Long getJobParameterValueId() {
		return jobParameterValueId;
	}

	public void setJobParameterValueId(Long jobParameterValueId) {
		this.jobParameterValueId = jobParameterValueId;
	}

	public JobParameterResource getJobParameterResource() {
		return jobParameterResource;
	}

	public void setJobParameterResource(JobParameterResource jobParameterResource) {
		this.jobParameterResource = jobParameterResource;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getDatetimeValue() {
		return datetimeValue;
	}

	public void setDatetimeValue(Date datetimeValue) {
		this.datetimeValue = datetimeValue;
	}

	public Double getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
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
		builder.append("JobParameterValueResource [jobParameterValueId=");
		builder.append(jobParameterValueId);
		builder.append(", jobParameterResource=");
		builder.append(jobParameterResource);
		builder.append(", booleanValue=");
		builder.append(booleanValue);
		builder.append(", dateValue=");
		builder.append(dateValue);
		builder.append(", datetimeValue=");
		builder.append(datetimeValue);
		builder.append(", floatValue=");
		builder.append(floatValue);
		builder.append(", integerValue=");
		builder.append(integerValue);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
