package com.qfree.obo.report.util;

import java.util.Date;

import org.joda.time.LocalTime;

public class DateUtils {

	/**
	 * Returns true if the time portion of the two Date vales are equal.
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean timePartsEqual(Date date1, Date date2) {
		/*
		 * Convert the two dates to Joda LocalTime objects so they can be 
		 * compared. Joda time is used here because Java 7 does not have 
		 * convenient methods for dealing with times that are not associated 
		 * with an instant in time. I suppose that I could have managed this 
		 * with standard Java 7 methods, but it is easier to just use Joda time.
		 */
		LocalTime lt1 = new LocalTime(date1);
		LocalTime lt2 = new LocalTime(date2);
		return lt1.equals(lt2);
	}

}
