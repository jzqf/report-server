package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
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

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobParameterValueRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterValueRepositoryTests.class);

	@Autowired
	JobParameterValueRepository jobParameterValueRepository;

	@Autowired
	JobRepository jobRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	DocumentFormatRepository documentFormatRepository;

	//	@Autowired
	//	ReportParameterRepository reportParameterRepository;
	//
	//	@Autowired
	//	ParameterTypeRepository parameterTypeRepository;

	@Test
	@Transactional
	public void count() {
		assertThat(jobParameterValueRepository.count(), is(0L));
	}

	@Test
	@Transactional
	public void findAll() {
		List<JobParameterValue> jobParameterValues = jobParameterValueRepository.findAll();
		assertThat(jobParameterValues.size(), is(0));
	}

	@Test
	@Transactional
	public void save_newJobParameterValue_singlevalued() {

		assertThat(jobRepository.count(), is(2L));
		assertThat(jobParameterValueRepository.count(), is(0L));

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		ReportVersion report04Version01 = report04.getReportVersions().get(0);
		assertThat(report04Version01, is(not(nullValue())));

		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));

		UUID uuidOfPdfFormat = UUID.fromString("30800d77-5fdd-44bc-94a3-1502bd307c1d");
		DocumentFormat pdfFormat = documentFormatRepository.findOne(uuidOfPdfFormat);
		assertThat(pdfFormat, is(not(nullValue())));

		/*
		 * Create a new Job. for report "Report name #04" for Role "aabb".
		 */
		//		Job newJob = new Job(report04, role_aabb);
		Job newJob = new Job(report04Version01, role_aabb, pdfFormat);

		/*
		 * Get all the parameters.
		 */
		//		List<ReportParameter> reportParameters = report04.getReportParameters();
		//		assertThat(reportParameters, hasSize(equalTo(1)));
		List<ReportParameter> reportParameters = report04Version01.getReportParameters();
		assertThat(reportParameters, hasSize(equalTo(1)));

		/*
		 * Create three JobParameterValue's for each multi-valued report 
		 * parameter for report "Report name #04". There should only be one 
		 * report parameter for report  "Report name #04" and it should be 
		 * multi-valued.
		 */
		for (ReportParameter rp : reportParameters) {
			JobParameterValue jpv;
			List<JobParameterValue> jpvs = new ArrayList<>();
			if (rp.getMultivalued()) {
				//				logger.info("newJob.getJobParameterValues() = {}", newJob.getJobParameterValues());
				jpv = new JobParameterValue(newJob, rp, "multi-value #1");
				jpvs.add(jpv);
				jpv = new JobParameterValue(newJob, rp, "multi-value #2");
				jpvs.add(jpv);
				jpv = new JobParameterValue(newJob, rp, "multi-value #3");
				jpvs.add(jpv);
				newJob.setJobParameterValues(jpvs);
			} else {
				// This case should not occur.
				jpv = new JobParameterValue(newJob, rp, "single value");
				jpvs.add(jpv);
				newJob.setJobParameterValues(jpvs);
			}
		}

		/*
		 * Save the new Job and its JobParameterValue's.
		 */
		Job savedJob = jobRepository.save(newJob);
		//		logger.info("savedJob = {}", savedJob);
		//		logger.info("savedJob.getjobId() = {}", savedJob.getJobId());
		//		logger.info("After save: unsavedJob.getJobId() = {}", unsavedJob.getJobId());

		assertThat(jobRepository.count(), is(3L));
		assertThat(jobParameterValueRepository.count(), is(3L));

		//TODO Need to write a LOT more tests
	}

	@Test
	@Transactional
	public void save_newJobParameterValue_multivalued() {

		assertThat(jobRepository.count(), is(2L));

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		ReportVersion report04Version01 = report04.getReportVersions().get(0);
		assertThat(report04Version01, is(not(nullValue())));

		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));

		UUID uuidOfPdfFormat = UUID.fromString("30800d77-5fdd-44bc-94a3-1502bd307c1d");
		DocumentFormat pdfFormat = documentFormatRepository.findOne(uuidOfPdfFormat);
		assertThat(pdfFormat, is(not(nullValue())));

		//		Job unsavedJob = new Job(report04, role_aabb);
		Job unsavedJob = new Job(report04Version01, role_aabb, pdfFormat);
		//		logger.info("unsavedJob = {}", unsavedJob);

		Job savedJob = jobRepository.save(unsavedJob);
		//		logger.info("savedJob = {}", savedJob);
		//		logger.info("savedJob.getjobId() = {}", savedJob.getJobId());
		//		logger.info("After save: unsavedJob.getJobId() = {}", unsavedJob.getJobId());

		assertThat(jobRepository.count(), is(3L));

		Long uuidFromSavedJob = savedJob.getJobId();
		Job foundJob = jobRepository.findOne(uuidFromSavedJob);

		/*
		 * TODO Replace this code with a custom "assertJob(...)" method.
		 */
		//		assertThat(foundJob.getName(), is("Some new parameter name"));
		//		assertThat(foundJob.getDescription(), is("Some new parameter description"));
		//		assertThat(foundJob.getRequired(), is(true));
	}

}