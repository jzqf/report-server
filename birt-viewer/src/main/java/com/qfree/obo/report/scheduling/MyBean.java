package com.qfree.obo.report.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("myBean")
public class MyBean {

	private static final Logger logger = LoggerFactory.getLogger(MyBean.class);

	public void printMessage() {
		logger.info("I am called by MethodInvokingJobDetailFactoryBean using SimpleTriggerFactoryBean");
	}

}