package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.domain.ParameterType;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.Widget;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ReportParameterRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTest.class);

	@Autowired
	ReportParameterRepository reportParameterRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	WidgetRepository widgetRepository;

	@Autowired
	ParameterTypeRepository parameterTypeRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(6, reportParameterRepository.count());
	}

	@Test
	@Transactional
	public void findAll() {
		List<ReportParameter> reportParameters = reportParameterRepository.findAll();
		assertEquals(6, reportParameters.size());
		//		for (ReportParameter reportParameter : reportParameters) {
		//			logger.info("reportParameter = {}", reportParameter);
		//		}
	}

	@Test
	@Transactional
	public void save_newReportParameter() {

		assertEquals(6, reportParameterRepository.count());

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		UUID uuidOfWidget1 = UUID.fromString("b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2");
		Widget widget1 = widgetRepository.findOne(uuidOfWidget1);
		assertThat(widget1, is(notNullValue()));

		UUID uuidOfParameterTypeDate = UUID.fromString("12d3f4f8-468d-4faf-be3a-5c15eaba4eb6");
		ParameterType parameterTypeDate = parameterTypeRepository.findOne(uuidOfParameterTypeDate);

		ReportParameter unsavedReportParameter = new ReportParameter(
				report04, "Some new parameter name", "Some new parameter description", parameterTypeDate, widget1, true);
		//		logger.info("unsavedReportParameter = {}", unsavedReportParameter);

		ReportParameter savedReportParameter = reportParameterRepository.save(unsavedReportParameter);
		//		logger.info("savedReportParameter = {}", savedReportParameter);
		//		logger.info("savedReportParameter.getreportParameterId() = {}", savedReportParameter.getReportParameterId());
		//		logger.info("After save: unsavedReportParameter.getReportParameterId() = {}",
		//				unsavedReportParameter.getReportParameterId());

		assertEquals(7, reportParameterRepository.count());

		UUID uuidFromSavedReportParameter = savedReportParameter.getReportParameterId();
		ReportParameter foundReportParameter = reportParameterRepository.findOne(uuidFromSavedReportParameter);

		/*
		 * TODO Replace this code with a custom "assertReportParameter(...)" method.
		 */
		assertThat(foundReportParameter.getName(), is("Some new parameter name"));
		assertThat(foundReportParameter.getDescription(), is("Some new parameter description"));
		assertThat(foundReportParameter.getRequired(), is(true));
	}

}
