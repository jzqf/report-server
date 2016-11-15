package com.qfree.obo.report.security;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
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

import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.service.AuthorityService;
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
	//	private HttpServletRequest httpServletRequest;  <- injected object is not usable

	//	@Autowired
	//	private ServletRequest servletRequest;  <- injected object is not usable

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private AuthorityService authorityService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	//	@Autowired
	//	private LdapTemplate ldapTemplate;

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

			//	/*
			//	 * LDAP test:
			//	 * 
			//	 * Still to do:
			//	 * 
			//	 * 1. The "hardcoded" part of the LDAP filter must be replaced by
			//	 *    a configuration parameter in config.properties or by a 
			//	 *    Configuration entity. In the test code below, this is the
			//	 *    string USER_BASE_FILTER. In practice, this may be more 
			//	 *    complex, e.g., if two attributes must be specified, it will 
			//	 *    look something like:
			//	 *    
			//	 *    	"(&(objectclass=person)(someattr=somevalue))".
			//	 *    
			//	 *    See the Javadoc for HardcodedFilter if you need a more 
			//	 *    complex base filter.
			//	 * 
			//	 * 2. The LDAP attribute that represents the username or user id 
			//	 *    that the value of "username" will be used for looking up the
			//	 *    LDAP entry for the user must be replaced by a configuration 
			//	 *    parameter in config.properties or by a Configuration entity. 
			//	 *    In the test code below, this is the string 
			//	 *    USERNAME_ATTRIBUTE.
			//	 *    
			//	 *    	filter.and(new EqualsFilter("uid", uid));
			//	 */
			//	String USER_BASE_FILTER = "(objectclass=person)";
			//	String USER_BASE_DN = "";
			//	String USERNAME_ATTRIBUTE = "uid";
			//
			//	String uid = "queeniew2";
			//	String passwordToAuthenticate = "queeniew2";
			//
			//	Filter baseUserFilter = new HardcodedFilter(USER_BASE_FILTER);
			//	AndFilter filter = new AndFilter();
			//	//filter.and(new EqualsFilter("objectclass", "person"));
			//	filter.and(baseUserFilter);
			//	filter.and(new EqualsFilter(USERNAME_ATTRIBUTE, uid));
			//	boolean authenticatedByLdap = false;
			//	try {
			//		/*
			//		 * The DN to use as the base of the search is "" here because
			//		 * the base DN is specified in the LdapContextSource bean in
			//		 * ServerConfig.
			//		 */
			//		authenticatedByLdap = ldapTemplate.authenticate(USER_BASE_DN, filter.encode(), passwordToAuthenticate);
			//		//authenticatedByLdap = ldapTemplate.authenticate("dc=qmcs,dc=local", filter.encode(), passwordToAuthenticate);
			//	} catch (Exception e) {
			//		logger.error("Exception thrown attempting to authenticate user via LDAP", e);
			//	}
			//	if (authenticatedByLdap) {
			//		logger.info("Successfully authenticated by LDAP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			//	} else {
			//		logger.info("*NOT* authenticated by LDAP :-( :-( :-( :-( :-( :-( :-( :-( :-( :-( :-(");
			//	}

			boolean authenticated = false;

			/*
			 * The special users Role.QFREE_ADMIN_ROLE_NAME and 
			 * Role.QFREE_REST_ADMIN_ROLE_NAME are always authenticated locally.
			 */
			if (!(username.equals(Role.QFREE_ADMIN_ROLE_NAME) || username.equals(Role.QFREE_REST_ADMIN_ROLE_NAME))) {

				String authenticationProviderUrl = configurationService.get(
						ParamName.AUTHENTICATION_PROVIDER_URL, null, String.class);
				String authenticationProviderHttpMethod = configurationService.get(
						ParamName.AUTHENTICATION_PROVIDER_HTTP_METHOD, null, String.class);
				//String authenticationProviderUrl = "http://www.vg.no";
				//authenticationProviderHttpMethod = "HEAD";
				if (authenticationProviderUrl != null && !authenticationProviderUrl.isEmpty()
						&& authenticationProviderHttpMethod != null && !authenticationProviderHttpMethod.isEmpty()) {

					HttpAuthenticationFeature basicAuthenticationFeature = HttpAuthenticationFeature.basic(username,
							password);

					Client client = ClientBuilder.newBuilder()
							.register(basicAuthenticationFeature)
							.build();
					WebTarget webTarget = client.target(authenticationProviderUrl);

					final long startTime = System.currentTimeMillis();
					//Response response = webTarget
					//		//.path("test/nop")
					//		.request()
					//		//.header("Accept", MediaType.TEXT_PLAIN);
					//		//.header("Accept", MediaType.TEXT_HTML);
					//		//.get();
					//		.head();
					Invocation.Builder invocationBuilder = webTarget
							//.path("test/nop")
							.request();
					//.header("Accept", MediaType.TEXT_PLAIN);
					//.header("Accept", MediaType.TEXT_HTML);

					Response response = null;
					switch (authenticationProviderHttpMethod) {
					case HttpMethod.GET:
						response = invocationBuilder.get();
						break;
					case HttpMethod.HEAD:
						response = invocationBuilder.head();
						break;
					case HttpMethod.OPTIONS:
						response = invocationBuilder.options();
						break;
					/*
					 * We do not allow "POST" or "PUT" because an entity 
					 * must specified that contains the POST or PUT data.
					 */
					//	case HttpMethod.POST:
					//		response = invocationBuilder.post();
					//		break;
					//	case HttpMethod.PUT:
					//		response = invocationBuilder.put();
					//		break;
					default:
						logger.warn("HTTP method {} not supported for external authentication",
								authenticationProviderHttpMethod);
						response = null;
					}
					final long endTime = System.currentTimeMillis();
					logger.info("External authentication request used {} ms", endTime - startTime);

					if (response != null) {
						logger.info("response.getStatus() = {}", response.getStatus());
						//	String resource = response.readEntity(String.class);
						//	System.out.println("resource = " + resource);
						if (response.getStatus() == Response.Status.OK.getStatusCode()) {
							logger.info("Request PASSED authentication by: ({}, {})",
									authenticationProviderHttpMethod, authenticationProviderUrl);
							authenticated = true;
						} else {
							logger.warn("Request FAILED authentication by: ({}, {})",
									authenticationProviderHttpMethod, authenticationProviderUrl);
						}
					} else {
						logger.warn("Request FAILED authentication by: ({}, {})",
								authenticationProviderHttpMethod, authenticationProviderUrl);
					}

				} else {
					logger.info("External authentication provider not configured.");
				}
			}

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
				/*
				 * Although a Role has been matched with the supplied username,
				 * it is still necessary to check that the Role is "active", 
				 * "enabled" and that it is a "login" role.
				 */
				if (role.getEnabled() && role.getActive() && role.isLoginRole()) {

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
							logger.warn("Local authentication is not possible. There is no password for role = {}",
									role);
						}
					}

					if (authenticated) {
						/*
						 * The final step is to locate all Authority entities
						 * to grant the user and then return a 
						 * UsernamePasswordAuthenticationToken that contains
						 * all security data associated with the authenticated
						 * principal.
						 */

						List<String> authorities = null;
						if (username.equals(Role.QFREE_ADMIN_ROLE_NAME)) {
							/*
							 * Authorities are granted to the QFREE_ADMIN_ROLE_NAME
							 * role regardless of whether they are active or not.
							 * This is because the "active" setting for an Authority
							 * will be managed by some sort of administrator to 
							 * control the operations available to normal report 
							 * server users. The QFREE_ADMIN_ROLE_NAME role is a 
							 * special role for Q-Free use only - we don't want this
							 * role's capabilities to be altered by normal report
							 * server administrative operations.
							 */
							Boolean activeAuthoritiesOnly = false;
							authorities = authorityService.findAuthorityNamesByRoleId(role.getRoleId(), activeAuthoritiesOnly);
						} else {
							/*
							 * Note that only names of *active* Authority entities 
							 * are returned here. This means that *inactive* 
							 * Authority entities that are linked to a Role will 
							 * not be made available to the Role here.
							 */
							Boolean activeAuthoritiesOnly = true;
							authorities = authorityService.findAuthorityNamesByRoleId(role.getRoleId(), activeAuthoritiesOnly);
						}
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
						final UserDetails principal = new ReportServerUser(
								role.getRoleId(),
								username, password,
								role.getEnabled(), role.getActive(), role.isLoginRole(),
								grantedAuths);
						final Authentication auth = new UsernamePasswordAuthenticationToken(
								principal, password, grantedAuths);
						return auth;
					}
				} else {
					if (!role.getEnabled()) {
						logger.warn("Attempt to authenticate with a disabled Role: {}", role);
					}
					if (!role.getActive()) {
						logger.warn("Attempt to authenticate with an inactive Role: {}", role);
					}
					if (!role.isLoginRole()) {
						logger.warn("Attempt to authenticate with a non-login Role: {}", role);
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
