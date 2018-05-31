package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.ReportParameter;
import com.qfree.bo.report.domain.SelectionListValue;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SelectionListValueCollectionResource
		extends AbstractCollectionResource<SelectionListValueResource, SelectionListValue> {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListValueCollectionResource.class);

	@XmlElement
	private List<SelectionListValueResource> items;

	public SelectionListValueCollectionResource() {
	}

	/**
	 * This constructor is only for <b>static</b> selection lists, since the
	 * {@link ReportParameter#selectionListValues} field of reportParameter will
	 * only contain {@link SelectionListValue} entities from the database.
	 * 
	 * <p>
	 * {@link SelectionListValue} entities that are stored in the database are
	 * <i>only</i> for <b>static</b> selection lists.
	 * 
	 * @param reportParameter
	 * @param uriInfo
	 * @param queryParams
	 * @param apiVersion
	 */
	public SelectionListValueCollectionResource(
			ReportParameter reportParameter,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				reportParameter.getSelectionListValues(),
				SelectionListValue.class,
				AbstractBaseResource.createHref(uriInfo, ReportParameter.class, reportParameter.getReportParameterId(),
						null),
				ResourcePath.SELECTIONLISTVALUES_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public SelectionListValueCollectionResource(
			List<SelectionListValue> selectionListValues,
			Class<SelectionListValue> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				selectionListValues,
				entityClass,
				baseResourceUri,
				collectionPath,
				uriInfo,
				queryParams,
				apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			/*
			 * We pass null for apiVersion since the version used in the 
			 * original request does not necessarily apply here.
			 */
			apiVersion = null;
			this.items = SelectionListValueResource.selectionListValueResourceListPageFromSelectionListValues(
					selectionListValues, uriInfo, queryParams, apiVersion);
		}

	}

	public List<SelectionListValueResource> getItems() {
		return items;
	}

	public void setItems(List<SelectionListValueResource> items) {
		this.items = items;
	}

}
