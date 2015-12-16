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
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.qfree.obo.report.db.RoleRepository;
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

	@Autowired
	private RoleRepository roleRepository;

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

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(reportServerAuthenticationProvider());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webcontent/birt/**");
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Check whether security is enabled.
		 */
		if (env.getProperty("appsecurity.enable").equals("true")) {

			http
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

					.antMatchers("/RequestHeaders")
					//.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS
					//		+ "') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					.access("hasAuthority('" + Authority.AUTHORITY_NAME_RUN_DIAGNOSTICS + "')")
					//         .access("isAuthenticated() or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")

					/*
					 * Report server ReST API:
					 */
					.antMatchers("/rest/**").authenticated()

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
					 * permitAll() doesnot disable any security filters. Testing
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

					/*
					 * All other URLs:
					 */
					.anyRequest().denyAll()
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

		} else {

			/*
			 * Turn off security. 
			 */
			http
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
							"MANAGE_AUTHORITIES",
							"MANAGE_FILEFORMATS",
							"MANAGE_FILESYNCING",
							"MANAGE_CATEGORIES",
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
					.and().csrf().disable();
		}
	}

}
