package com.qfree.obo.report.service;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.UuidCustomType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AuthorityServiceTests {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityServiceTests.class);

	@Autowired
	AuthorityService authorityService;

	@Test
	@Transactional
	public void findAuthoritiesForReportAdmin() {
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(Role.ADMIN_ROLE_ID);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		assertThat(authorities, hasSize(15));
	}

	@Test
	@Transactional
	public void findActiveAuthorityNamesForReportAdmin() {
		List<String> authorityNames = authorityService.findActiveAuthorityNamesByRoleId(Role.ADMIN_ROLE_ID);
		logger.info("authorityNames = {}", authorityNames);
		assertThat(authorityNames, is(not(nullValue())));
		assertThat(authorityNames, hasSize(15));
	}

	@Test
	@Transactional
	/**
	 * Role "aa" has no authorities of its own, but it is a direct child of the
	 * "reportadmin" Role. Hence, it should inherit all of the authorities of
	 * "reportadmin".
	 */
	public void findAuthoritiesForDirectChildOfReportadmin() {
		UUID uuidOfRole_aa = UUID.fromString("1f47643b-0cb7-42a1-82bd-ab912d369567");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_aa);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			assertThat(authorities, hasSize(15));
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
	 * authorities of "reportadmin".
	 */
	public void findAuthoritiesForDescendantReportadmin() {
		UUID uuidOfRole_aabc = UUID.fromString("f46f6b53-d70e-4044-91e2-ff749159fd90");
		List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_aabc);
		logger.info("authorities = {}", authorities);
		assertThat(authorities, is(not(nullValue())));
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			assertThat(authorities, hasSize(15));
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
