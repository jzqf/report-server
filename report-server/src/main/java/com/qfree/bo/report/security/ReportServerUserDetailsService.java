package com.qfree.obo.report.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.RoleRepository;

@Component
public class ReportServerUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(ReportServerUserDetailsService.class);

	private final RoleRepository roleRepository;

	@Autowired
	public ReportServerUserDetailsService(
			RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String ssoId) throws UsernameNotFoundException {

		/*
		 * Should this method be able to authenticate against the OBO somehow?
		 * No, because we do not have access here to the password that was 
		 * entered!
		 * 
		 * In order to authenticate against the OBO:
		 * 
		 *   1. The object returned from this method must contain the hashed
		 *      password for the user from the OBO's security user store.
		 *   
		 *   2. The password must have been hashed using the same hashing 
		 *      algorithm as was configured for this application. This is 
		 *      unacceptable, so we must conclude that we cannot authenticate
		 *      against the OBO from here.
		 * 
		 * We would need to retrieve the hashed password for the "ssoId" 
		 * value passed to this method. Is this sensible? Perhaps this can be an
		 * option, but in order to do this we need a way of enabling/disabling
		 * this option ad this application must be able to authenticate itself
		 * to the OBO. Perhaps this can be done by:
		 * 
		 *   1. Specifying somewhere the a GET URI to receive the hashed
		 *      password in the response (possibly Base-64 encoded).
		 *   
		 *   2. Specifying somewhere the user name to use for Basic 
		 *      Authentication.
		 *   
		 *   3. Specifying somewhere the password to use for Basic 
		 *      Authentication.
		 * 
		 * Could these values be set in config.properties? No, because we may
		 * want to set them to different values for different customers.
		 * Therefore, these values should be set using global Configuration
		 * entities. I need to load them using a Spring bean which is injected
		 * into this class. These values can be obtained from this bean using
		 * getters. The first time the getters are used, we must use
		 * Jersey client code to retrieve the appropriate values and cache them.
		 * Subsequent calls to the getters can then just return the cached 
		 * values.
		 * this appropriate 
		 */

		return null;
	}

}
