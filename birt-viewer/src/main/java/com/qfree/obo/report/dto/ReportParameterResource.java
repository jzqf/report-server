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
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportParameterResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID reportParameterId;

	@XmlElement
	private Integer orderIndex;

	@XmlElement
	private String name;

	@XmlElement
	private Integer dataType;

	@XmlElement
	private Integer controlType;

	@XmlElement
	private String promptText;

	@XmlElement
	private Boolean required;

	@XmlElement
	private Boolean multivalued;

	@XmlElement
	private String defaultValue;

	@XmlElement
	private String displayName;

	@XmlElement
	private String helpText;

	@XmlElement
	private String displayFormat;

	@XmlElement
	private Integer alignment;

	@XmlElement
	private Boolean hidden;

	@XmlElement
	private Boolean valueConcealed;

	@XmlElement
	private Boolean allowNewValues;

	@XmlElement
	private Boolean displayInFixedOrder;

	@XmlElement
	private Integer parameterType;

	@XmlElement
	private Integer autoSuggestThreshold;

	@XmlElement
	private Integer selectionListType;

	//@XmlElement
	//private String typeName;

	@XmlElement
	private String valueExpr;

	@XmlElement(name = "reportVersion")
	private ReportVersionResource reportVersionResource;

	@XmlElement(name = "parameterGroup")
	private ParameterGroupResource parameterGroupResource;

	@XmlElement(name = "selectionListValues")
	private SelectionListValueCollectionResource selectionListValues;

	//	private List<RoleParameterValue> roleParameterValues;
	//	private List<SubscriptionParameterValue> subscriptionParameterValues;
	//	private List<JobParameterValue> jobParameterValues;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
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
			this.orderIndex = reportParameter.getOrderIndex();
			this.dataType = reportParameter.getDataType();
			this.controlType = reportParameter.getControlType();
			this.name = reportParameter.getName();
			this.promptText = reportParameter.getPromptText();
			this.required = reportParameter.getRequired();
			this.multivalued = reportParameter.getMultivalued();
			this.defaultValue = reportParameter.getDefaultValue();
			this.displayName = reportParameter.getDisplayName();
			this.helpText = reportParameter.getHelpText();
			this.displayFormat = reportParameter.getDisplayFormat();
			this.alignment = reportParameter.getAlignment();
			this.hidden = reportParameter.getHidden();
			this.valueConcealed = reportParameter.getValueConcealed();
			this.allowNewValues = reportParameter.getAllowNewValues();
			this.displayInFixedOrder = reportParameter.getDisplayInFixedOrder();
			this.parameterType = reportParameter.getParameterType();
			this.autoSuggestThreshold = reportParameter.getAutoSuggestThreshold();
			this.selectionListType = reportParameter.getSelectionListType();
			//this.typeName = reportParameter.getTypeName();
			this.valueExpr = reportParameter.getValueExpr();
			this.createdOn = reportParameter.getCreatedOn();

			this.reportVersionResource = new ReportVersionResource(reportParameter.getReportVersion(),
					uriInfo, newQueryParams, apiVersion);
			/*
			 * If there is no selection list for the parameter, no 
			 * "selectionListValues" field will appear in the resource. The 
			 * alternative would be to always include the field, but its value
			 * will be an empty collection resource for the case when there is
			 * no selection list for the parameter.
			 * 
			 * Note: If the ReportParameter "reportParameter" passed to this
			 *       constructor was fetched using any sort of database query,
			 *       it will have a non-null "selectionListValues" field only if
			 *       there exists a STATIC selection list for the report
			 *       parameter, i.e., there must exist SelectionListValue 
			 *       entities linked to the ReportParameter, and 
			 *       SelectionListValue entities are only created for static
			 *       selection lists.
			 *       
			 *       If the ReportParameter has a dynamic selection list, it can
			 *       be fetched using the endpoint:
			 *       
			 *       ReportParameterController.getSelectionListValuesByReportParameterId...).
			 */
			logger.debug("Parameter = {}: reportParameter.getSelectionListValues() = {}",
					this.name, reportParameter.getSelectionListValues());
			if (reportParameter.getSelectionListValues() != null
					&& reportParameter.getSelectionListValues().size() > 0) {
				this.selectionListValues = new SelectionListValueCollectionResource(reportParameter,
						uriInfo, newQueryParams, apiVersion);
			}
			/*
			 * A related ParameterGroup is optional (normal parameters that are
			 * not part of a group do not have one), so we only create a related
			 * ParameterGroupResource if necessary.
			 */
			if (reportParameter.getParameterGroup() != null) {
				this.parameterGroupResource = new ParameterGroupResource(reportParameter.getParameterGroup(),
						uriInfo, newQueryParams, apiVersion);
			}
		}
	}

	public static List<ReportParameterResource> reportParameterResourceListPageFromReportParameters(
			List<ReportParameter> reportParameters,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (reportParameters != null) {

			/*
			 * The ReportParameter entity does not have an "active" field, but
			 * if it did and if we wanted to return REST resources that
			 * correspond to only active entities, it would be necessary to do
			 * one of two things *before* we extract a page of ReportParameter 
			 * entities below. Either:
			 * 
			 *   1. Filter the list "reportParameters" here to eliminate
			 *      inactive entities, or:
			 *   
			 *   2. Ensure that the list "reportParameters" was passed to this
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of ReportParameter entities to return as REST
			 * resources. If the "offset" & "limit" query parameters are
			 * specified, we extract a sublist of the List "reportParameters";
			 * otherwise, we use the whole list.
			 */
			List<ReportParameter> pageOfReportParameters = RestUtils.getPageOfList(reportParameters, queryParams);

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

			List<ReportParameterResource> reportParameterResources = new ArrayList<>(pageOfReportParameters.size());
			for (ReportParameter reportParameter : pageOfReportParameters) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				reportParameterResources.add(
						new ReportParameterResource(reportParameter, uriInfo, queryParamsWOPagination, apiVersion));
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

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getControlType() {
		return controlType;
	}

	public void setControlType(Integer controlType) {
		this.controlType = controlType;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
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

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public String getDisplayFormat() {
		return displayFormat;
	}

	public void setDisplayFormat(String displayFormat) {
		this.displayFormat = displayFormat;
	}

	public Integer getAlignment() {
		return alignment;
	}

	public void setAlignment(Integer alignment) {
		this.alignment = alignment;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Boolean getValueConcealed() {
		return valueConcealed;
	}

	public void setValueConcealed(Boolean valueConcealed) {
		this.valueConcealed = valueConcealed;
	}

	public Boolean getAllowNewValues() {
		return allowNewValues;
	}

	public void setAllowNewValues(Boolean allowNewValues) {
		this.allowNewValues = allowNewValues;
	}

	public Boolean getDisplayInFixedOrder() {
		return displayInFixedOrder;
	}

	public void setDisplayInFixedOrder(Boolean displayInFixedOrder) {
		this.displayInFixedOrder = displayInFixedOrder;
	}

	public Integer getParameterType() {
		return parameterType;
	}

	public void setParameterType(Integer parameterType) {
		this.parameterType = parameterType;
	}

	public Integer getAutoSuggestThreshold() {
		return autoSuggestThreshold;
	}

	public void setAutoSuggestThreshold(Integer autoSuggestThreshold) {
		this.autoSuggestThreshold = autoSuggestThreshold;
	}

	public Integer getSelectionListType() {
		return selectionListType;
	}

	public void setSelectionListType(Integer selectionListType) {
		this.selectionListType = selectionListType;
	}

	public String getValueExpr() {
		return valueExpr;
	}

	public void setValueExpr(String valueExpr) {
		this.valueExpr = valueExpr;
	}

	public ReportVersionResource getReportVersionResource() {
		return reportVersionResource;
	}

	public void setReportVersionResource(ReportVersionResource reportVersionResource) {
		this.reportVersionResource = reportVersionResource;
	}

	public ParameterGroupResource getParameterGroupResource() {
		return parameterGroupResource;
	}

	public void setParameterGroupResource(ParameterGroupResource parameterGroupResource) {
		this.parameterGroupResource = parameterGroupResource;
	}

	public SelectionListValueCollectionResource getSelectionListValues() {
		return selectionListValues;
	}

	public void setSelectionListValues(SelectionListValueCollectionResource selectionListValues) {
		this.selectionListValues = selectionListValues;
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
		builder.append(", promptText=");
		builder.append(promptText);
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
