package com.qfree.obo.report.dto;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class to serialize Java Date objects as strings that are formatted
 * dates without a time portion. This class also accepts strings of this format
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
public class DateAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(DateAdapter.class);

	private SimpleDateFormat format;

	public DateAdapter() {
		format = new SimpleDateFormat("yyyy-MM-dd");
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
		logger.debug("java.util.Date to marshal = {}", date);

		if (date != null) {
			try {
				return format.format(date);
			} catch (Exception e) {
				logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
				return (String) null;
			}
			// LocalDate localDate =
			// date.toInstant().atZone(ZoneId.of("Z")).toLocalDate();
			// logger.debug("Date converted to LocalDate = {}", localDate);
			// return localDate.toString();
		} else {
			return (String) null;
		}
	}

	/**
	 * This method is used to unmarshal dates specified as strings in JSON
	 * resources to Java Date's. The JSON objects are are submitted to this
	 * application via HTTP POST or PUT. They can be expressed in any time zone,
	 * as long as the offset from GMT is explicitly specified, either via "Z" or
	 * "±hh:mm". Examples:
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
	 * <td>"1958-05-06"</td>
	 * <td>1958-05-06</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * <p>
	 * Note that the value stored in the PostgreSQL timestamp (without timezone)
	 * column is always in UTC/GMT time.
	 */
	@Override
	public Date unmarshal(String dateStringWithoutTime) {
		logger.debug("dateWithoutTime = {}", dateStringWithoutTime);

		if (dateStringWithoutTime != null && !dateStringWithoutTime.isEmpty()) {
			try {

				LocalDate localDate = LocalDate.parse(dateStringWithoutTime);
				logger.debug("localDate = {}", localDate);
				/*
				 * Convert LocalDate to Date, assuming the instant in time is
				 * 
				 */
				Date unmarshalledDate = Date.from(localDate.atStartOfDay(ZoneId.of("Z")).toInstant());
				logger.debug("unmarshalled java.util.Date = {}", unmarshalledDate);
				return unmarshalledDate;

			} catch (DateTimeParseException e) {
				logger.error(
						"Exception caught converting dateStringWithoutTime to Date. dateStringWithoutTime = \"{}\". Exception = ",
						dateStringWithoutTime, e);
				return (Date) null;
			}
		} else {
			return (Date) null;
		}
	}

}