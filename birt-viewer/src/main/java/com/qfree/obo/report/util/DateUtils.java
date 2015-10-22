package com.qfree.obo.report.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.dto.DatetimeAdapter;

public class DateUtils {

	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	private static final SimpleDateFormat FORMAT_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final SimpleDateFormat FORMAT_LOCALDATETIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
		// return new
		// org.joda.time.DateTime(org.joda.time.DateTimeZone.UTC).toLocalDateTime().toDate();
		ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));
		return Date.from(zonedDateTime.withZoneSameLocal(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * Returns a {@link Date} by parsing an ISO 8601 date string. The date and
	 * time will be expressed in the GMT/UTC, even though the string
	 * representation of the date will show the current time zone.
	 * <p>
	 * Examples:
	 * <p>
	 * utcDateString = "2015-05-30T12:00:00.000Z" -> Sat May 30 12:00:00 CEST
	 * 2015<br>
	 * utcDateString = "2015-05-30T12:00:00.000+00:00" -> Sat May 30 12:00:00
	 * CEST 2015<br>
	 * utcDateString = "2015-05-30T12:00:00.000+02:00" -> Sat May 30 10:00:00
	 * CEST 2015<br>
	 * <p>
	 * The interpretation here is that:
	 * <p>
	 * The instance in time "12:00:00.000Z" is "12:00:00" in GMT/UTC time.<br>
	 * The instance in time "12:00:00+00:00" is "12:00:00" in GMT/UTC time.<br>
	 * The instance in time "12:00:00+02:00" is "10:00:00" in GMT/UTC time.<br>
	 * 
	 * @param utcDateString
	 * @return
	 */
	public static Date dateUtcFromIso8601String(String utcDateString) throws DateTimeParseException {

		logger.debug("----------------------------------------");
		logger.debug("utcDateString         = {}", utcDateString);

		/*
		 * Parse the ISO 8601 date string into a ZonedDateTime. Examples:
		 * 
		 *  utcDateString                    zonedDateTime.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-05-30T12:00:00.000Z      -> 2015-05-30T12:00Z
		 *  2015-05-30T12:00:00.000+00:00 -> 2015-05-30T12:00Z
		 *  2015-05-30T12:00:00.000+02:00 -> 2015-05-30T12:00+02:00
		 */
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(utcDateString);
		logger.debug("zonedDateTime         = {}", zonedDateTime);

		/*
		 * Convert zonedDateTime to a ZonedDateTime at UTC. Examples:
		 * 
		 *  zonedDateTime.toString()         zonedDateTimeAtUTC.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-05-30T12:00Z             -> 2015-05-30T12:00Z
		 *  2015-05-30T12:00Z             -> 2015-05-30T12:00Z
		 *  2015-05-30T12:00+02:00        -> 2015-05-30T10:00Z
		 *   
		 * In other words, it forces time zone offsets to a UTC representation.  
		 */
		ZonedDateTime zonedDateTimeAtUTC = ZonedDateTime.ofInstant(zonedDateTime.toInstant(),
				ZoneId.of("Z"));
		logger.debug("zonedDateTimeAtUTC    = {}", zonedDateTimeAtUTC);

		/*
		 * Convert zonedDateTimeAtUTC to a LocalDateTime. Examples:
		 * 
		 *  zonedDateTimeAtUTC.toString()    localDateTimeUTC.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-05-30T12:00Z             -> 2015-05-30T12:00
		 *  2015-05-30T12:00Z             -> 2015-05-30T12:00
		 *  2015-05-30T10:00Z             -> 2015-05-30T10:00
		 */
		LocalDateTime localDateTimeUTC = zonedDateTimeAtUTC.toLocalDateTime();
		logger.debug("localDateTimeUTC      = {}", localDateTimeUTC);

		/*
		 * Convert localDateTimeUTC to a ZonedDateTime at the default time zone.
		 * Examples:
		 * 
		 *  localDateTimeUTC.toString()      zonedDateTimeAtSysDef.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-05-30T12:00              -> 2015-05-30T12:00+02:00[Europe/Oslo]
		 *  2015-05-30T12:00              -> 2015-05-30T12:00+02:00[Europe/Oslo]
		 *  2015-05-30T10:00              -> 2015-05-30T10:00+02:00[Europe/Oslo]
		 */
		ZonedDateTime zonedDateTimeAtSysDef = localDateTimeUTC.atZone(ZoneId.systemDefault());
		logger.debug("zonedDateTimeAtSysDef = {}", zonedDateTimeAtSysDef);

		/*
		 * Convert zonedDateTimeAtSysDef to a java.util.Date. This is the 
		 * unmarshalled Date. Examples:
		 * 
		 *  zonedDateTimeAtSysDef.toString()       unmarshalledDate.toString()
		 *  ------------------------------         -----------------------------
		 *  2015-05-30T12:00+02:00[Europe/Oslo] -> Sat May 30 12:00:00 CEST 2015
		 *  2015-05-30T12:00+02:00[Europe/Oslo] -> Sat May 30 12:00:00 CEST 2015
		 *  2015-05-30T10:00+02:00[Europe/Oslo] -> Sat May 30 10:00:00 CEST 2015
		 */
		Date unmarshalledDate = Date.from(zonedDateTimeAtSysDef.toInstant());
		logger.debug("unmarshalledDate      = {}", unmarshalledDate);

		return unmarshalledDate;

		// Old code that used Joda time:
		// return
		// org.joda.time.DateTime.parse(utcDateString).toDateTime(org.joda.time.DateTimeZone.UTC)
		// .toLocalDateTime().toDate();
	}

	/**
	 * Performs the opposite operation as
	 * {@link #dateUtcFromIso8601String(String)}.
	 * 
	 * <p>
	 * This method will take the output of
	 * {@link #dateUtcFromIso8601String(String)} and return a string that is
	 * equivalent to that originally used. Equivalent means that it represents
	 * the same date, not that the strings will be identical.
	 * 
	 * @param date
	 * @return
	 */
	public static String Iso8601StringFromUtcDate(Date date) {

		if (date != null) {
			//try {
			return FORMAT_ISO8601.format(date);
			//} catch (Exception e) {
			//	logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			//	return (String) null;
			//}
		} else {
			return (String) null;
		}

	}

	/**
	 * Converts a string that represents a datetime to a java.util.Date on the
	 * report server (this application) that has the same date and same time
	 * value.
	 * 
	 * This assumes that the string is in the format that can be parsed by
	 * {@link LocalDateTime#parse(CharSequence)}, e.g., "2015-11-29T10:15:30".
	 * 
	 * Datetime values should be encoded in this String format in ReST resources
	 * when the datetime does not refer to any time zone in particular.
	 * 
	 * @param localDatetimeString
	 * @return
	 */
	public static Date dateServerTZFromLocalDatetimeString(String localDatetimeString) {

		logger.debug("----------------------------------------");
		logger.debug("localDatetimeString   = {}", localDatetimeString);

		LocalDateTime localDateTime = LocalDateTime.parse(localDatetimeString);
		logger.debug("localDateTime         = {}", localDateTime);

		ZonedDateTime zonedDateTimeAtSysDef = localDateTime.atZone(ZoneId.systemDefault());
		logger.debug("zonedDateTimeAtSysDef = {}", zonedDateTimeAtSysDef);

		Date date = Date.from(zonedDateTimeAtSysDef.toInstant());
		logger.debug("unmarshalledDate      = {}", date);

		return date;
	}

	/**
	 * Performs the opposite operation as
	 * {@link #dateServerTZFromLocalDatetimeString(String)}.
	 * 
	 * <p>
	 * This method will take the output of
	 * {@link #dateServerTZFromLocalDatetimeString(String)} and return a string
	 * that is equivalent to that originally used. Equivalent means that it
	 * represents the same date, not that the strings will be identical.
	 * 
	 * @param date
	 * @return
	 */
	public static String localDateTimeStringFromServerTZDate(Date date) {

		if (date != null) {
			//try {
			return FORMAT_LOCALDATETIME.format(date);
			//} catch (Exception e) {
			//	logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			//	return (String) null;
			//}
		} else {
			return (String) null;
		}

	}

	/**
	 * Converts a string that represents a datetime to a java.util.Date.
	 * 
	 * This assumes that the string was obtained from a BIRT rptdesign file via
	 * the BIRT API. in such a way that information is not lost in the
	 * conversion and the original string can be recovered, if necessary.
	 * 
	 * This datetime will probably used as a report parameter value. As such The
	 * report will make its own assumption regarding time zone to associate with
	 * it. For example, the query that the report uses to generate the data set
	 * might assume that it is expressed relative to UTC or something else.
	 * 
	 * The most important thing is probably that when the Date is converted back
	 * to a string, then the original string passed to this method should be
	 * recovered.
	 * 
	 * @param birtDatetimeString
	 * @return
	 */
	public static Date dateFromBirtDatetimeString(String birtDatetimeString) {

		//TODO Convert these to private static final fields
		DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		DateTimeFormatter dateTimeFormatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
		DateTimeFormatter dateTimeFormatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

		logger.debug("----------------------------------------");
		logger.debug("birtDatetimeString         = {}", birtDatetimeString);

		/*
		 * This is a crude way to support this range of formats, but it works
		 * fine.
		 */
		LocalDateTime localDateTime = null;
		try {
			localDateTime = LocalDateTime.parse(birtDatetimeString, dateTimeFormatter1);
		} catch (DateTimeParseException e) {
			try {
				localDateTime = LocalDateTime.parse(birtDatetimeString, dateTimeFormatter2);
			} catch (DateTimeParseException e2) {

				try {
					localDateTime = LocalDateTime.parse(birtDatetimeString, dateTimeFormatter3);
				} catch (DateTimeParseException e3) {
					localDateTime = LocalDateTime.parse(birtDatetimeString, dateTimeFormatter4);
				}
			}
		}

		logger.debug("localDateTime         = {}", localDateTime);

		ZonedDateTime zonedDateTimeAtSysDef = localDateTime.atZone(ZoneId.systemDefault());
		logger.debug("zonedDateTimeAtSysDef = {}", zonedDateTimeAtSysDef);

		Date date = Date.from(zonedDateTimeAtSysDef.toInstant());
		logger.debug("unmarshalledDate      = {}", date);

		return date;
	}

	/**
	 * Performs the opposite operation as {@link #dateFromBirtDatetimeString}.
	 * 
	 * This method take the output of {@link #dateFromBirtDatetimeString} and
	 * return a string that is equivalent to that originally used.
	 * 
	 * @param date
	 * @return
	 */
	public static String birtDatetimeStringFromDate(Date date) {

		//TODO Make this a private static final field.
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		if (date != null) {
			//try {
			return format.format(date);
			//} catch (Exception e) {
			//	logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			//	return (String) null;
			//}
		} else {
			return (String) null;
		}

	}

	/**
	 * Returns a {@link Date} by parsing an string that represents a date with
	 * no time information.
	 * 
	 * <p>
	 * <table>
	 * <caption>Examples</caption> <thead>
	 * <tr>
	 * <td>String</td>
	 * <td>{@link Date}</td>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>"1958-05-06"</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param dateStringWithoutTime
	 * @return
	 */
	public static Date dateFromDateStringWithoutTime(String dateStringWithoutTime) throws DateTimeParseException {

		logger.debug("-----------------------------------");
		logger.debug("dateStringWithoutTime       = {}", dateStringWithoutTime);

		LocalDate localDate = LocalDate.parse(dateStringWithoutTime);
		logger.debug("localDate                   = {}", localDate);
		/*
		 * Convert LocalDate to Date, assuming the instant in time is
		 * 
		 */
		// Date unmarshalledDate =
		// Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
		Date unmarshalledDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		logger.debug("unmarshalled java.util.Date = {}", unmarshalledDate);
		return unmarshalledDate;
	}

	public static Date dateFromBirtDateString(String dateStringWithoutTime) {
		return dateFromDateStringWithoutTime(dateStringWithoutTime);
	}

	/**
	 * Performs the opposite operation as {@link #dateFromBirtDateString}.
	 * 
	 * This method take the output of {@link #dateFromBirtDateString} and return
	 * a string that is equivalent to that originally used.
	 * 
	 * @param date
	 * @return
	 */
	public static String birtDateStringFromDate(Date date) {

		//TODO Make this a private static final field.
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			//try {
			return format.format(date);
			//} catch (Exception e) {
			//	logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			//	return (String) null;
			//}
		} else {
			return (String) null;
		}

	}

	/**
	 * Returns a {@link Date} by parsing an string that represents a time.
	 * 
	 * <p>
	 * <table>
	 * <caption>Examples</caption> <thead>
	 * <tr>
	 * <td>String</td>
	 * <td>{@link Date}</td>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>"18:40:15"</td>
	 * <td>Mon Sep 21 18:40:15 CEST 2015</td>
	 * </tr>
	 * <tr>
	 * <td>"18:40:15.123"</td>
	 * <td>Mon Sep 21 18:40:15 CEST 2015</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param timeString
	 * @return
	 */
	public static Date dateFromTimeString(String timeString) throws DateTimeParseException {

		logger.debug("-----------------------------------");
		logger.debug("timeString         = {}", timeString);

		LocalTime localTime = LocalTime.parse(timeString);
		logger.debug("localTime          = {}", localTime);

		/*
		* Combine the local time with the LocalDate in the local time
		* zone. It seems to do the job.
		*/
		ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.now(),
				localTime, ZoneId.systemDefault());
		logger.debug("zonedDateTime = {}", zonedDateTime);

		Date unmarshalledDate = Date.from(zonedDateTime.toInstant());
		logger.debug("unmarshalledDate = {}", unmarshalledDate);

		return unmarshalledDate;
	}

	public static Date dateFromBirtTimeString(String timeString) {
		return dateFromTimeString(timeString);
	}

	/**
	 * Performs the opposite operation as {@link #dateFromBirtDateString}.
	 * 
	 * This method take the output of {@link #dateFromBirtDateString} and return
	 * a string that is equivalent to that originally used.
	 * 
	 * @param date
	 * @return
	 */
	public static String birtTimeStringFromDate(Date date) {

		//TODO Make this a private static final field.
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		if (date != null) {
			//try {
			return format.format(date);
			//} catch (Exception e) {
			//	logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			//	return (String) null;
			//}
		} else {
			return (String) null;
		}

	}

	/**
	 * Oddly, the string representation of an Entity Date field annotated with
	 * {@literal @}Temporal(TemporalType.TIMESTAMP) is quite different than the
	 * string representation of a "normal" Java {@link Date} instance. Somehow,
	 * the underlying Date objects are different, but the details are not clear.
	 * The <b>string representation</b> of such a {@link Date} returned from an
	 * entity has no time zone information at all, but a "normal" Java
	 * {@link Date} does. This is likely related to the fact that timestamps in
	 * the database are stored in columns of type "timestamp without time zone",
	 * i.e., time zone information has been discarded. Examples of each are:
	 * 
	 * <ul>
	 * <li><code>timestampFromEntity.toString()</code> ->
	 * "2015-05-30 22:00:00.0"
	 * <li><code>normalDate.toString()</code> -> "Sat May 30 22:00:00 CEST 2015"
	 * </ul>
	 * 
	 * In order to compare objects of these "types" (technically, they are the
	 * same Java type) and assert that they are "equal" in a test, the Date
	 * returned from the entity should be converted to a more "normal" Java Date
	 * using this method.
	 * 
	 * @param entityTimestamp
	 * @return
	 */
	public static Date entityTimestampToNormalDate(Date entityTimestamp) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(entityTimestamp);
		return calendar.getTime();
	}

	/**
	 * Converts the Date field of a JPA entity class instance (which, by
	 * convention we have decided will always be interpreted relative to UTC) to
	 * a normal java.util.Date that can be used by the server in its default
	 * time zone.
	 * 
	 * @param entityTimestamp
	 * @return
	 */
	public static Date entityTimestampToServerTimezoneDate(Date entityTimestamp) {

		//	logger.debug("entityTimestamp    = {}", entityTimestamp);
		//	//System.out.println(String.format("entityTimestamp = %s", entityTimestamp));
		//
		//	/*
		//	 * Convert entityTimestamp to a LocalDateTime at the server's (default)
		//	 * time zone. This preserves the date and the time values; It just 
		//	 * strips the time zone information. Example:
		//	 * 
		//	 *  entityTimestamp.toString()      localDateTimeServerTZ.toString()
		//	 *  ------------------------------   ------------------------------
		//	 *  2015-10-04 20:45:00.0         -> 2015-10-04T20:45
		//	 */
		//	LocalDateTime localDateTimeServerTZ = LocalDateTime.ofInstant(entityTimestamp.toInstant(),
		//			ZoneId.systemDefault());
		//	logger.debug("localDateTimeServerTZ   = {}", localDateTimeServerTZ);
		//	//System.out.println(String.format("localDateTimeServerTZ = %s", localDateTimeServerTZ));
		//
		//	/*
		//	 * Convert localDateTimeServerTZ to a ZonedDateTime at UTC/GMT. Examples:
		//	 * 
		//	 *  localDateTimeServerTZ.toString() zonedDateTimeUTC.toString()
		//	 *  ------------------------------   ------------------------------
		//	 *  2015-10-04T20:45              -> 2015-10-04T20:45Z
		//	 */
		//	ZonedDateTime zonedDateTimeUTC = localDateTimeServerTZ.atZone(ZoneId.of("Z"));
		//	logger.debug("zonedDateTimeUTC = {}", zonedDateTimeUTC);
		//	//System.out.println(String.format("zonedDateTimeUTC = %s", zonedDateTimeUTC));
		//
		//	/*
		//	 * Convert zonedDateTimeUTC to a java.util.Date relative to the time 
		//	 * zone where the report server is located. Examples:
		//	 * 
		//	 *  zonedDateTimeUTC.toString()      dateAtServer.toString()
		//	 *  ------------------------------   -----------------------------
		//	 *  2015-10-04T20:45Z             -> Sun Oct 04 22:45:00 CEST 2015
		//	 */
		//	Date dateAtServer = Date.from(zonedDateTimeUTC.toInstant());
		//	logger.debug("dateAtServer       = {}", dateAtServer);
		//	//System.out.println(String.format("dateAtServer = %s", dateAtServer));
		//	return dateAtServer;

		return entityTimestampToServerTimezoneDate(entityTimestamp, ZoneId.of("Z"));
	}

	/**
	 * Converts the Date field of a JPA entity class instance to a normal
	 * java.util.Date that can be used by the server in its default time zone.
	 * 
	 * It differs from {@link #entityTimestampToServerTimezoneDate(Date)}
	 * (which, by convention assumes that the entity Date will be interpreted
	 * relative to UTC) in that it assumes that the entity Date will be
	 * interpreted relative to the time zone specified by zoneId.
	 * 
	 * @param entityTimestamp
	 * @param zoneId
	 * @return
	 */
	// GET entityTimestampToServerTimezoneDate(Date) TO CALL THIS METHOD, USING THE APPROPRIATE ZoneId FOR UTC/ZULU
	public static Date entityTimestampToServerTimezoneDate(Date entityTimestamp, ZoneId entityDateZoneId) {
		logger.debug("entityDateZoneId   = {}", entityDateZoneId);
		logger.debug("entityTimestamp    = {}", entityTimestamp);
		//System.out.println(String.format("entityTimestamp = %s", entityTimestamp));

		/*
		 * Notes about the example below:
		 * 
		 *   1. The "entity time zone" is "Canada/Pacific", i.e., 
		 *      entityTimestamp (which the value of a java.util.Date field of an
		 *      entity class instance) represents a date and time in the time 
		 *      zone "Canada/Pacific".
		 *   
		 *   2. For the entityTimestamp value used in this example, the time
		 *      here in Trondheim where the server is located is 9 hours ahead
		 *      of the instant represented by entityTimestamp.
		 */

		/*
		 * Convert entityTimestamp to a LocalDateTime at the server's (default)
		 * time zone. This preserves the date and the time values; It just 
		 * strips the time zone information. Example:
		 * 
		 *  entityTimestamp.toString()      localDateTimeServerTZ.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-10-08 06:00:00.0         -> 2015-10-08T06:00
		 */
		LocalDateTime localDateTimeServerTZ = LocalDateTime.ofInstant(entityTimestamp.toInstant(),
				ZoneId.systemDefault());
		logger.debug("localDateTimeServerTZ   = {}", localDateTimeServerTZ);
		//System.out.println(String.format("localDateTimeServerTZ = %s", localDateTimeServerTZ));

		/*
		 * Convert localDateTimeServerTZ to a ZonedDateTime at UTC/GMT. Examples:
		 * 
		 *  localDateTimeServerTZ.toString() zonedDateTimeEntityDateZoneId.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-10-08T06:00              -> 2015-10-08T06:00-07:00[Canada/Pacific]
		 */
		ZonedDateTime zonedDateTimeEntityDateZoneId = localDateTimeServerTZ.atZone(entityDateZoneId);
		logger.debug("zonedDateTimeEntityDateZoneId = {}", zonedDateTimeEntityDateZoneId);
		//System.out.println(String.format("zonedDateTimeEntityDateZoneId = %s", zonedDateTimeEntityDateZoneId));

		/*
		 * Convert zonedDateTimeEntityDateZoneId to a java.util.Date relative to the time 
		 * zone where the report server is located. Examples:
		 * 
		 *  zonedDateTimeEntityDateZoneId.toString()      dateAtServer.toString()
		 *  ------------------------------   -----------------------------
		 *  2015-10-08T06:00-07:00[Canada/Pacific]            -> Thu Oct 08 15:00:00 CEST 2015
		 */
		Date dateAtServer = Date.from(zonedDateTimeEntityDateZoneId.toInstant());
		logger.debug("dateAtServer       = {}", dateAtServer);
		//System.out.println(String.format("dateAtServer = %s", dateAtServer));

		return dateAtServer;
	}

	/**
	 * Convert a normal {@link javal.util.Date} to a new {@link javal.util.Date}
	 * , but such that when it is serialized by
	 * {@link DatetimeAdapter#marshal(Date)} the resulting string will correctly
	 * represent the original date in ISO-8601 format relative to "Zulu" time.
	 * 
	 * @param date
	 * @return
	 */
	public static Date normalDateToUtcTimezoneDate(Date date) {
		logger.debug("date    = {}", date);
		//System.out.println(String.format("date = %s", date));

		/*
		 * Convert date to a LocalDateTime relative to UTC. Example:
		 * (Note: This example uses a date where summer time is in effect)
		 * 
		 *  date.toString()                  localDateTimeUtc.toString()
		 *  ------------------------------   ------------------------------
		 *  Sun Oct 04 20:45:00 CEST 2015 -> 2015-10-04T18:45
		 */
		LocalDateTime localDateTimeUtc = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Z"));
		logger.debug("localDateTimeUtc   = {}", localDateTimeUtc);
		//System.out.println(String.format("localDateTimeUtc = %s", localDateTimeUtc));

		/*
		 * Convert localDateTimeUtc to a ZonedDateTime at the server's time 
		 * zone. Example:
		 * 
		 *  localDateTimeUtc.toString()      zonedDateTimeServerTZ.toString()
		 *  ------------------------------   ------------------------------
		 *  2015-10-04T18:45              -> 2015-10-04T18:45+02:00[Europe/Oslo]
		 */
		ZonedDateTime zonedDateTimeServerTZ = localDateTimeUtc.atZone(ZoneId.systemDefault());
		logger.debug("zonedDateTimeServerTZ = {}", zonedDateTimeServerTZ);
		//System.out.println(String.format("zonedDateTimeServerTZ = %s", zonedDateTimeServerTZ));

		/*
		 * Convert zonedDateTimeServerTZ to a java.util.Date relative to the 
		 * time zone where the report server is located. Example:
		 * 
		 *  zonedDateTimeServerTZ.toString()       dateAtServer.toString()
		 *  ------------------------------         -----------------------------
		 *  2015-10-04T18:45+02:00[Europe/Oslo] -> Sun Oct 04 18:45:00 CEST 2015
		 */
		Date dateAtUtc = Date.from(zonedDateTimeServerTZ.toInstant());
		logger.debug("dateAtServer       = {}", dateAtUtc);
		//System.out.println(String.format("dateAtUtc = %s", dateAtUtc));

		return dateAtUtc;
	}

	/**
	 * This performs the same function as method
	 * {@link #entityTimestampToNormalDate}, but for entity class attributes
	 * annotated with {@literal @}Temporal(TemporalType.Date)}. This method just
	 * delegates to {@link #entityTimestampToNormalDate} but it is useful to
	 * have different methods as markers for the different TemporalType's, in
	 * case attributes annoated with {@literal @}Temporal(TemporalType.Date)}
	 * requires a different treatment than objects annotated with {@literal @}
	 * Temporal(TemporalType.Timestamp)} in the furture.
	 * 
	 * @param entityDate
	 * @return
	 */
	public static Date entityDateToNormalDate(Date entityDate) {
		return entityTimestampToNormalDate(entityDate);
	}

	/**
	 * Returns true if the time portion of the two Date vales are equal.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean timePartsEqual(Date date1, Date date2) {
		logger.debug("date1 = {}, date2 = {}", date1, date2);

		/*
		 * date1 and/or date2 may come from a Date field of an entity class that
		 * is annotated with: @Temporal(TemporalType.TIMESTAMP). Such fields
		 * are stored in an SQL database column of type "time without time zone".
		 * When a value from such a column is loaded into a java.util.Date 
		 * object, the string representation of this Date object is only the 
		 * time - it does not have any date information. For example, we could 
		 * have the situation:
		 * 
		 *   date1.tostring() = "16:17:18"
		 *   date2.tostring() = "Sat Jan 01 16:17:18 CET 2000"
		 * 
		 * I don't know what is  going on behind the scenes for this to be 
		 * possible, but that is what happens. Unfortunately, for such Dates, an
		 * exception is thrown if you try to execute:
		 * 
		 *   date1.toInstant()
		 * 
		 * for this example where date1 is missing its date information. To 
		 * avoid this possibility, we compute new Dates, calendar1Date1 & 
		 * calendar2Date2 via Calendar objects. If date1 or date2 is missing its
		 * date information, this will set it to Jan 1, 1970. Then we *can* call
		 * the toInstant() method of these temporary interim Date objects.
		 * 
		 * All this work is done so that we can eventually create LocalTime
		 * objects for both of the dates passed to this method so that we can
		 * compare them for equality. It is a lot of work.
		 * 
		 * Using Joda time was much simpler (see the code below that is 
		 * commented out).
		 */

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date1);
		Date calendar1Date1 = calendar1.getTime();
		logger.debug("calendar1Date1 = {}", calendar1Date1);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date2);
		Date calendar2Date2 = calendar2.getTime();
		logger.debug("calendar2Date2 = {}", calendar2Date2);

		Instant instant1 = calendar1Date1.toInstant();
		Instant instant2 = calendar2Date2.toInstant();
		logger.debug("instant1 = {}, instant2 = {}", instant1, instant2);

		ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(instant1, ZoneId.systemDefault());
		ZonedDateTime zonedDateTime2 = ZonedDateTime.ofInstant(instant2, ZoneId.systemDefault());
		logger.debug("zonedDateTime1 = {}, zonedDateTime2 = {}", zonedDateTime1, zonedDateTime2);
		LocalTime localTime1 = zonedDateTime1.toLocalTime();
		LocalTime localTime2 = zonedDateTime2.toLocalTime();
		logger.debug("localTime1 = {}, localTime2 = {}", localTime1, localTime2);
		logger.debug("localTime1.equals(localTime2) = {}", localTime1.equals(localTime2));

		return localTime1.equals(localTime2);

		// /*
		// * Old code that uses Joda time:
		// *
		// * Convert the two dates to Joda LocalTime objects so they can be
		// * compared. Joda time is used here because Java 7 does not have
		// * convenient methods for dealing with times that are not associated
		// * with an instant in time. I suppose that I could have managed this
		// * with standard Java 7 methods, but it is easier to just use Joda
		// time.
		// */
		// org.joda.time.LocalTime lt1 = new org.joda.time.LocalTime(date1);
		// org.joda.time.LocalTime lt2 = new org.joda.time.LocalTime(date2);
		// return lt1.equals(lt2);
	}

	/**
	 * Convert a Date value that comes from an entity class instance field that
	 * is stored in an SQL database column of type "time without time zone".
	 * 
	 * @param entityTimeDate
	 * @return
	 */
	public static LocalTime localTimeFromEntityTimeDate(Date entityTimeDate) {
		logger.debug("entityTimeDate = {}", entityTimeDate);

		/*
		 * entityTimeDate may come from a Date field of an entity class that
		 * is annotated with: @Temporal(TemporalType.TIMESTAMP). Such fields
		 * are stored in an SQL database column of type "time without time zone".
		 * When a value from such a column is loaded into a java.util.Date 
		 * object, the string representation of this Date object is only the 
		 * time - it does not have any date information. For example, we could 
		 * have the situation:
		 * 
		 *   entityTimeDate.tostring() = "16:17:18"
		 * 
		 * On the other hand, if a "normal" java.util.Date object is passed to
		 * this method as entityTimeDate, we could have, e.g.,
		 * 
		 *   entityTimeDate.tostring() = "Sat Jan 01 16:17:18 CET 2000"
		 * 
		 * I don't know what is going on behind the scenes for this to be 
		 * possible, but that is what happens. Unfortunately, for such Dates
		 * that appeat to only hold a time value, an exception is thrown if you 
		 * try to execute:
		 * 
		 *   entityTimeDate.toInstant()
		 * 
		 * To avoid this, we compute new a Date here, calendarDate via a 
		 * Calendar object.
		 * 
		 * All this work is done so that we can eventually create a LocalTime
		 * object. It is a lot of work.
		 */

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(entityTimeDate);
		Date calendarDate = calendar.getTime();
		logger.debug("calendarDate = {}", calendarDate);

		Instant instant = calendarDate.toInstant();
		logger.debug("instant = {}", instant);

		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		logger.debug("zonedDateTime = {}", zonedDateTime);

		LocalTime localTime = zonedDateTime.toLocalTime();
		logger.debug("localTime = {}", localTime);

		return localTime;
	}

	public static void main(String[] args) {

		System.out.println("DateUtils.nowUtc()  = " + DateUtils.nowUtc());

		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000Z\")       = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000Z"));
		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000+00:00\")  = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000+00:00"));
		System.out.println("DateUtils.dateUtcFromIso8601String(\"2015-05-30T12:00:00.000+02:00\")  = "
				+ DateUtils.dateUtcFromIso8601String("2015-05-30T12:00:00.000+02:00"));

		System.out.println("DateUtils.dateFromDateStringWithoutTime(\"1958-05-06\")      = "
				+ DateUtils.dateFromDateStringWithoutTime("1958-05-06"));

		System.out.println("DateUtils.dateFromTimeString(\"18:40:15\")      = "
				+ DateUtils.dateFromTimeString("18:40:15"));
		System.out.println("DateUtils.dateFromTimeString(\"18:40:15.123\")  = "
				+ DateUtils.dateFromTimeString("18:40:15.123"));

		System.out.println("");
		Date date1 = DateUtils.dateFromBirtDatetimeString("1958-05-06 12:30:59");
		System.out.println("DateUtils.dateFromBirtDatetimeString(\"1958-05-06 12:30:59\")        = " + date1);
		System.out.println("DateUtils.birtDatetimeStringFromDate(" + date1 + ") = "
				+ DateUtils.birtDatetimeStringFromDate(date1));
		Date date7 = DateUtils.dateFromBirtDatetimeString("1958-05-06 12:30:59.9");
		System.out.println("DateUtils.dateFromBirtDatetimeString(\"1958-05-06 12:30:59:9\")      = " + date7);
		System.out.println("DateUtils.birtDatetimeStringFromDate(" + date7 + ") = "
				+ DateUtils.birtDatetimeStringFromDate(date7));
		Date date2 = DateUtils.dateFromBirtDatetimeString("1958-05-06 12:30:59.999");
		System.out.println("DateUtils.dateFromBirtDatetimeString(\"1958-05-06 12:30:59:999\")    = " + date2);
		System.out.println("DateUtils.birtDatetimeStringFromDate(" + date2 + ") = "
				+ DateUtils.birtDatetimeStringFromDate(date2));

		System.out.println("");
		Date date3 = DateUtils.dateFromBirtDateString("1958-05-06");
		System.out.println("DateUtils.dateFromBirtDateString(\"1958-05-06\")                 = " + date3);
		System.out.println("DateUtils.birtDateStringFromDate(" + date3 + ") = "
				+ DateUtils.birtDateStringFromDate(date3));

		System.out.println("");
		Date date4 = DateUtils.dateFromBirtTimeString("12:30:59");
		System.out.println("DateUtils.dateFromBirtTimeString(\"12:30:59\")                    = " + date4);
		System.out.println("DateUtils.birtTimeStringFromDate(" + date4 + ") = "
				+ DateUtils.birtTimeStringFromDate(date4));
		Date date6 = DateUtils.dateFromBirtTimeString("12:30:59.9");
		System.out.println("DateUtils.dateFromBirtTimeString(\"12:30:59:9\")                  = " + date6);
		System.out.println("DateUtils.birtTimeStringFromDate(" + date6 + ") = "
				+ DateUtils.birtTimeStringFromDate(date6));
		Date date5 = DateUtils.dateFromBirtTimeString("12:30:59.999");
		System.out.println("DateUtils.dateFromBirtTimeString(\"12:30:59:999\")                = " + date5);
		System.out.println("DateUtils.birtTimeStringFromDate(" + date5 + ") = "
				+ DateUtils.birtTimeStringFromDate(date5));

		System.out.println("");
		date1 = DateUtils.dateFromBirtDatetimeString("2015-10-04 20:45:00");
		date2 = entityTimestampToServerTimezoneDate(date1);
		System.out.println(String.format("entityTimestampToServerTimezoneDate(%s) = %s", date1, date2));

		//System.out.println("");
		date1 = DateUtils.dateFromBirtDatetimeString("2015-10-04 20:45:00");
		date2 = normalDateToUtcTimezoneDate(date1);

		System.out.println("");
		String localDatetimeString = null;
		localDatetimeString = "2015-10-04T20:45:00";
		System.out.println(String.format("dateServerTZFromLocalDatetimeString(\"%s\") = %s", localDatetimeString,
				dateServerTZFromLocalDatetimeString(localDatetimeString)));
		localDatetimeString = "2015-10-04T20:45:00.001";
		System.out.println(String.format("dateServerTZFromLocalDatetimeString(\"%s\") = %s", localDatetimeString,
				dateServerTZFromLocalDatetimeString(localDatetimeString)));
		localDatetimeString = "2015-10-04T20:45:00.01";
		System.out.println(String.format("dateServerTZFromLocalDatetimeString(\"%s\") = %s", localDatetimeString,
				dateServerTZFromLocalDatetimeString(localDatetimeString)));
		localDatetimeString = "2015-10-04T20:45:00.1";
		System.out.println(String.format("dateServerTZFromLocalDatetimeString(\"%s\") = %s", localDatetimeString,
				dateServerTZFromLocalDatetimeString(localDatetimeString)));
		//localDatetimeString = "2015-10-04T20:45:00Z"; // <- DateTimeParseException thrown
		//System.out.println(String.format("dateServerTZFromLocalDatetimeString(\"%s\") = %s", localDatetimeString,
		//		dateServerTZFromLocalDatetimeString(localDatetimeString)));

		String utcDateString;
		Date utcDate;
		String recoveredString;

		utcDateString = "2015-05-30T12:00:00.000Z";
		utcDate = dateUtcFromIso8601String(utcDateString);
		recoveredString = Iso8601StringFromUtcDate(utcDate);
		logger.info("\nutcDateString   = {}, \nutcDate         = {}, \nrecoveredString = {}",
				utcDateString, utcDate, recoveredString);

		utcDateString = "2015-05-30T12:00:00.000+00:00";
		utcDate = dateUtcFromIso8601String(utcDateString);
		recoveredString = Iso8601StringFromUtcDate(utcDate);
		logger.info("\nutcDateString   = {}, \nutcDate         = {}, \nrecoveredString = {}",
				utcDateString, utcDate, recoveredString);

		utcDateString = "2015-05-30T12:00:00.000+02:00";
		utcDate = dateUtcFromIso8601String(utcDateString);
		recoveredString = Iso8601StringFromUtcDate(utcDate);
		logger.info("\nutcDateString   = {}, \nutcDate         = {}, \nrecoveredString = {}",
				utcDateString, utcDate, recoveredString);

		String localDateTimeString;
		Date serverTZDate;

		localDateTimeString = "1961-11-04T16:00:00.001";
		serverTZDate = dateServerTZFromLocalDatetimeString(localDateTimeString);
		recoveredString = localDateTimeStringFromServerTZDate(serverTZDate);
		logger.info("\nlocalDateTimeString = {}, \nserverTZDate        = {}, \nrecoveredString     = {}",
				localDateTimeString, serverTZDate, recoveredString);

	}
}
