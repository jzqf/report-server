package com.qfree.obo.report.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompareUtils {

	private static final Logger logger = LoggerFactory.getLogger(CompareUtils.class);

	/**
	 * Returns true if the two objects hold differnet values; otherwise, returns
	 * false.
	 * 
	 * @param object1
	 * @param object2
	 * @return
	 */
	public static boolean different(Object object1, Object object2) {

		if (object1 == null) {
			return object2 != null;
		} else if (object2 == null) {
			return false;
		} else {
			return !object1.equals(object2);
		}
	}
}