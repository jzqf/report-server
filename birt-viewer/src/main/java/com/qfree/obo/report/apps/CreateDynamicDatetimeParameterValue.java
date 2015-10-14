package com.qfree.obo.report.apps;

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

import com.qfree.obo.report.util.DateUtils;

public class CreateDynamicDatetimeParameterValue {

	private static final Logger logger = LoggerFactory.getLogger(CreateDynamicDatetimeParameterValue.class);

	public CreateDynamicDatetimeParameterValue() {
	}

	public static void main(String[] args) {

		Integer getYearNumber = null;
		//		Integer getYearNumber = 2013;
		//		Integer getYearsAgo = null;
		Integer getYearsAgo = 1;

		Integer getMonthNumber = null;
		//		Integer getMonthNumber = 2;
		//		Integer getMonthsAgo = null;
		Integer getMonthsAgo = 3;

		Integer getWeeksAgo = 1;

		Integer getDaysAgo = 28;

		Integer getDayOfWeekNumber = 1;

		Integer getDayOfMonthNumber = -1;

		Integer getDayOfWeekInMonthOrdinal = 2; // 2nd
		Integer getDayOfWeekInMonthNumber = 5; // Friday

		Integer getDurationToAddYears = 1;
		Integer getDurationToAddMonths = 2;
		Integer getDurationToAddWeeks = 3;
		Integer getDurationToAddDays = 4;
		Integer getDurationToAddHours = 5;
		Integer getDurationToAddMinutes = 6;
		Integer getDurationToAddSeconds = 7;

		LocalDateTime localDateTime = LocalDateTime.now();
		logger.info("localDateTime = {}", localDateTime);

		if (getYearNumber != null) {
			/*
			 * Set year number of localDateTime to specified year number.
			 */
			try {
				localDateTime = localDateTime.withYear(getYearNumber);
				logger.info("After setting year number to {}. localDateTime = {}", getYearNumber, localDateTime);
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
				logger.info("After moving localDateTime back {} years. localDateTime = {}", getYearsAgo, localDateTime);
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
				logger.info("After setting month number to {}. localDateTime = {}", getMonthNumber, localDateTime);
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
				logger.info("After moving localDateTime back {} months. localDateTime = {}",
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
				logger.info("After moving localDateTime back {} weeks. localDateTime = {}", getWeeksAgo, localDateTime);
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
				logger.info("After moving localDateTime back {} days. localDateTime = {}", getDaysAgo, localDateTime);
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
				logger.info(
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
					logger.info("After setting day-of-month of localDateTime to {}. localDateTime = {}",
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
					logger.info("lastDayOfMonth = {}", lastDayOfMonth);
					localDateTime = localDateTime.with(ChronoField.DAY_OF_MONTH,
							lastDayOfMonth + getDayOfMonthNumber.longValue() + 1);
					logger.info("After setting day-of-month of localDateTime to {}. localDateTime = {}",
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
				logger.info(
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
				logger.info("After adding {} years. localDateTime = {}", getDurationToAddYears, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddYears={}. localDateTime = {}. Exception: {}",
						getDurationToAddYears, localDateTime, e);
			}
		}
		if (getDurationToAddMonths != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddMonths.longValue(), ChronoUnit.MONTHS);
				logger.info("After adding {} months. localDateTime = {}", getDurationToAddMonths, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddMonths={}. localDateTime = {}. Exception: {}",
						getDurationToAddMonths, localDateTime, e);
			}
		}
		if (getDurationToAddWeeks != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddWeeks.longValue(), ChronoUnit.WEEKS);
				logger.info("After adding {} weeks. localDateTime = {}", getDurationToAddWeeks, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddWeeks={}. localDateTime = {}. Exception: {}",
						getDurationToAddWeeks, localDateTime, e);
			}
		}
		if (getDurationToAddDays != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddDays.longValue(), ChronoUnit.DAYS);
				logger.info("After adding {} days. localDateTime = {}", getDurationToAddDays, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddDays={}. localDateTime = {}. Exception: {}",
						getDurationToAddDays, localDateTime, e);
			}
		}
		if (getDurationToAddHours != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddHours.longValue(), ChronoUnit.HOURS);
				logger.info("After adding {} hours. localDateTime = {}", getDurationToAddHours, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddHours={}. localDateTime = {}. Exception: {}",
						getDurationToAddHours, localDateTime, e);
			}
		}
		if (getDurationToAddMinutes != null){
			try {
				localDateTime = localDateTime.plus(getDurationToAddMinutes.longValue(), ChronoUnit.MINUTES);
				logger.info("After adding {} minutes. localDateTime = {}", getDurationToAddMinutes, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddMinutes={}. localDateTime = {}. Exception: {}",
						getDurationToAddMinutes, localDateTime, e);
			}
		}
		if (getDurationToAddSeconds != null) {
			try {
				localDateTime = localDateTime.plus(getDurationToAddSeconds.longValue(), ChronoUnit.SECONDS);
				logger.info("After adding {} seconds. localDateTime = {}", getDurationToAddSeconds, localDateTime);
			} catch (DateTimeException | ArithmeticException e) {
				logger.warn(
						"Adjustment cannot be made for getDurationToAddSeconds={}. localDateTime = {}. Exception: {}",
						getDurationToAddSeconds, localDateTime, e);
			}
		}

		/*
		 * If we are computing a report parameter of type "Date", not 
		 * "Datetime", the time part of localDateTime must be discarded.
		 * 
		 * This LocalDate may also be used for a report parameter of type 
		 * "Datetime", provided a value was specified for fakeEntityTimeDate.
		 * In this case we combine the LocalDate with the specified
		 * fakeEntityTimeDate to create a LocalDateTime.
		 */
		LocalDate localDate = localDateTime.toLocalDate();
		logger.info("localDate = {}", localDate);

		//DATE:

		//DATETIME:
		Date fakeEntityTimeDate = new Date();
		logger.info("fakeEntityTimeDate = {}", fakeEntityTimeDate);
		if (fakeEntityTimeDate != null) {
			LocalTime localTime = DateUtils.localTimeFromEntityTimeDate(fakeEntityTimeDate);
			logger.info("localTime = {}", localTime);

			localDateTime = LocalDateTime.of(localDate, localTime);
			logger.info("localDateTime = {}", localDateTime);

		} else {
			//DO NOTHING HERE?????
		}
		Date datetimeValue = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		logger.info("datetimeValue = {}", datetimeValue);

	}
}
