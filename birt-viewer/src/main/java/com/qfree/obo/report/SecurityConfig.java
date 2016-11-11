package com.qfree.obo.report;

import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.security.ReportServerAuthenticationProvider;
import com.qfree.obo.report.security.filter.DelegateRequestMatchingFilter;
import com.qfree.obo.report.security.filter.LogRequestsFilter;
import com.qfree.obo.report.security.filter.RoleReportFilter;

/**
 * Configure security details for this application.
 * 
 * <p>
 * Security is implemented using Spring Security tools.
 * 
 * @author Jeffrey Zelt
 *
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true) // Enables @Secured for method security, but does not seem to work
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enables use of @PreAuthorize, ... to enforce method level security
@PropertySource("classpath:config.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	/*
	 * log(rounds) for initializing the BCryptPasswordEncoder.
	 */
	private static final int BCRYPT_STRENGTH = 8;

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
	}

	/*
	 * This bean provides support for Spring Security in Spring Data. In
	 * particular, it seems to expose Spring Security as SpEL expressions in 
	 * Spring Data queries.
	 */
	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	/*
	 * I registered this filter as a Spring bean here only so that it supports
	 * @Autowired DI.
	 */
	@Bean
	public RoleReportFilter roleReportFilter() {
		return new RoleReportFilter();
	}

	@Bean
	public Filter requestMatchingRoleReportFilter() {
		RoleReportFilter roleReportFilter = roleReportFilter();

		/*
		 * These patterns should *not* include the servlet context, which is:
		 * 
		 *   "/report-server"    when the application is deployed as a WAR file
		 *   
		 *   ""                  when the application is run via 
		 *                       $ mvn clean spring-boot:run
		 * 
		 * The 3 patterns here match the URL mappings for BIRT servlets that 
		 * display reports.
		 */
		RequestMatcher birtServlet1 = new AntPathRequestMatcher("/frameset/**");
		RequestMatcher birtServlet2 = new AntPathRequestMatcher("/run/**");
		RequestMatcher birtServlet3 = new AntPathRequestMatcher("/preview/**");
		RequestMatcher matcher = new OrRequestMatcher(birtServlet1, birtServlet2, birtServlet3);

		return new DelegateRequestMatchingFilter(matcher, roleReportFilter);
		//return new RoleReportFilter();
	}

	/*
	 * I registered this filter as a Spring bean here only so that it supports
	 * @Autowired DI.
	 */
	@Bean
	public LogRequestsFilter logRequestsFilter() {
		return new LogRequestsFilter();
	}

	@Bean
	public AuthenticationProvider reportServerAuthenticationProvider() {
		return new ReportServerAuthenticationProvider();
	}

	/*
	 * Without this FilterRegistrationBean beans, the filter registered here 
	 * run 3 times for each request. This may be do to Spring Boot automatically 
	 * registering Filter classes as filters for Filter classes that are 
	 * registered as Spring beans. However, I want this filters to run only
	 * once per request, and this is configured below in the overridden Spring
	 * Security configure(HttpSecurity http) method.
	 */
	@Bean
	public FilterRegistrationBean requestMatchingRoleReportFilterRegistration() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(requestMatchingRoleReportFilter());
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	/*
	 * Without this FilterRegistrationBean beans, the filters registered here 
	 * runs automatically for each request. This may be do to Spring Boot 
	 * automatically registering Filter classes as filters for Filter classes 
	 * that are registered as Spring beans. I registered this filter as a 
	 * Spring bean above only so that it would support @Autowired DI. This 
	 * filter is used by requestMatchingRoleReportFilter(); I do not want this
	 * filter to be inserted in any filter chain.
	 */
	@Bean
	public FilterRegistrationBean roleReportFilterRegistration() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(roleReportFilter());
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	/*
	 * Without this FilterRegistrationBean beans, the filter registered here 
	 * run 3 times for each request. This may be do to Spring Boot automatically 
	 * registering Filter classes as filters for Filter classes that are 
	 * registered as Spring beans. However, I want this filters to run only
	 * once per request, and this is configured below in the overridden Spring
	 * Security configure(HttpSecurity http) method.
	 */
	@Bean
	public FilterRegistrationBean LogRequestsFilterRegistration() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(logRequestsFilter());
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	/**
	 * This bean is used to create an LdapTemplate bean.
	 * 
	 * @return
	 */
	@Bean
	public LdapContextSource contextSource() {
		LdapContextSource contextSource = new LdapContextSource();
		//		contextSource.setUrl(env.getRequiredProperty("ldap.url"));
		//		contextSource.setBase(env.getRequiredProperty("ldap.base"));
		//		contextSource.setUserDn(env.getRequiredProperty("ldap.user"));
		//		contextSource.setPassword(env.getRequiredProperty("ldap.password"));
		/*
		 * For testing purposes,this needs to be updated to the IP number of
		 * my VirtualBox VM named "debian" that is configured with an OpenLDAP
		 * server. To get this IP number:
		 * 
		 * 	1. Start the VM.
		 * 	2. Log in with user="root", password="qfreerd".
		 * 	3. Run "ifconfig". Use the IP number for the "eth0" interface.
		 */
		contextSource.setUrl("ldap://192.168.6.79:389");
		contextSource.setBase("dc=qmcs,dc=local");
		contextSource.setUserDn("cn=admin,dc=qmcs,dc=local");
		contextSource.setPassword("qfreeLDAP");
		//contextSource.afterPropertiesSet(); //??????????????????????????????? TRY WITHOUT! ?????????????????????????????
		return contextSource;
	}

	/**
	 * This bean is used to authenticate an HTTP connection against an LDAP
	 * server (which might be a proxy to another service such as Active
	 * Directory).
	 * <p>
	 * This authentication, if configured to be used, is done by a
	 * {@link ReportServerAuthenticationProvider} bean.
	 * 
	 * @return
	 */
	@Bean
	public LdapTemplate ldapTemplate() {
		return new LdapTemplate(contextSource());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(reportServerAuthenticationProvider());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		/*
		 * This is so that media asset files (images, CSS files, JavaScript files,
		 * etc.) are ignored by Spring Security. This means that Spring Security
		 * will not require that a Basic Authentication header is provided, and if
		 * an Authentication header *is* provided, it will be ignored.
		 */
		web.ignoring().antMatchers("/webcontent/birt/**");
		/*
		 * This does not seem to have any effect because the HTTP requests that
		 * are generated to request the asset files from this directory tree are
		 * somehow rewritten by BIRT so that the files in this directory tree
		 * are requested using URLs that do not include "/webcontent/qfree/". It
		 * may be that BIRT performs some sort of server-side processing before 
		 * report file is sent to the client so that these files can be requested
		 * by URLs that do not include ""/webcontent/qfree/". In fact, it seems
		 * that images are requested via query parameters, e.g., something like:
		 * http://localhost:8081/report-server/preview?__imageid=file6a7b9263158434ac2872.png
		 * This can be seen by detailed logging in RoleReportFilter.
		 */
		//web.ignoring().antMatchers("/webcontent/qfree/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Check whether security is enabled.
		 */
		if (env.getProperty("appsecurity.enable").equals("true")) {

			http
					/*
					 * By inserting requestMatchingRoleReportFilter() *after*
					 * FilterSecurityInterceptor.class, this ensures that my
					 * RoleReportFilter will have access to the the name of the
					 * authenticated security principal which RoleReportFilter
					 * can use to look up the authorities granted to the user
					 * who made the request as well as determine if that user 
					 * should have access to the report requested. This is 
					 * because authentication (provided by my 
					 * ReportServerAuthenticationProvider somehow via 
					 * FilterSecurityInterceptor, I believe) will be performed 
					 * *before* the request is passed to RoleReportFilter.
					 */
					.addFilterAfter(requestMatchingRoleReportFilter(), FilterSecurityInterceptor.class)
					/*
					 * Use this filter to log all request URIs. This may be 
					 * necessary to determine why an HTTP request fails. Make
					 * sure the configuration below does not exclude a URI that
					 * needs to be processed.
					 */
					//.addFilterAfter(logRequestsFilter(), SecurityContextPersistenceFilter.class)

					.authorizeRequests()

					.antMatchers("/upload_report.html")
					//.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS
					//		+ "') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
					//         .access("isAuthenticated() or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")

					.antMatchers("/upload_asset.html")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")

					.antMatchers("/upload_asset_2.html")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")

					.antMatchers("/upload_document.html")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")

					.antMatchers("/RequestHeaders")
					//.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS
					//		+ "') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
					//         .access("isAuthenticated() or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")

					/*
					 * Report server ReST API:
					 * 
					 * Granted authorities that are specified here, e.g., 
					 * "USE_RESTAPI"are checked for FIRST by Spring Security, 
					 * and then only if that check *succeeds* are any 
					 * authorities that are specified via a @PreAuthorize
					 * annotation on the controller method checked for. 
					 * 
					 * This ordering influences the type of error message 
					 * returned for a failed authorization. If a failure occurs
					 * because the authenticated role does not have the 
					 * authority(ies) specified here by ".access(...)", then an
					 * HTML error page is returned. However, if this check 
					 * succeeds, but the condition specified by the 
					 * @PreAuthorize annotation on the controller methid fails,
					 * then a JSON object is returned that describes the 
					 * authorization failure.
					 */
					//.antMatchers("/rest/**").authenticated()
					.antMatchers("/rest/**").access("hasAuthority('" + Authority.AUTHORITY_NAME_USE_RESTAPI + "')")

					/*
					 * This pattern matches the URLs used by Q-Free-authored
					 * reports to request media/asset files (CSS files, 
					 * JavaScript files, images, ...).
					 */
					.antMatchers("/webcontent/qfree/**").authenticated()

					/*
					 * These 3 patterns match the URL mappings for BIRT 
					 * "viewservlets" that display reports.
					 */
					.antMatchers("/frameset/**").authenticated()
					.antMatchers("/run/**").authenticated()
					.antMatchers("/preview/**").authenticated()

					/*
					 * This pattern matches the URLs used by the BIRT 
					 * "viewservlets" to request media/asset files (CSS files, 
					 * JavaScript files, images, ...).
					 */
					/*
					 * permitAll() does not disable any security filters. Testing
					 * shows that the "authenticate" method of my custom 
					 * AuthenticationProvider that is registered above, 
					 * "ReportServerAuthenticationProvider" is still executed
					 * for each request. This could lead to a large number of
					 * external authentication provider requests, which could
					 * slow things down. To avoid this behaviour, I tell Spring 
					 * Security to completely ignore this Ant pattern in
					 * configure(WebSecurity web) above. 
					 * 
					 * If I ever choose to have these requests authenticated,
					 * then I must both:
					 * 
					 *   1. Change ".permitAll()" -> .authenticated() here, and:
					 *   
					 *   2. Remove or comment ou the following line from 
					 *      configure(WebSecurity web) above:
					 *      
					 *        web.ignoring().antMatchers("/webcontent/birt/**")
					 */
					.antMatchers("/webcontent/birt/**").permitAll() // to speed things up
					//.antMatchers("/webcontent/birt/**").authenticated()

					//					/*
					//					 * This covers, in particular, the home page which exposes
					//					 * the test reports and some links.
					//					 */
					//					.antMatchers("/**")
					//					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")

					/*
					 * All other URLs.
					 * 
					 * This covers, in particular, the home page which exposes
					 * the test reports and some links.
					 */
					.anyRequest().access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
					//.anyRequest().denyAll()
					//.anyRequest().authenticated()
					//.anyRequest().permitAll()

					.and().httpBasic().realmName("Q-Free Report Server")

					/*
					 * This tells Spring Security to *NOT* create a session, 
					 * i.e., it will never set a cookie named JSESSIONID with a
					 * UUID value such as A7E9BDD3E80D5FC534B9B6F49F3B7125. 
					 * Since no session is ever created, Spring Security will 
					 * expect authentication credentials in each request and it
					 * will check these credentials against its user store for
					 * each request.
					 */
					.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

			/*
			 * Enforce channel security.
			 */
			if (env.getProperty("appsecurity.require.https").equals("true")) {
				http
						.requiresChannel()
						.antMatchers("/upload_report.html").requiresSecure()
						.antMatchers("/rest/**").requiresSecure()
						.anyRequest().requiresSecure();
			} else {
				http
						.requiresChannel()
						.antMatchers("/upload_report.html").requiresInsecure()
						.antMatchers("/rest/**").requiresInsecure()
						.anyRequest().requiresInsecure();
			}

			/*
			 * Spring Security enables CSRF by default, and it expects a
			 * CSRF token for any state-changing request (this includes
			 * most requests that do *not* use the HTTP methods GET, 
			 * HEAD, OPTIONS or TRACE). If such requests do not carry a
			 * CSRF token, the request will fail with a CsrfException.
			 */
			http.csrf().disable();

			/*
			 * Prevents the "X-Frame-Options" header from being added to the 
			 * response. This is to allow the BIRT reports to be displayed in
			 * an iFrame.
			 */
			http.headers().frameOptions().disable();

		} else {

			/*
			 * Turn off security. 
			 */
			http //.headers().frameOptions().disable().and()
					.authorizeRequests()
					//	.anyRequest().authenticated()
					//	.and().httpBasic().realmName("Q-Free Report Server")
					.anyRequest().permitAll()
					/*
					 * We need to assign these authorities to the "anonymous"
					 * user in order to satisfy method-level security 
					 * constraints imposed with @PreAuthorize.
					 */
					.and().anonymous().authorities(
							"USE_RESTAPI",
							"MANAGE_ASSETS",
							"MANAGE_ASSETTREES",
							"MANAGE_ASSETTYPES",
							"MANAGE_AUTHORITIES",
							"MANAGE_CATEGORIES",
							"MANAGE_DOCUMENTS",
							"MANAGE_FILEFORMATS",
							"MANAGE_FILESYNCING",
							"MANAGE_JOBPROCESSOR",
							"MANAGE_JOBS",
							"DELETE_JOBS",
							"MANAGE_JOBSTATUSES",
							"MANAGE_PREFERENCES",
							"MANAGE_REPORTS",
							"UPLOAD_REPORTS",
							"MANAGE_ROLES",
							"MANAGE_SUBSCRIPTIONS",
							"DELETE_SUBSCRIPTIONS")
					.and().headers().frameOptions().disable()
					.and().csrf().disable();
		}
	}

}
