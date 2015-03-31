package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ReportParameterRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTest.class);

	@Autowired
	ReportParameterRepository reportParameterRepository;

	@Autowired
	ReportRepository reportRepository;

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
		assertNotNull(report04);

		ReportParameter unsavedReportParameter = new ReportParameter(
				report04, "ParamAbbrevNotUsed", "ParamDescriptionNotUsed", true);
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
		assertEquals("ParamAbbrevNotUsed", foundReportParameter.getAbbreviation());
		assertEquals("ParamDescriptionNotUsed", foundReportParameter.getDescription());
		assertEquals(true, foundReportParameter.getActive());
	}

}
