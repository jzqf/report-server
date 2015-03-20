package com.qfree.obo.report.domain_generated;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the report database table.
 * 
 */
@Entity
@Table(name = "report_uuid", schema = "reporting")
public class ReportUuid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="report_id", unique=true, nullable=false)
	private String reportId;

	@Column(name="created_on", nullable=false)
	private Timestamp createdOn;

	@Column(nullable=false, length=80)
	private String name;

	//bi-directional many-to-one association to ReportCategory
	@ManyToOne
	@JoinColumn(name="report_category_id", nullable=false)
	private ReportCategoryUuid reportCategory;

	public ReportUuid() {
	}

	public String getReportId() {
		return this.reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public Timestamp getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReportCategoryUuid getReportCategory() {
		return this.reportCategory;
	}

	public void setReportCategory(ReportCategoryUuid reportCategory) {
		this.reportCategory = reportCategory;
	}

}