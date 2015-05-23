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
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportVersionCollectionResource extends AbstractCollectionResource<ReportVersionResource> {
	//public class ReportVersionCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionCollectionResource.class);

	@XmlElement
	private List<ReportVersionResource> items;

	public ReportVersionCollectionResource() {
	}

	public ReportVersionCollectionResource(Report report, UriInfo uriInfo,
			List<String> expand, Map<String, List<String>> extraQueryParams, RestApiVersion apiVersion) {
		this(
				ReportVersionResource.listFromReport(report,uriInfo, expand, extraQueryParams, apiVersion),
				ReportVersion.class,
				AbstractBaseResource.createHref(uriInfo, Report.class, report.getReportId(), null),
				ResourcePath.REPORTVERSIONS_PATH,
				extraQueryParams, uriInfo, expand, apiVersion);
	}

	public ReportVersionCollectionResource(List<ReportVersionResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, null, uriInfo, expand, apiVersion);
	}

	public ReportVersionCollectionResource(List<ReportVersionResource> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath, Map<String, List<String>> extraQueryParams,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(items, entityClass, baseResourceUri, collectionPath, extraQueryParams, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportVersionResource>
		//super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportVersionResource>
		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource

		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		if (expand.contains(expandParam)) {
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
