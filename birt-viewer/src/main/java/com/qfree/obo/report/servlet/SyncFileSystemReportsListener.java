package com.qfree.obo.report.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
@WebListener
public class SyncFileSystemReportsListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(SyncFileSystemReportsListener.class);

	ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("The report server context has been initialized.");
		servletContext = servletContextEvent.getServletContext();

		//	logger.info("servletContext.getContextPath() = {}", servletContext.getContextPath());
		//	logger.info("servletContext.getRealPath(\"\") = {}", servletContext.getRealPath(""));
		//	logger.info("servletContext.getRealPath(\"/\") = {}", servletContext.getRealPath("/"));
		//
		//	Enumeration<String> servletContextinitParamEnum = servletContext.getInitParameterNames();
		//	while (servletContextinitParamEnum.hasMoreElements()) {
		//		String servletContextInitParamName = servletContextinitParamEnum.nextElement();
		//		logger.info("servletContextInitParamName = {}", servletContextInitParamName);
		//	}
		//	logger.info("BIRT_VIEWER_WORKING_FOLDER = {}", servletContext.getInitParameter("BIRT_VIEWER_WORKING_FOLDER"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("The report server context has been destroyed.");
	}
}