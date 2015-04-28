package com.qfree.obo.report.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

public class DateUtils {

	/**
	 * Returns a Java Date corresponding to the current datetime such that if
	 * it is stored in a PostgreSQL table column of type "timestamp without 
	 * timezone", the value will represent the current instant in time in for
	 * the GMT time zone.<p>
	 * <p>
	 * For example, if our current local time zone is GMT+2 (e.g., Trondheim),
	 * then if the local time is:<p>
	 * <p>
	 * May 1, 2015 12:00:00 CEST,<p>
	 * <p>
	 * then the Java Date returned by this method will be:<p>
	 * <p>
	 * May 1, 2015 10:00:00 CEST<p>
	 * <p>
	 * Note that this is not the correct datetime, but when this value is stored
	 * in a PostgreSQL "timestamp without timezone" column, the time zone will
	 * be discarded and the value inserted will be<p>
	 * <p>
	 * 2015-05-01 10:00:00<p>
	 * <p>
	 * which is the correct date and time when expressed in GMT/UTC.
	 * 
	 * @return
	 */
	public static Date nowUtc() {
		return new DateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
	}

	/**
	 * Returns true if the time portion of the two Date vales are equal.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean timePartsEqual(Date date1, Date date2) {
		/*
		 * Convert the two dates to Joda LocalTime objects so they can be 
		 * compared. Joda time is used here because Java 7 does not have 
		 * convenient methods for dealing with times that are not associated 
		 * with an instant in time. I suppose that I could have managed this 
		 * with standard Java 7 methods, but it is easier to just use Joda time.
		 */
		LocalTime lt1 = new LocalTime(date1);
		LocalTime lt2 = new LocalTime(date2);
		return lt1.equals(lt2);
	}

}
