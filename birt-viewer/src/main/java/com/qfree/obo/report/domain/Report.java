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
	@JoinColumn(name = "spitter")
	private ReportCategory spitter;

	@Column
	private String message;

	@Column
	private Date postedTime;

	private Report() {
	}

	public Report(Long reportId, ReportCategory spitter, String message, Date postedTime) {
		this.reportId = reportId;
		this.spitter = spitter;
		this.message = message;
		this.postedTime = postedTime;
	}

	public Long getReportId() {
		return this.reportId;
	}

	public String getMessage() {
		return this.message;
	}

	public Date getPostedTime() {
		return this.postedTime;
	}

	public ReportCategory getSpitter() {
		return this.spitter;
	}

}
