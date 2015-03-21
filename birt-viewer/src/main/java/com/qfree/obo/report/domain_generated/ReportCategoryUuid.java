package com.qfree.obo.report.domain_generated;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the report_category database table.
 * 
 */
@Entity
@Table(name = "report_category_uuid", schema = "reporting")
public class ReportCategoryUuid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	// type="pg-uuid" only works for postgresql
	@Type(type = "pg-uuid")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_category_id", columnDefinition = "uuid", unique = true, nullable = false)
	private UUID reportCategoryId;

	@Column(nullable=false, length=20)
	private String abbreviation;

	@Column(nullable=false)
	private Boolean active;

	@Column(nullable=false, length=25)
	private String description;

	//bi-directional many-to-one association to Report
	@OneToMany(mappedBy="reportCategory")
	private List<ReportUuid> reports;

	public ReportCategoryUuid() {
	}

	public UUID getReportCategoryId() {
		return this.reportCategoryId;
	}

	public void setReportCategoryId(UUID reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}

	public String getAbbreviation() {
		return this.abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ReportUuid> getReports() {
		return this.reports;
	}

	public void setReports(List<ReportUuid> reports) {
		this.reports = reports;
	}

	public ReportUuid addReport(ReportUuid report) {
		getReports().add(report);
		report.setReportCategory(this);

		return report;
	}

	public ReportUuid removeReport(ReportUuid report) {
		getReports().remove(report);
		report.setReportCategory(null);

		return report;
	}

}