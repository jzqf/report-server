package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportVersionCollectionResource extends AbstractCollectionResource<ReportVersionResource> {
	//public class ReportVersionCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionCollectionResource.class);

	@XmlElement
	private List<ReportVersionResource> items;

	public ReportVersionCollectionResource() {
	}

	public ReportVersionCollectionResource(Report report, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(
				ReportVersionResource.listFromReport(report, uriInfo, queryParams, apiVersion),
				ReportVersion.class,
				AbstractBaseResource.createHref(uriInfo, Report.class, report.getReportId(), null),
				ResourcePath.REPORTVERSIONS_PATH,
				uriInfo, queryParams, apiVersion);
	}

	public ReportVersionCollectionResource(List<ReportVersionResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public ReportVersionCollectionResource(List<ReportVersionResource> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, uriInfo, queryParams, apiVersion);  // if class extends AbstractCollectionResource<ReportVersionResource>
		//super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportVersionResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<ReportVersionResource> getItems() {
		return items;
	}

	public void setItems(List<ReportVersionResource> items) {
		this.items = items;
	}

}
