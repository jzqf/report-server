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
	//public class ReportParameterCollectionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterCollectionResource.class);

	@XmlElement
	private List<ReportParameterResource> items;

	public ReportParameterCollectionResource() {
	}

	public ReportParameterCollectionResource(ReportVersion reportVersion, UriInfo uriInfo,
			List<String> expand, Map<String, List<String>> extraQueryParams, RestApiVersion apiVersion) {
		this(
				ReportParameterResource.listFromReportVersion(reportVersion,
						uriInfo, expand, extraQueryParams, apiVersion),
				ReportParameter.class,
				AbstractBaseResource.createHref(uriInfo, ReportVersion.class, reportVersion.getReportVersionId(), null),
				ResourcePath.REPORTPARAMETERS_PATH,
				extraQueryParams, uriInfo, expand, apiVersion);
	}

	public ReportParameterCollectionResource(List<ReportParameterResource> items, Class<?> entityClass,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {
		//
		//		super(items, entityClass, uriInfo, expand, apiVersion);  // if class extends AbstractCollectionResource<ReportParameterResource>
		//		//super(entityClass, null, uriInfo, expand, apiVersion);  // if class extends AbstractBaseResource
		//
		//		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		//		if (expand.contains(expandParam)) {
		//			this.items = items;
		//		}
		this(items, entityClass, null, null, null, uriInfo, expand, apiVersion);
	}

	public ReportParameterCollectionResource(List<ReportParameterResource> items, Class<?> entityClass,
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

	public List<ReportParameterResource> getItems() {
		return items;
	}

	public void setItems(List<ReportParameterResource> items) {
		this.items = items;
	}

}
