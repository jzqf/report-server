package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "report_parameter", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ReportParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_parameter_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportParameterId;

	@Column(name = "abbreviation", nullable = false, length = 32)
	private String abbreviation;

	@Column(name = "description", nullable = false, length = 32)
	private String description;

	@Column(name = "active", nullable = false)
	private Boolean active;

	//	@OneToMany(targetEntity = Report.class, fetch = FetchType.EAGER, mappedBy = "reportCategory")
	//	private List<Report> reports;

	public ReportParameter() {
	}

	public ReportParameter(String abbreviation, String description, Boolean active) {
		this.abbreviation = abbreviation;
		this.description = description;
		this.active = active;
	}

	//	public ReportParameter(UUID reportParameterId, String abbreviation, String description, Boolean active) {
	//		//super();
	//		//		if (reportParameterId == null) {
	//		//			reportParameterId = java.util.UUID.randomUUID();
	//		//		}
	//		this.reportParameterId = reportParameterId;
	//		this.abbreviation = abbreviation;
	//		this.description = description;
	//		this.active = active;
	//	}

	public UUID getReportParameterId() {
		return this.reportParameterId;
	}

	public void setReportParameterId(UUID reportParameterId) {
		this.reportParameterId = reportParameterId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportParameter [reportParameterId=");
		builder.append(reportParameterId);
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}


	//	public List<ReportUuid> getReports() {
	//		return this.reports;
	//	}
	//
	//	public void setReports(List<ReportUuid> reports) {
	//		this.reports = reports;
	//	}
	//
	//	public ReportUuid addReport(ReportUuid report) {
	//		getReports().add(report);
	//		report.setReportCategory(this);
	//
	//		return report;
	//	}
	//
	//	public ReportUuid removeReport(ReportUuid report) {
	//		getReports().remove(report);
	//		report.setReportCategory(null);
	//
	//		return report;
	//	}

}