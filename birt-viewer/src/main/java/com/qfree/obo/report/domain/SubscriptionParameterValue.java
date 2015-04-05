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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * The persistent class for the "subscription_parameter_value" database table.
 * 
 * Instances/rows represent templates for how to generate JobParameterValue's
 * for a Subscription when the Subscription's report is run. There can be only a
 * single SubscriptionParameterValue for single-valued related ReportParameter 
 * for a Subscription. There can be multiple SubscriptionParameterValue's for 
 * multi-valued related ReportParameter for a Subscription.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "subscription_parameter_value", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class SubscriptionParameterValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "subscription_parameter_value_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID subscriptionParameterValue;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "subscription_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_reportparameter_subscription"),
			columnDefinition = "uuid")
	private Subscription subscription;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscriptionparametervalue_reportparameter"),
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	@Column(name = "string_value", nullable = true, length = 80)
	private String stringValue;

	@Temporal(TemporalType.TIME)
	@Column(name = "time_value", nullable = true)
	private Date timeValue;

	@Column(name = "year_number", nullable = true)
	private Integer yearNumber;

	@Column(name = "years_relative", nullable = true)
	private Integer yearsRelative;

	@Column(name = "month_number", nullable = true)
	private Integer monthNumber;

	@Column(name = "months_relative", nullable = true)
	private Integer monthsRelative;

	@Column(name = "week_of_month_number", nullable = true)
	private Integer weekOfMonthNumber;

	@Column(name = "week_of_year_number", nullable = true)
	private Integer weekOfYearNumber;

	@Column(name = "weeks_relative", nullable = true)
	private Integer weeksRelative;

	@Column(name = "day_of_week_number", nullable = true)
	private Integer dayOfWeekNumber;

	@Column(name = "day_of_month_number", nullable = true)
	private Integer dayOfMonthNumber;

	@Column(name = "days_relative", nullable = true)
	private Integer daysRelative;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public SubscriptionParameterValue() {
	}

	public SubscriptionParameterValue(Subscription subscription, ReportParameter reportParameter, String stringValue) {
		this(subscription, reportParameter, stringValue, new Date());
	}

	public SubscriptionParameterValue(Subscription subscription, ReportParameter reportParameter, String stringValue,
			Date createdOn) {
		this.subscription = subscription;
		this.reportParameter = reportParameter;
		this.stringValue = stringValue;
		this.createdOn = createdOn;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public UUID getSubscriptionParameterValue() {
		return this.subscriptionParameterValue;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String description) {
		this.stringValue = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionParameterValue [subscription=");
		builder.append(subscription);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append("]");
		return builder.toString();
	}

}