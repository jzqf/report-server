package com.qfree.bo.report.domain;

import java.util.Comparator;

public class ReportVersionsSortByVersionCode implements Comparator<ReportVersion> {

	@Override
	public int compare(ReportVersion reportVersion1, ReportVersion reportVersion2) {
		return reportVersion1.getVersionCode().compareTo(reportVersion2.getVersionCode());
	}

}
