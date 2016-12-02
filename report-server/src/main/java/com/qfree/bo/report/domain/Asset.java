package com.qfree.bo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.bo.report.dto.AssetResource;
import com.qfree.bo.report.util.DateUtils;

/**
 * The persistent class for the "asset" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "asset", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "filename", "asset_tree_id", "asset_type_id" },
						name = "uq_asset_name_tree_type") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Asset implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "asset_id", unique = true, nullable = false)
	//	private Long assetId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "asset_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID assetId;

	@NotBlank
	@Column(name = "filename", nullable = false, length = 256)
	private String filename;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "asset_tree_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_asset_assettree") ,
			columnDefinition = "uuid")
	private AssetTree assetTree;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "asset_type_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_asset_assettype") ,
			columnDefinition = "uuid")
	private AssetType assetType;

	/*
	 * In order for lazy loading to work, it is necessary for Document to have 
	 * a non-private no-argument constructor. If Document's no-arg constructor 
	 * is private, an org.springframework.orm.jpa.JpaSystemException will be 
	 * thrown that will report something like:
	 * 
	 *   Javassist Enhancement failed: com.qfree.bo.report.domain.Document; 
	 *   nested exception is org.hibernate.HibernateException: Javassist 
	 *   Enhancement failed: com.qfree.bo.report.domain.Document
	 * 
	 * Should this relation be made @OnetoOne?
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "document_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_asset_document") ,
			columnDefinition = "uuid")
	private Document document;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Asset() {
	}

	public Asset(AssetTree assetTree, AssetType assetType, Document document, String filename, Boolean active) {
		this(assetTree, assetType, document, filename, active, DateUtils.nowUtc());
	}

	public Asset(AssetTree assetTree, AssetType assetType, Document document, String filename, Boolean active,
			Date createdOn) {
		this(null, assetTree, assetType, document, filename, active, createdOn);
	}

	public Asset(AssetResource assetResource, AssetTree assetTree, AssetType assetType, Document document) {
		this(
				assetResource.getAssetId(),
				assetTree,
				assetType,
				document,
				assetResource.getFilename(),
				assetResource.isActive(),
				assetResource.getCreatedOn());
	}

	/**
	 * Constructor to create a shallow copy of an existing {@link Asset}.
	 * 
	 * @param asset
	 */
	public Asset(Asset asset) {
		this(
				asset.getAssetId(),
				asset.getAssetTree(),
				asset.getAssetType(),
				asset.getDocument(),
				asset.getFilename(),
				asset.isActive(),
				asset.getCreatedOn());
	}

	public Asset(UUID assetId, AssetTree assetTree, AssetType assetType, Document document, String filename,
			Boolean active, Date createdOn) {
		this.assetId = assetId;
		this.assetTree = assetTree;
		this.assetType = assetType;
		this.document = document;
		this.filename = filename;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getAssetId() {
		return this.assetId;
	}

	public String getFilename() {
		return this.filename;
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
		return this.createdOn;
	}

	public AssetTree getAssetTree() {
		return this.assetTree;
	}

	public void setAssetTree(AssetTree assetTree) {
		this.assetTree = assetTree;
	}

	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Asset [assetId=");
		builder.append(assetId);
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
