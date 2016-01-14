package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.dto.RoleResource;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public RoleService(
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Role saveNewFromResource(RoleResource roleResource) {
		RestUtils.ifNewResourceIdNotNullThen403(roleResource.getRoleId(), Role.class,
				"roleId", roleResource.getRoleId());
		/*
		 * We do not support setting "encodedPassword" directly because we
		 * need to ensure that the hashing algorithm used by the configured
		 * "passwordEncoder" bean is used. In order to set "encodedPassword",
		 * a value must be supplied for "unencodedPassword" and then 
		 *  "encodedPassword" will be computed from it.
		 */
		roleResource.setEncodedPassword(null);
		return saveOrUpdateFromResource(roleResource);
	}

	@Transactional
	public Role saveExistingFromResource(RoleResource roleResource) {
		return saveOrUpdateFromResource(roleResource);
	}

	@Transactional
	public Role saveOrUpdateFromResource(RoleResource roleResource) {
		logger.debug("roleResource = {}", roleResource);

		Role role = new Role(roleResource);
		
		if (roleResource.getUnencodedPassword() != null && !roleResource.getUnencodedPassword().isEmpty()) {
			/*
			 * Either a password as been specified for a new Role being created
			 * or a new password has been specified to update an existing Role.
			 * Both cases are treated the same: Create a hash of the raw
			 * (unencoded) password and store it in "encodedPassword".
			 */
			role.setEncodedPassword(passwordEncoder.encode(roleResource.getUnencodedPassword()));
		}
		
		logger.debug("role = {}", role);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * Role.
		 */
		role = roleRepository.save(role);
		logger.debug("role (created/updated) = {}", role);

		return role;
	}
}
