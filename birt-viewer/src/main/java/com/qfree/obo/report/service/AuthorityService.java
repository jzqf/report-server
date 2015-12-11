package com.qfree.obo.report.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.AuthorityRepository;
import com.qfree.obo.report.domain.Authority;

@Component
@Transactional
public class AuthorityService {

	private static final Logger logger = LoggerFactory.getLogger(AuthorityService.class);

	private final AuthorityRepository authorityRepository;

	@Autowired
	public AuthorityService(AuthorityRepository authorityRepository) {
		this.authorityRepository = authorityRepository;
	}

	@Transactional
	public List<Authority> getActiveAuthoritiesByRoleId(UUID roleId) {

		List<String> uuidStrings = authorityRepository.findActiveAuthorityIdsByRoleIdRecursive(roleId.toString());
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

}
