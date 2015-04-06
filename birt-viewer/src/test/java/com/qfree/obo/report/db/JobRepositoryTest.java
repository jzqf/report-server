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

import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class JobRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(JobRepositoryTest.class);

	@Autowired
	JobRepository jobRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ReportRepository reportRepository;

	//	@Autowired
	//	ReportParameterRepository reportParameterRepository;
	//
	//	@Autowired
	//	ParameterTypeRepository parameterTypeRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(2, jobRepository.count());
	}

	@Test
	@Transactional
	public void findAll() {
		List<Job> jobs = jobRepository.findAll();
		assertEquals(2, jobs.size());
	}

	@Test
	@Transactional
	public void save_newJob() {

		assertEquals(2, jobRepository.count());

		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));

		UUID uuidOfRole_aabb = UUID.fromString("ee56f34d-dbb4-41c1-9d30-ce29cf973820");
		Role role_aabb = roleRepository.findOne(uuidOfRole_aabb);
		assertThat(role_aabb, is(notNullValue()));

		Job unsavedJob = new Job(report04, role_aabb);
		//		logger.info("unsavedJob = {}", unsavedJob);

		Job savedJob = jobRepository.save(unsavedJob);
		//		logger.info("savedJob = {}", savedJob);
		//		logger.info("savedJob.getjobId() = {}", savedJob.getJobId());
		//		logger.info("After save: unsavedJob.getJobId() = {}", unsavedJob.getJobId());

		assertEquals(3, jobRepository.count());

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
