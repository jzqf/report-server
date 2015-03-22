package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;

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

import com.qfree.obo.report.domain.ReportParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
//@ContextConfiguration(classes = com.qfree.obo.report.apps.JpaConfig.class)
public class ReportParameterRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTest.class);

	@Autowired
	ReportParameterRepository reportParameterRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(4, reportParameterRepository.count());
	}

	@Test
	@Transactional
	public void findAll() {
		List<ReportParameter> reportParameters = reportParameterRepository.findAll();
		assertEquals(4, reportParameters.size());

		for (ReportParameter reportParameter : reportParameters) {
			logger.info("reportParameter = {}", reportParameter);
		}
	}

	@Test
	@Transactional
	public void save_newReportParameter() {

		assertEquals(4, reportParameterRepository.count());

		ReportParameter reportParameter = new ReportParameter("ParamAbbrev", "ParamDescription", true);
		//		logger.info("reportParameter = {}", reportParameter);

		ReportParameter saved = reportParameterRepository.save(reportParameter);
		//		logger.info("saved = {}", saved);
		//		logger.info("saved.getreportParameterId() = {}", saved.getReportParameterId());
		//		logger.info("After save: reportParameter.getReportParameterId() = {}", reportParameter.getReportParameterId());

		assertEquals(5, reportParameterRepository.count());

		UUID uuidFromSavedEntity = saved.getReportParameterId();
		ReportParameter foundReportParameter = reportParameterRepository.findOne(uuidFromSavedEntity);

		/*
		 * TODO Replace this code with a custom "assertReportParameter(...)" method.
		 */
		assertEquals("ParamAbbrev", foundReportParameter.getAbbreviation());
		assertEquals("ParamDescription", foundReportParameter.getDescription());
		assertEquals(true, foundReportParameter.getActive());
	}

}
