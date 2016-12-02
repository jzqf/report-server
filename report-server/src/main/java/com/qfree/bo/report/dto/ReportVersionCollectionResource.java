package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Report;
import com.qfree.bo.report.domain.ReportVersion;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportVersionCollectionResource
		extends AbstractCollectionResource<ReportVersionResource, ReportVersion> {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionCollectionResource.class);

	@XmlElement
	private List<ReportVersionResource> items;

	public ReportVersionCollectionResource() {
	}

	public ReportVersionCollectionResource(
			Report report,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				report.getReportVersions(),
				ReportVersion.class,
				AbstractBaseResource.createHref(uriInfo, Report.class, report.getReportId(), null),
				ResourcePath.REPORTVERSIONS_PATH,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public ReportVersionCollectionResource(
			List<ReportVersion> reportVersions,
			Class<ReportVersion> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				reportVersions,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public ReportVersionCollectionResource(
			List<ReportVersion> reportVersions,
			Class<ReportVersion> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				reportVersions,
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
			this.items = ReportVersionResource.reportVersionResourceListPageFromReportVersions(
					reportVersions, uriInfo, queryParams, apiVersion);
		}
	}

	public List<ReportVersionResource> getItems() {
		return items;
	}

	public void setItems(List<ReportVersionResource> items) {
		this.items = items;
	}

}
