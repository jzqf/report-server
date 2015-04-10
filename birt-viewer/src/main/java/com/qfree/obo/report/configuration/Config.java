package com.qfree.obo.report.configuration;

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
		TEST_FLOAT(ParamType.FLOAT),
		TEST_INTEGER(ParamType.INTEGER),
		TEST_STRING(ParamType.STRING),
		TEST_TEXT(ParamType.TEXT),
		TEST_TIME(ParamType.TIME),
		/* 
		 * No [configuration] record is created for this parameter in the test
		 * data created by .../test-data.sql. This allows it to be used for unit
		 * tests related to configuration parameters that have not been set.
		 */
		TEST_NOTSET(ParamType.STRING);//TODO CREATE TEST CASE THAT READS AND SETS THIS PARAMETER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

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
		FLOAT,
		INTEGER,
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
			//			Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING
			//					.toString());
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
			case FLOAT:
				object = configuration.getFloatValue();
				break;
			case INTEGER:
				object = configuration.getIntegerValue();
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

	/*
	 * There is one "set" method for each possible parameter data type value 
	 * that can be persisted.
	 * NOOOOOO. CAN WE JUST PASS AN "Object" AND THEN TREAT IT GENERICALLY?????????????????????????????????????????????????????
	 */

	public void set(ParamName paramName, String value, ParamType paramType, Role role) {
		//TODO Write me!
	}

	@Transactional
	public void set(ParamName paramName, String value) {
		set(paramName, value, null);
	}

	@Transactional
	public void set(ParamName paramName, String value, Role role) {
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
		logger.info("After findByParamName: configuration={}", configuration);
		if (configuration == null) {
			logger.info("Creating new Configuration");
			configuration = new Configuration(paramName, role);
		} else {
			logger.info(
					"After findByParamName: paramName={}, value={}, role={}, configuration={}, configuration.getConfigurationId()={}",
					paramName, value, role, configuration, configuration.getConfigurationId());
		}
		logger.info(
				"Before setStringValue: paramName={}, value={}, role={}, configuration={}, configuration.getConfigurationId()={}",
				paramName, value, role, configuration, configuration.getConfigurationId());
		configuration.setStringValue(value);
		logger.info(
				"After  setStringValue: paramName={}, value={}, role={}, configuration={}, configuration.getConfigurationId()={}",
				paramName, value, role, configuration, configuration.getConfigurationId());
		configurationRepository.save(configuration);
		logger.info(
				"After save           : paramName={}, value={}, role={}, configuration={}, configuration.getConfigurationId()={}",
				paramName, value, role, configuration, configuration.getConfigurationId());
		//TODO Write me!  Set string_value always!
	}
}
