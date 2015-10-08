package com.qfree.obo.report.dto;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.scheduling.schedulers.SubscriptionScheduler;
import com.qfree.obo.report.util.DateUtils;

/**
 * Adapter class for the SubscriptionResource.deliveryDatetimeRunAt field to serialize Java
 * Date objects as strings as well as deserializes strings into Java Date
 * objects.
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
public class SubscriptionRunAtDateTimeAdapter extends XmlAdapter<String, Date> {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionRunAtDateTimeAdapter.class);

	private final SimpleDateFormat format_ISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private final SimpleDateFormat format_LocalDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@Override
	public String marshal(Date date) throws Exception {
		logger.debug("date = {}", date);

		if (date != null) {
			try {
				if (SubscriptionScheduler.RUNAT_ENTITY_DATE_TZ_DYNAMIC) {
					return format_LocalDateTime.format(date);
				} else {
					return format_ISO8601.format(date);
				}
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
	 * via HTTP POST or PUT.
	 * 
	 * <p>
	 * The format expected for the sting encoding of the datetimes depends on
	 * the configuration parameter
	 * {@link SubscriptionScheduler#RUNAT_ENTITY_DATE_TZ_DYNAMIC}:
	 * 
	 * <p>
	 * <code>true</code>: The string must be expressed in a format that can be
	 * parse by {@link LocalDateTime#parse(CharSequence)}, e.g.,
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
				if (SubscriptionScheduler.RUNAT_ENTITY_DATE_TZ_DYNAMIC) {
					String localDateTimeAsString = dateAsString;
					return DateUtils.dateServerTZFromLocalDatetimeString(localDateTimeAsString);
				} else {
					String utcDateAsString = dateAsString;
					return DateUtils.dateUtcFromIso8601String(utcDateAsString);
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