package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(JobRepositoryTests.class);

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

	@Test
	@Transactional
	public void count() {
		assertThat(jobRepository.count(), is(2L));
	}

	@Test
	@Transactional
	public void findAll() {
		List<Job> jobs = jobRepository.findAll();
		assertThat(jobs.size(), is(2));
	}

	@Test
	@Transactional
	public void save_newJob() {

		assertThat(jobRepository.count(), is(2L));

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		ReportVersion report04Version01 = report04.getReportVersions().get(0);
		assertThat(report04Version01, is(not(nullValue())));

		UUID uuidOfPdfFormat = UUID.fromString("30800d77-5fdd-44bc-94a3-1502bd307c1d");
		DocumentFormat pdfFormat = documentFormatRepository.findOne(uuidOfPdfFormat);
		assertThat(pdfFormat, is(not(nullValue())));

		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));

		JobStatus jobStatusQueued = jobStatusRepository.findOne(JobStatus.QUEUED_ID);
		assertThat(jobStatusQueued, is(notNullValue()));

		Job unsavedJob = new Job(
				null,
				jobStatusQueued,
				null,
				report04Version01,
				role_aabb,
				pdfFormat,
				null,
				null,
				null,
				null);
		Job savedJob = jobRepository.save(unsavedJob);

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
