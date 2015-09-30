package com.qfree.obo.report.scheduling.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AnotherBean {

	private static final Logger logger = LoggerFactory.getLogger(AnotherBean.class);

	public void printAnotherMessage() {
		logger.info("I am called by Quartz jobBean using CronTriggerFactoryBean");
	}

}