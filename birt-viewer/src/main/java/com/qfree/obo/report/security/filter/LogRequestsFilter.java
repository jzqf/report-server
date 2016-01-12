package com.qfree.obo.report.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter class ofr logging request URIs.
 * 
 * @author Jeffrey Zelt
 *
 */
public class LogRequestsFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(LogRequestsFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		//logger.info("request = {}", request);
		//logger.info("request.getRequestURL() = {}", request.getRequestURL());
		logger.info("request.getRequestURI() = {}", request.getRequestURI());
		//logger.info("request.getContextPath() = {}", request.getContextPath());
		//logger.info("request.getServletPath() = {}", request.getServletPath());
		//logger.info("request.getPathInfo() = {}", request.getPathInfo());// <--- ****************
		//	for (Map.Entry<String, String[]> mapEntry : request.getParameterMap().entrySet()) {
		//		logger.info("    ParameterMap<{}> = {}", mapEntry.getKey(), mapEntry.getValue());
		//	}
		//Map<String, String[]> parameterMap = request.getParameterMap();

		chain.doFilter(req, res);
		return;
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