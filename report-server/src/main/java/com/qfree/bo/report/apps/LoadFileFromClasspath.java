package com.qfree.bo.report.apps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.exceptions.ReportingException;

public class LoadFileFromClasspath {

	private static final Logger logger = LoggerFactory.getLogger(LoadFileFromClasspath.class);

	public static void main(String[] args) throws ReportingException {

		ClassLoader classLoader = LoadFileFromClasspath.class.getClassLoader();
		logger.info("classLoader.getResource(\"templates/job_delivery_email_subject.txt\").getFile( = {}",
				classLoader.getResource("templates/job_delivery_email_subject.txt").getFile());
		File emailSubjectTemplateFile = new File(
				classLoader.getResource("templates/job_delivery_email_subject.txt").getFile());
		Path emailSubjectTemplatePath = emailSubjectTemplateFile.toPath();
		logger.info("emailSubjectTemplatePath = {}", emailSubjectTemplatePath);
		String emailSubjectTemplateText = null;
		try {
			emailSubjectTemplateText = new String(Files.readAllBytes(emailSubjectTemplatePath),
					Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new ReportingException("Error loading e-mail subject template from classpath ", e);
		}
		logger.info("emailSubjectTemplateText ={}", emailSubjectTemplateText);

	}

}
