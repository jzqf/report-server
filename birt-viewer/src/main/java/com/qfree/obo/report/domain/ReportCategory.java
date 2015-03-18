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
@Table(name = "report_category")
public class ReportCategory {

	private ReportCategory() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_category_id")
	private Long reportCategoryId;

	@Column(name = "username")
	private String username;

	@Column(name = "unused_report_category_field_1")
	private String unusedReportCategoryField1;

	@Column(name = "fullname")
	private String fullName;

	@Column(name = "unused_report_category_field_2")
	private String unusedReportCategoryField2;

	@Column(name = "updateByEmail")
	private boolean updateByEmail;

	@Column(name = "unused_report_category_field_3")
	private String unusedReportCategoryField3;

	@OneToMany(targetEntity = Report.class, fetch = FetchType.EAGER, mappedBy = "spitter")
	private List<Report> spittles;

	public ReportCategory(Long reportCategoryId, String username, String unusedReportCategoryField1, String fullName,
			String unusedReportCategoryField2, boolean updateByEmail) {
		this.reportCategoryId = reportCategoryId;
		this.username = username;
		this.unusedReportCategoryField1 = unusedReportCategoryField1;
		this.fullName = fullName;
		this.unusedReportCategoryField2 = unusedReportCategoryField2;
		this.updateByEmail = updateByEmail;
		this.unusedReportCategoryField3 = "Newbie";
	}

	public Long getReportCategoryId() {
		return reportCategoryId;
	}

	public String getUsername() {
		return username;
	}

	public String getUnusedReportCategoryField1() {
		return unusedReportCategoryField1;
	}

	public String getFullName() {
		return fullName;
	}

	public String getUnusedReportCategoryField2() {
		return unusedReportCategoryField2;
	}

	public boolean isUpdateByEmail() {
		return updateByEmail;
	}

	public String getUnusedReportCategoryField3() {
		return unusedReportCategoryField3;
	}

	public List<Report> getSpittles() {
		return spittles;
	}
}
