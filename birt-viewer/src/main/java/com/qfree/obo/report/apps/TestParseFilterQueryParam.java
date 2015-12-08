package com.qfree.obo.report.apps;

import java.util.List;
import java.util.Map;

import com.qfree.obo.report.exceptions.ReportingException;
import com.qfree.obo.report.util.RestUtils;

public class TestParseFilterQueryParam {

	public static void main(String[] args) {

		String filterQueryParamText = "jobStatus.eq.\"FAI\"L\"ED\".or.jobStatus.eq.\"COMPLETED\".and.someField.ne.\"some\" value\".and.annotherField.gt.\"10\"";
		try {
			List<List<Map<String, String>>> filterConditions = RestUtils.parseFilterQueryParam(filterQueryParamText);
		} catch (ReportingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
