package com.qfree.bo.report.service;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
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
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.UuidCustomType;

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
public class AuthorityServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityServiceTests.class);
	
	/*
	 * Number of Authority's granted directly to the Role with username:
	 * "reportadmin".
	 */
	private static final int NUM_AUTHORITIES_GRANTED_TO_ADMINROLE = 15;

	@Autowired
	AuthorityService authorityService;

	@Test
	@Transactional
	public void findAuthoritiesForReportAdmin() {
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(Role.ADMIN_ROLE_ID);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		assertThat(authorities, hasSize(NUM_AUTHORITIES_GRANTED_TO_ADMINROLE));
	}

	@Test
	@Transactional
	public void findAllAuthorityNamesForReportAdmin() {
		List<String> authorityNames = authorityService.findAuthorityNamesByRoleId(Role.ADMIN_ROLE_ID, false);
		logger.info("authorityNames = {}", authorityNames);
		assertThat(authorityNames, is(not(nullValue())));
		assertThat(authorityNames, hasSize(NUM_AUTHORITIES_GRANTED_TO_ADMINROLE));
	}

	@Test
	@Transactional
	public void findActiveAuthorityNamesForReportAdmin() {
		List<String> authorityNames = authorityService.findAuthorityNamesByRoleId(Role.ADMIN_ROLE_ID, true);
		logger.info("authorityNames = {}", authorityNames);
		assertThat(authorityNames, is(not(nullValue())));
		assertThat(authorityNames, hasSize(NUM_AUTHORITIES_GRANTED_TO_ADMINROLE));
	}

	@Test
	@Transactional
	/**
	 * Role "aa" has no authorities of its own, but it is a direct child of the
	 * "reportadmin" Role. Hence, it should inherit all of the authorities of
	 * "reportadmin" *even though* role "aa" is set as "inactive". This is
	 * because a role does not need to be active in order to query which 
	 * Authorities are granted to it, but any Authorities that are granted
	 * to ancestor (parent, grandparent, ...) Roles are only returned here 
	 * these ancestor roles, as well as any intermediate roles, are "active".
	 */
	public void findAuthoritiesForDirectChildOfReportadmin() {
		UUID uuidOfRole_aa = UUID.fromString("1f47643b-0cb7-42a1-82bd-ab912d369567");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_aa);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			assertThat(authorities, hasSize(NUM_AUTHORITIES_GRANTED_TO_ADMINROLE));
		} else {
			/*
			 * authorityService.getActiveAuthoritiesByRoleId(...) does not take
			 * into account role inheritance in we are using H2 (because it does
			 * not support recursive CTEs).
			 */
			assertThat(authorities, hasSize(0));
		}
	}

	@Test
	@Transactional
	/**
	 * Role "aabc" has no authorities of its own, but it is a descendant of the
	 * "reportadmin" Role (via Role "aa"). Hence, it should inherit all of the
	 * authorities of "reportadmin". But it does not (the number of Authorities
	 * located in this test is zero), because the intermediate role "aa" is
	 * set to be "inactive".
	 */
	public void findAuthoritiesForDescendantReportadminInactiveIntermediateRole() {
		UUID uuidOfRole_aabc = UUID.fromString("f46f6b53-d70e-4044-91e2-ff749159fd90");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_aabc);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			assertThat(authorities, hasSize(0));
		} else {
			/*
			 * authorityService.getActiveAuthoritiesByRoleId(...) does not take
			 * into account role inheritance in we are using H2 (because it does
			 * not support recursive CTEs).
			 */
			assertThat(authorities, hasSize(0));
		}
	}

	@Test
	@Transactional
	/**
	 * Role "acab" has no authorities of its own, but it is a descendant of the
	 * "reportadmin" Role (via Role "aa"). Hence, it should inherit all of the
	 * authorities of "reportadmin".
	 */
	public void findAuthoritiesForDescendantReportadmin() {
		UUID uuidOfRole_acab = UUID.fromString("7ad44167-6dd9-46eb-a613-69c1e31e51d6");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_acab);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			assertThat(authorities, hasSize(NUM_AUTHORITIES_GRANTED_TO_ADMINROLE));
		} else {
			/*
			 * authorityService.getActiveAuthoritiesByRoleId(...) does not take
			 * into account role inheritance in we are using H2 (because it does
			 * not support recursive CTEs).
			 */
			assertThat(authorities, hasSize(0));
		}
	}

	@Test
	@Transactional
	/**
	 * Role "a" has no authorities of its own and neither is a direct child or
	 * any other sort of descendant of the "reportadmin" Role. Hence, it should
	 * not have any authorities.
	 */
	public void findAuthoritiesForNonAdminRootRole() {
		UUID uuidOfRole_a = UUID.fromString("e73ee6a5-5236-4630-aba1-de18e76b8105");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_a);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		assertThat(authorities, hasSize(0));
	}

	@Test
	@Transactional
	/**
	 * Role "abbc" has no authorities of its own and neither is a direct child
	 * or any other sort of descendant of the "reportadmin" Role. Hence, it
	 * should not have any authorities.
	 */
	public void findAuthoritiesForNonAdminLeafRole() {
		UUID uuidOfRole_abbc = UUID.fromString("12ff9c46-d3ab-4e5e-a00f-f1acde700256");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_abbc);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		assertThat(authorities, hasSize(0));
	}

}
