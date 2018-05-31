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

import com.qfree.bo.report.domain.Asset;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class AssetResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AssetResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID assetId;

	@XmlElement(name = "assetTree")
	private AssetTreeResource assetTreeResource;

	@XmlElement(name = "assetType")
	private AssetTypeResource assetTypeResource;

	@XmlElement(name = "document")
	private DocumentResource documentResource;

	@XmlElement
	private String filename;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public AssetResource() {
	}

	public AssetResource(Asset asset, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(Asset.class, asset.getAssetId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Asset.class).getExpandParam();
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
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			this.assetId = asset.getAssetId();
			this.assetTreeResource = new AssetTreeResource(asset.getAssetTree(),
					uriInfo, newQueryParams, apiVersion);
			this.assetTypeResource = new AssetTypeResource(asset.getAssetType(),
					uriInfo, newQueryParams, apiVersion);
			this.documentResource = new DocumentResource(asset.getDocument(),
					uriInfo, newQueryParams, apiVersion);
			this.filename = asset.getFilename();
			this.active = asset.isActive();
			this.createdOn = asset.getCreatedOn();

			logger.debug("asset = {}", asset);
		}
	}

	public static List<AssetResource> assetResourceListPageFromAssets(List<Asset> assets, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (assets != null) {

			/*
			 * The Asset has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * Asset entities below. Either:
			 * 
			 *   1. Filter the list "assets" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "assets" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Asset entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "assets"; 
			 * otherwise, we use the whole list.
			 */
			List<Asset> pageOfAssets = RestUtils.getPageOfList(assets, queryParams);

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

			List<AssetResource> assetResources = new ArrayList<>(pageOfAssets.size());
			for (Asset asset : pageOfAssets) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				assetResources.add(new AssetResource(asset, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return assetResources;
		} else {
			return null;
		}
	}

	public UUID getAssetId() {
		return assetId;
	}

	public void setAssetId(UUID assetId) {
		this.assetId = assetId;
	}

	public AssetTreeResource getAssetTreeResource() {
		return assetTreeResource;
	}

	public void setAssetTreeResource(AssetTreeResource assetTreeResource) {
		this.assetTreeResource = assetTreeResource;
	}

	public AssetTypeResource getAssetTypeResource() {
		return assetTypeResource;
	}

	public void setAssetTypeResource(AssetTypeResource assetTypeResource) {
		this.assetTypeResource = assetTypeResource;
	}

	public DocumentResource getDocumentResource() {
		return documentResource;
	}

	public void setDocumentResource(DocumentResource documentResource) {
		this.documentResource = documentResource;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Boolean isActive() {
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssetResource [assetId=");
		builder.append(assetId);
		builder.append(", assetTreeResource=");
		builder.append(assetTreeResource);
		builder.append(", assetTypeResource=");
		builder.append(assetTypeResource);
		builder.append(", documentResource=");
		builder.append(documentResource);
		builder.append(", filename=");
		builder.append(filename);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
