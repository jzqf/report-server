package com.qfree.bo.report.security.filter;

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

import com.qfree.bo.report.db.ReportVersionRepository;
import com.qfree.bo.report.db.RoleReportRepository;
import com.qfree.bo.report.db.RoleRepository;
import com.qfree.bo.report.domain.ReportVersion;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.RoleReport;
import com.qfree.bo.report.domain.UuidCustomType;
import com.qfree.bo.report.rest.server.RoleController;
import com.qfree.bo.report.security.ReportServerUser;
import com.qfree.bo.report.util.ReportUtils;

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
		logger.debug("request = {}", request);
		logger.debug("request.getRequestURL() = {}", request.getRequestURL());
		logger.debug("request.getRequestURI() = {}", request.getRequestURI());
		logger.debug("request.getContextPath() = {}", request.getContextPath());
		logger.debug("request.getServletPath() = {}", request.getServletPath());
		logger.debug("request.getPathInfo() = {}", request.getPathInfo());// <- This always seems to be null

		Map<String, String[]> parameterMap = request.getParameterMap();
		//logger.debug("parameterMap = {}", parameterMap);
		for (Map.Entry<String, String[]> mapEntry : parameterMap.entrySet()) {
			logger.info("parameterMap<{}> = {}", mapEntry.getKey(), mapEntry.getValue());
		}

		/*
		 * Extract the report file name from the request URI. it will be 
		 * assigned to the "__report" query parameter. The URL should be of the
		 * form:
		 * 
		 *   http(s)://<BaseURL>/frameset?__report=somereport.rptdesign&...
		 * 
		 * where instead of "frameset", "run" or "preview" may also be used.
		 * These URL patterns to match on are configured in SecurityConfig.java
		 * when the DelegateRequestMatchingFilter bean is created.
		 * 
		 * The report name from this URL example is:
		 * 
		 *   somereport.rptdesign
		 */
		String[] reportFileNames = parameterMap.get("__report");
		logger.debug("reportFileNames = {}", reportFileNames);
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
		 * When a report is requested using a URL of the form just described,
		 * this original request may trigger additional requests to load images
		 * or BIRT-generated charts that are displayed by the report. These 
		 * additional requests will not have a "__report" query parameter. 
		 * But it seems that it _will_ have a "__imageid" query parameter. If
		 * the request does not have a "__report" query parameter, but it 
		 * _does_ have a "__imageid" query parameter, we should accept the 
		 * request; otherwise, these objects, e.g., images or charts, will not
		 * be rendered.
		 * 
		 * As for the "__report" query parameter, there should only be a single
		 * "__imageid" query parameter. This is tested for here, and if so the
		 * single image name is assigned to "image".
		 */
		String[] images = parameterMap.get("__imageid");
		logger.debug("images = {}", images);
		String image = null;
		if (images != null && images.length == 1) {
			image = images[0];
		}
		logger.debug("image = {}", image);

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

		if (userHasAccessToReportOrImage(user, reportFilename, image)) {

			/*
			 * Insert RP_REPORT_REQUESTED_BY and possibly additional parameters
			 * into the request.
			 * 
			 * RP_REPORT_REQUESTED_BY is a report parameter that can be used to
			 * display the username of the authenticated principal that
			 * requested the report.
			 */
			ServletRequest wrappedReq = insertExtraRequestParameters(request, user, reportFilename);

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

			chain.doFilter(wrappedReq, res);
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
	 * <p>
	 * If this report filename is null or empty, then it is assumed that an
	 * image, not a report, has been requested. In this case, <code>true</code>
	 * is returned if the name of the specified image is not null and not empty.
	 * There is currently no reason to restrict access to images. Images may
	 * correspond to uploaded assets or BIRT charts.
	 * <p>
	 * user.getUsername() must match an existing Role that is active and
	 * enabled.
	 * 
	 * @param user
	 * @param reportFilename
	 * @param image
	 * @return
	 */
	private boolean userHasAccessToReportOrImage(ReportServerUser user, String reportFilename, String image) {
		//		if (1 == 1) {
		//			return true;
		//		}
		logger.info("user           = {}", user);
		logger.info("reportFilename = {}", reportFilename);
		logger.info("image          = {}", image);
		if (user != null) {
			/*
			 * "user.getUsername()" must match an existing Role that is active and enabled.
			 */
			if (user.isActive() && user.isEnabled()) {

				if (reportFilename != null && !reportFilename.isEmpty()) {

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
								Boolean activeInheritedRolesOnly = true;
								List<String> availableReportVersions = reportVersionRepository
										.findReportVersionFilenamesByRoleIdRecursive(user.getRoleId().toString(),
												activeReportsOnly, activeInheritedRolesOnly);
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

				} else if (image != null && !image.isEmpty()) {
					/*
					 * The request was for an image or a BIRT chart, so we let
					 * it go through. Currently, we do not check if the name 
					 * matches some pattern because I don't know how to define
					 * a valid or invalid pattern. If the connection is for
					 * returning a BIRT chart, it seems that the file extension
					 * "svg" is used if the Servlet mapping "frameset" or "run"
					 * is used to display the report, i.e., the String "image" 
					 * always ends with ".svg", but the rest of the filename is 
					 * different for each connection. If the Servlet mapping 
					 * "preview" is used to display the report, then a similar 
					 * behaviour is observed, but the file extension is always
					 * "png". When the file "logo.png" is requested, "image" 
					 * ends with ".png", but here also the rest of the name is 
					 * different for each connection.
					 */
					return true;
				} else {
					/*
					 * This case should not occur, but if it does, we reject 
					 * the request, just to be safe.
					 * 
					 * Other sorts of assets, e.g., JavaScript files, CSS files,
					 * BIRT libraries, Java properties files ..., are used 
					 * server-side and so they are not served by HTTP requests.
					 */
					//return true;
				}

			}
		}
		return false;
	}

	private ServletRequest insertExtraRequestParameters(
			HttpServletRequest request,
			ReportServerUser user,
			String reportFilename) {

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

		/*
		 * reportVersion will be null if one of the test reports in the "test"
		 * directory are being displayed or if the URL requests and image, not
		 * a report. We check for that below to avoid a NullPointerException.
		 */
		ReportVersion reportVersion = null;
		if (reportFilename != null) {
			reportVersion = reportVersionRepository.findByFileName(reportFilename);
		}

		String[] reportRequestedBy = new String[1];
		reportRequestedBy[0] = user.getUsername();
		logger.info("reportRequestedBy[0] = {}", reportRequestedBy[0]);

		/*
		 * TODO If reportVersion is null, do not call this method to insert these extra query parameters at all?
		 * If reportVersion is null and I do not call this method at all, then
		 * hopefully we can just return the original request instead of the
		 * wrapped request that is created here.
		 */

		String[] reportName = new String[1];
		reportName[0] = reportVersion != null ? reportVersion.getReport().getName() : "";
		logger.info("reportName[0]        = {}", reportName[0]);

		String[] reportNumber = new String[1];
		reportNumber[0] = reportVersion != null ? reportVersion.getReport().getNumber().toString() : "";
		logger.info("reportNumber[0]      = {}", reportNumber[0]);

		String[] reportVersionName = new String[1];
		reportVersionName[0] = reportVersion != null ? reportVersion.getVersionName() : "";
		logger.info("reportVersionName[0] = {}", reportVersionName[0]);

		Map<String, String[]> extraParams = new TreeMap<>();
		extraParams.put(ReportUtils.RP_REPORT_REQUESTED_BY, reportRequestedBy);
		extraParams.put(ReportUtils.RP_REPORT_NAME, reportName);
		extraParams.put(ReportUtils.RP_REPORT_NUMBER, reportNumber);
		extraParams.put(ReportUtils.RP_REPORT_VERSION, reportVersionName);

		/*
		 * This will *replace* the RP_REPORT_REQUESTED_BY, RP_REPORT_NAME, ...,
		 * special report parameters in the original request (if they exist in
		 * the original request).
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