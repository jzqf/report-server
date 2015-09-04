package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ReportParameterRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTests.class);

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
		assertThat(reportParameterRepository.count(), is(equalTo(13L)));
	}

	@Test
	@Transactional
	public void findAll() {
		List<ReportParameter> reportParameters = reportParameterRepository.findAll();
		assertThat(reportParameters.size(), is(equalTo(13)));
		//		for (ReportParameter reportParameter : reportParameters) {
		//			logger.info("reportParameter = {}", reportParameter);
		//		}
	}

	@Test
	@Transactional
	public void save_newReportParameter() {

		long currentNumParams = 13;

		assertThat(reportParameterRepository.count(), is(equalTo(currentNumParams)));

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		ReportVersion report04Version01 = report04.getReportVersions().get(0);
		assertThat(report04Version01, is(not(nullValue())));

		//	UUID uuidOfWidget1 = UUID.fromString("b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2");
		//	Widget widget1 = widgetRepository.findOne(uuidOfWidget1);
		//	assertThat(widget1, is(notNullValue()));
		//
		//	UUID uuidOfParameterTypeDate = UUID.fromString("12d3f4f8-468d-4faf-be3a-5c15eaba4eb6");
		//	ParameterType parameterTypeDate = parameterTypeRepository.findOne(uuidOfParameterTypeDate);

		/* Query for the current maximum value of orderIndex for all 
		 * ReportParameter's for report04. This should be equal to the number of
		 * ReportParameter's for report04 because orderIndex is 1-based and 
		 * is incremented by one for each parameter added to the report.
		 */
		//		Integer maxOrderIndex = reportParameterRepository.maxOrderIndex(report04);
		Integer maxOrderIndex = reportParameterRepository.maxOrderIndex(report04Version01);
		assertThat(maxOrderIndex, is(equalTo(1)));

		Integer dataType_Date = IParameterDefn.TYPE_DATE;
		Integer controlType_Checkbox = IScalarParameterDefn.CHECK_BOX;
		Boolean required = true;
		Boolean multivalued = false;
		
		String defaultValue=null;
		String displayName= null;
		String helpText= null;
		String displayFormat=null;
		Integer alignment=IScalarParameterDefn.AUTO;
		Boolean hidden=false;
		Boolean valueConcealed=false;
		Boolean allowNewValues=false;
		Boolean displayInFixedOrder=true;
		Integer parameterType=IParameterDefnBase.SCALAR_PARAMETER;
		Integer autoSuggestThreshold=100;
		Integer selectionListType=IParameterDefn.TYPE_STRING;
		//String typeName = ;
		String valueExpr = null;
		ParameterGroup parameterGroup=null;

		//	ReportParameter unsavedReportParameter = new ReportParameter(
		//			report04Version01, maxOrderIndex + 1, parameterTypeDate, widget1,
		//			"Some new parameter name", "Some new parameter prompt text",
		//			required, multivalued);
		ReportParameter unsavedReportParameter = new ReportParameter(
				report04Version01, maxOrderIndex + 1, dataType_Date, controlType_Checkbox,
				"Some new parameter name", "Some new parameter prompt text",
				required, multivalued,
				defaultValue,
				displayName,
				helpText,
				displayFormat,
				alignment,
				hidden,
				valueConcealed,
				allowNewValues,
				displayInFixedOrder,
				parameterType,
				autoSuggestThreshold,
				selectionListType,
				//parameter.get("TypeName") != null ? (String) parameter.get("TypeName") : null,
				valueExpr,
				parameterGroup);
		//		logger.info("unsavedReportParameter = {}", unsavedReportParameter);

		ReportParameter savedReportParameter = reportParameterRepository.save(unsavedReportParameter);
		//		logger.info("savedReportParameter = {}", savedReportParameter);
		//		logger.info("savedReportParameter.getreportParameterId() = {}", savedReportParameter.getReportParameterId());
		//		logger.info("After save: unsavedReportParameter.getReportParameterId() = {}",
		//				unsavedReportParameter.getReportParameterId());

		assertThat(reportParameterRepository.count(), is(equalTo(currentNumParams + 1)));

		/*
		 * Check that max(orderIndex) has been incremented for report04.
		 */
		//		Integer newMaxOrderIndex = reportParameterRepository.maxOrderIndex(report04);
		Integer newMaxOrderIndex = reportParameterRepository.maxOrderIndex(report04Version01);
		assertThat(newMaxOrderIndex, is(equalTo(maxOrderIndex + 1)));

		UUID uuidFromSavedReportParameter = savedReportParameter.getReportParameterId();
		ReportParameter foundReportParameter = reportParameterRepository.findOne(uuidFromSavedReportParameter);

		/*
		 * TODO Replace this code with a custom "assertReportParameter(...)" method.
		 */
		assertThat(foundReportParameter.getName(), is("Some new parameter name"));
		assertThat(foundReportParameter.getPromptText(), is("Some new parameter prompt text"));
		assertThat(foundReportParameter.getRequired(), is(true));
	}

}
