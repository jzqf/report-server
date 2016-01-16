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
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.dto.DocumentResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "document" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "document", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Document implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "document_id", unique = true, nullable = false)
	//	private Long documentId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "document_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID documentId;

	@Column(name = "content", nullable = true)
	private byte[] content;

	//	@OneToMany(targetEntity = Asset.class, mappedBy = "document")
	//	private List<Asset> assets;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Document() {
	}

	public Document(byte[] content) {
		this(null, content, DateUtils.nowUtc());
	}

	public Document(byte[] content, Date createdOn) {
		this(null, content, createdOn);
	}

	public Document(DocumentResource documentResource) {
		this(
				documentResource.getDocumentId(),
				/*
				 * This converts the hex-encoded string to a byte array.
				 */
				DatatypeConverter.parseHexBinary(documentResource.getContent()),
				documentResource.getCreatedOn());
	}

	public Document(UUID documentId, byte[] content, Date createdOn) {
		this.documentId = documentId;
		this.content = content;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getDocumentId() {
		return documentId;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
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
		builder.append("Document [documentId=");
		builder.append(documentId);
		builder.append(", content=");
		builder.append("<" + ((content != null) ? content.length : 0) + " bytes>");
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
