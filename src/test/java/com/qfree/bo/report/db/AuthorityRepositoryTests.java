package com.qfree.bo.report.db;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.domain.Authority;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
/*
 * It is not clear that this test class dirties the Spring application context,
 * but I have experienced that if this annotation is not used on some test 
 * classes, then tests in *other* test classes can experience errors, i.e.,
 * exceptions (not test failures). For example if this annotation was not used 
 * in test class ReportCategoryRepositoryTests, then exceptions where thrown 
 * from the tests in ConfigurationServiceTests. In this case the log file 
 * contained messages that included things like:
 * 
 *    SQL Error: 90079, SQLState: 90079
 *    Schema "REPORTING" not found; SQL statement:
 *    org.hibernate.exception.GenericJDBCException: could not prepare statement
 * 
 * This problem seems to be related in some way to how the H2 RDBMS is used for
 * testing. Although this problem has been shown to be repeatable as long as 
 * code and dependencies are not changed, it is *not* repeatable if things do
 * change. Therefore, I have decided to annotate *all* test classes with the
 * @DirtiesContext annotation that access the H2 DB in any way.
 */
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
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
