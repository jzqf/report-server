package com.qfree.obo.report.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SyncFileSystemReportsListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(SyncFileSystemReportsListener.class);

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		logger.info("The report server is starting up...");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		logger.info("The report server is shutting down...");
	}
}