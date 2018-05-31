package com.qfree.bo.report.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class AssetSyncResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AssetSyncResource.class);

	//	@XmlElement
	//	@XmlJavaTypeAdapter(UuidAdapter.class)
	//	private UUID assetSyncId;

	//	@XmlElement
	//	private String name;
	//
	//	@XmlElement
	//	private Integer number;

	@XmlElement
	private List<String> assetsDeleted;

	@XmlElement
	private List<String> assetsNotDeleted;

	@XmlElement
	private List<String> assetsCreated;

	@XmlElement
	private List<String> assetsNotCreated;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public AssetSyncResource() {
	}

	public AssetSyncResource(
			List<String> assetsDeleted,
			List<String> assetsNotDeleted,
			List<String> assetsCreated,
			List<String> assetsNotCreated) {
		super();
		this.assetsDeleted = assetsDeleted;
		this.assetsNotDeleted = assetsNotDeleted;
		this.assetsCreated = assetsCreated;
		this.assetsNotCreated = assetsNotCreated;
	}

	//	public AssetSyncResource(AssetSync assetSync, UriInfo uriInfo, List<String> expand, 
	//			Map<String, List<String>> extraQueryParams, RestApiVersion apiVersion) {
	//
	//		super(AssetSync.class, assetSync.getAssetSyncId(), uriInfo, expand, apiVersion);
	//
	//		String expandParam = ResourcePath.forEntity(AssetSync.class).getExpandParam();
	//		if (expand.contains(expandParam)) {
	//			/*
	//			 * Make a copy of the "expand" list from which expandParam is
	//			 * removed. This list should be used when creating new resources
	//			 * here, instead of the original "expand" list. This is done to 
	//			 * avoid the unlikely event of a long list of chained expansions
	//			 * across relations.
	//			 */
	//			List<String> expandElementRemoved = new ArrayList<>(expand);
	//			expandElementRemoved.remove(expandParam);
	//	/*
	//	 * Make a copy of the original queryParams Map and then replace the 
	//	 * "expand" array with expandElementRemoved.
	//	 */
	//	Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
	//	newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);
	//
	//			/*
	//			 * Clear apiVersion since its current value is not necessarily
	//			 * applicable to any resources associated with fields of this class. 
	//			 * See ReportResource for a more detailed explanation.
	//			 */
	//			apiVersion = null;
	//
	//			this.assetSyncId = assetSync.getAssetSyncId();
	//			this. = assetSync.;
	//			this. = assetSync.;
	//			this.active = assetSync.isActive();
	//			this.createdOn = assetSync.getCreatedOn();
	//
	//		}
	//	}

	public List<String> getAssetsDeleted() {
		return assetsDeleted;
	}

	public void setAssetsDeleted(List<String> assetsDeleted) {
		this.assetsDeleted = assetsDeleted;
	}

	public List<String> getAssetsNotDeleted() {
		return assetsNotDeleted;
	}

	public void setAssetsNotDeleted(List<String> assetsNotDeleted) {
		this.assetsNotDeleted = assetsNotDeleted;
	}

	public List<String> getAssetsCreated() {
		return assetsCreated;
	}

	public void setAssetsCreated(List<String> assetsCreated) {
		this.assetsCreated = assetsCreated;
	}

	public List<String> getAssetsNotCreated() {
		return assetsNotCreated;
	}

	public void setAssetsNotCreated(List<String> assetsNotCreated) {
		this.assetsNotCreated = assetsNotCreated;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssetSyncResource [assetsDeleted=");
		builder.append(assetsDeleted);
		builder.append(", assetsNotDeleted=");
		builder.append(assetsNotDeleted);
		builder.append(", assetsCreated=");
		builder.append(assetsCreated);
		builder.append(", assetsNotCreated=");
		builder.append(assetsNotCreated);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
