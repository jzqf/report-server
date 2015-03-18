package com.qfree.obo.report.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "report")
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long reportId;

	@ManyToOne
	@JoinColumn(name = "report_category_id")
	private ReportCategory reportCategory;

	@Column(name = "name")
	private String name;

	@Column
	private Date postedTime;

	private Report() {
	}

	public Report(Long reportId, ReportCategory reportCategory, String name, Date postedTime) {
		this.reportId = reportId;
		this.reportCategory = reportCategory;
		this.name = name;
		this.postedTime = postedTime;
	}

	public Long getReportId() {
		return this.reportId;
	}

	public String getName() {
		return this.name;
	}

	public Date getPostedTime() {
		return this.postedTime;
	}

	public ReportCategory getReportCategory() {
		return this.reportCategory;
	}

}
