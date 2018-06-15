package com.qfree.bo.report.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Some of the code in this class was adapted from:
 * <p>
 * <a href="http://naleid.com/blog/2013/09/10/logging-to-splunk-in-keyvalue-pairs">
 * http://naleid.com/blog/2013/09/10/logging-to-splunk-in-keyvalue-pairs</a>
 * 
 * @author jeffreyz
 *
 */
public class LoggingUtils {

	public static String toSplunkString(Object... mapInfo) {
		return toSplunkString(varArgMap(mapInfo));
	}

	public static Map<String, Object> varArgMap(Object... mapInfo) {
		final Map<String, Object> result = new LinkedHashMap<>();
		if (mapInfo.length % 2 == 1) {
			throw new IllegalArgumentException("arguments must be even in number");
		}
		for (int i = 0; i < mapInfo.length; i += 2) {
			final Object o1 = mapInfo[i];
			if (!(o1 instanceof String)) {
				throw new IllegalArgumentException("odd arguments must be String values so they can be keys");
			}
			final Object o2 = mapInfo[i + 1];
			result.put((String) o1, o2);
		}
		return result;
	}

	public static String toSplunkString(Map<String, Object> map) {
		StringBuilder buffer = new StringBuilder();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (buffer.length() > 1) {
				buffer.append(", ");
			}
			buffer.append(entry.getKey() == null ? null : entry.getKey().toString())
					.append("=\"")
					.append(StringEscapeUtils.escapeJava(entry.getValue() == null ? null : entry.getValue().toString()))
					.append("\"");
		}
		return buffer.toString();
	}

}
