package com.qfree.bo.report.domain;

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

import com.qfree.bo.report.dto.ReportCategoryResource;
import com.qfree.bo.report.util.DateUtils;

/**
 * The persistent class for the "report_category" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report_category", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ReportCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "report_category_id", unique = true, nullable = false)
	//	private Long reportCategoryId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_category_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportCategoryId;

	@NotBlank
	@Column(name = "abbreviation", nullable = false, length = 32)
	private String abbreviation;

	@NotBlank
	@Column(name = "description", nullable = false, length = 32)
	private String description;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@OneToMany(targetEntity = Report.class, mappedBy = "reportCategory")
	private List<Report> reports;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private ReportCategory() {
	}

	public ReportCategory(String description, String abbreviation) {
		this(null, description, abbreviation, true, DateUtils.nowUtc());
	}

	public ReportCategory(String description, String abbreviation, Boolean active) {
		this(null, description, abbreviation, active, DateUtils.nowUtc());
	}

	public ReportCategory(String description, String abbreviation, Boolean active, Date createdOn) {
		this(null, description, abbreviation, active, createdOn);
	}

	public ReportCategory(ReportCategoryResource reportCategoryResource) {
		this(
				reportCategoryResource.getReportCategoryId(),
				reportCategoryResource.getDescription(),
				reportCategoryResource.getAbbreviation(),
				reportCategoryResource.isActive(),
				//				(reportCategoryResource.isActive() != null) ? reportCategoryResource.isActive() : true,
				reportCategoryResource.getCreatedOn());
	}

	public ReportCategory(UUID reportCategoryId, String description, String abbreviation, Boolean active, Date createdOn) {
		this.reportCategoryId = reportCategoryId;
		this.description = description;
		this.abbreviation = abbreviation;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getReportCategoryId() {
		return reportCategoryId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Report> getReports() {
		return reports;
	}

	public void setReports(List<Report> reports) {
		this.reports = reports;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportCategory [reportCategoryId=");
		builder.append(reportCategoryId);
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
