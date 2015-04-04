package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * The persistent class for the "document_format" database table.
 * 
 * Used to specify the graphical GUI element that will be used to prompt for
 * a value, or for values, for a report parameter.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "document_format", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class DocumentFormat implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "document_format_id", unique = true, nullable = false)
	//	private Long documentFormatId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "document_format_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID documentFormatId;

	@OneToMany(targetEntity = Subscription.class, mappedBy = "documentFormat")
	private List<Subscription> subscriptions;

	/*
	 * E.g., "OpenDocument Spreadsheet", "PDF", "PowerPoint", ...
	 */
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	/*
	 * Extension of the file generated when a particular document format is 
	 * requested.
	 */
	@Column(name = "file_extension", nullable = false, length = 8)
	private String fileExtension;

	/*
	 * The value assigned to the BIRT "__format" URL query parameter in order
	 * to request that a report be delivered in the specified format.
	 */
	@Column(name = "birt_format", nullable = false, length = 8)
	private String birtFormat;

	@Column(name = "active", nullable = false)
	private boolean active;

	//	@OneToMany(targetEntity = Subscription.class, mappedBy = "documentFormat")
	//	private List<Subscription> subscriptions;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private DocumentFormat() {
	}

	public DocumentFormat(String name, String fileExtension, String birtFormat) {
		this(name, fileExtension, birtFormat, true, new Date());
	}

	public DocumentFormat(String name, String fileExtension, String birtFormat, boolean active) {
		this(name, fileExtension, birtFormat, active, new Date());
	}

	public DocumentFormat(String name, String fileExtension, String birtFormat, boolean active, Date createdOn) {
		this.name = name;
		this.fileExtension = fileExtension;
		this.birtFormat = birtFormat;
		this.active = active;
		this.createdOn = createdOn;
	}

	public UUID getDocumentFormatId() {
		return documentFormatId;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirtFormat() {
		return birtFormat;
	}

	public void setBirtFormat(String birtFormat) {
		this.birtFormat = birtFormat;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DocumentFormat [name=");
		builder.append(name);
		builder.append(", fileExtension=");
		builder.append(fileExtension);
		builder.append(", birtFormat=");
		builder.append(birtFormat);
		return builder.toString();
	}

}
