package com.qfree.obo.report.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
		 *   
		 * This LocalDateTime can now be expressed relative to the default
		 * time zone.
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
	 * Oddly, the string representation of an Entity Date field annotated with
	 * {@literal @}Temporal(TemporalType.TIMESTAMP) is quite different than the 
	 * string representation of a "normal" Java Date instance. Somehow, the 
	 * underlying Date objects are different, but the details are not clear. Th
	 * STRING REPRESENTATION of such a Date returned from an entity has no time 
	 * zone information at all, but a "normal" Java does. Examples of each are
	 * <pre><code>timestampFromEntity.toString()  -> "2015-05-30 22:00:00.0"
	 *normalDate.toString()      -> "Sat May 30 22:00:00 CEST 2015"</code></pre>
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
	 * This performs the same function as method 
	 * {@link #entityTimestampToNormalDate}, but for entity class attributes
	 * annotated with {@literal @}Temporal(TemporalType.Date)}. This method just
	 * delegates to {@link #entityTimestampToNormalDate} but it is useful to 
	 * have different methods as markers for the different TemporalType's, in
	 * case attributes annoated with {@literal @}Temporal(TemporalType.Date)} 
	 * requires a different  treatment than objects annotated with 
	 * {@literal @}Temporal(TemporalType.Timestamp)} in the furture.
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
		 * date1 and/or date2 may come from a Date filed of an entity class that
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

}
