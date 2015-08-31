package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "widget" database table.
 * 
 * Used to specify the graphical GUI element that will be used to prompt for
 * a value, or for values, for a report parameter.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "widget", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Widget implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "widget_id", unique = true, nullable = false)
	//	private Long widgetId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "widget_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID widgetId;

	@NotBlank
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	@NotBlank
	@Column(name = "description", nullable = false, length = 80)
	private String description;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@OneToMany(targetEntity = ReportParameter.class, mappedBy = "widget")
	private List<ReportParameter> reportParameters;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Widget() {
	}

	public Widget(String description, String name) {
		this(description, name, true, DateUtils.nowUtc());
	}

	public Widget(String description, String name, Boolean active) {
		this(description, name, active, DateUtils.nowUtc());
	}

	public Widget(String description, String name, Boolean active, Date createdOn) {
		this.description = description;
		this.name = name;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getWidgetId() {
		return widgetId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportCategory [widgetId=");
		builder.append(widgetId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}
}
