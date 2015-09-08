package com.qfree.obo.report.service;

import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BirtService {

	private static final Logger logger = LoggerFactory.getLogger(BirtService.class);

	private IReportEngine engine;
	private IReportRunnable design;

	public IReportEngine createBirtReportEngine() {

		return engine;
	}

	public void shutdownBirt() {

	}
}
