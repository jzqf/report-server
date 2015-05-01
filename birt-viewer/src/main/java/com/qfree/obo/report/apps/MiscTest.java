package com.qfree.obo.report.apps;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.util.DateUtils;

public class MiscTest {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest.class);

	public static void main(String[] args) {

		String s;
		Date date;

		s = "2015-05-30T12:00:00.000Z";
		date = DateUtils.dateUtcFromIso8601String(s);
		logger.info("s = {},      date = {}", s, date);

		s = "2015-05-30T12:00:00.000+00:00";
		date = DateUtils.dateUtcFromIso8601String(s);
		logger.info("s = {}, date = {}", s, date);

		s = "2015-05-30T12:00:00.000+02:00";
		date = DateUtils.dateUtcFromIso8601String(s);
		logger.info("s = {}, date = {}", s, date);

	}
}