package com.qfree.obo.report.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "report_category", schema = "reporting")
public class ReportCategory {

	private ReportCategory() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_category_id")
	private Long reportCategoryId;

	@Column(name = "description")
	private String description;

	@Column(name = "abbreviation")
	private String abbreviation;

	@Column(name = "active")
	private boolean active;

	@OneToMany(targetEntity = Report.class, fetch = FetchType.EAGER, mappedBy = "reportCategory")
	private List<Report> reports;

	public ReportCategory(Long reportCategoryId, String description, String abbreviation, boolean active) {
		this.reportCategoryId = reportCategoryId;
		this.description = description;
		this.abbreviation = abbreviation;
		this.active = active;
	}

	public Long getReportCategoryId() {
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
