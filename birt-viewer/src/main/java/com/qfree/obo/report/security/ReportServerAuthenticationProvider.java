package com.qfree.obo.report.security;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qfree.obo.report.db.AuthorityRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.service.ConfigurationService;

@PropertySource("classpath:config.properties")
public class ReportServerAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(ReportServerAuthenticationProvider.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation.
	 */
	@Autowired
	private Environment env;

	//	@Autowired
	//	private HttpServletRequest httpServletRequest;

	//	@Autowired
	//	private ServletRequest servletRequest;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	//private AuthorityService authorityService;
	private AuthorityRepository authorityRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

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

		final String username = authentication.getName();
		final String password = authentication.getCredentials().toString();
		logger.info("username = {}, password = {}", username, password);

		if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {

			boolean authenticated = false;

			/*
			 * The special user QFREE_ADMIN_ROLE_NAME is always authenticated
			 * locally.
			 */
			if (!username.equals(Role.QFREE_ADMIN_ROLE_NAME)) {

				String authenticationProviderUrl = configurationService.get(ParamName.AUTHENTICATION_PROVIDER_URL,
						null, String.class);
				//String authenticationProviderUrl = "http://www.apple.com";
				if (authenticationProviderUrl != null && !authenticationProviderUrl.isEmpty()) {

					HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(username,
							password);

					Client client = ClientBuilder.newBuilder()
							.register(basicAuthenticationFeature)
							.build();
					WebTarget webTarget = client.target(authenticationProviderUrl);

					final long startTime = System.currentTimeMillis();
					Response response = webTarget
							//.path("test/nop")
							.request()
							//.header("Accept", MediaType.TEXT_PLAIN);
							//.header("Accept", MediaType.TEXT_HTML);
							//.get();
							.head();
					final long endTime = System.currentTimeMillis();
					logger.info("External authentication request used {} ms", endTime - startTime);

					logger.info("response.getStatus() = {}", response.getStatus());
					//	String resource = response.readEntity(String.class);
					//	System.out.println("resource = " + resource);
					if (response.getStatus() == Response.Status.OK.getStatusCode()) {
						logger.info("Request PASSED authentication by: {}", authenticationProviderUrl);
						authenticated = true;
					} else {
						logger.warn("Request FAILED authentication by: {}", authenticationProviderUrl);
					}

				} else {
					logger.info("External authentication provider not configured.");
				}
			}
			authenticated = true;
			Role role = roleRepository.findByUsername(username);
			if (authenticated && role == null && env.getProperty("appsecurity.auto.create.roles").equals("true")) {
				/*
				 * If all of the following are true:
				 * 
				 *    1. A user is successfully authenticated by an external
				 *       authentication provider,
				 *       
				 *    2. There is no corresponding Role in the report server 
				 *       database for the authenticated user,
				 *    
				 *    3. The configuration parameter
				 *       "appsecurity.auto.create.roles" is equal to "true",
				 * 
				 * then a Role entity is *automatically* created here for the 
				 * user.
				 * 
				 * If, however, "appsecurity.auto.create.roles" is "false", then
				 * a Role will need to be created by another means before this
				 * user can successfully be authenticated here because a Role is
				 * needed for *authorization* in the report server application.
				 */
				role = new Role(username);
				role = roleRepository.save(role);
			}

			/*
			 * Whether or not the connection is authenticated by an external 
			 * authentication provider, there must be a Role in the report
			 * server database matching the principal (username) from the 
			 * authentication header. This Role will be used for *authorization"
			 * in this application. If the connection is *not* authenticated
			 * externally, we also use the Role for authentication (which means 
			 * that the hashed password from the Role must match the password
			 * from the authentication header).
			 */
			if (role != null) {

				if (!authenticated) {
					if (role.getEncodedPassword() != null && !role.getEncodedPassword().isEmpty()) {
						/*
						 * The connection was not authenticated externally. Either
						 * an external authentication provider is not configured or
						 * authentication by the external provider failed. For both
						 * cases we then try to authenticate locally against the 
						 * Role we located. This means that if the password stored 
						 * with the Role is *different* than that stored by the 
						 * external authenticator, and if the user specifies the
						 * password stored with the Role, then authentication by the
						 * external provider will first fail, but then 
						 * authentication will succeed during this local 
						 * authentication check. This is a feature, not a bug.
						 */
						if (passwordEncoder.matches(password, role.getEncodedPassword())) {
							authenticated = true;
							logger.info("Local authentcation PASSED. password = {}, role = {}", password, role);
						} else {
							logger.warn("Local authentcation FAILED. password = {}, role = {}", password, role);
						}
					} else {
						logger.warn("Local authentication is not possible. There is no password for role = {}", role);
					}
				}

				if (authenticated) {
					/*
					 * Although the connection has been authenticated, we
					 * still need to check the Role to see if it is both
					 * "active" and "enabled".
					 */
					if (role.getEnabled() && role.getActive()) {

						//List<Authority> authorities = authorityService.getActiveAuthoritiesByRoleId(role.getRoleId());
						List<String> authorities = authorityRepository
								.findActiveAuthorityNamesByRoleIdRecursive(role.getRoleId().toString());
						logger.info("authorities = {}", authorities);

						final List<GrantedAuthority> grantedAuths = new ArrayList<>(authorities.size());
						for (String authority : authorities) {
							grantedAuths.add(new SimpleGrantedAuthority(authority));
						}
						if (username.equals(Role.QFREE_ADMIN_ROLE_NAME)) {
							/*
							 * The built-in Q-Free admin role is always given
							 * this authority. It is not represented as an
							 * Authority entity because we do not want it to be
							 * given to any normal report server user roles.
							 */
							grantedAuths.add(new SimpleGrantedAuthority(Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS));
						}
						//final UserDetails principal = new User(username, password, grantedAuths);
						final UserDetails principal = new ReportServerUser(role.getRoleId(), username, password,
								grantedAuths);
						final Authentication auth = new UsernamePasswordAuthenticationToken(
								principal, password, grantedAuths);
						return auth;
					}
				}

			} else {
				logger.error("No Role for username = {}", username);
			}

		} else {
			logger.warn("The name and/or password are missing. The connection cannot be authenticated."
					+ " username = {}, password = {}", username, password);
		}
		return null;
	}

	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
