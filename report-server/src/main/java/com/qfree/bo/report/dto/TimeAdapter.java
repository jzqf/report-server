package com.qfree.bo.report.dto;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.DateUtils;

/**
 * Adapter class to serialize Java Date objects as strings that represent only
 * the time portion of the Date. This class also accepts strings of this format
 * and deserializes them into Java Date objects.
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
public class TimeAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(TimeAdapter.class);

	private SimpleDateFormat format;

	public TimeAdapter() {
		format = new SimpleDateFormat("HH:mm:ss.SSS");
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

	/**
	 * Converts a java.util.date to a string that represents only the time
	 * portion of the date.
	 */
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
	 * This method unmarshals a String containing a time, e.g., "18:40:15" or
	 * "18:40:15.123" to Java Date objects.
	 * 
	 * The String comes from an attribute of a JSON resources. The JSON objects
	 * are are submitted to this application via HTTP POST or PUT.
	 */
	@Override
	public Date unmarshal(String timeString) {
		logger.debug("timeString = {}", timeString);

		if (timeString != null && !timeString.equals("")) {
			try {
				return DateUtils.dateFromTimeString(timeString);
			} catch (DateTimeParseException e) {
				logger.error("Exception caught converting timeString to Date. timeString = \"{}\". Exception = ",
						timeString, e);
				return (Date) null;
			}
		} else {
			return (Date) null;
		}
	}

}