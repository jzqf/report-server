package com.qfree.bo.report.apps;

import java.util.Date;
import java.util.Locale;

import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.engine.api.ReportParameterConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.DateUtils;

public class BirtReportParameterConverterTests {

	private static final Logger logger = LoggerFactory.getLogger(BirtReportParameterConverterTests.class);

	public static void main(String[] args) {

		logger.info("\n");
		Date date = new Date();
		logger.info("date               = {}", date);

		ReportParameterConverter paramConverter;

		logger.info("");
		String birtDatetimeString = DateUtils.birtDatetimeStringFromDate(date);
		logger.info("birtDatetimeString = {}", birtDatetimeString);
		paramConverter = new ReportParameterConverter(DateUtils.BIRT_DATETIME_FORMAT_STRING, Locale.getDefault());
		Object birtDateTimeObject = paramConverter.parse(birtDatetimeString, IParameterDefn.TYPE_DATE_TIME);
		logger.info("birtDateTimeObject = {}", birtDateTimeObject);
		Date dateCastFromBirtDateTimeObject = (Date) birtDateTimeObject;
		logger.info("dateCastFromBirtDateTimeObject = {}", dateCastFromBirtDateTimeObject);

		logger.info("");
		String birtDateString = DateUtils.birtDateStringFromDate(date);
		logger.info("birtDateString     = {}", birtDateString);
		paramConverter = new ReportParameterConverter(DateUtils.BIRT_DATE_FORMAT_STRING, Locale.getDefault());
		Object birtDateObject = paramConverter.parse(birtDateString, IParameterDefn.TYPE_DATE);
		logger.info("birtDateObject     = {}", birtDateObject);
		Date dateCastFromBirtDateObject = (Date) birtDateObject;
		logger.info("dateCastFromBirtDateObject = {}", dateCastFromBirtDateObject);

		logger.info("");
		String birtTimeString = DateUtils.birtTimeStringFromDate(date);
		logger.info("birtTimeString     = {}", birtTimeString);
		paramConverter = new ReportParameterConverter(DateUtils.BIRT_TIME_FORMAT_STRING, Locale.getDefault());
		Object birtTimeObject = paramConverter.parse(birtTimeString, IParameterDefn.TYPE_TIME);
		logger.info("birtTimeObject     = {}", birtTimeObject);
		Date dateCastFromBirtTimeObject = (Date) birtTimeObject;
		logger.info("dateCastFromBirtTimeObject = {}", dateCastFromBirtTimeObject);

	}

}
