package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class DocumentFormatCollectionResource extends AbstractCollectionResource<DocumentFormatResource> {
	// public class DocumentFormatCollectionResource extends
	// AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFormatCollectionResource.class);

	@XmlElement
	private List<DocumentFormatResource> items;

	public DocumentFormatCollectionResource() {
	}

	public DocumentFormatCollectionResource(List<DocumentFormatResource> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(items, entityClass, uriInfo, queryParams, apiVersion);  // if class extends AbstractCollectionResource<ReportResource>
		//super(entityClass, null, uriInfo, queryParams, apiVersion);  // if class extends AbstractBaseResource

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		if (ResourcePath.expand(entityClass, expand)) {
			this.items = items;
		}
	}

	public List<DocumentFormatResource> getItems() {
		return items;
	}

	public void setItems(List<DocumentFormatResource> items) {
		this.items = items;
	}

}
