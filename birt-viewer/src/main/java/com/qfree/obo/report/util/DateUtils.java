package com.qfree.obo.report.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * Returns a {@link Date} corresponding to the current datetime such that if
	 * it is stored in a PostgreSQL table column of type "timestamp without 
	 * timezone", the value will represent the current instant in time in for
	 * the GMT time zone.
	 * <p>
	 * For example, if our current local time zone is GMT+2 (e.g., Trondheim),
	 * then if the local time is:
	 * <p>
	 * May 1, 2015 12:00:00 CEST,
	 * <p>
	 * then the Java Date returned by this method will be:
	 * <p>
	 * May 1, 2015 10:00:00 CEST
	 * <p>
	 * Note that this is not the correct datetime, but when this value is stored
	 * in a PostgreSQL "timestamp without timezone" column, the time zone will
	 * be discarded and the value inserted will be:
	 * <p>
	 * 2015-05-01 10:00:00
	 * <p>
	 * which is the correct date and time when expressed in GMT/UTC.
	 * 
	 * @return
	 */
	public static Date nowUtc() {
		return new DateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
	}

	/**
	 * Returns a {@link Date} by parsing an ISO 8601 date string. The date and
	 * time will be expressed in the GMT/UTC, even though the string 
	 * representation of the date will show the current time zone.
	 * <p>
	 * Examples:
	 * <p>
	 * dateString = "2015-05-30T12:00:00.000Z"      -> Sat May 30 12:00:00 CEST 2015<br>
	 * dateString = "2015-05-30T12:00:00.000+00:00" -> Sat May 30 12:00:00 CEST 2015<br>
	 * dateString = "2015-05-30T12:00:00.000+02:00" -> Sat May 30 10:00:00 CEST 2015<br>
	 * <p>
	 * The interpretation here is that:
	 * <p>
	 * The instance in time "12:00:00.000Z"  is "12:00:00" in GMT/UTC time.<br>
	 * The instance in time "12:00:00+00:00" is "12:00:00" in GMT/UTC time.<br>
	 * The instance in time "12:00:00+02:00" is "10:00:00" in GMT/UTC time.<br>
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date dateUtcFromIso8601String(String dateString) {
		return DateTime.parse(dateString).toDateTime(DateTimeZone.UTC).toLocalDateTime().toDate();
	}

	/**
	 * Oddly, the string representation of an Entity Date field annotated with
	 * @Temporal(TemporalType.TIMESTAMP) is quite different than the string 
	 * representation of the Date "currentCreatedOn". Somehow, the underlying
	 * Date objects are different, but the details are not clear. The string 
	 * representation of such a Date returned from an entity has no time zone 
	 * information at all, but a "normal" Java does. Examples of each are
	 *  
	 * dateFromEntity.toString()  -> "2015-05-30 22:00:00.0"
	 * normalDate.toString()      -> "Sat May 30 22:00:00 CEST 2015"
	 * 
	 * In order to compare objects of these "types" (technically, they are the
	 * same Java type) and assert that they are "equal" in a test, the Date
	 * returned from the entity should be converted to a more "normal" Java Date
	 * using this method.
	 * 
	 * @param entityDate
	 * @return
	 */
	public static Date entityTimestampToNormalDate(Date entityDate) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(entityDate);
		return calendar.getTime();
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
