package com.qfree.obo.report.security.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Configures Spring Security's {@link DelegatingFilterProxy} without using
 * web.xml.
 * 
 * {@link WebApplicationInitializer} implementations are detected automatically,
 * so they can be placed in any package of this application.
 * 
 * @author Jeffrey Zelt
 *
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {
}
