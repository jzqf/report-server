package com.qfree.obo.report.domain_generated;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * The persistent class for the report database table.
 * 
 */
@Entity
@Table(name = "report_uuid", schema = "reporting")
public class ReportUuid implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	// type="pg-uuid" only works for postgresql
	@Type(type = "pg-uuid")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_id", columnDefinition = "uuid", unique = true, nullable = false)
	private UUID reportId;

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

	public UUID getReportId() {
		return this.reportId;
	}

	public void setReportId(UUID reportId) {
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