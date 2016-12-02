package com.qfree.bo.report.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.util.DateUtils;

/**
 * Adapter class for resource fields that represent BIRT report parameter
 * datetime values.
 * 
 * <p>
 * Two behaviours are supported. Both assume that the {@link java.util.Date} has
 * come from an entity class object where the value was stored in a database
 * column of type "timestamp without time zone". The difference is how the value
 * should be interpretted:
 * 
 * <ol>
 * <li>The date and time portions are relative to UTC.</li>
 * <li>The date and time portions are expressed in the local, default time zone
 * for the server.</li>
 * </ol>
 * 
 * <p>
 * The behaviour impplemented here depends on the configuration parameter
 * {@link #DATETIME_REPORT_PARAMETERS_NO_TIMEZONE}.
 * 
 * <p>
 * This class serializes {@link java.util.Date} objects as strings as well as
 * deserializes strings into Java Date objects.
 * 
 * <p>
 * Note: According to the Javadoc for the marshal & unmarshal methods, if
 * there's an error during the conversion, the exception will be eaten . The
 * caller is responsible for reporting the error to the user through
 * ValidationEventHandler. I just log any exceptions that are caught in these
 * methods below.
 * 
 * @author jeffreyz
 *
 */
public class DatetimeReportParameterDateTimeAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(DatetimeReportParameterDateTimeAdapter.class);

	private static final boolean DATETIME_REPORT_PARAMETERS_NO_TIMEZONE = true;

	/**
	 * This method marshals a {@link Date} to a string that will be returned as
	 * a JSON attribute of a ReST resource object.
	 */
	@Override
	public String marshal(Date date) throws Exception {
		logger.debug("date = {}", date);

		try {
			if (DATETIME_REPORT_PARAMETERS_NO_TIMEZONE) {
				return DateUtils.localDateTimeStringFromServerTZDate(date);
			} else {
				return DateUtils.Iso8601StringFromUtcDate(date);
			}
		} catch (Exception e) {
			logger.error("Exception caught formatting date '{}'. Exception: ", date.toString(), e);
			return (String) null;
		}
	}

	/**
	 * This method unmarshals datetimes specified as strings in JSON resources
	 * to Java Date's. The JSON objects are are submitted to this application
	 * via HTTP POST or PUT.
	 * 
	 * <p>
	 * <code>true</code>: The string must be expressed in a format that can be
	 * parsed by {@link LocalDateTime#parse(CharSequence)}, e.g.,
	 * "2015-11-29T10:15:30". If the Date returned from this method is stored in
	 * a PostgreSQL timestamp (without timezone) column, it will be assumed to
	 * be in the default time zone of the report server.
	 * 
	 * <p>
	 * <code>false</code>: The string must be expressed in ISO-8601 format in
	 * any time zone, as long as the offset from GMT is explicitly specified,
	 * either via "Z" or "±hh:mm". If the Date returned from this method is
	 * stored in a PostgreSQL timestamp (without timezone) column, it will be in
	 * UTC/GMT time. For more details regarding this case, see
	 * {@link DatetimeAdapter#unmarshal(String)}.
	 */
	@Override
	public Date unmarshal(String dateAsString) {
		logger.debug("utcDateAsString = {}", dateAsString);

		if (dateAsString != null && !dateAsString.equals("")) {
			try {
				if (DATETIME_REPORT_PARAMETERS_NO_TIMEZONE) {
					return DateUtils.dateServerTZFromLocalDatetimeString(dateAsString);
				} else {
					return DateUtils.dateUtcFromIso8601String(dateAsString);
				}
			} catch (DateTimeParseException e) {
				logger.error(
						"Exception caught converting utcDateAsString to Date. utcDateAsString= \"{}\". Exception = ",
						dateAsString, e);
				return (Date) null;
			}
		} else {
			return (Date) null;
		}
	}

}