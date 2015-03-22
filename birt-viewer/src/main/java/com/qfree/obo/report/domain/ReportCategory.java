package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "report_category", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UUIDCustomType.class)
public class ReportCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "report_category_id", unique = true, nullable = fals)
	//	private Long reportCategoryId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_category_id", columnDefinition = "uuid", unique = true, nullable = false)
	private UUID reportCategoryId;

	@Column(name = "description")
	private String description;

	@Column(name = "abbreviation")
	private String abbreviation;

	@Column(name = "active")
	private boolean active;

	@OneToMany(targetEntity = Report.class, fetch = FetchType.EAGER, mappedBy = "reportCategory")
	private List<Report> reports;

	private ReportCategory() {
	}

	public ReportCategory(String description, String abbreviation, boolean active) {
		this.description = description;
		this.abbreviation = abbreviation;
		this.active = active;
	}

	//	public ReportCategory(UUID reportCategoryId, String description, String abbreviation, boolean active) {
	//		this.reportCategoryId = reportCategoryId;
	//		this.description = description;
	//		this.abbreviation = abbreviation;
	//		this.active = active;
	//	}

	public UUID getReportCategoryId() {
		return reportCategoryId;
	}

	public String getDescription() {
		return description;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public boolean isActive() {
		return active;
	}

	public List<Report> getReports() {
		return reports;
	}
}
