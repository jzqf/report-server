package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.AssetTypeResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "asset_type" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "asset_type", schema = "reporting", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "name" }, name = "uq_assettype_name"),
		@UniqueConstraint(columnNames = { "abbreviation" }, name = "uq_assettype_abbreviation"),
		@UniqueConstraint(columnNames = { "directory" }, name = "uq_assettype_directory") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class AssetType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "asset_type_id", unique = true, nullable = false)
	//	private Long assetTypeId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "asset_type_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID assetTypeId;

	@NotBlank
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	@NotBlank
	@Column(name = "abbreviation", nullable = false, length = 32)
	private String abbreviation;

	@NotBlank
	@Column(name = "directory", nullable = false, length = 256)
	private String directory;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	//	@OneToMany(targetEntity = Asset.class, mappedBy = "assetType")
	//	private List<Asset> assets;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private AssetType() {
	}

	public AssetType(String name, String abbreviation, String directory) {
		this(null, name, abbreviation, directory, true, DateUtils.nowUtc());
	}

	public AssetType(String name, String abbreviation, String directory, Boolean active) {
		this(null, name, abbreviation, directory, active, DateUtils.nowUtc());
	}

	public AssetType(String name, String abbreviation, String directory, Boolean active, Date createdOn) {
		this(null, name, abbreviation, directory, active, createdOn);
	}

	public AssetType(AssetTypeResource assetTypeResource) {
		this(
				assetTypeResource.getAssetTypeId(),
				assetTypeResource.getName(),
				assetTypeResource.getAbbreviation(),
				assetTypeResource.getDirectory(),
				assetTypeResource.isActive(),
				assetTypeResource.getCreatedOn());
	}

	public AssetType(UUID assetTypeId, String name, String abbreviation, String directory, Boolean active,
			Date createdOn) {
		this.assetTypeId = assetTypeId;
		this.name = name;
		this.abbreviation = abbreviation;
		this.directory = directory;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getAssetTypeId() {
		return assetTypeId;
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

	//	public List<Asset> getAssets() {
	//		return assets;
	//	}
	//
	//	public void setAssets(List<Asset> assets) {
	//		this.assets = assets;
	//	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssetType [assetTypeId=");
		builder.append(assetTypeId);
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
