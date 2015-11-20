package com.qfree.obo.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
	protected void configure(HttpSecurity http) throws Exception {

		/*
		 * Check whether we are running unit/integration tests. If so, we 
		 * disable security.
		 */
		if (env.getProperty("app.version").equals("*test*") || true) {

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
					//	.antMatchers("/spitters/me").authenticated()
					//	.antMatchers(HttpMethod.POST, "/spittles").authenticated()
					.anyRequest().authenticated();

		}
	}

}
