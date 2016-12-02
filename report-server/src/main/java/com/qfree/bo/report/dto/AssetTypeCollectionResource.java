package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.AssetType;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class AssetTypeCollectionResource
		extends AbstractCollectionResource<AssetTypeResource, AssetType> {

	private static final Logger logger = LoggerFactory.getLogger(AssetTypeCollectionResource.class);

	@XmlElement
	private List<AssetTypeResource> items;

	public AssetTypeCollectionResource() {
	}

	public AssetTypeCollectionResource(
			List<AssetType> assetTypes,
			Class<AssetType> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				assetTypes,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public AssetTypeCollectionResource(
			List<AssetType> assetTypes,
			Class<AssetType> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				assetTypes,
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
			this.items = AssetTypeResource.assetTypeResourceListPageFromAssetTypes(
					assetTypes, uriInfo, queryParams, apiVersion);
		}
	}

	public List<AssetTypeResource> getItems() {
		return items;
	}

	public void setItems(List<AssetTypeResource> items) {
		this.items = items;
	}

}
