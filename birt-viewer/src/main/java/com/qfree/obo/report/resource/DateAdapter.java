package com.qfree.obo.report.resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(DateAdapter.class);

	private SimpleDateFormat format;

	public DateAdapter() {
		format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		/*
		 * Don't set the time zone here. It will shift the hours based on the
		 * difference from our client (or maybe server?) local time zone from 
		 * UTC".
		 */
		//		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		/* If we do not set "lenient" to false here, then the dateAsString 
		 * "1958-05-06" will be parsed without throwing an exception even if 
		 * the format string is set to "yyyyMMdd". However, in this case it will
		 * not be parsed to the correct date!  This is terrible, because no 
		 * error will be raised and execution will continue as if a different 
		 * date was specified.  The setLenient(false) call here ensures that the
		 * date string strictly adheres to the specified format string.
		 */
		//		format.setLenient(false);
	}

	@Override
	public String marshal(Date date) throws Exception {
		logger.info("date = {}", date);

		if (date != null) {
			try {
				return format.format(date);
			} catch (Exception e) {
				logger.warn("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
				return (String) null;
				//			return "";
			}
		} else {
			return (String) null;
			//			return "";
		}
	}

	/**
	 * This method is used to unmarshal Datetimes specified as strings in JSON
	 * resources to Java Date's. The JSON objects are are submitted to this
	 * application via HTTP POST or PUT. They can be expressed in any time zone,
	 * as long as the ofset from GMT is explicitly specified, either via "Z" or
	 * "±hh:mm". Examples:
	 * 
	 *  String from JSON object      Value stored in PostgreSQL timestamp column
	 * 
	 * "1958-05-06T12:00:00.000Z"         1958-05-06 12:00:00
	 * "1958-05-06T12:00:00.000+02:00"    1958-05-06 10:00:00
	 * "1958-05-06T12:00:00.000+04:00"    1958-05-06 08:00:00
	 * 
	 * Note that the value stored in the PostgreSQL timestamp (without timezone)
	 * column is always in UTC/GMT time.
	 */
	@Override
	public Date unmarshal(String utcDateAsString) {
		logger.info("dateAsString = {}", utcDateAsString);

		if (utcDateAsString != null && !utcDateAsString.equals("")) {
			try {

				//				Calendar calendar = DatatypeConverter.parseDateTime(utcDateAsString);
				//				//				calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
				//				return calendar.getTime();

				DateTime datetime = DateTime.parse(utcDateAsString).withZone(DateTimeZone.UTC);
				return datetime.toLocalDateTime().toDate();

			} catch (IllegalArgumentException e) {
				logger.warn("Exception caught converting String to Calendar. String = \"{}\". Exception = ",
						utcDateAsString,
						e);
				return (Date) null;
			}
			//			try {
			//				return format.parse(dateAsString);
			//			} catch (ParseException e) {
			//				logger.warn("Failed to parse string '{}'. Exception: {}", dateAsString, e);
			//				return (Date) null;
			//			}
		} else {
			return (Date) null;
		}
	}

}