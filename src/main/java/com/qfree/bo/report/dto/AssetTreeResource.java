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

import com.qfree.bo.report.domain.AssetTree;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
//@XmlJavaTypeAdapter(value = UuidAdapter.class, type = UUID.class) <- doesn't work
public class AssetTreeResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AssetTreeResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID assetTreeId;

	@XmlElement
	private String name;

	@XmlElement
	private String abbreviation;

	@XmlElement
	private String directory;

	//	@XmlElement
	//	private List<Asset> assets;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public AssetTreeResource() {
	}

	public AssetTreeResource(AssetTree assetTree, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(AssetTree.class, assetTree.getAssetTreeId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(AssetTree.class).getExpandParam();
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

			this.assetTreeId = assetTree.getAssetTreeId();
			this.name = assetTree.getName();
			this.abbreviation = assetTree.getAbbreviation();
			this.directory = assetTree.getDirectory();
			//		this.assets=assets;
			this.active = assetTree.isActive();
			this.createdOn = assetTree.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public static List<AssetTreeResource> assetTreeResourceListPageFromAssetTrees(
			List<AssetTree> assetTrees,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (assetTrees != null) {

			/*
			 * The AssetTree has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * AssetTree entities below. Either:
			 * 
			 *   1. Filter the list "assetTrees" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "assetTrees" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of AssetTree entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "assetTrees"; 
			 * otherwise, we use the whole list.
			 */
			List<AssetTree> pageOfAssetTrees = RestUtils.getPageOfList(assetTrees, queryParams);

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

			List<AssetTreeResource> assetTreeResources = new ArrayList<>(pageOfAssetTrees.size());
			for (AssetTree assetTree : pageOfAssetTrees) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				assetTreeResources
						.add(new AssetTreeResource(assetTree, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return assetTreeResources;
		} else {
			return null;
		}
	}

	public UUID getAssetTreeId() {
		return assetTreeId;
	}

	public void setAssetTreeId(UUID assetTreeId) {
		this.assetTreeId = assetTreeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
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
		builder.append("AssetTreeResource [assetTreeId=");
		builder.append(assetTreeId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", directory=");
		builder.append(directory);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
