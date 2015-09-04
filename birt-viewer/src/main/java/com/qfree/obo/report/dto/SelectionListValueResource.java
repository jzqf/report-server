package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
	@XmlJavaTypeAdapter(DateAdapter.class)
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

	public static List<SelectionListValueResource> listFromReportParameter(ReportParameter reportParameter,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		if (reportParameter.getSelectionListValues() != null) {
			List<SelectionListValue> selectionListValues = reportParameter.getSelectionListValues();
			List<SelectionListValueResource> selectionListValueResources = new ArrayList<>(selectionListValues.size());
			for (SelectionListValue selectionListValue : selectionListValues) {
				selectionListValueResources.add(
						new SelectionListValueResource(selectionListValue, uriInfo, queryParams, apiVersion));
			}
			return selectionListValueResources;
		} else {
			return null;
		}
	}

}
