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
					.withUser("ui").password("ui").roles("UI").and()
					.withUser("admin").password("admin").roles("UI", "ADMIN");

		}
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Check whether we are running unit/integration tests. If so, we 
		 * disable security.
		 */
		if (env.getProperty("app.version").equals("*test*")) {

			/*
			 * Turn off security. 
			 */
			http.authorizeRequests().anyRequest().permitAll();

			/*
			 * I am not sure why CSRF must be disabled, but if it is not 
			 * disabled here, HTTP status 403 is returned for JAX-RS client 
			 * requests to the ReST controllers via Spring Boot's embedded 
			 * Tomcat server. See:
			 * 
			 * http://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
			 */
			http.csrf().disable();

		} else {

			http
					.authorizeRequests()

					.antMatchers("/upload_report.html")
					.access("isAuthenticated() or hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
					//.access("isAuthenticated() or permitAll")
					//.antMatchers("/upload_report.html").authenticated()

					.antMatchers("/rest/**").authenticated() // report server ReST API

					//	.antMatchers(HttpMethod.POST, "/xxxxxx").authenticated()

					.anyRequest().permitAll()
					//.anyRequest().authenticated()

					.and()
					.formLogin().and()
					.httpBasic();
		}
	}

}
