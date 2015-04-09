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

	public Object get(ParamName name) {
		return get(name, null);
	}

	public Object get(ParamName name, Role role) {
		/*
		 * First look for role-specific value if a Role is specified, and then 
		 * for global default, i.e., (role==null), if no role-specific value is
		 * found.
		 */
		Object object = null;
		if (role != null) {
			//TODO Write me!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}

		if (object == null) {
			/* 
			 * Look for global default.
			 */
			//			Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING
			//					.toString());
			logger.info("name = {}", name);
			Configuration configuration = configurationRepository.findByParamName(name);
			logger.info("configuration = {}", configuration);
			//			object = "Finish writing Config.get!";
			//TODO MUST DO TJIS IN A CASE OR WHATEVER:
			/*
			 * We use an enum so that we can hardwire the data type of each 
			 * parameter.
			 */
			switch (name) {
			case TEST_BOOLEAN:
				object = configuration.getBooleanValue();
				break;
			case TEST_BYTEARRAY:
				object = configuration.getByteaValue();
				break;
			case TEST_DATE:
				object = configuration.getDateValue();
				break;
			case TEST_DATETIME:
				object = configuration.getDatetimeValue();
				break;
			case TEST_FLOAT:
				object = configuration.getFloatValue();
				break;
			case TEST_INTEGER:
				object = configuration.getIntegerValue();
				break;
			case TEST_STRING:
				object = configuration.getStringValue();
				break;
			case TEST_TEXT:
				object = configuration.getTextValue();
				break;
			case TEST_TIME:
				object = configuration.getTimeValue();
				break;
			default:
				logger.error("Untreated case. name = {}", name);
				//TODO Throw a custom (or even a standard) exception here?
				break;
			}
		}

		//TODO THROW CUSTOM EXCEPTION IF NO PARAM FOUND + LOG

		return object;
	}

	public void set(ParamName name, String value, Role role) {
		//TODO Write me!
	}
}
