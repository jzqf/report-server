package com.qfree.obo.report.apps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscTest {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest.class);

	public static void main(String[] args) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

		String dateString;
		Calendar calendar;
		Date date;
		DateTime datetime;
		LocalDateTime ldt;

		System.out.println("");
		//		dateString = "1958-05-06T13:00:00.000Z";
		dateString = "1958-05-06T13:00:00.000+02:00";
		System.out.println("dateString = " + dateString);

		datetime = DateTime.parse(dateString).withZone(DateTimeZone.UTC);
		date = datetime.toLocalDateTime().toDate();
		System.out.println("date (using Joda time) = " + date);

		calendar = DatatypeConverter.parseDateTime(dateString);
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		date = calendar.getTime();
		System.out.println("date = " + date);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		System.out.println("Set time zone to GMT");
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		date = calendar.getTime();
		System.out.println("date = " + date);

		//		dateString = "1958-05-06T12:00:00.000Z";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		//		date = calendar.getTime();
		//		System.out.println("date = " + date);
		//		System.out.println("\"" + dateString + "\" --> \"" + format.format(date) + "\"");
		//		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		System.out.println("Set time zone to GMT");
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		//		date = calendar.getTime();
		//		System.out.println("date = " + date);
		//		System.out.println("\"" + dateString + "\" --> \"" + format.format(date) + "\"");

		//		System.out.println("\n");
		//		dateString = "1958-05-06T12:00:00.000+02:00";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		//		date = calendar.getTime();
		//		System.out.println("date = " + date);
		//		System.out.println("\"" + dateString + "\" --> \"" + format.format(date) + "\"");
		//		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		//		System.out.println("Set time zone to GMT");
		//		System.out.println("calendar.getTimeZone() = " + calendar.getTimeZone());
		//		date = calendar.getTime();
		//		System.out.println("date = " + date);
		//		System.out.println("\"" + dateString + "\" --> \"" + format.format(date) + "\"");

		//		dateString = "1958-05-06T18:30:45.864Z";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		date = calendar.getTime();
		//		//		System.out.println("\ndate = " + date);
		//		//		System.out.println("format.format(date) = " + format.format(date));
		//		System.out.println("\"" + dateString + "\" --> \"" + format.format(date) + "\"");
		//
		//		dateString = "1958-05-06T18:30:45.86Z";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		date = calendar.getTime();
		//		//		System.out.println("\ndate = " + date);
		//		//		System.out.println("format.format(date) = " + format.format(date));
		//		System.out.println("\"" + dateString + "\"  --> \"" + format.format(date) + "\"");
		//
		//		dateString = "1958-05-06T18:30:45.8Z";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		date = calendar.getTime();
		//		//		System.out.println("\ndate = " + date);
		//		//		System.out.println("format.format(date) = " + format.format(date));
		//		System.out.println("\"" + dateString + "\"   --> \"" + format.format(date) + "\"");
		//
		//		dateString = "1958-05-06T18:30:45.Z";
		//		try {
		//			calendar = DatatypeConverter.parseDateTime(dateString);
		//			//		System.out.println("calendar = " + calendar);
		//			date = calendar.getTime();
		//			//		System.out.println("\ndate = " + date);
		//			//		System.out.println("format.format(date) = " + format.format(date));
		//			System.out.println("\"" + dateString + "\"    --> \"" + format.format(date) + "\"");
		//		} catch (IllegalArgumentException e) {
		//			System.out.println("Caught IllegalArgumentException: " + e.getStackTrace().toString());
		//			logger.warn("Exception caught converting String to Calendar. String = \"{}\". Exception = ", dateString,
		//					e);
		//		}
		//
		//		dateString = "1958-05-06T18:30:45Z";
		//		calendar = DatatypeConverter.parseDateTime(dateString);
		//		//		System.out.println("calendar = " + calendar);
		//		date = calendar.getTime();
		//		//		System.out.println("\ndate = " + date);
		//		//		System.out.println("format.format(date) = " + format.format(date));
		//		System.out.println("\"" + dateString + "\"     --> \"" + format.format(date) + "\"");
		//
		//		dateString = "1958-05-06 18:30:45Z";
		//		try {
		//			calendar = DatatypeConverter.parseDateTime(dateString);
		//			//		System.out.println("calendar = " + calendar);
		//			date = calendar.getTime();
		//			//		System.out.println("\ndate = " + date);
		//			//		System.out.println("format.format(date) = " + format.format(date));
		//			System.out.println("\"" + dateString + "\"    --> \"" + format.format(date) + "\"");
		//		} catch (IllegalArgumentException e) {
		//			System.out.println("Caught IllegalArgumentException: " + e);
		//		}

		System.out.println("");
		System.out.println("new Date() = " + new Date());
	}
}