package com.qfree.obo.report.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
		TEST_BOOLEAN,
		TEST_BYTEARRAY,
		TEST_DATE,
		TEST_DATETIME,
		TEST_FLOAT,
		TEST_INTEGER,
		TEST_STRING,
		TEST_TEXT,
		TEST_TIME
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

	public Object get(ParamName name) {
		return get(name, null);
	}

	public Object get(ParamName name, Role role) {
		/*
		 * First look for role-specific value if a Role is specified.
		 */
		Object object = null;
		if (role != null) {
			//TODO Write me!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}

		/*
		 * If no role-specific value has been found...
		 */
		if (object == null) {
			/* 
			 * ...Look for a global (role-independent) default value for the 
			 * parameter.
			 */
			//			Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING
			//					.toString());
			logger.info("name = {}", name);
			Configuration configuration = configurationRepository.findByParamName(name);
			//TODO THROW CUSTOM EXCEPTION IF NO PARAM FOUND + LOG
			logger.info("configuration = {}", configuration);

			/*
			 * Get the type of the parameter value so that we can retrieve the
			 * value with the appropriate property. 
			 */
			ParamType paramType = configuration.getParamType();
			switch (paramType) {
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
				logger.error("Untreated case. paramType = {}", paramType);
				//TODO Throw a custom (or even a standard) exception here?
				break;
			}
		}

		return object;
	}

	public void set(ParamName name, String value, ParamType paramType, Role role) {
		//TODO Write me!
	}
}
