package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportParameterCollectionResource extends AbstractCollectionResource<ReportParameterResource> {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterCollectionResource.class);

	@XmlElement
	private List<ReportParameterResource> items;

	public ReportParameterCollectionResource() {
	}

	public ReportParameterCollectionResource(ReportVersion reportVersion, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				ReportParameterResource.listFromReportVersion(reportVersion, uriInfo, queryParams, apiVersion),
				ReportParameter.class,
				AbstractBaseResource.createHref(uriInfo, ReportVersion.class, reportVersion.getReportVersionId(), null),
				ResourcePath.REPORTPARAMETERS_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public ReportParameterCollectionResource(List<ReportParameterResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public ReportParameterCollectionResource(List<ReportParameterResource> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<ReportParameterResource> getItems() {
		return items;
	}

	public void setItems(List<ReportParameterResource> items) {
		this.items = items;
	}

}
