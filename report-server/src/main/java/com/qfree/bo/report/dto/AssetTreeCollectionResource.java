package com.qfree.bo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.AssetTree;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class AssetTreeCollectionResource
		extends AbstractCollectionResource<AssetTreeResource, AssetTree> {

	private static final Logger logger = LoggerFactory.getLogger(AssetTreeCollectionResource.class);

	@XmlElement
	private List<AssetTreeResource> items;

	public AssetTreeCollectionResource() {
	}

	public AssetTreeCollectionResource(
			List<AssetTree> assetTrees,
			Class<AssetTree> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(
				assetTrees,
				entityClass,
				null,
				null,
				uriInfo,
				queryParams,
				apiVersion);
	}

	public AssetTreeCollectionResource(
			List<AssetTree> assetTrees,
			Class<AssetTree> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(
				assetTrees,
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
			this.items = AssetTreeResource.assetTreeResourceListPageFromAssetTrees(
					assetTrees, uriInfo, queryParams, apiVersion);
		}
	}

	public List<AssetTreeResource> getItems() {
		return items;
	}

	public void setItems(List<AssetTreeResource> items) {
		this.items = items;
	}

}
