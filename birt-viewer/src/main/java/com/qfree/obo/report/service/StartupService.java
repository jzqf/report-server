package com.qfree.obo.report.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//@Component  <- not needed because bean is explicitly created in ApplicationConfig.java
public class StartupService {

	private static final Logger logger = LoggerFactory.getLogger(StartupService.class);

	@Autowired
	private ReportSyncService reportSyncService;

	//	@Autowired
	//	ServletContext servletContext;

	@PostConstruct
	public void initialize() {
		//logger.info("Synchronizing reports in the file system with the database...");
		//		logger.info("servletContext = {}", servletContext);
	}
}
