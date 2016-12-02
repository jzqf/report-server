package com.qfree.obo.report.db;

//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
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

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.Authority;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AuthorityRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityRepositoryTests.class);

	@Autowired
	AuthorityRepository authorityRepository;

	@Test
	@Transactional
	public void count() {
		assertThat(authorityRepository.count(), is(equalTo(19L)));
	}

	@Test
	@Transactional
	public void findOne() {
		UUID uuidOfAuthority_DELETE_JOBS = UUID.fromString("ace1edd3-6a5b-4b40-a802-79616472b893");
		Authority authority_DELETE_JOBS = authorityRepository.findOne(uuidOfAuthority_DELETE_JOBS);
		assertThat(authority_DELETE_JOBS, is(not(nullValue())));
		assertThat(authority_DELETE_JOBS.getAuthorityId().toString(),
				is(equalTo(uuidOfAuthority_DELETE_JOBS.toString())));
		assertThat(authority_DELETE_JOBS.getName(), is(equalTo("DELETE_JOBS")));
		assertThat(authority_DELETE_JOBS.isActive(), is(equalTo(true)));
	}

}
