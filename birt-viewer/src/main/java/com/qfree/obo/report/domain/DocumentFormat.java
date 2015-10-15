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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.util.DateUtils;

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

	@OneToMany(targetEntity = Job.class, mappedBy = "documentFormat")
	private List<Job> jobs;

	/**
	 * Name of the document format. Examples are:<br>
	 * <pre>
	 *   OpenDocument Spreadsheet
	 *   PDF
	 *   PowerPoint
	 *   ...
	 * <pre>
	 */
	@NotBlank
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	/**
	 * Extension of the file generated when a particular document format is 
	 * requested. Examples are:<br>
	 * <pre>
	 *   html
	 *   ods
	 *   pdf
	 *   ppt
	 *   ...
	 * <pre>
	 */
	@NotBlank
	@Column(name = "file_extension", nullable = false, length = 12)
	private String fileExtension;

	/**
	 * Internet media type (also referred to as MIME type or Content-type) for 
	 * the document format. Examples are:<br>
	 * <pre>
	 *   text/html
	 *   application/vnd.oasis.opendocument.spreadsheet
	 *   application/pdf
	 *   application/vnd.ms-powerpoint
	 *   ...
	 * <pre>
	 */
	@NotBlank
	@Column(name = "media_type", nullable = false, length = 100)
	private String mediaType;

	/**
	 * The value assigned to the BIRT "__format" URL query parameter in order
	 * to request that a report be delivered in the specified format.
	 */
	@NotBlank
	@Column(name = "birt_format", nullable = false, length = 12)
	private String birtFormat;

	/**
	 * true if the document format is binary. This implies that the document
	 * must be Base64 encoded in order to store it in the "document" text column
	 * of [job].
	 */
	@NotNull
	@Column(name = "binary_data", nullable = false)
	private Boolean binaryData;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	//	@OneToMany(targetEntity = Subscription.class, mappedBy = "documentFormat")
	//	private List<Subscription> subscriptions;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private DocumentFormat() {
	}

	public DocumentFormat(String name, String fileExtension, String mediaType, String birtFormat, Boolean binary) {
		this(null, name, fileExtension, mediaType, birtFormat, binary, true, DateUtils.nowUtc());
	}

	public DocumentFormat(String name, String fileExtension, String mediaType, String birtFormat, Boolean binary,
			Boolean active) {
		this(null, name, fileExtension, mediaType, birtFormat, binary, active, DateUtils.nowUtc());
	}

	public DocumentFormat(DocumentFormatResource documentFormatResource) {
		this(
				documentFormatResource.getDocumentFormatId(),
				documentFormatResource.getName(),
				documentFormatResource.getFileExtension(),
				documentFormatResource.getMediaType(),
				documentFormatResource.getBirtFormat(),
				documentFormatResource.getBinaryData(),
				documentFormatResource.getActive(),
				documentFormatResource.getCreatedOn());
	}

	public DocumentFormat(UUID documentFormatId, String name, String fileExtension, String mediaType, String birtFormat,
			Boolean binary,
			Boolean active, Date createdOn) {
		this.documentFormatId = documentFormatId;
		this.name = name;
		this.fileExtension = fileExtension;
		this.mediaType = mediaType;
		this.birtFormat = birtFormat;
		this.binaryData = binary;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
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

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
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

	public UUID getDocumentFormatId() {
		return documentFormatId;
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
