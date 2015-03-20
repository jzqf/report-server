package com.qfree.obo.report.domain_generated;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the report_category database table.
 * 
 */
@Entity
@Table(name = "report_category_uuid", schema = "reporting")
public class ReportCategoryUuid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="report_category_id", unique=true, nullable=false)
	private String reportCategoryId;

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

	public String getReportCategoryId() {
		return this.reportCategoryId;
	}

	public void setReportCategoryId(String reportCategoryId) {
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