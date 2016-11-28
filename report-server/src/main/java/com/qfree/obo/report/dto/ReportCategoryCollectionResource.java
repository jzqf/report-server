package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportCategoryCollectionResource
		extends AbstractCollectionResource<ReportCategoryResource, ReportCategory> {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryCollectionResource.class);

	@XmlElement
	private List<ReportCategoryResource> items;

	public ReportCategoryCollectionResource() {
	}

	public ReportCategoryCollectionResource(
			List<ReportCategory> reportCategories,
			Class<ReportCategory> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				reportCategories,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public ReportCategoryCollectionResource(
			List<ReportCategory> reportCategories,
			Class<ReportCategory> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				reportCategories,
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
			this.items = ReportCategoryResource.reportCategoryResourceListPageFromReportCategories(
					reportCategories, uriInfo, queryParams, apiVersion);
		}
	}

	public List<ReportCategoryResource> getItems() {
		return items;
	}

	public void setItems(List<ReportCategoryResource> items) {
		this.items = items;
	}

}
