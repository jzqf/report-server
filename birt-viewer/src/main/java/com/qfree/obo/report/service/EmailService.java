package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation.
	 */
	private final Environment env;

	private final String defaultSmtpHost;

	@Autowired
	public EmailService(Environment env) {
		this.env = env;
		this.defaultSmtpHost = env.getProperty("mail.smtp.host");
	}

	public void sendEmail() {
		logger.info("defaultSmtpHost = {}", defaultSmtpHost);
	}

}
