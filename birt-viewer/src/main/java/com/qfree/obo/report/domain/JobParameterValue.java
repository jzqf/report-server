package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "job_parameter_value" database table.
 * 
 * Instances/rows represent last used values by a specified Job for a specified
 * ReportParameter. Multivalued ReportParameter's can have multiple 
 * JobParameterValue's for a single Job. Single-valued ReportParameter's may 
 * only have a single JobParameterValue for a single Job.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "job_parameter_value", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class JobParameterValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_parameter_value_id", unique = true, nullable = false)
	private Long jobParameterValueId;
	//	@NotNull
	//	@Type(type = "uuid-custom")
	//	//	@Type(type = "pg-uuid")
	//	@GeneratedValue(generator = "uuid2")
	//	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	//	@Column(name = "job_parameter_value_id", unique = true, nullable = false,
	//			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	//	private UUID jobParameterValueId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "job_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_jobparametervalue_jobparameter") )
	//			columnDefinition = "uuid")
	private JobParameter jobParameter;

	/**
	 * Value used for a Job for a report parameters of data type = Boolean.
	 */
	@Column(name = "boolean_value", nullable = true)
	private Boolean booleanValue;

	/**
	 * Value used for a Job for a report parameters of data type = Date.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "date_value", nullable = true)
	private Date dateValue;

	/**
	 * Value used for a Job for a report parameters of data type = Datetime.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime_value", nullable = true)
	private Date datetimeValue;

	/**
	 * Value used for a Job for a report parameters of data type = Float.
	 */
	/*
	 * TODO Can this also be used for report parameters of type "Decimal"?
	 */
	@Column(name = "float_value", nullable = true)
	private Double floatValue;

	/**
	 * Value used for a Job for a report parameters of data type = Integer.
	 */
	@Column(name = "integer_value", nullable = true)
	private Integer integerValue;

	/**
	 * Value used for a Job for a report parameters of data type = String.
	 * 
	 * TODO Store string version of other data types in this field? This will
	 * allow us to always examine a value that was used by looking at this
	 * field.
	 * 
	 * The value is stored as text, regardless of its native data type.
	 */
	//@NotBlank
	@Column(name = "string_value", nullable = true, length = 80)
	private String stringValue;

	/**
	 * Value used for a Job for a report parameters of data type = Time.
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "time_value", nullable = true)
	private Date timeValue;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public JobParameterValue() {
	}

	public JobParameterValue(
			JobParameter jobParameter,
			Boolean booleanValue,
			Date dateValue,
			Date datetimeValue,
			Double floatValue,
			Integer integerValue,
			String stringValue,
			Date timeValue) {
		this(
				jobParameter,
				booleanValue,
				dateValue,
				datetimeValue,
				floatValue,
				integerValue,
				stringValue,
				timeValue,
				DateUtils.nowUtc());
	}

	public JobParameterValue(
			JobParameter jobParameter,
			Boolean booleanValue,
			Date dateValue,
			Date datetimeValue,
			Double floatValue,
			Integer integerValue,
			String stringValue,
			Date timeValue,
			Date createdOn) {
		/*
		 * TODO Check that only one *Value parameter is not null?
		 * TODO Set stringValue based on the parameter that is not-null (if that parameter is not itself stringValue)?
		 */
		this.jobParameter = jobParameter;
		this.booleanValue = booleanValue;
		this.dateValue = dateValue;
		this.datetimeValue = datetimeValue;
		this.floatValue = floatValue;
		this.integerValue = integerValue;
		this.stringValue = stringValue;
		this.timeValue = timeValue;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public JobParameter getJobParameter() {
		return jobParameter;
	}

	public void setJobParameter(JobParameter jobParameter) {
		this.jobParameter = jobParameter;
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

	public Long getJobParameterValueId() {
		return jobParameterValueId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobParameterValue [jobParameterValueId=");
		builder.append(jobParameterValueId);
		builder.append(", jobParameter=");
		builder.append(jobParameter);
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
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}