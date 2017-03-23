package com.qfree.bo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Document;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
//@XmlJavaTypeAdapter(value = UuidAdapter.class, type = UUID.class) <- doesn't work
public class DocumentResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(DocumentResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID documentId;

	/**
	 * The document's content, Base64-encoded.
	 * 
	 * The content must be encoded because the document may represent a binary
	 * file, e.g., a JPEG file. JSON attributes must be expressed as text
	 * values.
	 */
	@XmlElement
	//	private byte[] content;
	private String content;

	//	@XmlElement
	//	private List<Asset> assets;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public DocumentResource() {
	}

	public DocumentResource(Document document, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(Document.class, document.getDocumentId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Document.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

			this.documentId = document.getDocumentId();
			//	this.content = document.getContent();
			this.content = String.format("<%s bytes>",
					(document.getContent() != null) ? document.getContent().length : 0);
			this.createdOn = document.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public static List<DocumentResource> documentResourceListPageFromDocuments(
			List<Document> documents,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (documents != null) {

			/*
			 * Create a List of Document entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "documents"; 
			 * otherwise, we use the whole list.
			 */
			List<Document> pageOfDocuments = RestUtils.getPageOfList(documents, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<DocumentResource> documentResources = new ArrayList<>(pageOfDocuments.size());
			for (Document document : pageOfDocuments) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				documentResources
						.add(new DocumentResource(document, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return documentResources;
		} else {
			return null;
		}
	}

	public UUID getDocumentId() {
		return documentId;
	}

	public void setDocumentId(UUID documentId) {
		this.documentId = documentId;
	}

	//	public byte[] getContent() {
	public String getContent() {
		return content;
	}

	//	public void setContent(byte[] content) {
	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DocumentResource [documentId=");
		builder.append(documentId);
		builder.append(", content=");
		builder.append(content);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
