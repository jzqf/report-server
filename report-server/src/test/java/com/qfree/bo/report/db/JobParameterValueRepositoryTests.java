package com.qfree.bo.report.db;

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

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.db.DocumentFormatRepository;
import com.qfree.bo.report.db.JobParameterRepository;
import com.qfree.bo.report.db.JobParameterValueRepository;
import com.qfree.bo.report.db.JobRepository;
import com.qfree.bo.report.db.JobStatusRepository;
import com.qfree.bo.report.db.ReportRepository;
import com.qfree.bo.report.db.RoleRepository;
import com.qfree.bo.report.domain.DocumentFormat;
import com.qfree.bo.report.domain.Job;
import com.qfree.bo.report.domain.JobParameter;
import com.qfree.bo.report.domain.JobParameterValue;
import com.qfree.bo.report.domain.JobStatus;
import com.qfree.bo.report.domain.Report;
import com.qfree.bo.report.domain.ReportParameter;
import com.qfree.bo.report.domain.ReportVersion;
import com.qfree.bo.report.domain.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobParameterValueRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterValueRepositoryTests.class);

	@Autowired
	private JobParameterValueRepository jobParameterValueRepository;

	@Autowired
	private JobParameterRepository jobParameterRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private DocumentFormatRepository documentFormatRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

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
	public void save_newJobParameterValues_multivalued() {

		assertThat(jobRepository.count(), is(2L));
		assertThat(jobParameterRepository.count(), is(0L));
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

		JobStatus jobStatusQueued = jobStatusRepository.findOne(JobStatus.QUEUED_ID);
		assertThat(jobStatusQueued, is(notNullValue()));

		/*
		 * Create a new Job. for report "Report name #04" for Role "aabb".
		 */
		//		Job newJob = new Job(report04, role_aabb);
		Job newJob = new Job(
				null,
				jobStatusQueued,
				null,
				report04Version01,
				role_aabb,
				pdfFormat,
				"someone@somedomain.com");

		/*
		 * Get all the parameters for the report. There should be only one with
		 * attributes:
		 *     data_type    = 6
		 *     control_type = 0
		 *     required     = true
		 *     multivalued  = true
		 *     ...   
		 */
		//		List<ReportParameter> reportParameters = report04.getReportParameters();
		//		assertThat(reportParameters, hasSize(equalTo(1)));
		List<ReportParameter> reportParameters = report04Version01.getReportParameters();
		assertThat(reportParameters, hasSize(equalTo(1)));

		/*
		 * Create three JobParameterValue's for the multi-valued report 
		 * parameter for report "Report name #04". There should only be one 
		 * report parameter for report  "Report name #04" and it should be 
		 * multi-valued, but I write this code as a loop in case this changes
		 * in the future.
		 */
		List<JobParameter> jobParameters = new ArrayList<>();
		newJob.setJobParameters(jobParameters);
		for (ReportParameter reportParameter : reportParameters) {

			JobParameter jobParameter = new JobParameter(newJob, reportParameter);
			jobParameters.add(jobParameter);

			/*
			 * Create 3 JobParameterValue entities for jobParameter.
			 */
			JobParameterValue jobParameterValue;
			List<JobParameterValue> jobParameterValues = new ArrayList<>();
			jobParameter.setJobParameterValues(jobParameterValues);
			if (reportParameter.getMultivalued()) {
				jobParameterValue = new JobParameterValue(jobParameter, null, null, null, null, 123, null, null);
				jobParameterValues.add(jobParameterValue);
				jobParameterValue = new JobParameterValue(jobParameter, null, null, null, null, 321, null, null);
				jobParameterValues.add(jobParameterValue);
				jobParameterValue = new JobParameterValue(jobParameter, null, null, null, null, 0, null, null);
				jobParameterValues.add(jobParameterValue);
			} else {
				// This case should not occur.
				jobParameterValue = new JobParameterValue(jobParameter, null, null, null, null, -666, null, null);
				jobParameterValues.add(jobParameterValue);
			}
		}

		/*
		 * Save the new Job, its JobParameter's (one one) and the JobParameter's
		 * related JobParameterValue's (3 of them).
		 */
		Job savedJob = jobRepository.save(newJob);
		logger.info("savedJob = {}", savedJob);

		assertThat(jobRepository.count(), is(3L));
		assertThat(jobParameterRepository.count(), is(1L));
		assertThat(jobParameterValueRepository.count(), is(3L));

		assertThat(savedJob.getJobParameters(), hasSize(1));

		JobParameter savedJob_JobParameter1 = savedJob.getJobParameters().get(0);
		assertThat(savedJob_JobParameter1.getJobParameterValues(), hasSize(3));

		//		logger.info("savedJob.getjobId() = {}", savedJob.getJobId());
		//		logger.info("After save: newJob.getJobId() = {}", newJob.getJobId());

		//TODO Need to write a LOT more tests
	}

}
