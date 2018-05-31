package com.qfree.bo.report.apps;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.db.AuthorityRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.service.AuthorityService;

public class TestRoleAuthorities {

	private static final Logger logger = LoggerFactory.getLogger(TestRoleAuthorities.class);

	//@Transactional
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

		logger.info("\n\n\n");

		AuthorityRepository authorityRepository = context.getBean(AuthorityRepository.class);
		AuthorityService authorityService = context.getBean(AuthorityService.class);

//UUID uuidOfAuthority_DELETE_JOBS = UUID.fromString("ace1edd3-6a5b-4b40-a802-79616472b893");
//Authority authority_DELETE_JOBS = authorityRepository.findOne(uuidOfAuthority_DELETE_JOBS);
//logger.info("authority_DELETE_JOBS = {}", authority_DELETE_JOBS);

		Boolean activeAuthoritiesOnly = true;
		Boolean activeInheritedRolesOnly = true;
		
		List<String> uuidStrings_ADMIN_ROLE = authorityRepository
				.findAuthorityIdsByRoleId(Role.ADMIN_ROLE_ID.toString(), activeAuthoritiesOnly);
		logger.info("----------");
		logger.info("uuidStrings_ADMIN_ROLE.size() = {}", uuidStrings_ADMIN_ROLE.size());
		logger.info("uuidStrings_ADMIN_ROLE = {}",        uuidStrings_ADMIN_ROLE);

		/*
		 * "user1" is not linked to the "reportadmin" Role, but I have set it
		 * to be currently a child of "user4".
		 */
		UUID uuidOfRole_user1 = UUID.fromString("29fe8a1f-7826-4df0-8bfd-151b54198655");
		List<String> uuidStrings_user1 = authorityRepository
				.findAuthorityIdsByRoleId(uuidOfRole_user1.toString(), activeAuthoritiesOnly);
		logger.info("----------");
		logger.info("----------");
		logger.info("---------- user 1 is a child of user4, which is a child of reportadmin:");
		logger.info("uuidStrings_user1.size() = {}", uuidStrings_user1.size());
		//logger.info("uuidStrings_user1 = {}",        uuidStrings_user1);
		for (String u : uuidStrings_user1) {
			Authority authority = authorityRepository.findOne(UUID.fromString(u));
			logger.info("Authority (direct) for user1: {}, {}", authority.getAuthorityId(), authority.getName());
		}
		
		List<String> uuidStrings_user1_recursive = authorityRepository
				.findAuthorityIdsByRoleIdRecursive(uuidOfRole_user1.toString(), activeAuthoritiesOnly, activeInheritedRolesOnly);
		logger.info("----------");
		logger.info("uuidStrings_user1_recursive.size() = {}", uuidStrings_user1_recursive.size());
		//logger.info("uuidStrings_user1_recursive = {}",        uuidStrings_user1_recursive);
		for (String uuid : uuidStrings_user1_recursive) {
			Authority authority = authorityRepository.findOne(UUID.fromString(uuid));
			logger.info("Authority (recursive) for user1: {}, {}", authority.getAuthorityId(), authority.getName());
		}

		/*
		 * "user4" *IS* linked to the "reportadmin" Role.
		 */
		UUID uuidOfRole_user4 = UUID.fromString("46e477dc-085f-4714-a24f-742428579fcc");
		List<String> uuidStrings_user4 = authorityRepository
				.findAuthorityIdsByRoleId(uuidOfRole_user4.toString(), activeAuthoritiesOnly);
		logger.info("----------");
		logger.info("----------");
		logger.info("---------- user4 is a child of reportadmin:");
		logger.info("uuidStrings_user4.size() = {}", uuidStrings_user4.size());
		//logger.info("uuidStrings_user4 = {}",        uuidStrings_user4);
		for (String u : uuidStrings_user4) {
			Authority authority = authorityRepository.findOne(UUID.fromString(u));
			logger.info("Authority (direct) for user4: {}, {}", authority.getAuthorityId(), authority.getName());
		}

		List<String> uuidStrings_user4_recursive = authorityRepository
				.findAuthorityIdsByRoleIdRecursive(uuidOfRole_user4.toString(), activeAuthoritiesOnly, activeInheritedRolesOnly);
		logger.info("----------");
		logger.info("uuidStrings_user4_recursive.size() = {}", uuidStrings_user4_recursive.size());
		//logger.info("uuidStrings_user4_recursive = {}",        uuidStrings_user4_recursive);
		for (String u : uuidStrings_user4_recursive) {
			Authority authority = authorityRepository.findOne(UUID.fromString(u));
			logger.info("Authority (recursive) for user4: {}, {}", authority.getAuthorityId(), authority.getName());
		}

		List<Authority> authorities_reportadmin = authorityService.getActiveAuthoritiesByRoleId(Role.ADMIN_ROLE_ID);
		logger.info("----------");
		logger.info("authorities_reportadmin.size() = {}", authorities_reportadmin.size());
		logger.info("authorities_reportadmin = {}",        authorities_reportadmin);

		List<Authority> authorities_user1 = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_user1);
		logger.info("----------");
		logger.info("authorities_user1.size() = {}", authorities_user1.size());
		logger.info("authorities_user1 = {}",        authorities_user1);

		List<Authority> authorities_user4 = authorityService.getActiveAuthoritiesByRoleId(uuidOfRole_user4);
		logger.info("----------");
		logger.info("authorities_user4.size() = {}", authorities_user4.size());
		logger.info("authorities_user4 = {}",        authorities_user4);

		logger.info("\n\n\n");

		context.close();
	}

}