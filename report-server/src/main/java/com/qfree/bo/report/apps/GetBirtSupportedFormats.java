package com.qfree.bo.report.apps;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.service.BirtService;

public class GetBirtSupportedFormats {

	private static final Logger logger = LoggerFactory.getLogger(GetBirtSupportedFormats.class);

	public static void main(String[] args) {

		BirtService birtService = new BirtService();

		try {
			IReportEngine engine = birtService.getBirtReportEngine();

			String[] supportedFormats = engine.getSupportedFormats();
			logger.info("supportedFormats.length = {}", supportedFormats.length);
			logger.info("supportedFormats =");
			for (String format : supportedFormats) {
				logger.info("    {}", format);
			}

		} catch (BirtException e) {
			e.printStackTrace();
		}

	}

}
