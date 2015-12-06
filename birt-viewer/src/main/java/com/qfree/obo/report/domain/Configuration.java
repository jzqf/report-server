package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.dto.ConfigurationResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "configuration" database table.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
/*
 * This unique constrain allows multiple rows with the same value for 
 * param_name if role_id=null. This is not want I want. I will need to enforce
 * the unique constraint for role_id=null through careful coding.
 */
@Table(name = "configuration", schema = "reporting",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "param_name", "role_id" },
						name = "uc_configuration_paramname_role") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Configuration implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * These enum elements are stored in the report server database as strings.
	 * Therefore, they should never be changed or deleted.
	 */
	public enum ParamName {
		/*
		 * For unit tests:
		 */
		TEST_BOOLEAN(ParamType.BOOLEAN),
		TEST_BYTEARRAY(ParamType.BYTEARRAY),
		TEST_DATE(ParamType.DATE),
		TEST_DATETIME(ParamType.DATETIME),
		TEST_DOUBLE(ParamType.DOUBLE),
		TEST_FLOAT(ParamType.FLOAT),
		TEST_INTEGER(ParamType.INTEGER),
		TEST_LONG(ParamType.LONG),
		TEST_STRING(ParamType.STRING),
		TEST_TEXT(ParamType.TEXT),
		TEST_TIME(ParamType.TIME),
		/* 
		 * No [configuration] record is created for this parameter in the test
		 * data created by .../test-data.sql. This allows it to be used for unit
		 * tests related to configuration parameters that have not been set.
		 */
		TEST_NOTSET(ParamType.STRING),

		/*
		 * Holds the report server database version. This is used when upgrading
		 * the database to ensure that the appropriate upgrade scripts are run.
		 */
		DB_VERSION(ParamType.INTEGER),

		/*
		 * Holds the URL to which external authentication requests should be
		 * sent.
		 */
		AUTHENTICATION_PROVIDER_URL(ParamType.STRING);
	
		private ParamType paramType;
	
		private ParamName(ParamType paramType) {
			this.paramType = paramType;
		}
	
		public ParamType paramType() {
			return paramType;
		}
	}

	/**
	 * These enum elements are stored in the report server database as strings.
	 * Therefore, they should never be changed or deleted.
	 */
	public enum ParamType {
		BOOLEAN,
		BYTEARRAY,
		DATE,
		DATETIME,
		DOUBLE,
		FLOAT,
		INTEGER,
		LONG,
		STRING,
		TEXT,
		TIME
	}

	@Id
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "configuration_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID configurationId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	/**
	 * Default parameter values correspond to roll == null.
	 */
	@JoinColumn(name = "role_id", nullable = true,
			foreignKey = @ForeignKey(name = "fk_configuration_role"),
			columnDefinition = "uuid")
	private Role role;

	@NotNull
	@Column(name = "param_name", nullable = false, length = 64)
	@Enumerated(EnumType.STRING)
	private ParamName paramName;

	@NotNull
	@Column(name = "param_type", nullable = false, length = 16)
	@Enumerated(EnumType.STRING)
	private ParamType paramType;

	@Column(name = "boolean_value", nullable = true)
	private Boolean booleanValue;

	@Column(name = "bytea_value", nullable = true)
	private byte[] byteaValue;

	@Temporal(TemporalType.DATE)
	@Column(name = "date_value", nullable = true)
	private Date dateValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "datetime_value", nullable = true)
	private Date datetimeValue;

	@Column(name = "double_value", nullable = true)
	private Double doubleValue;

	@Column(name = "float_value", nullable = true)
	private Float floatValue;

	@Column(name = "integer_value", nullable = true)
	private Integer integerValue;

	@Column(name = "long_value", nullable = true)
	private Long longValue;

	@Column(name = "string_value", nullable = true, length = 1000)
	private String stringValue;

	@Column(name = "text_value", nullable = true, columnDefinition = "text")
	private String textValue;

	@Temporal(TemporalType.TIME)
	@Column(name = "time_value", nullable = true)
	private Date timeValue;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	public Configuration() {
		this(null, null, null, DateUtils.nowUtc());
	}

	public Configuration(ParamName paramName) {
		this(paramName, null, paramName.paramType(), DateUtils.nowUtc());
	}

	public Configuration(ParamName paramName, Role role) {
		this(paramName, role, paramName.paramType(), DateUtils.nowUtc());
	}

	public Configuration(ParamName paramName, Role role, ParamType paramType, Date createdOn) {
		this(
				null,
				role,
				paramName,
				paramType,
				null, null, null, null, null, null, null, null, null, null, null,
				(createdOn != null) ? createdOn : DateUtils.nowUtc());
	}

	public Configuration(ConfigurationResource configurationResource, Role role) {
		this(
				configurationResource.getConfigurationId(),
				role,
				configurationResource.getParamName(),
				configurationResource.getParamType(),
				configurationResource.getBooleanValue(),
				configurationResource.getByteaValue(),
				configurationResource.getDateValue(),
				configurationResource.getDatetimeValue(),
				configurationResource.getDoubleValue(),
				configurationResource.getFloatValue(),
				configurationResource.getIntegerValue(),
				configurationResource.getLongValue(),
				configurationResource.getStringValue(),
				configurationResource.getTextValue(),
				configurationResource.getTimeValue(),
				configurationResource.getCreatedOn());
	}

	public Configuration(
			UUID configurationId,
			Role role,
			ParamName paramName,
			ParamType paramType,
			Boolean booleanValue,
			byte[] byteaValue,
			Date dateValue,
			Date datetimeValue,
			Double doubleValue,
			Float floatValue,
			Integer integerValue,
			Long longValue,
			String stringValue,
			String textValue,
			Date timeValue,
			Date createdOn) {
		super();
		this.configurationId = configurationId;
		this.role = role;
		this.paramName = paramName;
		this.paramType = paramType;
		this.booleanValue = booleanValue;
		this.byteaValue = byteaValue;
		this.dateValue = dateValue;
		this.datetimeValue = datetimeValue;
		this.doubleValue = doubleValue;
		this.floatValue = floatValue;
		this.integerValue = integerValue;
		this.longValue = longValue;
		this.stringValue = stringValue;
		this.textValue = textValue;
		this.timeValue = timeValue;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getConfigurationId() {
		return this.configurationId;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ParamName getParamName() {
		return paramName;
	}

	public void setParamName(ParamName paramName) {
		this.paramName = paramName;
	}

	public ParamType getParamType() {
		return paramType;
	}

	public void setParamType(ParamType paramType) {
		this.paramType = paramType;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public byte[] getByteaValue() {
		return byteaValue;
	}

	public void setByteaValue(byte[] byteaValue) {
		this.byteaValue = byteaValue;
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

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public Float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Configuration [configurationId=");
		builder.append(configurationId);
		builder.append(", role=");
		builder.append(role);
		builder.append(", paramName=");
		builder.append(paramName);
		builder.append(", paramType=");
		builder.append(paramType);
		builder.append(", booleanValue=");
		builder.append(booleanValue);
		builder.append(", byteaValue=");
		builder.append("<" + ((byteaValue != null) ? byteaValue.length : 0) + " bytes>");
		builder.append(", dateValue=");
		builder.append(dateValue);
		builder.append(", datetimeValue=");
		builder.append(datetimeValue);
		builder.append(", doubleValue=");
		builder.append(doubleValue);
		builder.append(", floatValue=");
		builder.append(floatValue);
		builder.append(", integerValue=");
		builder.append(integerValue);
		builder.append(", longValue=");
		builder.append(longValue);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append(", textValue=");
		builder.append(textValue);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}