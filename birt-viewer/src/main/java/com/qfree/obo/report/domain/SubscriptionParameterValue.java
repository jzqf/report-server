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

import com.qfree.obo.report.dto.SubscriptionParameterValueResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "subscription_parameter_value" database table.
 * 
 * Instances/rows represent values or templates for how to generate
 * JobParameterValue's for a Subscription when the Subscription's report is run
 * and a Job is created. There can be only a single SubscriptionParameterValue
 * for single-valued related ReportParameter for a Subscription. There can be
 * multiple SubscriptionParameterValue's for multi-valued related
 * ReportParameter for a Subscription.
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
	private UUID subscriptionParameterValueId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "subscription_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscriptionparametervalue_subscriptionparameter") ,
			columnDefinition = "uuid")
	private SubscriptionParameter subscriptionParameter;

	/**
	 * The parameter value to use, if its data type is "Boolean".
	 */
	@Column(name = "boolean_value", nullable = true)
	private Boolean booleanValue;

	/**
	 * The parameter value to use, if its data type is "Date" <i>and</i> a fixed
	 * date value is specified for the parameter.
	 * 
	 * This will not be used if the various SubscriptionParameterValue date
	 * offset fields are used to generate a dynamic date value that depends on
	 * when the report is run.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "date_value", nullable = true)
	private Date dateValue;

	/**
	 * The parameter value to use, if its data type is "Datetime" <i>and</i> a
	 * fixed datetime value is specified for the parameter.
	 * 
	 * This will not be used if the various SubscriptionParameterValue date
	 * offset fields are used to generate a dynamic datetime value that depends
	 * on when the report is run.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime_value", nullable = true)
	private Date datetimeValue;

	/**
	 * The parameter value to use, if its data type is "Float".
	 */
	/*
	 * TODO Can this also be used for report parameters of type "Decimal"?
	 */
	@Column(name = "float_value", nullable = true)
	private Double floatValue;

	/**
	 * The parameter value to use, if its data type is "Integer".
	 */
	@Column(name = "integer_value", nullable = true)
	private Integer integerValue;

	/**
	 * The parameter value to use, if its data type is "String".
	 */
	@Column(name = "string_value", nullable = true, length = 80)
	private String stringValue;

	/**
	 * The parameter value to use, if its data type is "Time".
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "time_value", nullable = true)
	private Date timeValue;

	/**
	 * 4-digit year for generating a dynamic date or datetime value.
	 */
	@Column(name = "year_number", nullable = true)
	private Integer yearNumber;

	/**
	 * Offset for generating a dynamic date or datetime value:
	 * 
	 * <p>
	 * +n: means "n" years in the past <br>
	 * 0: means this year
	 */
	@Column(name = "years_ago", nullable = true)
	private Integer yearsAgo;

	/**
	 * 2-digit month for generating a dynamic date or datetime value.
	 * 
	 * Used with {@link java.time.Month} enum.
	 */
	@Column(name = "month_number", nullable = true)
	private Integer monthNumber;

	/**
	 * Offset for generating a dynamic date or datetime value:
	 * 
	 * <p>
	 * +n: means "n" months in the past <br>
	 * 0: means this month
	 */
	@Column(name = "months_ago", nullable = true)
	private Integer monthsAgo;

	// @Column(name = "week_of_month_number", nullable = true)
	// private Integer weekOfMonthNumber;
	//
	// @Column(name = "week_of_year_number", nullable = true)
	// private Integer weekOfYearNumber;

	/**
	 * Offset for generating a dynamic date or datetime value:
	 * 
	 * <p>
	 * +n: means "n" weeks in the past <br>
	 * 0: means this week
	 */
	@Column(name = "weeks_ago", nullable = true)
	private Integer weeksAgo;

	/**
	 * Used to specify the N<sup>th</sup> occurrence of a day-of-week in a
	 * month.
	 * 
	 * <p>
	 * The day-of-week that this applies to is specified by
	 * {@link #dayOfWeekInMonthNumber}.
	 * 
	 * <p>
	 * Possible values:
	 * 
	 * <dl>
	 * <dt>...</dt>
	 * <dd>&nbsp;</dd>
	 * <dt>-2:</dt>
	 * <dd>2<sup>nd</sup> to last occurrence of the day-of-week in the current
	 * month</dd>
	 * <dt>-1:</dt>
	 * <dd>Last occurrence of the day-of-week in the current month</dd>
	 * </dl>
	 * <dt>0:</dt>
	 * <dd>Last occurrence of the day-of-week in the <i>previous</i> month</dd>
	 * </dl>
	 * <dt>1:</dt>
	 * <dd>First occurrence of the day-of-week in the current month</dd>
	 * <dt>2:</dt>
	 * <dd>Second occurrence of the day-of-week in the current month</dd>
	 * <dt>...</dt>
	 * <dd></dd>
	 * </dl>
	 * 
	 * <p>
	 * The presence of "_in_month" in the field name refers to the behaviour
	 * that after adjustment the date will be in the same month as before the
	 * adjustment, unless this ordinal value is sufficiently large in absolute
	 * value that it forces the date to be shifted to an earlier or later month.
	 */
	@Column(name = "day_of_week_in_month_ordinal", nullable = true)
	private Integer dayOfWeekInMonthOrdinal;

	/**
	 * The day-of-week to use with {@link #dayOfWeekInMonthOrdinal}.
	 * 
	 * <p>
	 * Possible values:
	 * 
	 * <dl>
	 * <dt>1:</dt>
	 * <dd>Monday</dd>
	 * <dt>2:</dt>
	 * <dd>Tuesday</dd>
	 * <dt>3:</dt>
	 * <dd>Wednesday</dd>
	 * <dt>4:</dt>
	 * <dd>Thursday</dd>
	 * <dt>5:</dt>
	 * <dd>Friday</dd>
	 * <dt>6:</dt>
	 * <dd>Saturday</dd>
	 * <dt>7:</dt>
	 * <dd>Sunday</dd>
	 * </dl>
	 * 
	 * <p>
	 * The presence of "_in_month" in the field name refers to the behaviour
	 * that after adjustment the date will be in the same month as before the
	 * adjustment, unless this ordinal value is sufficiently large in absolute
	 * value that it forces the date to be shifted to an earlier or later month.
	 */
	@Column(name = "day_of_week_in_month_number", nullable = true)
	private Integer dayOfWeekInMonthNumber;

	/**
	 * Use to shift the day of week within the current Monday-to-Sunday week,
	 * even if it causes the month to change.
	 */
	/*
	 * Use: $date.with(ChronoField.DAY_OF_WEEK, dayOfWeekNumber) ?
	 */
	@Column(name = "day_of_week_number", nullable = true)
	private Integer dayOfWeekNumber;

	/*
	 * Implement with: LocalDate.withDayOfMonth(dayOfMonthNumber) *if* 
	 * dayOfMonthNumber > 0.
	 * 
	 * If dayOfMonthNumber < 0:
	 * 
	 *   -1: last day of the month
	 *   -2: 2nd to last day of the month
	 *   ...
	 */
	@Column(name = "day_of_month_number", nullable = true)
	private Integer dayOfMonthNumber;

	@Column(name = "days_ago", nullable = true)
	private Integer daysAgo;

	/**
	 * The number of years to shift a date or datetime forward after all other
	 * date adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_years", nullable = true)
	private Integer durationToAddYears;

	/**
	 * The number of months to shift a date or datetime forward after all other
	 * date adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_months", nullable = true)
	private Integer durationToAddMonths;

	/**
	 * The number of weeks (7-day periods) to shift a date or datetime forward
	 * after all other date adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_weeks", nullable = true)
	private Integer durationToAddWeeks;

	/**
	 * The number of days to shift a date or datetime forward after all other
	 * date adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_days", nullable = true)
	private Integer durationToAddDays;

	/**
	 * The number of hours to shift a datetime forward after all other date
	 * adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_hours", nullable = true)
	private Integer durationToAddHours;

	/**
	 * The number of minutes to shift a datetime forward after all other date
	 * adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_minutes", nullable = true)
	private Integer durationToAddMinutes;

	/**
	 * The number of seconds to shift a datetime forward after all other date
	 * adjustments have been applied.
	 * 
	 * This can be used together with the other "duration_to_add" fields to
	 * ensure that a "to" date is exactly a specified time period after a "from"
	 * date.
	 */
	@Column(name = "duration_to_add_seconds", nullable = true)
	private Integer durationToAddSeconds;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public SubscriptionParameterValue() {
	}

	public SubscriptionParameterValue(SubscriptionParameter subscriptionParameter) {
		this(
				null,
				subscriptionParameter,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				DateUtils.nowUtc());
	}

	public SubscriptionParameterValue(
			SubscriptionParameter subscriptionParameter,
			Boolean booleanValue,
			Date dateValue,
			Date datetimeValue,
			Double floatValue,
			Integer integerValue,
			String stringValue,
			Date timeValue,
			Integer yearNumber,
			Integer yearsAgo,
			Integer monthNumber,
			Integer monthsAgo,
			Integer weeksAgo,
			Integer dayOfWeekInMonthOrdinal,
			Integer dayOfWeekInMonthNumber,
			Integer dayOfWeekNumber,
			Integer dayOfMonthNumber,
			Integer daysAgo,
			Integer durationToAddYears,
			Integer durationToAddMonths,
			Integer durationToAddWeeks,
			Integer durationToAddDays,
			Integer durationToAddHours,
			Integer durationToAddMinutes,
			Integer durationToAddSeconds) {
		this(
				null,
				subscriptionParameter,
				booleanValue,
				dateValue,
				datetimeValue,
				floatValue,
				integerValue,
				stringValue,
				timeValue,
				yearNumber,
				yearsAgo,
				monthNumber,
				monthsAgo,
				weeksAgo,
				dayOfWeekInMonthOrdinal,
				dayOfWeekInMonthNumber,
				dayOfWeekNumber,
				dayOfMonthNumber,
				daysAgo,
				durationToAddYears,
				durationToAddMonths,
				durationToAddWeeks,
				durationToAddDays,
				durationToAddHours,
				durationToAddMinutes,
				durationToAddSeconds,
				DateUtils.nowUtc());
	}

	public SubscriptionParameterValue(
			SubscriptionParameter subscriptionParameter,
			RoleParameterValue roleParameterValue) {
		this(
				null,
				subscriptionParameter,
				roleParameterValue.getBooleanValue(),
				roleParameterValue.getDateValue(),
				roleParameterValue.getDatetimeValue(),
				roleParameterValue.getFloatValue(),
				roleParameterValue.getIntegerValue(),
				roleParameterValue.getStringValue(),
				roleParameterValue.getTimeValue(),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				DateUtils.nowUtc());
	}

	public SubscriptionParameterValue(
			SubscriptionParameter subscriptionParameter,
			SubscriptionParameterValueResource subscriptionParameterValueResource) {
		this(
				null,
				subscriptionParameter,
				subscriptionParameterValueResource.getBooleanValue(),
				subscriptionParameterValueResource.getDateValue(),
				subscriptionParameterValueResource.getDatetimeValue(),
				subscriptionParameterValueResource.getFloatValue(),
				subscriptionParameterValueResource.getIntegerValue(),
				subscriptionParameterValueResource.getStringValue(),
				subscriptionParameterValueResource.getTimeValue(),
				subscriptionParameterValueResource.getYearNumber(),
				subscriptionParameterValueResource.getYearsAgo(),
				subscriptionParameterValueResource.getMonthNumber(),
				subscriptionParameterValueResource.getMonthsAgo(),
				subscriptionParameterValueResource.getWeeksAgo(),
				subscriptionParameterValueResource.getDayOfWeekInMonthOrdinal(),
				subscriptionParameterValueResource.getDayOfWeekInMonthNumber(),
				subscriptionParameterValueResource.getDayOfWeekNumber(),
				subscriptionParameterValueResource.getDayOfMonthNumber(),
				subscriptionParameterValueResource.getDaysAgo(),
				subscriptionParameterValueResource.getDurationToAddYears(),
				subscriptionParameterValueResource.getDurationToAddMonths(),
				subscriptionParameterValueResource.getDurationToAddWeeks(),
				subscriptionParameterValueResource.getDurationToAddDays(),
				subscriptionParameterValueResource.getDurationToAddHours(),
				subscriptionParameterValueResource.getDurationToAddMinutes(),
				subscriptionParameterValueResource.getDurationToAddSeconds(),
				subscriptionParameterValueResource.getCreatedOn());
	}

	public SubscriptionParameterValue(
			UUID subscriptionParameterValueId,
			SubscriptionParameter subscriptionParameter,
			Boolean booleanValue,
			Date dateValue,
			Date datetimeValue,
			Double floatValue,
			Integer integerValue,
			String stringValue,
			Date timeValue,
			Integer yearNumber,
			Integer yearsAgo,
			Integer monthNumber,
			Integer monthsAgo,
			Integer weeksAgo,
			Integer dayOfWeekInMonthOrdinal,
			Integer dayOfWeekInMonthNumber,
			Integer dayOfWeekNumber,
			Integer dayOfMonthNumber,
			Integer daysAgo,
			Integer durationToAddYears,
			Integer durationToAddMonths,
			Integer durationToAddWeeks,
			Integer durationToAddDays,
			Integer durationToAddHours,
			Integer durationToAddMinutes,
			Integer durationToAddSeconds,
			Date createdOn) {
		super();
		this.subscriptionParameterValueId = subscriptionParameterValueId;
		this.subscriptionParameter = subscriptionParameter;
		this.booleanValue = booleanValue;
		this.dateValue = dateValue;
		this.datetimeValue = datetimeValue;
		this.floatValue = floatValue;
		this.integerValue = integerValue;
		this.stringValue = stringValue;
		this.timeValue = timeValue;
		this.yearNumber = yearNumber;
		this.yearsAgo = yearsAgo;
		this.monthNumber = monthNumber;
		this.monthsAgo = monthsAgo;
		this.weeksAgo = weeksAgo;
		this.dayOfWeekInMonthOrdinal = dayOfWeekInMonthOrdinal;
		this.dayOfWeekInMonthNumber = dayOfWeekInMonthNumber;
		this.dayOfWeekNumber = dayOfWeekNumber;
		this.dayOfMonthNumber = dayOfMonthNumber;
		this.daysAgo = daysAgo;
		this.durationToAddYears = durationToAddYears;
		this.durationToAddMonths = durationToAddMonths;
		this.durationToAddWeeks = durationToAddWeeks;
		this.durationToAddDays = durationToAddDays;
		this.durationToAddHours = durationToAddHours;
		this.durationToAddMinutes = durationToAddMinutes;
		this.durationToAddSeconds = durationToAddSeconds;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public SubscriptionParameter getSubscriptionParameter() {
		return subscriptionParameter;
	}

	public void setSubscriptionParameter(SubscriptionParameter subscriptionParameter) {
		this.subscriptionParameter = subscriptionParameter;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getDatetimeValue() {
		return datetimeValue;
	}

	public void setDatetimeValue(Date datetimeValue) {
		this.datetimeValue = datetimeValue;
	}

	public Double getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public Integer getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(Integer yearNumber) {
		this.yearNumber = yearNumber;
	}

	public Integer getYearsAgo() {
		return yearsAgo;
	}

	public void setYearsAgo(Integer yearsAgo) {
		this.yearsAgo = yearsAgo;
	}

	public Integer getMonthNumber() {
		return monthNumber;
	}

	public void setMonthNumber(Integer monthNumber) {
		this.monthNumber = monthNumber;
	}

	public Integer getMonthsAgo() {
		return monthsAgo;
	}

	public void setMonthsAgo(Integer monthsAgo) {
		this.monthsAgo = monthsAgo;
	}

	public Integer getWeeksAgo() {
		return weeksAgo;
	}

	public void setWeeksAgo(Integer weeksAgo) {
		this.weeksAgo = weeksAgo;
	}

	public Integer getDayOfWeekInMonthOrdinal() {
		return dayOfWeekInMonthOrdinal;
	}

	public void setDayOfWeekInMonthOrdinal(Integer dayOfWeekInMonthOrdinal) {
		this.dayOfWeekInMonthOrdinal = dayOfWeekInMonthOrdinal;
	}

	public Integer getDayOfWeekInMonthNumber() {
		return dayOfWeekInMonthNumber;
	}

	public void setDayOfWeekInMonthNumber(Integer dayOfWeekInMonthNumber) {
		this.dayOfWeekInMonthNumber = dayOfWeekInMonthNumber;
	}

	public Integer getDayOfWeekNumber() {
		return dayOfWeekNumber;
	}

	public void setDayOfWeekNumber(Integer dayOfWeekNumber) {
		this.dayOfWeekNumber = dayOfWeekNumber;
	}

	public Integer getDayOfMonthNumber() {
		return dayOfMonthNumber;
	}

	public void setDayOfMonthNumber(Integer dayOfMonthNumber) {
		this.dayOfMonthNumber = dayOfMonthNumber;
	}

	public Integer getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(Integer daysAgo) {
		this.daysAgo = daysAgo;
	}

	public Integer getDurationToAddYears() {
		return durationToAddYears;
	}

	public void setDurationToAddYears(Integer durationToAddYears) {
		this.durationToAddYears = durationToAddYears;
	}

	public Integer getDurationToAddMonths() {
		return durationToAddMonths;
	}

	public void setDurationToAddMonths(Integer durationToAddMonths) {
		this.durationToAddMonths = durationToAddMonths;
	}

	public Integer getDurationToAddWeeks() {
		return durationToAddWeeks;
	}

	public void setDurationToAddWeeks(Integer durationToAddWeeks) {
		this.durationToAddWeeks = durationToAddWeeks;
	}

	public Integer getDurationToAddDays() {
		return durationToAddDays;
	}

	public void setDurationToAddDays(Integer durationToAddDays) {
		this.durationToAddDays = durationToAddDays;
	}

	public Integer getDurationToAddHours() {
		return durationToAddHours;
	}

	public void setDurationToAddHours(Integer durationToAddHours) {
		this.durationToAddHours = durationToAddHours;
	}

	public Integer getDurationToAddMinutes() {
		return durationToAddMinutes;
	}

	public void setDurationToAddMinutes(Integer durationToAddMinutes) {
		this.durationToAddMinutes = durationToAddMinutes;
	}

	public Integer getDurationToAddSeconds() {
		return durationToAddSeconds;
	}

	public void setDurationToAddSeconds(Integer durationToAddSeconds) {
		this.durationToAddSeconds = durationToAddSeconds;
	}

	public UUID getSubscriptionParameterValueId() {
		return subscriptionParameterValueId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionParameterValue [subscriptionParameterValueId=");
		builder.append(subscriptionParameterValueId);
		builder.append(", subscriptionParameterId=");
		builder.append(subscriptionParameter.getSubscriptionParameterId());
		builder.append(", booleanValue=");
		builder.append(booleanValue);
		builder.append(", dateValue=");
		builder.append(dateValue);
		builder.append(", datetimeValue=");
		builder.append(datetimeValue);
		builder.append(", floatValue=");
		builder.append(floatValue);
		builder.append(", integerValue=");
		builder.append(integerValue);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append(", yearNumber=");
		builder.append(yearNumber);
		builder.append(", yearsAgo=");
		builder.append(yearsAgo);
		builder.append(", monthNumber=");
		builder.append(monthNumber);
		builder.append(", monthsAgo=");
		builder.append(monthsAgo);
		builder.append(", weeksAgo=");
		builder.append(weeksAgo);
		builder.append(", dayOfWeekInMonthOrdinal=");
		builder.append(dayOfWeekInMonthOrdinal);
		builder.append(", dayOfWeekInMonthNumber=");
		builder.append(dayOfWeekInMonthNumber);
		builder.append(", dayOfWeekNumber=");
		builder.append(dayOfWeekNumber);
		builder.append(", dayOfMonthNumber=");
		builder.append(dayOfMonthNumber);
		builder.append(", daysAgo=");
		builder.append(daysAgo);
		builder.append(", durationToAddYears=");
		builder.append(durationToAddYears);
		builder.append(", durationToAddMonths=");
		builder.append(durationToAddMonths);
		builder.append(", durationToAddWeeks=");
		builder.append(durationToAddWeeks);
		builder.append(", durationToAddDays=");
		builder.append(durationToAddDays);
		builder.append(", durationToAddHours=");
		builder.append(durationToAddHours);
		builder.append(", durationToAddMinutes=");
		builder.append(durationToAddMinutes);
		builder.append(", durationToAddSeconds=");
		builder.append(durationToAddSeconds);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}