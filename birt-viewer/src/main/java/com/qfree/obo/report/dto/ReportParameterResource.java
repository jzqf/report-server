package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportParameterResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID reportParameterId;

	@XmlElement
	private String name;

	@XmlElement
	private String description;

	@XmlElement
	private Boolean required;

	@XmlElement
	private Boolean multivalued;

	@XmlElement
	private Integer orderIndex;

	@XmlElement(name = "reportVersion")
	private ReportVersionResource reportVersionResource;

	//	@XmlElement(name = "parameterType")
	//	private ParameterTypeResource parameterTypeResource;

	//	@XmlElement(name = "widget")
	//	private WidgetResource widgetResource;

	//	private List<RoleParameterValue> roleParameterValues;
	//	private List<SubscriptionParameterValue> subscriptionParameterValues;
	//	private List<JobParameterValue> jobParameterValues;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date createdOn;

	public ReportParameterResource() {
	}

	public ReportParameterResource(ReportParameter reportParameter, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(ReportParameter.class, reportParameter.getReportParameterId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(ReportParameter.class).getExpandParam();
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

			this.reportParameterId = reportParameter.getReportParameterId();
			this.name = reportParameter.getName();
			this.description = reportParameter.getDescription();
			this.required = reportParameter.getRequired();
			this.multivalued = reportParameter.getMultivalued();
			this.orderIndex = reportParameter.getOrderIndex();
			this.createdOn = reportParameter.getCreatedOn();
			this.reportVersionResource = new ReportVersionResource(reportParameter.getReportVersion(),
					uriInfo, newQueryParams, apiVersion);
		}
	}

	public static List<ReportParameterResource> listFromReportVersion(ReportVersion reportVersion, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		if (reportVersion.getReportParameters() != null) {
			List<ReportParameter> reportParameters = reportVersion.getReportParameters();
			List<ReportParameterResource> reportParameterResources = new ArrayList<>(reportParameters.size());
			for (ReportParameter reportParameter : reportParameters) {
				reportParameterResources.add(
						new ReportParameterResource(reportParameter, uriInfo, queryParams, apiVersion));
			}
			return reportParameterResources;
		} else {
			return null;
		}
	}

	public UUID getReportParameterId() {
		return reportParameterId;
	}

	public void setReportParameterId(UUID reportParameterId) {
		this.reportParameterId = reportParameterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getMultivalued() {
		return multivalued;
	}

	public void setMultivalued(Boolean multivalued) {
		this.multivalued = multivalued;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public ReportVersionResource getReportVersionResource() {
		return reportVersionResource;
	}

	public void setReportVersionResource(ReportVersionResource reportVersionResource) {
		this.reportVersionResource = reportVersionResource;
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
		builder.append("ReportParameterResource [reportParameterId=");
		builder.append(reportParameterId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", required=");
		builder.append(required);
		builder.append(", multivalued=");
		builder.append(multivalued);
		builder.append(", orderIndex=");
		builder.append(orderIndex);
		builder.append(", reportVersionResource=");
		builder.append(reportVersionResource);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
