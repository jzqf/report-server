package com.qfree.obo.report.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * A filter that delegates to a specified filter if the URL matches a specified
 * pattern.
 * 
 * <p>
 * If the URL does <i>not</i> match the specified pattern, then the filter is
 * bypassed.
 * 
 * @author Jeffrey Zelt
 *
 */
public class DelegateRequestMatchingFilter implements Filter {

	/**
	 * The filter to apply if the URL matches the specified pattern.
	 */
	private Filter delegateFilter;

	/**
	 * Specifies the pattern that the URL must match in order that the delegate
	 * filter be applied.
	 * 
	 * This can be a complex condition that logically OR's several simpler
	 * conditions.
	 */
	private RequestMatcher requestMatcher;

	public DelegateRequestMatchingFilter(RequestMatcher requestMatcher, Filter delegate) {
		this.requestMatcher = requestMatcher;
		this.delegateFilter = delegate;
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		if (requestMatcher.matches(request)) {
			/*
			 * Apply the specified filter.
			 */
			delegateFilter.doFilter(req, resp, chain);
		} else {
			/*
			 * Bypass the specified filter and send the request to the next 
			 * filter in the chain.
			 */
			chain.doFilter(req, resp);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing.	
	}

	@Override
	public void destroy() {
		// Do nothing
	}
}