package com.qfree.bo.report.apps;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAvailableZoneIds {

	private static final Logger logger = LoggerFactory.getLogger(GetAvailableZoneIds.class);

	public GetAvailableZoneIds() {
	}

	public static void main(String[] args) {

		Set<String> zoneIds = ZoneId.getAvailableZoneIds();
		String[] zoneIdsArray = zoneIds.toArray(new String[] {});
		Arrays.sort(zoneIdsArray);
		for (String zoneId : zoneIdsArray) {
			System.out.println(zoneId);
		}

	}

}
