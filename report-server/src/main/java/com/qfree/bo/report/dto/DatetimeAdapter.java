package com.qfree.bo.report.dto;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.DateUtils;

/**
 * Adapter class to serialize Java Date objects as strings that are formatted
 * according to the ISO-8601 standard for UTC. This class also accepts strings
 * of this format and deserializes them into Java Date objects.
 * 
 * Note: According to the Javadoc for the marshal & unmarshal methods, if
 * there's an error during the conversion, the exception will be eaten . The
 * caller is responsible for reporting the error to the user through
 * ValidationEventHandler. I just log any exceptions that are caught in these
 * methods below.
 * 
 * @author jeffreyz
 *
 */
public class DatetimeAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(DatetimeAdapter.class);

	private SimpleDateFormat format;

	public DatetimeAdapter() {
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		/*
		 * Don't set the time zone here. It will shift the hours based on the
		 * difference from our client (or maybe server?) local time zone from 
		 * UTC".
		 */
		// format.setTimeZone(TimeZone.getTimeZone("UTC"));
		/* If we do not set "lenient" to false here, then the utcDateAsString 
		 * "1958-05-06" will be parsed without throwing an exception even if 
		 * the format string is set to "yyyyMMdd". However, in this case it will
		 * not be parsed to the correct date!  This is terrible, because no 
		 * error will be raised and execution will continue as if a different 
		 * date was specified.  The setLenient(false) call here ensures that the
		 * date string strictly adheres to the specified format string.
		 */
		// format.setLenient(false);
	}

	@Override
	public String marshal(Date date) throws Exception {
		logger.debug("date = {}", date);

		if (date != null) {
			try {
				return format.format(date);
			} catch (Exception e) {
				logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
				return (String) null;
			}
		} else {
			return (String) null;
		}
	}

	/**
	 * This method unmarshals datetimes specified as strings in JSON resources
	 * to Java Date's. The JSON objects are are submitted to this application
	 * via HTTP POST or PUT. They can be expressed in any time zone, as long as
	 * the offset from GMT is explicitly specified, either via "Z" or "±hh:mm".
	 * 
	 * <p>
	 * Important: The string representing the datetime <b><i>must</i></b> have a
	 * time zone specified. See the second line in the table below where a time
	 * zone is not specified. The resulting timestamp value is not what is
	 * expected (a time component of 11:00:00). The reason why the result is
	 * 11:00:00 is probably because the following conversion occurs deep inside
	 * the JVM:
	 * 
	 * <p>
	 * When "1958-05-06T12:00:00.000" is used to create a Java Date, Java
	 * assumes that this is for the current time zone where this code is
	 * executing. In this case it is CEST = UTC+2. If this Java Date were to be
	 * converted to a string with .toString(), I believe the result would be:
	 * 
	 * <p>
	 * "Tue May 06 11:00:00 CET 1958",
	 * 
	 * <p>
	 * which is "correct, but referenced relative to CET, not CEST. When stored
	 * in a PostgreSQL "timestamp without timezone" column, the time zone is
	 * discarded, leaving:
	 * 
	 * <p>
	 * 1958-05-06 11:00:00
	 * 
	 * <p>
	 * Examples:
	 * 
	 * <p>
	 * <table>
	 * <caption>Unmarshalling example</caption> <thead>
	 * <tr>
	 * <td>String from JSON object</td>
	 * <td>Value stored in PostgreSQL timestamp column</td>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>"1958-05-06T12:00:00.000Z"</td>
	 * <td>1958-05-06 12:00:00</td>
	 * </tr>
	 * <tr>
	 * <td>"1958-05-06T12:00:00.000"</td>
	 * <td>1958-05-06 11:00:00</td>
	 * </tr>
	 * <tr>
	 * <td>"1958-05-06T12:00:00.000+02:00"</td>
	 * <td>1958-05-06 10:00:00</td>
	 * </tr>
	 * <tr>
	 * <td>"1958-05-06T12:00:00.000+04:00"</td>
	 * <td>1958-05-06 08:00:00</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * <p>
	 * Note that this method ensures that the value stored in a PostgreSQL
	 * timestamp (without timezone) column will always be in UTC/GMT time.
	 */
	@Override
	public Date unmarshal(String utcDateAsString) {
		logger.debug("utcDateAsString = {}", utcDateAsString);

		if (utcDateAsString != null && !utcDateAsString.equals("")) {
			try {
				return DateUtils.dateUtcFromIso8601String(utcDateAsString);
			} catch (DateTimeParseException e) {
				logger.error(
						"Exception caught converting utcDateAsString to Date. utcDateAsString= \"{}\". Exception = ",
						utcDateAsString, e);
				return (Date) null;
			}
		} else {
			return (Date) null;
		}
	}

}