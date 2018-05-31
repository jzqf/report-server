package com.qfree.bo.report.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * It does not seem to be possible to inject Spring beans into a 
 * ServletContextListener, perhaps because a ServletContextListener is created
 * by the server and is therefore not managed by Spring.
 */
//@Component
@WebListener
public class SyncFileSystemReportsListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(SyncFileSystemReportsListener.class);

	//	@Autowired
	//	private ReportSyncService reportSyncService;

	ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("The report server context has been initialized.");
		//		servletContext = servletContextEvent.getServletContext();
		//
		//		logger.info("servletContext.getContextPath() = {}", servletContext.getContextPath());
		//		logger.info("servletContext.getRealPath(\"\") = {}", servletContext.getRealPath(""));
		//		logger.info("servletContext.getRealPath(\"/\") = {}", servletContext.getRealPath("/"));
		//
		//		Enumeration<String> servletContextinitParamEnum = servletContext.getInitParameterNames();
		//		while (servletContextinitParamEnum.hasMoreElements()) {
		//			String servletContextInitParamName = servletContextinitParamEnum.nextElement();
		//			logger.info("servletContextInitParamName = {}", servletContextInitParamName);
		//		}
		//		logger.info("BIRT_VIEWER_WORKING_FOLDER = {}", servletContext.getInitParameter("BIRT_VIEWER_WORKING_FOLDER"));
		//
		//		logger.info("About to create AnnotationConfigApplicationContext...");
		//		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		//		logger.info("... context = {}", context);
		//		ReportSyncService reportSyncService = null;
		//
		//		logger.info("reportSyncService = {}", reportSyncService);
		//
		//		context.close();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("The report server context has been destroyed.");
	}
}