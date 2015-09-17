package com.qfree.obo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.util.DateUtils;

public class MiscTest2 {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest2.class);

	public static void main(String[] args) {

		System.out.println("DateUtils.nowUtc()  = " + DateUtils.nowUtc());

		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000Z\")       = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000Z"));
		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000+00:00\")  = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000+00:00"));
		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000+02:00\")  = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000+02:00"));

	}
}