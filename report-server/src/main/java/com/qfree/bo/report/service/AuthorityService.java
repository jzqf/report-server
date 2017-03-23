package com.qfree.bo.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.AuthorityRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.domain.UuidCustomType;

@Component
@Transactional
public class AuthorityService {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityService.class);

	private final AuthorityRepository authorityRepository;

	@Autowired
	public AuthorityService(AuthorityRepository authorityRepository) {
		this.authorityRepository = authorityRepository;
	}

	/**
	 * Returns an {@link Authority} {@link List} for all {@link Authority}
	 * entities that are linked <b>directly</b> to a {@link Role}, i.e.,
	 * {@link Role} inheritance is not taken into account.
	 * 
	 * @param roleId
	 * @return
	 */
	@Transactional
	public List<Authority> getActiveAuthoritiesByRoleIdDirect(UUID roleId) {
		Boolean activeAuthoritiesOnly=true;

		List<String> uuidStrings = authorityRepository.findAuthorityIdsByRoleId(roleId.toString(), activeAuthoritiesOnly);
		List<Authority> authorities = new ArrayList<>(uuidStrings.size());
		for (String uuidString : uuidStrings) {
			try {
				UUID authorityId = UUID.fromString(uuidString);
				Authority authority = authorityRepository.findOne(authorityId);
				if (authority != null) {
					authorities.add(authority);
				}
			} catch (IllegalArgumentException e) {
				logger.error("Illegal value for authorityId: {}", uuidString);
			}
		}
		return authorities;
	}

	/**
	 * Returns an {@link Authority} {@link List} for a {@link Role} specified
	 * by its id. If a PostgreSQL database is being used, {@link Role} 
	 * inheritance will be taken into account. If H2 is being used (for unit
	 * and integration tests), then {@link Role} inheritance will not be taken 
	 * into account.
	 * 
	 * @param roleId
	 * @return
	 */
	@Transactional
	public List<Authority> getActiveAuthoritiesByRoleId(UUID roleId) {

		/*
		 * This returns a list of Strings, each of which represents a UUID id
		 * of and Authority. 
		 */
		Boolean activeAuthoritiesOnly = true;
		List<String> uuidStrings = findAuthorityIdsByRoleId(roleId, activeAuthoritiesOnly);
		/*
		 * The rest of this method generates a list of Authority entities from
		 * the list of Authority ids that are expressed as Strings, i.e., it
		 * performs the conversion:  List<String> -> List<Authority>
		 */
		List<Authority> authorities = new ArrayList<>(uuidStrings.size());
		for (String uuidString : uuidStrings) {
			try {
				UUID authorityId = UUID.fromString(uuidString);
				Authority authority = authorityRepository.findOne(authorityId);
				if (authority != null) {
					authorities.add(authority);
				}
			} catch (IllegalArgumentException e) {
				logger.error("Illegal value for authorityId: {}", uuidString);
			}
		}
		return authorities;
	}

	public List<String> findAuthorityIdsByRoleId(UUID roleId, Boolean activeAuthoritiesOnly) {
		/*
		 * The H2 database does not support recursive CTE expressions, so it is 
		 * necessary to run different code if the database is not PostgreSQL.
		 * This only affects integration tests, because only PostreSQL is used
		 * in production. 
		 */
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			Boolean activeInheritedRolesOnly = true;
			return authorityRepository.findAuthorityIdsByRoleIdRecursive(roleId.toString(), 
					activeAuthoritiesOnly, activeInheritedRolesOnly);
		} else {
			return authorityRepository.findAuthorityIdsByRoleId(roleId.toString(), activeAuthoritiesOnly);
		}
	}

	public List<String> findAuthorityNamesByRoleId(UUID roleId, Boolean activeAuthoritiesOnly) {
		/*
		 * The H2 database does not support recursive CTE expressions, so it is 
		 * necessary to run different code if the database is not PostgreSQL.
		 * This only affects unit/integration tests, because only PostreSQL is 
		 * used in production. 
		 */
		if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {
			Boolean activeInheritedRolesOnly = true;
			return authorityRepository.findAuthorityNamesByRoleIdRecursive(roleId.toString(), 
					activeAuthoritiesOnly, activeInheritedRolesOnly);
		} else {
			return authorityRepository.findAuthorityNamesByRoleId(roleId.toString(), activeAuthoritiesOnly);
		}
	}

}
