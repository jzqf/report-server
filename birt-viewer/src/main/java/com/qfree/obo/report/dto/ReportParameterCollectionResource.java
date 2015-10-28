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
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportParameterCollectionResource
		extends AbstractCollectionResourceXXXXXX<ReportParameterResource, ReportParameter> {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterCollectionResource.class);

	@XmlElement
	private List<ReportParameterResource> items;

	public ReportParameterCollectionResource() {
	}

	public ReportParameterCollectionResource(
			ReportVersion reportVersion,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				reportVersion.getReportParameters(),
				ReportParameter.class,
				AbstractBaseResource.createHref(uriInfo, ReportVersion.class, reportVersion.getReportVersionId(), null),
				ResourcePath.REPORTPARAMETERS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public ReportParameterCollectionResource(
			List<ReportParameter> reportParameters,
			Class<ReportParameter> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				reportParameters,
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
			this.items = ReportParameterResource.reportParameterResourceListPageFromReportParameters(
					reportParameters, uriInfo, queryParams, apiVersion);
		}
	}

	public List<ReportParameterResource> getItems() {
		return items;
	}

	public void setItems(List<ReportParameterResource> items) {
		this.items = items;
	}

}
