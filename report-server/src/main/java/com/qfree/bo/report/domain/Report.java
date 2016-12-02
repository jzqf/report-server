package com.qfree.bo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.bo.report.dto.ReportResource;
import com.qfree.bo.report.util.DateUtils;

/**
 * The persistent class for the "report" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
@NamedQuery(name = "Report.findByCreated", query = "select r from Report r order by r.createdOn desc")
public class Report implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "report_id", unique = true, nullable = false)
	//	private Long reportId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "report_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID reportId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "report_category_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_report_reportcategory"),
			columnDefinition = "uuid")
	private ReportCategory reportCategory;

	@NotBlank
	@Column(name = "name", nullable = false, length = 80)
	private String name;

	@NotNull
	@Column(name = "number", nullable = false)
	private Integer number;

	@NotNull
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its ReportVersion's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<ReportVersion> reportVersions;

	//	/*
	//	 * cascade = CascadeType.ALL:
	//	 *     Deleting a Report will delete all of its ReportParameter's.
	//	 */
	//	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	//	private List<ReportParameter> reportParameters;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Report will delete all of its RoleReport's.
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	private List<RoleReport> roleReports;

	//	/*
	//	 * cascade = CascadeType.ALL:
	//	 *     Deleting a Report will delete all of its Subscription's.
	//	 */
	//	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	//	private List<Subscription> reportSubscriptions;

	//	/*
	//	 * cascade = CascadeType.ALL:
	//	 *     Deleting a Report will delete all of its Job's.
	//	 */
	//	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
	//	private List<Job> jobs;

	//  @NotBlank
	//	@Column(name = "rptdesign", nullable = false, columnDefinition = "text")
	//	private String rptdesign;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Report() {
	}

	public Report(ReportCategory reportCategory, String name, Integer number, Integer sortOrder, Boolean active) {
		this(reportCategory, name, number, sortOrder, active, DateUtils.nowUtc());
	}

	public Report(ReportCategory reportCategory, String name, Integer number, Integer sortOrder, Boolean active,
			Date createdOn) {
		this(null, reportCategory, name, number, sortOrder, active, createdOn);
	}

	public Report(ReportResource reportResource, ReportCategory reportCategory) {
		this(
				reportResource.getReportId(),
				reportCategory,
				reportResource.getName(),
				reportResource.getNumber(),
				reportResource.getSortOrder(),
				reportResource.isActive(),
				reportResource.getCreatedOn());
	}

	public Report(UUID reportId, ReportCategory reportCategory, String name, Integer number, Integer sortOrder,
			Boolean active, Date createdOn) {
		this.reportId = reportId;
		this.reportCategory = reportCategory;
		this.name = name;
		this.number = number;
		this.sortOrder = (sortOrder != null) ? sortOrder : number;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getReportId() {
		return this.reportId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public ReportCategory getReportCategory() {
		return this.reportCategory;
	}

	public void setReportCategory(ReportCategory reportCategory) {
		this.reportCategory = reportCategory;
	}

	public List<ReportVersion> getReportVersions() {
		return reportVersions;
	}

	public void setReportVersions(List<ReportVersion> reportVersions) {
		this.reportVersions = reportVersions;
	}

	public List<RoleReport> getRoleReports() {
		return roleReports;
	}

	public void setRoleReports(List<RoleReport> roleReports) {
		this.roleReports = roleReports;
	}

	//	public String getRptdesign() {
	//		return rptdesign;
	//	}
	//
	//	public void setRptdesign(String rptdesign) {
	//		this.rptdesign = rptdesign;
	//	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [reportId=");
		builder.append(reportId);
		builder.append(", reportCategory=");
		builder.append(reportCategory);
		builder.append(", name=");
		builder.append(name);
		builder.append(", number=");
		builder.append(number);
		builder.append("]");
		return builder.toString();
	}

}
