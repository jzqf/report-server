package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Document;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class DocumentCollectionResource
		extends AbstractCollectionResource<DocumentResource, Document> {

	private static final Logger logger = LoggerFactory.getLogger(DocumentCollectionResource.class);

	@XmlElement
	private List<DocumentResource> items;

	public DocumentCollectionResource() {
	}

	public DocumentCollectionResource(
			List<Document> documents,
			Class<Document> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				documents,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public DocumentCollectionResource(
			List<Document> documents,
			Class<Document> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				documents,
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
			this.items = DocumentResource.documentResourceListPageFromDocuments(
					documents, uriInfo, queryParams, apiVersion);
		}
	}

	public List<DocumentResource> getItems() {
		return items;
	}

	public void setItems(List<DocumentResource> items) {
		this.items = items;
	}

}
