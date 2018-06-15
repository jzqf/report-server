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

import com.qfree.bo.report.domain.DocumentFormat;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
//@XmlJavaTypeAdapter(value = UuidAdapter.class, type = UUID.class) <- doesn't work
public class DocumentFormatResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFormatResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID documentFormatId;

	@XmlElement
	private String name;

	@XmlElement
	private String fileExtension;

	@XmlElement
	private String internetMediaType;

	@XmlElement
	private String birtFormat;

	@XmlElement
	private Boolean binaryData;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	@XmlElement(name = "subscriptions")
	private SubscriptionCollectionResource subscriptionCollectionResource;

	public DocumentFormatResource() {
	}

	public DocumentFormatResource(DocumentFormat documentFormat, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(DocumentFormat.class, documentFormat.getDocumentFormatId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(DocumentFormat.class).getExpandParam();
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

			/*
			 * This is how to support pagination in collection resources that
			 * are attributes of an instance resource. This inserts default
			 * pagination attributes into the query parameter map 
			 * "newQueryParams".
			 */
			List<String> pageOffset = new ArrayList<>();
			List<String> pageLimit = new ArrayList<>();
			RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, newQueryParams);

			this.documentFormatId = documentFormat.getDocumentFormatId();
			this.name = documentFormat.getName();
			this.fileExtension = documentFormat.getFileExtension();
			this.internetMediaType = documentFormat.getInternetMediaType();
			this.birtFormat = documentFormat.getBirtFormat();
			this.binaryData = documentFormat.getBinaryData();
			this.active = documentFormat.getActive();
			this.createdOn = documentFormat.getCreatedOn();
			this.subscriptionCollectionResource = new SubscriptionCollectionResource(documentFormat,
					uriInfo, newQueryParams, apiVersion);
		}
		logger.debug("this = {}", this);
	}

	public static List<DocumentFormatResource> documentFormatResourceListPageFromDocumentFormats(
			List<DocumentFormat> documentFormats,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (documentFormats != null) {

			/*
			 * The DocumentFormat has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * DocumentFormat entities below. Either:
			 * 
			 *   1. Filter the list "documentFormats" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "documentFormats" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of DocumentFormat entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "documentFormats"; 
			 * otherwise, we use the whole list.
			 */
			List<DocumentFormat> pageOfDocumentFormats = RestUtils.getPageOfList(documentFormats, queryParams);

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

			List<DocumentFormatResource> documentFormatResources = new ArrayList<>(pageOfDocumentFormats.size());
			for (DocumentFormat documentFormat : pageOfDocumentFormats) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				documentFormatResources.add(
						new DocumentFormatResource(documentFormat, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return documentFormatResources;
		} else {
			return null;
		}
	}

	public UUID getDocumentFormatId() {
		return documentFormatId;
	}

	public void setDocumentFormatId(UUID documentFormatId) {
		this.documentFormatId = documentFormatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getInternetMediaType() {
		return internetMediaType;
	}

	public void setInternetMediaType(String internetMediaType) {
		this.internetMediaType = internetMediaType;
	}

	public String getBirtFormat() {
		return birtFormat;
	}

	public void setBirtFormat(String birtFormat) {
		this.birtFormat = birtFormat;
	}

	public Boolean getBinaryData() {
		return binaryData;
	}

	public void setBinaryData(Boolean binaryData) {
		this.binaryData = binaryData;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public SubscriptionCollectionResource getSubscriptionCollectionResource() {
		return subscriptionCollectionResource;
	}

	public void setSubscriptionCollectionResource(SubscriptionCollectionResource subscriptionCollectionResource) {
		this.subscriptionCollectionResource = subscriptionCollectionResource;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DocumentFormatResource [documentFormatId=");
		builder.append(documentFormatId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", fileExtension=");
		builder.append(fileExtension);
		builder.append(", internetMediaType=");
		builder.append(internetMediaType);
		builder.append(", birtFormat=");
		builder.append(birtFormat);
		builder.append(", binaryData=");
		builder.append(binaryData);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
