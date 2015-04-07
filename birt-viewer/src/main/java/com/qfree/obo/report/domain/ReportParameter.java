package com.qfree.obo.report.domain;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
				//				@UniqueConstraint(columnNames = { "report_id", "order_index" },
				@UniqueConstraint(columnNames = { "report_version_id", "order_index" },
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

	/**
	 * If this is false, a GUI should display a checkbox labeled "Is null" (or 
	 * something similar) next to the input widget for the parameter. If this
	 * is checked, the widget for entering a value should be disabled to 
	 * indicate that no value can be entered.
	 */
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
	@JoinColumn(name = "report_version_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_report"),
			columnDefinition = "uuid")
	private ReportVersion reportVersion;
	//	@JoinColumn(name = "report_id", nullable = false,
	//			foreignKey = @ForeignKey(name = "fk_reportparameter_report"),
	//			columnDefinition = "uuid")
	//	private Report report;

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

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     RoleParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<RoleParameterValue> roleParameterValues;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     SubscriptionParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<SubscriptionParameterValue> subscriptionParameterValues;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a ReportParameter will delete all of its 
	 *     JobParameterValue's.
	 */
	@OneToMany(mappedBy = "reportParameter", cascade = CascadeType.ALL)
	private List<JobParameterValue> jobParameterValues;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public ReportParameter() {
	}

	public ReportParameter(ReportVersion reportVersion, String name, String description, ParameterType parameterType,
			Widget widget, Boolean required, Boolean multivalued, Integer orderIndex) {
		this(reportVersion, name, description, parameterType, widget, required, multivalued, orderIndex, new Date());
	}

	public ReportParameter(ReportVersion reportVersion, String name, String description, ParameterType parameterType,
			Widget widget,
			Boolean required, Boolean multivalued, Integer orderIndex, Date createdOn) {
		this.reportVersion = reportVersion;
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

	public ReportVersion getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(ReportVersion reportVersion) {
		this.reportVersion = reportVersion;
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

	public List<RoleParameterValue> getRoleParameterValues() {
		return roleParameterValues;
	}

	public void setRoleParameterValues(List<RoleParameterValue> roleParameterValuess) {
		this.roleParameterValues = roleParameterValuess;
	}

	public List<SubscriptionParameterValue> getSubscriptionParameterValues() {
		return subscriptionParameterValues;
	}

	public void setSubscriptionParameterValues(List<SubscriptionParameterValue> subscriptionParameterValues) {
		this.subscriptionParameterValues = subscriptionParameterValues;
	}

	public List<JobParameterValue> getJobParameterValues() {
		return jobParameterValues;
	}

	public void setJobParameterValues(List<JobParameterValue> jobParameterValues) {
		this.jobParameterValues = jobParameterValues;
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
		return required;
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
		builder.append("ReportParameter [reportVersion=");
		builder.append(reportVersion);
		builder.append(", name=");
		builder.append(name);
		builder.append(", required=");
		builder.append(required);
		builder.append(", multivalued=");
		builder.append(multivalued);
		builder.append(", orderIndex=");
		builder.append(orderIndex);
		builder.append("]");
		return builder.toString();
	}

}