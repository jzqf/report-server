package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class SelectionListValueCollectionResource extends AbstractCollectionResource<SelectionListValueResource> {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListValueCollectionResource.class);

	@XmlElement
	private List<SelectionListValueResource> items;

	public SelectionListValueCollectionResource() {
	}

	public SelectionListValueCollectionResource(ReportParameter reportParameter, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				SelectionListValueResource.listFromReportParameter(reportParameter, uriInfo, queryParams, apiVersion),
				SelectionListValue.class,
				AbstractBaseResource.createHref(uriInfo, ReportParameter.class, reportParameter.getReportParameterId(),
						null),
				ResourcePath.SELECTIONLISTVALUES_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public SelectionListValueCollectionResource(
			List<SelectionListValueResource> items,
			Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public SelectionListValueCollectionResource(
			List<SelectionListValueResource> items,
			Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);// if class extends AbstractCollectionResource<SelectionListValueResource>

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<SelectionListValueResource> getItems() {
		return items;
	}

	public void setItems(List<SelectionListValueResource> items) {
		this.items = items;
	}

}
