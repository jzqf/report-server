package com.qfree.bo.report.apps;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDynamicDatetimeParameterValue {

	private static final Logger logger = LoggerFactory.getLogger(CreateDynamicDatetimeParameterValue.class);

	public CreateDynamicDatetimeParameterValue() {
	}

	public static void main(String[] args) {
		logger.info("\n\n");

		LocalTime localTimeForDatetimeValue = null;
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		localTimeForDatetimeValue = LocalTime.parse("23:59:59.999");

		Boolean getDurationSubtractOneDayForDates = true;

		Integer getYearNumber = null;
		Integer getYearsAgo = null;
		Integer getMonthNumber = null;
		Integer getMonthsAgo = null;
		Integer getWeeksAgo = null;
		Integer getDaysAgo = null;
		Integer getDayOfWeekNumber = null;
		Integer getDayOfMonthNumber = null;
		Integer getDayOfWeekInMonthOrdinal = null;
		Integer getDayOfWeekInMonthNumber = null;
		Integer getDurationToAddYears = null;
		Integer getDurationToAddMonths = null;
		Integer getDurationToAddWeeks = null;
		Integer getDurationToAddDays = null;
		Integer getDurationToAddHours = null;
		Integer getDurationToAddMinutes = null;
		Integer getDurationToAddSeconds = null;

		// Now:
		//		getDaysAgo = 0;

		// 24 hours ago:
		getDaysAgo = 1;

		// First day of current year:
		//		getMonthNumber = 1;
		//		getDayOfMonthNumber = 1;

		// First day of previous year:
		//		getYearsAgo = 1;
		//		getMonthNumber = 1;
		//		getDayOfMonthNumber = 1;

		// Start of first day of previous month:
		//		getMonthsAgo = 1;
		//		getDayOfMonthNumber = 1;

		// End of last day of previous month (solution #1 for datetimes):
		//		localTimeForDatetimeValue = LocalTime.parse("23:59:59.999");
		//		getMonthsAgo = 1;
		//		getDayOfMonthNumber = -1;

		// End of last day of previous month (solution #2 for datetimes):
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		getDayOfMonthNumber = 1;

		// Start of Monday of previous week:
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		getWeeksAgo = 1;
		//		getDayOfWeekNumber = 1;

		// Friday of previous week:
		//		getWeeksAgo = 1;
		//		getDayOfWeekNumber = 5;

		// End of Friday of previous week:
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		getWeeksAgo = 1;
		//		getDayOfWeekNumber = 6;

		// Start of first Monday in the current month:
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		getDayOfWeekInMonthOrdinal = 1;
		//		getDayOfWeekInMonthNumber = 1;

		// (End of) Friday after the first Monday in the current month:
		//		localTimeForDatetimeValue = LocalTime.parse("00:00:00");
		//		getDayOfWeekInMonthOrdinal = 1;
		//		getDayOfWeekInMonthNumber = 1;
		//		getDurationToAddDays = 5;

		//		getYearNumber = ;
		//		getYearsAgo = ;
		//		getMonthNumber = ;
		//		getMonthsAgo = ;
		//		getWeeksAgo = ;
		//		getDaysAgo = ;
		//		getDayOfWeekNumber = ;
		//		getDayOfMonthNumber = ;
		//		getDayOfWeekInMonthOrdinal = ;
		//		getDayOfWeekInMonthNumber = ;
		//		getDurationToAddYears = ;
		//		getDurationToAddMonths = ;
		//		getDurationToAddWeeks = ;
		//		getDurationToAddDays = ;
		//		getDurationToAddHours = ;
		//		getDurationToAddMinutes = ;
		//		getDurationToAddSeconds = ;

		logger.info("timeValue = {}", localTimeForDatetimeValue);

		if (getYearNumber != null) {
			logger.info("yearNumber = {}", getYearNumber);
		}
		if (getYearsAgo != null) {
			logger.info("yearsAgo = {}", getYearsAgo);
		}
		if (getMonthNumber != null) {
			logger.info("monthNumber = {}", getMonthNumber);
		}
		if (getMonthsAgo != null) {
			logger.info("monthsAgo = {}", getMonthsAgo);
		}
		if (getWeeksAgo != null) {
			logger.info("weeksAgo = {}", getWeeksAgo);
		}
		if (getDaysAgo != null) {
			logger.info("daysAgo = {}", getDaysAgo);
		}
		if (getDayOfWeekNumber != null) {
			logger.info("dayOfWeekNumber = {}", getDayOfWeekNumber);
		}
		if (getDayOfMonthNumber != null) {
			logger.info("dayOfMonthNumber = {}", getDayOfMonthNumber);
		}
		if (getDayOfWeekInMonthOrdinal != null) {
			logger.info("dayOfWeekInMonthOrdinal = {}", getDayOfWeekInMonthOrdinal);
		}
		if (getDayOfWeekInMonthNumber != null) {
			logger.info("dayOfWeekInMonthNumber = {}", getDayOfWeekInMonthNumber);
		}
		if (getDurationToAddYears != null) {
			logger.info("durationToAddYears = {}", getDurationToAddYears);
		}
		if (getDurationToAddMonths != null) {
			logger.info("durationToAddMonths = {}", getDurationToAddMonths);
		}
		if (getDurationToAddWeeks != null) {
			logger.info("durationToAddWeeks = {}", getDurationToAddWeeks);
		}
		if (getDurationToAddDays != null) {
			logger.info("durationToAddDays = {}", getDurationToAddDays);
		}
		if (getDurationToAddHours != null) {
			logger.info("durationToAddHours = {}", getDurationToAddHours);
		}
		if (getDurationToAddMinutes != null) {
			logger.info("durationToAddMinutes = {}", getDurationToAddMinutes);
		}
		if (getDurationToAddSeconds != null) {
			logger.info("durationToAddSeconds = {}", getDurationToAddSeconds);
		}

		logger.info("-------------------------------------------");

		//LocalDateTime localDateTime = LocalDateTime.now();
		LocalDateTime localDateTime = LocalDateTime.of(2015, 11, 25, 03, 15, 30);
		logger.info("localDateTime = {}", localDateTime);

		if (getYearNumber != null) {
			/*
			 * Set year number of localDateTime to specified year number.
			 */
			try {
				localDateTime = localDateTime.withYear(getYearNumber);
				logger.debug("After setting year number to {}. localDateTime = {}", getYearNumber, localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getYearNumber: {}. localDateTime = {}. Exception: {}",
						getYearNumber, localDateTime, e);
			}
		} else if (getYearsAgo != null) {
			/*
			 * Move localDateTime backwards specified number of years.
			 */
			try {
				localDateTime = localDateTime.plusYears(-getYearsAgo.longValue());
				logger.debug("After moving localDateTime back {} years. localDateTime = {}", getYearsAgo,
						localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getYearsAgo: {}. localDateTime = {}. Exception: {}",
						getYearsAgo, localDateTime, e);
			}
		}

		if (getMonthNumber != null) {
			/*
			 * Set month number of localDateTime to specified month number.
			 */
			try {
				localDateTime = localDateTime.withMonth(getMonthNumber);
				logger.debug("After setting month number to {}. localDateTime = {}", getMonthNumber, localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getMonthNumber: {}. localDateTime = {}. Exception: {}",
						getMonthNumber, localDateTime, e);
			}
		} else if (getMonthsAgo != null) {
			/*
			 * Move localDateTime backwards specified number of months.
			 */
			try {
				localDateTime = localDateTime.plusMonths(-getMonthsAgo.longValue());
				logger.debug("After moving localDateTime back {} months. localDateTime = {}",
						getMonthsAgo, localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getMonthsAgo: {}. localDateTime = {}. Exception: {}",
						getMonthsAgo, localDateTime, e);
			}
		}

		if (getWeeksAgo != null) {
			/*
			 * Move localDateTime backwards specified number of weeks.
			 */
			try {
				localDateTime = localDateTime.plusWeeks(-getWeeksAgo.longValue());
				logger.debug("After moving localDateTime back {} weeks. localDateTime = {}", getWeeksAgo,
						localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getWeeksAgo: {}. localDateTime = {}. Exception: {}",
						getWeeksAgo, localDateTime, e);
			}
		}

		if (getDaysAgo != null) {
			/*
			 * Move localDateTime backwards specified number of days.
			 */
			try {
				localDateTime = localDateTime.plusDays(-getDaysAgo.longValue());
				logger.debug("After moving localDateTime back {} days. localDateTime = {}", getDaysAgo, localDateTime);
			} catch (DateTimeException e) {
				logger.warn("Illegal value for getDaysAgo: {}. localDateTime = {}. Exception: {}",
						getDaysAgo, localDateTime, e);
			}
		}

		if (getDayOfWeekNumber != null) {
			/*
			 * Move the day-of-week number (1-7) of localDateTime, within the 
			 * current Monday-to-Sunday week, even if it causes the month to 
			 * change.
			 */
			try {
				localDateTime = localDateTime.with(ChronoField.DAY_OF_WEEK, getDayOfWeekNumber.longValue());
				logger.debug(
						"After setting day-of-week of localDateTime to {}, even if it causes the month to change. localDateTime = {}",
						getDayOfWeekNumber, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn("Illegal value for getDayOfWeekNumber: {}. localDateTime = {}. Exception: {}",
						getDayOfWeekNumber, localDateTime, e);
			}
		}

		if (getDayOfMonthNumber != null) {
			try {
				if (getDayOfMonthNumber > 0 && getDayOfMonthNumber <= 31) {
					/*
					 * Move the day-of-month number (1-31) of localDateTime.
					 */
					localDateTime = localDateTime.with(ChronoField.DAY_OF_MONTH, getDayOfMonthNumber.longValue());
					logger.debug("After setting day-of-month of localDateTime to {}. localDateTime = {}",
							getDayOfMonthNumber, localDateTime);
				} else if (getDayOfMonthNumber < 0 && getDayOfMonthNumber >= -31) {
					/*
					 * Move the day-of-month number (1-31) of localDateTime relative
					 * to the last day of the month.
					 * 
					 *   -1: Last day of the month
					 *   -2: 2nd to last day of the month
					 *   -3: 3rd to last day of the month
					 *     ...
					 */
					long lastDayOfMonth = localDateTime.range(ChronoField.DAY_OF_MONTH).getMaximum();
					logger.debug("lastDayOfMonth = {}", lastDayOfMonth);
					localDateTime = localDateTime.with(ChronoField.DAY_OF_MONTH,
							lastDayOfMonth + getDayOfMonthNumber.longValue() + 1);
					logger.debug("After setting day-of-month of localDateTime to {}. localDateTime = {}",
							lastDayOfMonth + getDayOfMonthNumber.longValue() + 1, localDateTime);
				} else {
					/*
					 * Zero has no meaning.
					 */
					logger.warn("Illegal value for getDayOfMonthNumber: {}", getDayOfMonthNumber);
				}
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn("Illegal value for getDayOfMonthNumber: {}. localDateTime = {}. Exception: {}",
						getDayOfMonthNumber, localDateTime, e);
			}
		}

		if (getDayOfWeekInMonthOrdinal != null && getDayOfWeekInMonthNumber != null) {
			/*
			 * Select the Nth day-of-week in the same month (if possible) as
			 * localDateTime. Here:
			 * 
			 *   N           = getDayOfWeekInMonthOrdinal
			 *   day-of-week = getDayOfWeekInMonthNumber (1:Monday, ...7:Sunday)
			 */
			try {
				TemporalAdjuster dayOfWeekInMonthAdjuster = TemporalAdjusters.dayOfWeekInMonth(
						getDayOfWeekInMonthOrdinal,
						DayOfWeek.of(getDayOfWeekInMonthNumber));
				localDateTime = localDateTime.with(dayOfWeekInMonthAdjuster);
				logger.debug(
						"After setting Nth day-of-week with getDayOfWeekInMonthOrdinal={}, getDayOfWeekInMonthNumber={}. localDateTime = {}",
						getDayOfWeekInMonthOrdinal, getDayOfWeekInMonthNumber, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDayOfWeekInMonthOrdinal={}, getDayOfWeekInMonthNumber={}. localDateTime = {}. Exception: {}",
						getDayOfWeekInMonthOrdinal, getDayOfWeekInMonthNumber, localDateTime, e);
			}
		}

		if(getDurationToAddYears != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddYears.longValue(), ChronoUnit.YEARS);
				logger.debug("After adding {} years. localDateTime = {}", getDurationToAddYears, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddYears={}. localDateTime = {}. Exception: {}",
						getDurationToAddYears, localDateTime, e);
			}
		}
		if (getDurationToAddMonths != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddMonths.longValue(), ChronoUnit.MONTHS);
				logger.debug("After adding {} months. localDateTime = {}", getDurationToAddMonths, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddMonths={}. localDateTime = {}. Exception: {}",
						getDurationToAddMonths, localDateTime, e);
			}
		}
		if (getDurationToAddWeeks != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddWeeks.longValue(), ChronoUnit.WEEKS);
				logger.debug("After adding {} weeks. localDateTime = {}", getDurationToAddWeeks, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddWeeks={}. localDateTime = {}. Exception: {}",
						getDurationToAddWeeks, localDateTime, e);
			}
		}
		if (getDurationToAddDays != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddDays.longValue(), ChronoUnit.DAYS);
				logger.debug("After adding {} days. localDateTime = {}", getDurationToAddDays, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddDays={}. localDateTime = {}. Exception: {}",
						getDurationToAddDays, localDateTime, e);
			}
		}
		if (getDurationToAddHours != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddHours.longValue(), ChronoUnit.HOURS);
				logger.debug("After adding {} hours. localDateTime = {}", getDurationToAddHours, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddHours={}. localDateTime = {}. Exception: {}",
						getDurationToAddHours, localDateTime, e);
			}
		}
		if (getDurationToAddMinutes != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddMinutes.longValue(), ChronoUnit.MINUTES);
				logger.debug("After adding {} minutes. localDateTime = {}", getDurationToAddMinutes, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddMinutes={}. localDateTime = {}. Exception: {}",
						getDurationToAddMinutes, localDateTime, e);
			}
		}
		if (getDurationToAddSeconds != null) {
			try {
				localDateTime = localDateTime.plus(getDurationToAddSeconds.longValue(), ChronoUnit.SECONDS);
				logger.debug("After adding {} seconds. localDateTime = {}", getDurationToAddSeconds, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddSeconds={}. localDateTime = {}. Exception: {}",
						getDurationToAddSeconds, localDateTime, e);
			}
		}

		//DATE:
		/*
		 * If we are computing a report parameter of type "Date", not 
		 * "Datetime", the time part of localDateTime must be discarded.
		 */
		LocalDateTime localDateTimeForDateValue = localDateTime;
		if (getDurationSubtractOneDayForDates) {
			/*
			 * But we only subtract one day *if* a "duration" 
			 * attribute has been set that shifts the date in
			 * units of at least one day, i.e., year, month,
			 * week or day.
			 */
			if (getDurationToAddYears != null
					|| getDurationToAddMonths != null
					|| getDurationToAddWeeks != null
					|| getDurationToAddDays != null) {
				logger.debug(
						"Subtracting one day from localDateTimeForDateValue because durationToAddYears=true");
				try {
					localDateTimeForDateValue = localDateTimeForDateValue.plus(-1L, ChronoUnit.DAYS);
					logger.debug("After subtracting 1 day . localDateTimeForDateValue = {}", localDateTimeForDateValue);
				} catch (DateTimeException | ArithmeticException e) {
					logger.warn(
							"Exception thrown subtracting one day from localDateTimeForDateValue. localDateTimeForDateValue = {}. Exception: {}",
							localDateTimeForDateValue, e);
				}
			}
		}
		LocalDate localDateForDateValue = localDateTimeForDateValue.toLocalDate();
		logger.info("*** localDateForDateValue = {}", localDateForDateValue);
		Date dateValue = Date
				.from(localDateForDateValue.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		logger.info("Dynamically generated dateValue = {}", dateValue);

		//DATETIME:
		/*
		 * If we are computing a report parameter of type "Datetime", not 
		 * "Date", the time part of localDateTime must be discarded *if* a value
		 * was specified for localTimeForDatetimeValue.  In this case we combine
		 * the LocalDate with the specified LocalTime to create a LocalDateTime.
		 */
		LocalDate localDateForDatetimeValue = localDateTime.toLocalDate();
		logger.info("localDateForDatetimeValue = {}", localDateForDatetimeValue);
		LocalDateTime localDateTimeForDatetimeValue = localDateTime;
		if (localTimeForDatetimeValue != null) {
			localDateTimeForDatetimeValue = LocalDateTime.of(localDateForDatetimeValue, localTimeForDatetimeValue);
		}
		logger.info("*** localDateTimeForDatetimeValue = {}", localDateTimeForDatetimeValue);
		Date datetimeValue = Date.from(localDateTimeForDatetimeValue.atZone(ZoneId.systemDefault()).toInstant());
		logger.info("Dynamically generated datetimeValue = {}", datetimeValue);

	}
}
