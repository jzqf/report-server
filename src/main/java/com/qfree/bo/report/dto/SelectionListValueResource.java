package com.qfree.bo.report.dto;

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

import com.qfree.bo.report.domain.SelectionListValue;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class SelectionListValueResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListValueResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID selectionListValueId;

	@XmlElement(name = "reportParameter")
	private ReportParameterResource reportParameterResource;

	@XmlElement
	private Integer orderIndex;

	@XmlElement
	private String valueAssigned;

	@XmlElement
	private String valueDisplayed;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public SelectionListValueResource() {
	}

	public SelectionListValueResource(SelectionListValue selectionListValue, UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(SelectionListValue.class, selectionListValue.getSelectionListValueId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(SelectionListValue.class).getExpandParam();
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

			this.selectionListValueId = selectionListValue.getSelectionListValueId();
			this.reportParameterResource = new ReportParameterResource(selectionListValue.getReportParameter(),
					uriInfo, newQueryParams, apiVersion);
			this.orderIndex = selectionListValue.getOrderIndex();
			this.valueAssigned = selectionListValue.getValueAssigned();
			this.valueDisplayed = selectionListValue.getValueDisplayed();
			this.createdOn = selectionListValue.getCreatedOn();
		}
	}

	public static List<SelectionListValueResource> selectionListValueResourceListPageFromSelectionListValues(
			List<SelectionListValue> selectionListValues,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (selectionListValues != null) {

			/*
			 * The SelectionListValue entity class does not have an "active"
			 * field, but if it did and if we wanted to return REST resources
			 * that correspond to only active entities, it would be necessary to
			 * do one of two things *before* we extract a page of
			 * SelectionListValue entities below. Either:
			 * 
			 *   1. Filter the list "selectionListValues" here to eliminate
			 *      inactive entities, or:
			 *   
			 *   2. Ensure that the list "selectionListValues" was passed to 
			 *      this method was *already* filtered to remove inactive
			 *      entities.
			 */

			/*
			 * Create a List of SelectionListValue entities to return as REST
			 * resources. If the "offset" & "limit" query parameters are
			 * specified, we extract a sublist of the List 
			 * "selectionListValues"; otherwise, we use the whole list.
			 */
			List<SelectionListValue> pageOfSelectionListValues = RestUtils.getPageOfList(selectionListValues,
					queryParams);

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

			List<SelectionListValueResource> selectionListValueResources = new ArrayList<>(
					pageOfSelectionListValues.size());
			for (SelectionListValue selectionListValue : pageOfSelectionListValues) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */

				SelectionListValueResource selectionListValueResource = new SelectionListValueResource(
						selectionListValue, uriInfo, queryParamsWOPagination, apiVersion);
				if (selectionListValue.getSelectionListValueId() == null) {
					/*
					 * It is possible for the Id of selectionListValue to be null.
					 * 
					 * This will be the case if the value is associated with a
					 * *dynamic* selection list, in which case the list of
					 * SelectionListValue entities, "selectionListValues" was 
					 * created by the service method 
					 * ReportParameterService.getDynamicSelectionListValues(...).
					 * If this case, selectionListValue was not retrieved from the 
					 * report server database and, therefore it will not have an 
					 * "Id". For this case, the value of the "href" attribute for
					 * the selectionListValueResource object constructed above will
					 * not be meaningful and, therefore, we set the "href" attribute
					 * to null here.
					 */
					selectionListValueResource.setHref(null);
				}

				selectionListValueResources.add(selectionListValueResource);
			}
			return selectionListValueResources;
		} else {
			return null;
		}
	}

	public UUID getSelectionListValueId() {
		return selectionListValueId;
	}

	public void setSelectionListValueId(UUID selectionListValueId) {
		this.selectionListValueId = selectionListValueId;
	}

	public ReportParameterResource getReportParameterResource() {
		return reportParameterResource;
	}

	public void setReportParameterResource(ReportParameterResource reportParameterResource) {
		this.reportParameterResource = reportParameterResource;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getValueAssigned() {
		return valueAssigned;
	}

	public void setValueAssigned(String valueAssigned) {
		this.valueAssigned = valueAssigned;
	}

	public String getValueDisplayed() {
		return valueDisplayed;
	}

	public void setValueDisplayed(String valueDisplayed) {
		this.valueDisplayed = valueDisplayed;
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
		builder.append("SelectionListValueResource [selectionListValueId=");
		builder.append(selectionListValueId);
		builder.append(", reportParameterResource=");
		builder.append(reportParameterResource);
		builder.append(", orderIndex=");
		builder.append(orderIndex);
		builder.append(", valueAssigned=");
		builder.append(valueAssigned);
		builder.append(", valueDisplayed=");
		builder.append(valueDisplayed);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
