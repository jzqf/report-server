package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * The persistent class for the "report_parameter" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "report_parameter", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "report_id", "order_index" },
						name = "uc_report_parameter_order_index") })
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

	/**
	 * The name of the report parameter as defined in the BIRT report.
	 * 
	 * This is the string that must be used to refer to the report parameter
	 * in a URL when requesting a report. It is used to provide one or more
	 * values for the parameter. 
	 */
	@Column(name = "name", nullable = false, length = 32)
	private String name;

	@Column(name = "description", nullable = false, length = 80)
	private String description;

	@Column(name = "required", nullable = false)
	private Boolean required;

	@Column(name = "multivalued", nullable = false)
	private Boolean multivalued;

	@Column(name = "order_index", nullable = false)
	private Integer orderIndex;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_report"),
			columnDefinition = "uuid")
	private Report report;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "parameter_type_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_parametertype"),
			columnDefinition = "uuid")
	private ParameterType parameterType;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "widget_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_widget"),
			columnDefinition = "uuid")
	private Widget widget;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public ReportParameter() {
	}

	public ReportParameter(Report report, String name, String description, ParameterType parameterType,
			Widget widget, Boolean required, Boolean multivalued, Integer orderIndex) {
		this(report, name, description, parameterType, widget, required, multivalued, orderIndex, new Date());
	}

	public ReportParameter(Report report, String name, String description, ParameterType parameterType, Widget widget,
			Boolean required, Boolean multivalued, Integer orderIndex, Date createdOn) {
		this.report = report;
		this.name = name;
		this.description = description;
		this.parameterType = parameterType;
		this.widget = widget;
		this.required = required;
		this.multivalued = multivalued;
		this.orderIndex = orderIndex;
		this.createdOn = createdOn;
	}

	public UUID getReportParameterId() {
		return this.reportParameterId;
	}

	public void setReportParameterId(UUID reportParameterId) {
		this.reportParameterId = reportParameterId;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public Widget getWidget() {
		return widget;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRequired() {
		return this.required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getMultivalued() {
		return multivalued;
	}

	public void setMultivalued(Boolean multivalued) {
		this.multivalued = multivalued;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportParameter [reportParameterId=");
		builder.append(reportParameterId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}