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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

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

	@Column(name = "name", nullable = false, length = 32)
	private String name;

	@Column(name = "description", nullable = false, length = 80)
	private String description;

	@Column(name = "multiple_select", nullable = false)
	private boolean multipleSelect;

	@Column(name = "active", nullable = false)
	private boolean active;

	@OneToMany(targetEntity = ReportParameter.class, mappedBy = "widget")
	private List<ReportParameter> reportParameters;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Widget() {
	}

	public Widget(String description, String name, boolean multipleSelect) {
		this(description, name, multipleSelect, true, new Date());
	}

	public Widget(String description, String name, boolean multipleSelect, boolean active) {
		this(description, name, multipleSelect, active, new Date());
	}

	public Widget(String description, String name, boolean multipleSelect, boolean active, Date createdOn) {
		this.description = description;
		this.name = name;
		this.multipleSelect = multipleSelect;
		this.active = active;
		this.createdOn = createdOn;
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

	public boolean isMultipleSelect() {
		return multipleSelect;
	}

	public void setMultipleSelect(boolean multipleSelect) {
		this.multipleSelect = multipleSelect;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
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
