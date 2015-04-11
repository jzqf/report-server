package com.qfree.obo.report.configuration;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ConfigurationRepository;
import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Role;

public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	@Autowired
	ConfigurationRepository configurationRepository;

	/**
	 * These enum elements are stored in the report server database as
	 * strings. They are used by the {@link Configuration} entity class. 
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
		TEST_NOTSET(ParamType.STRING);

		private ParamType paramType;

		private ParamName(ParamType paramType) {
			this.paramType = paramType;
		}

		public ParamType paramType() {
			return paramType;
		}
	}

	/**
	 * These enum elements are stored in the report server database as
	 * strings. They are used by the {@link Configuration} entity class. 
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

	public Object get(ParamName paramName) {
		return get(paramName, null);
	}

	public Object get(ParamName paramName, Role role) {
		/*
		 * First look for role-specific value if a Role is specified.
		 */
		Configuration configuration = null;
		Object object = null;
		if (role != null) {
			configuration = configurationRepository.findByParamName(paramName, role);
		}

		/*
		 * If no role-specific Configuration has been found for the specified 
		 * ParamName...
		 */
		if (configuration == null) {
			/* 
			 * ...Look for a global (role-independent) default Configuration.
			 * 
			 * Do *not* call findByParamName(paramName, role) here with 
			 * role=null. This will not work set comments where this query is
			 * defined in ConfigurationRepository.
			 */
			configuration = configurationRepository.findByParamName(paramName);
		}

		if (configuration != null) {
			/*
			 * Retrieve the parameter value using the appropriate getter based
			 * on the data type of the value as recorded in the Configuration
			 * entity. 
			 */
			switch (configuration.getParamType()) {
			case BOOLEAN:
				object = configuration.getBooleanValue();
				break;
			case BYTEARRAY:
				object = configuration.getByteaValue();
				break;
			case DATE:
				object = configuration.getDateValue();
				break;
			case DATETIME:
				object = configuration.getDatetimeValue();
				break;
			case DOUBLE:
				object = configuration.getDoubleValue();
				break;
			case FLOAT:
				object = configuration.getFloatValue();
				break;
			case INTEGER:
				object = configuration.getIntegerValue();
				break;
			case LONG:
				object = configuration.getLongValue();
				break;
			case STRING:
				object = configuration.getStringValue();
				break;
			case TEXT:
				object = configuration.getTextValue();
				break;
			case TIME:
				object = configuration.getTimeValue();
				break;
			default:
				logger.error("Untreated case. paramType = {}", configuration.getParamType());
				//TODO Throw a custom (or even a standard) exception here?
				break;
			}

		} else {
			logger.error("A Configuration entity does not exist for paramName = {}", paramName);
			//TODO Throw a custom (or even a standard) exception here?
		}

		return object;
	}

	@Transactional
	public void set(ParamName paramName, Object value) {
		set(paramName, value, null);
	}

	@Transactional
	public void set(ParamName paramName, Object value, Role role) {
		logger.info("paramName={}, value={}, role={}", paramName, value, role);
		/*
		 * Check if an existing Configuration does not exist, create one. Then
		 * update the Configuration with the specified value.
		 */
		Configuration configuration = null;
		if (role == null) {
			configuration = configurationRepository.findByParamName(paramName);
		} else {
			/*
			 * Do *not* call findByParamName(paramName, role) here with 
			 * role=null. This will not work set comments where this query is
			 * defined in ConfigurationRepository.
			 */
			configuration = configurationRepository.findByParamName(paramName, role);
		}
		if (configuration == null) {
			configuration = new Configuration(paramName, role);
		}

		switch (paramName.paramType()) {
		case BOOLEAN:
			configuration.setBooleanValue((Boolean) value);
			break;
		case BYTEARRAY:
			configuration.setByteaValue((byte[]) value);
			break;
		case DATE:
			configuration.setDateValue((Date) value);
			break;
		case DATETIME:
			configuration.setDatetimeValue((Date) value);
			break;
		case DOUBLE:
			configuration.setDoubleValue((Double) value);
			break;
		case FLOAT:
			configuration.setFloatValue((Float) value);
			break;
		case INTEGER:
			configuration.setIntegerValue((Integer) value);
			break;
		case LONG:
			configuration.setLongValue((Long) value);
			break;
		case STRING:
			configuration.setStringValue((String) value);
			break;
		case TEXT:
			configuration.setTextValue((String) value);
			break;
		case TIME:
			configuration.setTimeValue((Date) value);
			break;
		default:
			logger.error("Untreated case. paramType = {}", paramName.paramType());
			//TODO Throw a custom (or even a standard) exception here?
			break;
		}

		/*
		 * Set stringValue field for non-String parameters. This allows
		 * information for all parameters to be displayed using just the 
		 * stringValue field.
		 */
		if (paramName.paramType() != ParamType.STRING) {
			switch (paramName.paramType()) {
			case BYTEARRAY:
				configuration.setStringValue(String.valueOf(configuration.getByteaValue().length) + " bytes");
				break;
			case TEXT:
				String textValue = configuration.getTextValue();
				if (textValue.length() > 40) {
					textValue = textValue.substring(0, 40) + "...";
				}
				configuration.setStringValue(textValue);
				break;
			default:
				configuration.setStringValue(value.toString());
				break;
			}
		}

		configurationRepository.save(configuration);
	}
}
