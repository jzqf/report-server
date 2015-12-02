package com.qfree.obo.report.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

// TODO Use "extends GenericFilterBean" here? or a Spring Security filter?
public class RoleReportFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(RoleReportFilter.class);

	@Override
	public void destroy() {
		// Do nothing
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		logger.info("In doFilter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		HttpServletRequest request = (HttpServletRequest) req;
		logger.info("request = {}", request);
		logger.info("request.getRequestURI() = {}", request.getRequestURI());
		logger.info("request.getRequestURL() = {}", request.getRequestURL());
		logger.info("request.getServletPath() = {}", request.getServletPath());
		logger.info("request.getContextPath() = {}", request.getContextPath());
		logger.info("request.getPathInfo() = {}", request.getPathInfo());// <--- ****************
		//logger.info("request.getParameterMap() = {}", request.getParameterMap());
		for (Map.Entry<String, String[]> mapEntry : request.getParameterMap().entrySet()) {
			logger.info("    ParameterMap<{}> = {}", mapEntry.getKey(), mapEntry.getValue());
		}

		//	Enumeration<String> attributeNames = request.getAttributeNames();
		//	while (attributeNames.hasMoreElements()) {
		//		String attrName = attributeNames.nextElement();
		//		Object attrValue = request.getAttribute(attrName);
		//		logger.info("    (attrName, attrValue) = ({}, {})", attrName, attrValue);
		//	}
		//	if (request.getAttribute(
		//			"org.springframework.web.context.request.RequestContextListener.REQUEST_ATTRIBUTES") instanceof ServletRequestAttributes) {
		//		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) request
		//				.getAttribute("org.springframework.web.context.request.RequestContextListener.REQUEST_ATTRIBUTES");
		//		HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
		//		logger.info("httpServletRequest.getRequestURI() = {}", httpServletRequest.getRequestURI());
		//		logger.info("httpServletRequest.getRequestURL() = {}", httpServletRequest.getRequestURL());
		//		logger.info("httpServletRequest.getServletPath() = {}", httpServletRequest.getServletPath());
		//		logger.info("httpServletRequest.getContextPath() = {}", httpServletRequest.getContextPath());
		//		logger.info("httpServletRequest.getPathInfo() = {}", httpServletRequest.getPathInfo());
		//	} else {
		//		logger.info("************** CRAP! ***************");
		//	}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.info("authentication = {}", authentication);
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			Object principal = authentication.getPrincipal();
			logger.info("principal.getClass().getName() = {}", principal.getClass().getName());
			logger.info("principal = {}", principal);
			logger.info("principal.toString() = {}", principal.toString());
			if (principal instanceof User) {
				User user = (User) principal;
				logger.info("user = {}", user);
				logger.info("user.getUsername()    = {}", user.getUsername());
				logger.info("user.getPassword()    = {}", user.getPassword());
				logger.info("user.getAuthorities() = {}", user.getAuthorities());
				Collection<GrantedAuthority> authorities = user.getAuthorities();
				for (GrantedAuthority grantedAuthority : authorities) {
					if (grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_RESTAPI")) {
						logger.info("User has authority ROLE_RESTAPI");
					}
				}
			}
		}

		//		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		//		if (roles.contains("ROLE_USER")) {
		//			request.getSession().setAttribute("myVale", "myvalue");
		//		}

		if (true) {
			HttpServletResponse httpResponse = (HttpServletResponse) res;
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return; // Break the filter chain
		}

		chain.doFilter(req, res);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing.	
	}
}