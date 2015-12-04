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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.qfree.obo.report.db.RoleRepository;

public class RoleReportFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(RoleReportFilter.class);

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		logger.info("----- In doFilter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		HttpServletRequest request = (HttpServletRequest) req;
		//	logger.info("request = {}", request);
		//	logger.info("request.getRequestURI() = {}", request.getRequestURI());
		//	logger.info("request.getRequestURL() = {}", request.getRequestURL());
		//	logger.info("request.getServletPath() = {}", request.getServletPath());
		//	logger.info("request.getContextPath() = {}", request.getContextPath());
		//	logger.info("request.getPathInfo() = {}", request.getPathInfo());// <--- ****************
		//	for (Map.Entry<String, String[]> mapEntry : request.getParameterMap().entrySet()) {
		//		logger.info("    ParameterMap<{}> = {}", mapEntry.getKey(), mapEntry.getValue());
		//	}
		Map<String, String[]> parameterMap = request.getParameterMap();

		/*
		 * Extract the report file name from the request URI. it will be 
		 * assigned to the "__report" query parameter. The URL should be of the
		 * form:
		 * 
		 *   http(s)://<BaseURL>/frameset?__report=somereport.rptdesign&...
		 * 
		 * where instead of "frameset", "run" or "preview" may also be used.
		 * These URL patterns to match on are configured in SecurityConfig.java
		 * when the DelegateRequestMatchingFilter bean is defined.
		 * 
		 * The report name from this URL example is:
		 * 
		 *   somereport.rptdesign
		 */
		String[] reportFileNames = parameterMap.get("__report");
		String reportFilename = null;
		/*
		 * We check that *exactly* one report is specified. This should always
		 * be the case so this should not be over-restrictive. We do test for
		 * this to catch the case where someone is crafting URLs to try to 
		 * view a report that they do not have access to.
		 */
		if (reportFileNames != null && reportFileNames.length == 1) {
			reportFilename = reportFileNames[0];
		}
		logger.info("reportFilename = {}", reportFilename);

		// LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG LOG in case of error!!!!!!!!!!!!1

		/*
		 * Extract name of principal. This is the user name from the HTTP 
		 * Authorization header).
		 */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("authentication = {}", authentication);
		String username = null;
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			Object principal = authentication.getPrincipal();
			logger.debug("principal.getClass().getName() = {}", principal.getClass().getName());
			logger.debug("principal = {}", principal);
			logger.debug("principal.toString() = {}", principal.toString());
			if (principal instanceof User) {
				User user = (User) principal;
				username = user.getUsername();
				logger.debug("user = {}", user);
				logger.debug("user.getUsername()    = {}", user.getUsername());
				logger.debug("user.getPassword()    = {}", user.getPassword());
				logger.debug("user.getAuthorities() = {}", user.getAuthorities());
				Collection<GrantedAuthority> authorities = user.getAuthorities();
				for (GrantedAuthority grantedAuthority : authorities) {
					if (grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_RESTAPI")) {
						logger.debug("User has authority ROLE_RESTAPI");
					}
				}
			}
		}
		logger.info("username = {}", username);

		logger.info("roleRepository = {}", roleRepository);

		//TODO Return false if username==null or does not match a Role, or... or if reportFileName...
		if (userHasAccessToReport(username, reportFilename)) {
			chain.doFilter(req, res);
			return;
		}

		logger.warn("User does not have access to requested report. HTTP 403 Forbidden will be returned.\n"
				+ "    username       = {}\n"
				+ "    reportFilename = {}", username, reportFilename);

		HttpServletResponse httpResponse = (HttpServletResponse) res;
		httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return; // Break the filter chain

	}

	private boolean userHasAccessToReport(String username, String reportFileName) {
		if (username != null && !username.isEmpty() && reportFileName != null && !reportFileName.isEmpty()) {

			//TODO Add code below to check the user has access to the report!

			/*
			 * "username" must match an existing Role that is active and 
			 * enabled.
			 */
			// ...

			/*
			 * "reportFileName" must match an existing Report Version that is 
			 * active. In addition, the linked Report must also be active.
			 */
			// ...

			/*
			 * Finally, there must exist a RoleReport entity for the Role and
			 * Report located above. This specifies that the user has access to
			 * the report.
			 */
			// ...

			return true;

		}
		return false;
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