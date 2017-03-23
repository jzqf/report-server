package com.qfree.bo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.db.ConfigurationRepository;
import com.qfree.bo.report.domain.Configuration.ParamName;
import com.qfree.bo.report.service.ConfigurationService;

public class ConfigurationDataTypes {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationDataTypes.class);

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

		ConfigurationRepository configurationRepository = context.getBean(ConfigurationRepository.class);
		ConfigurationService configurationService = context.getBean(ConfigurationService.class);

		logger.info("\n\n\n");

		Object booleanObject = configurationService.get(ParamName.TEST_BOOLEAN);
		Object byteArrayObject = configurationService.get(ParamName.TEST_BYTEARRAY); // byteArrayObject is null
		//byte[] byteArrayObject = new byte[0];
		byteArrayObject = new byte[0];
		Object dateObject = configurationService.get(ParamName.TEST_DATE);
		Object datetimeObject = configurationService.get(ParamName.TEST_DATETIME);
		Object doubleObject = configurationService.get(ParamName.TEST_DOUBLE);
		Object floatObject = configurationService.get(ParamName.TEST_FLOAT);
		Object integerObject = configurationService.get(ParamName.TEST_INTEGER);
		Object longObject = configurationService.get(ParamName.TEST_LONG);
		Object stringObject = configurationService.get(ParamName.TEST_STRING);
		Object textObject = configurationService.get(ParamName.TEST_TEXT);
		Object timeObject = configurationService.get(ParamName.TEST_TIME);

		logger.info("booleanObject = {}, class = {}", booleanObject, booleanObject.getClass().getName());
		logger.info("byteArrayObject = {}, class = {}", byteArrayObject, byteArrayObject.getClass().getName());
		logger.info("dateObject = {}, class = {}", dateObject, dateObject.getClass().getName());
		logger.info("datetimeObject = {}, class = {}", datetimeObject, datetimeObject.getClass().getName());
		logger.info("doubleObject = {}, class = {}", doubleObject, doubleObject.getClass().getName());
		logger.info("floatObject = {}, class = {}", floatObject, floatObject.getClass().getName());
		logger.info("integerObject = {}, class = {}", integerObject, integerObject.getClass().getName());
		logger.info("longObject = {}, class = {}", longObject, longObject.getClass().getName());
		logger.info("stringObject = {}, class = {}", stringObject, stringObject.getClass().getName());
		logger.info("textObject = {}, class = {}", textObject, textObject.getClass().getName());
		logger.info("timeObject = {}, class = {}", timeObject, timeObject.getClass().getName());

		logger.info("\n\n\n");

		context.close();
	}

}