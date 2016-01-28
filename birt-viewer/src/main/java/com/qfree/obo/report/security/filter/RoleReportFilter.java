package com.qfree.obo.report.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.springframework.security.core.context.SecurityContextHolder;

import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.db.RoleReportRepository;
import com.qfree.obo.report.db.RoleRepository;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.RoleReport;
import com.qfree.obo.report.domain.UuidCustomType;
import com.qfree.obo.report.rest.server.RoleController;
import com.qfree.obo.report.security.ReportServerUser;

public class RoleReportFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(RoleReportFilter.class);

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ReportVersionRepository reportVersionRepository;

	@Autowired
	private RoleReportRepository roleReportRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		//	logger.info("request = {}", request);
		//	logger.info("request.getRequestURL() = {}", request.getRequestURL());
		//	logger.info("request.getRequestURI() = {}", request.getRequestURI());
		//	logger.info("request.getContextPath() = {}", request.getContextPath());
		//	logger.info("request.getServletPath() = {}", request.getServletPath());
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
		logger.debug("reportFilename = {}", reportFilename);

		/*
		 * Extract name of principal. This is the user name from the HTTP 
		 * Authorization header).
		 */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("authentication = {}", authentication);
		Object principal = null;
		ReportServerUser user = null;
		//String username = null;
		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			principal = authentication.getPrincipal();
			//logger.debug("principal.getClass().getName() = {}", principal.getClass().getName());
			//logger.debug("principal = {}", principal);
			//logger.debug("principal.toString() = {}", principal.toString());
			//if (principal instanceof User) {
			//	User user = (User) principal;
			if (principal instanceof ReportServerUser) {
				user = (ReportServerUser) principal;
				//username = user.getUsername();
				//logger.debug("user = {}", user);
				//logger.debug("user.getUsername()    = {}", user.getUsername());
				//logger.debug("user.getPassword()    = {}", user.getPassword());
				//logger.debug("user.isEnabled()      = {}", user.isEnabled());
				//logger.debug("user.isActive()       = {}", user.isActive());
				//logger.debug("user.getAuthorities() = {}", user.getAuthorities());
				//Collection<GrantedAuthority> authorities = user.getAuthorities();
				//for (GrantedAuthority grantedAuthority : authorities) {
				//	if (grantedAuthority.getAuthority().equalsIgnoreCase(Authority.AUTHORITY_NAME_MANAGE_REPORTS)) {
				//		logger.debug("User has authority MANAGE_REPORTS");
				//	}
				//}
			} else {
				logger.error("principal is *not* an instance of ReportServerUser. principal = {}", principal);
			}
		}

		if (userHasAccessToReport(user, reportFilename)) {

			/*
			 * Insert "report_requested_by" and possibly additional parameters
			 * into the request.
			 * 
			 * "report_requested_by" is a report parameter can can be used to
			 * display the username of the authenticated principal that
			 * requested the report.
			 */
			req = insertExtraRequestParameters(request, user); // This re-uses "req"
			//Map<String, String[]> newParameterMap = req.getParameterMap();
			//for (Map.Entry<String, String[]> entry : newParameterMap.entrySet()) {
			//	String key = entry.getKey();
			//	String[] values = entry.getValue();
			//	//if (values != null) {
			//	//	logger.info("key, value = {}, {}", key, values[0]);
			//	//}
			//	logger.info("key = {}", key);
			//	if (values != null) {
			//		for (String s : values) {
			//			logger.info("    value = {}", s);
			//		}
			//	}
			//}

			chain.doFilter(req, res);
			return;
		}

		/*
		 * This will also occur if no ReportVersion exists for the value of 
		 * reportFilename specified in the request. But it is better to return
		 * a 403 here than 404 because we do not want to reveal more 
		 * information to someone who is sniffing around than is absolutely 
		 * necessary.
		 */
		logger.warn("User does not have access to requested report. HTTP 403 Forbidden will be returned.\n"
				+ "    user           = {}\n"
				+ "    reportFilename = {}", user, reportFilename);

		HttpServletResponse httpResponse = (HttpServletResponse) res;
		httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		return; // Break the filter chain

	}

	/**
	 * Returns <code>true</code> if the {@link Role} that corresponds to the
	 * specified {@link ReportServerUser} security principle has access to the
	 * report with the specified filename.
	 * 
	 * user.getUsername() must match an existing Role that is active and
	 * enabled.
	 * 
	 * @param user
	 * @param reportFilename
	 * @return
	 */
	private boolean userHasAccessToReport(ReportServerUser user, String reportFilename) {
		if (user != null && reportFilename != null && !reportFilename.isEmpty()) {

			logger.info("reportFilename = {}", reportFilename);
			logger.info("user           = {}", user);

			/*
			 * "user.getUsername()" must match an existing Role that is active and enabled.
			 */
			if (user.isActive() && user.isEnabled()) {

				if (reportFilename.equals("test/test_db_config.rptdesign")
						|| reportFilename.equals("test/test_rs_config.rptdesign")) {
					return true;
				}

				/*
				 * "reportFilename" must match an existing Report Version that
				 * is active. In addition, the linked Report must also be
				 * active.
				 */
				ReportVersion reportVersion = reportVersionRepository.findByFileName(reportFilename);
				logger.info("reportVersion = {}", reportVersion);
				if (reportVersion != null && reportVersion.isActive()) {

					/*
					 * Finally, there must exist a RoleReport entity for the Role and
					 * Report located above. This indicates that the user has access to
					 * the report.
					 * 
					 * If I modify this code and it then triggers a Lazy...Exception,
					 * try annotating this method with @Transactional(readOnly = true).
					 */

					if (RoleController.ALLOW_ALL_REPORTS_FOR_EACH_ROLE) {
						return true;
					} else {
						/*
						 * The H2 database does not support recursive CTE expressions, so it is 
						 * necessary to run different code if the database is not PostgreSQL.
						 * This only affects integration tests, because only PostreSQL is used
						 * in production. 
						 */
						if (UuidCustomType.DB_VENDOR.equals(UuidCustomType.POSTGRESQL_VENDOR)) {

							Boolean activeReportsOnly = true;
							List<String> availableReportVersions = reportVersionRepository
									.findReportVersionFilenamesByRoleIdRecursive(user.getRoleId().toString(),
											activeReportsOnly);
							for (String filename : availableReportVersions) {
								if (reportFilename.equals(filename)) {
									return true;
								}
							}

						} else {

							/*
							 * This code only treats those Reports associated with RoleReport
							 * entities that are *directly* linked to the Role role. This does *not*
							 * include Reports associated with RoleReport entities that are linked 
							 * to ancestors (parents, grandparents, ...) of Role role.
							 */
							RoleReport roleReport = roleReportRepository.findByRoleRoleIdAndReportReportId(
									user.getRoleId(),
									reportVersion.getReport().getReportId());
							if (roleReport != null && roleReport.getReport().isActive()) {
								return true;
							}

						}
					}

				}
			}

		}
		return false;
	}

	private ServletRequest insertExtraRequestParameters(HttpServletRequest request, ReportServerUser user) {

		/*
		 * TODO Only insert a "requested_by" report parameter if one has not already
		 * been provided. We may want to modify this behaviour at some point to
		 * catch the case where a reporting user tries to craft his/her own URL
		 * for nefarious purposes.
		 */
		//Map<String, String[]> parameterMap = request.getParameterMap();
		//for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
		//	String key = entry.getKey();
		//	String[] values = entry.getValue();
		//	logger.info("key = {}", key);
		//	if (values != null) {
		//		for (String s : values) {
		//			logger.info("    value = {}", s);
		//		}
		//	}
		//}

		String[] requestedBy = new String[1];
		requestedBy[0] = user.getUsername();
		logger.info("requestedBy[0] = {}", requestedBy[0]);

		Map<String, String[]> extraParams = new TreeMap<String, String[]>();
		extraParams.put("report_requested_by", requestedBy);

		/*
		 * This will REPLACE the "report_requested_by" parameter in the original
		 * request if it exists in the original request.
		 */
		return new ModifiableParametersRequestWrapper(request, extraParams);
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