package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.DocumentFormat;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class DocumentFormatCollectionResource
		extends AbstractCollectionResource<DocumentFormatResource, DocumentFormat> {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFormatCollectionResource.class);

	@XmlElement
	private List<DocumentFormatResource> items;

	public DocumentFormatCollectionResource() {
	}

	public DocumentFormatCollectionResource(
			List<DocumentFormat> documentFormats,
			Class<DocumentFormat> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				documentFormats,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public DocumentFormatCollectionResource(
			List<DocumentFormat> documentFormats,
			Class<DocumentFormat> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				documentFormats,
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
			this.items = DocumentFormatResource.documentFormatResourceListPageFromDocumentFormats(
					documentFormats, uriInfo, queryParams, apiVersion);
		}
	}

	public List<DocumentFormatResource> getItems() {
		return items;
	}

	public void setItems(List<DocumentFormatResource> items) {
		this.items = items;
	}

}
