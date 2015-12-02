package com.qfree.obo.report.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.qfree.obo.report.db.RoleRepository;

public class ReportServerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(ReportServerAuthenticationProvider.class);

	//	@Autowired
	//	private HttpServletRequest httpServletRequest;

	//	@Autowired
	//	private ServletRequest servletRequest;

	@Autowired
	RoleRepository roleRepository;

	public ReportServerAuthenticationProvider() {
		super();
	}

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

		//	System.out.println("\nIn CustomAuthenticationProvider.authenticate(...)!");
		//	System.out.println("authentication.getDetails() = " + authentication.getDetails());
		//	System.out.println("servletRequest = " + servletRequest);
		//	System.out.println("httpServletRequest = " + httpServletRequest);
		//	System.out.println("httpServletRequest.getPathInfo() = " + httpServletRequest.getPathInfo());
		//	System.out.println("httpServletRequest.getParameterMap() = " + httpServletRequest.getParameterMap());

		final String name = authentication.getName();
		final String password = authentication.getCredentials().toString();
		logger.info("name     = {}", name);
		logger.info("password = {}", password);

		/*
		 * I may want to check for local Role entities first? But
		 */
		logger.info("roleRepository = {}", roleRepository);

		if (name.equals("ui") && password.equals("ui")) {
			//if (name.equals("admin") && password.equals("system")) {
			final List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_RESTAPI"));
			final UserDetails principal = new User(name, password, grantedAuths);
			final Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
			return auth;
		} else {
			return null;
		}
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
