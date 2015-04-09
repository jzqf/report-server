package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.configuration.Config;
import com.qfree.obo.report.configuration.Config.ParamName;
import com.qfree.obo.report.domain.Configuration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ConfigurationRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepositoryTest.class);

	@Autowired
	ConfigurationRepository configurationRepository;

	@Autowired
	Config config;

	@Test
	@Transactional
	public void countTestRecords() {
		assertThat(configurationRepository.count(), is(9L));
		assertThat(configurationRepository.count(), is(equalTo(9L)));
	}

	@Test
	@Transactional
	public void integerValueExistsGlobal() {
		UUID uuidOfglobalIntegerValueConfiguration = UUID.fromString("b4fd2271-26fb-4db7-bfe7-07211d849364");
		Configuration globalIntegerValueConfiguration = configurationRepository
				.findOne(uuidOfglobalIntegerValueConfiguration);
		assertThat(globalIntegerValueConfiguration, is(not(nullValue())));
		assertThat(globalIntegerValueConfiguration.getIntegerValue(), is(42));
	}

	@Test
	@Transactional
	public void integerValueFetchGlobal() {
		//		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_INTEGER.toString());
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_INTEGER);
		assertThat(configuration, is(not(nullValue())));
		Integer integerValue = configuration.getIntegerValue();
		assertThat(integerValue, is(not(nullValue())));
		assertThat(integerValue, is(42));
	}

	@Test
	@Transactional
	public void stringValueExistsGlobal() {
		UUID uuidOfGlobalStringValueConfiguration = UUID.fromString("62f9ca07-79ce-48dd-8357-873191ea8b9d");
		Configuration GlobalStringValueConfiguration = configurationRepository
				.findOne(uuidOfGlobalStringValueConfiguration);
		assertThat(GlobalStringValueConfiguration, is(not(nullValue())));
		assertThat(GlobalStringValueConfiguration.getStringValue(), is("Meaning of life"));
	}

	@Test
	@Transactional
	public void stringValueFetchGlobal() {
		//		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING.toString());
		Configuration configuration = configurationRepository.findByParamName(ParamName.TEST_STRING);
		assertThat(configuration, is(not(nullValue())));
		String stringValue = configuration.getStringValue();
		assertThat(stringValue, is(not(nullValue())));
		assertThat(stringValue, is("Meaning of life"));
	}

	//	@Test
	//	@Transactional
	//	public void stringValueFetchGlobalFromConfig() {
	//		Object stringValueObject = Config.get(ParamName.TEST_STRING);
	//		assertThat(stringValueObject, is(instanceOf(String.class)));
	//		assertThat((String) stringValueObject, is("Meaning of life"));
	//	}

	@Test
	@Transactional
	public void stringValueFetchGlobalFromConfig() {
		Object stringValueObject = config.get(ParamName.TEST_STRING);
		assertThat(stringValueObject, is(instanceOf(String.class)));
		assertThat((String) stringValueObject, is("Meaning of life"));
	}

	/**
	 * Attempt to fetch a global (Role==null) value that does not exist in the
	 * [configuration] table. This should throw a custom XXXXXXXXXXXXXXXXXXXXX.
	 */

	//	@Test
	//	@Transactional
	//	public void findAll() {
	//		List<ReportParameter> reportParameters = reportParameterRepository.findAll();
	//		assertEquals(6, reportParameters.size());
	//		//		for (ReportParameter reportParameter : reportParameters) {
	//		//			logger.info("reportParameter = {}", reportParameter);
	//		//		}
	//	}
	//
	//	@Test
	//	@Transactional
	//	public void save_newReportParameter() {
	//
	//		assertEquals(6, reportParameterRepository.count());
	//
	//		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
	//		Report report04 = reportRepository.findOne(uuidOfReport04);
	//		assertThat(report04, is(not(nullValue())));
	//
	//		ReportVersion report04Version01 = report04.getReportVersions().get(0);
	//		assertThat(report04Version01, is(not(nullValue())));
	//
	//		UUID uuidOfWidget1 = UUID.fromString("b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2");
	//		Widget widget1 = widgetRepository.findOne(uuidOfWidget1);
	//		assertThat(widget1, is(notNullValue()));
	//
	//		UUID uuidOfParameterTypeDate = UUID.fromString("12d3f4f8-468d-4faf-be3a-5c15eaba4eb6");
	//		ParameterType parameterTypeDate = parameterTypeRepository.findOne(uuidOfParameterTypeDate);
	//
	//		/* Query for the current maximum value of orderIndex for all 
	//		 * ReportParameter's for report04. This should be equal to the number of
	//		 * ReportParameter's for report04 because orderIndex is 1-based and 
	//		 * is incremented by one for each parameter added to the report.
	//		 */
	//		//		Integer maxOrderIndex = reportParameterRepository.maxOrderIndex(report04);
	//		Integer maxOrderIndex = reportParameterRepository.maxOrderIndex(report04Version01);
	//		assertThat(maxOrderIndex, is(equalTo(1)));
	//
	//		Boolean required = true;
	//		Boolean multivalued = false;
	//
	//		//		ReportParameter unsavedReportParameter = new ReportParameter(
	//		//				report04, "Some new parameter name", "Some new parameter description", parameterTypeDate, widget1,
	//		//				required, multivalued, maxOrderIndex + 1);
	//		ReportParameter unsavedReportParameter = new ReportParameter(
	//				report04Version01, "Some new parameter name", "Some new parameter description", parameterTypeDate,
	//				widget1,
	//				required, multivalued, maxOrderIndex + 1);
	//		//		logger.info("unsavedReportParameter = {}", unsavedReportParameter);
	//
	//		ReportParameter savedReportParameter = reportParameterRepository.save(unsavedReportParameter);
	//		//		logger.info("savedReportParameter = {}", savedReportParameter);
	//		//		logger.info("savedReportParameter.getreportParameterId() = {}", savedReportParameter.getReportParameterId());
	//		//		logger.info("After save: unsavedReportParameter.getReportParameterId() = {}",
	//		//				unsavedReportParameter.getReportParameterId());
	//
	//		assertEquals(7, reportParameterRepository.count());
	//
	//		/*
	//		 * Check that max(orderIndex) has been incremented for report04.
	//		 */
	//		//		Integer newMaxOrderIndex = reportParameterRepository.maxOrderIndex(report04);
	//		Integer newMaxOrderIndex = reportParameterRepository.maxOrderIndex(report04Version01);
	//		assertThat(newMaxOrderIndex, is(equalTo(maxOrderIndex + 1)));
	//
	//		UUID uuidFromSavedReportParameter = savedReportParameter.getReportParameterId();
	//		ReportParameter foundReportParameter = reportParameterRepository.findOne(uuidFromSavedReportParameter);
	//
	//		/*
	//		 * TODO Replace this code with a custom "assertReportParameter(...)" method.
	//		 */
	//		assertThat(foundReportParameter.getName(), is("Some new parameter name"));
	//		assertThat(foundReportParameter.getDescription(), is("Some new parameter description"));
	//		assertThat(foundReportParameter.getRequired(), is(true));
	//	}

}
