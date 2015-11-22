package com.qfree.obo.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
////@ComponentScan(basePackageClasses = {
////		com.qfree.obo.report.scheduling.jobs.ComponentScanPackageMarker.class,
////		com.qfree.obo.report.scheduling.schedulers.ComponentScanPackageMarker.class,
////})
@PropertySource("classpath:config.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	//public class SecurityConfig {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * or @PropertySources annotation above.
	 */
	@Autowired
	private Environment env;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {

		/*
		 * Check whether we are running unit/integration tests. If so, we 
		 * disable security.
		 */
		if (env.getProperty("app.version").equals("*test*")) {

		} else {

			auth
					.inMemoryAuthentication()
					.withUser("ui").password("ui").authorities("ROLE_RESTAPI").and()
					.withUser("admin").password("admin").authorities("ROLE_ADMIN", "ROLE_RESTAPI");
			//.withUser("ui").password("ui").roles("RESTAPI").and()
			//.withUser("admin").password("admin").roles("ADMIN", "RESTAPI");

		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Check whether we are running unit/integration tests. If so, we 
		 * disable security.
		 */
		if (env.getProperty("app.version").equals("*test*")) {

			http
					/*
					 * Turn off security. 
					 */
					.authorizeRequests().anyRequest().permitAll()

					/*
					 * CSRF must be disabled since integration tests do not include a
					 * CSRF synchronizer token. Spring Security enables CSRF by default,
					 * and it expects a CSRF token for any state-changing request (this
					 * includes most requests that do *not* use the HTTP methods GET, 
					 * HEAD, OPTIONS or TRACE). If such requests do not carry a CSRF 
					 * token, the request will fail with a CsrfException. I think that
					 * this results in an HTTP status 403 for state-changing JAX-RS 
					 * client requests to the ReST controllers via Spring Boot's
					 * embedded Tomcat server. See:
					 * 
					 * http://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
					 */
					.and().csrf().disable();

		} else {

			http
					.authorizeRequests()

					.antMatchers("/upload_report.html")
					//					.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					//.access("isAuthenticated() or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					//.access("isAuthenticated() or permitAll")
					.authenticated()

					//.antMatchers("/RequestHeaders")
					//.access("hasRole('ROLE_ADMIN') or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")

					/*
					 * Report server ReST API:
					 */
					.antMatchers("/rest/**").access("hasRole('ROLE_RESTAPI')")
					//.antMatchers("/rest/**").authenticated()
					//.antMatchers("/rest/**").permitAll()

					//	.antMatchers(HttpMethod.POST, "/xxxxxx").authenticated()

					/*
					 * All other URLs:
					 */
					.anyRequest().permitAll()
					//.anyRequest().authenticated()

					/*
					 * Enforce channel security.
					 */
					.and().requiresChannel()
					.antMatchers("/upload_report.html").requiresInsecure()
					.antMatchers("/rest/**").requiresInsecure()
					.anyRequest().requiresInsecure()

					/*
					 * If the user is not authenticated, this tells Spring 
					 * Security to display a very simple log-in form where the
					 * user can specify a user name and password.
					 */
					.and().formLogin()

					.and().httpBasic().realmName("Q-Free Report Server")

					.and().csrf().disable();
		}
	}

}
