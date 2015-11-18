package com.qfree.obo.report.util;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.exceptions.ReportingException;
import com.qfree.obo.report.exceptions.ResourceFilterParseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class RestUtilsTests {

	private static final Logger logger = LoggerFactory.getLogger(RestUtilsTests.class);

	@Test
	@Transactional
	public void checkPaginationQueryParams() {
		List<String> pageOffset = new ArrayList<>();
		List<String> pageLimit = new ArrayList<>();
		Map<String, List<String>> queryParams = new HashMap<>();

		final String paginationLimitDefault = Integer.toString(RestUtils.PAGINATION_LIMIT_DEFAULT);

		pageOffset.add("-1");
		pageOffset.add("10");

		pageLimit.add("1000");
		pageLimit.add("2");

		RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);

		assertThat(pageOffset, hasSize(1));
		assertThat(pageLimit, hasSize(1));
		//assertThat(queryParams, isMapHasSize(2)); // <- Hamcrest v1.4?
		assertThat(queryParams.size(), is(2));

		assertThat(pageOffset, hasItems("0"));
		assertThat(pageLimit, hasItems(paginationLimitDefault));
		assertThat(queryParams, hasEntry(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset));
		assertThat(queryParams, hasEntry(ResourcePath.PAGE_LIMIT_QP_KEY, pageLimit));

		/*
		 * Reuse these objects for next test.
		 */
		pageOffset.clear();
		pageLimit.clear();
		queryParams.clear();

		pageOffset.add("1000000");
		pageLimit.add("4");

		RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);

		assertThat(pageOffset, hasSize(1));
		assertThat(pageLimit, hasSize(1));
		//assertThat(queryParams, isMapHasSize(2)); // <- Hamcrest v1.4?
		assertThat(queryParams.size(), is(2));

		assertThat(pageOffset, hasItems("1000000"));
		assertThat(pageLimit, hasItems("4"));
		assertThat(queryParams, hasEntry(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset));
		assertThat(queryParams, hasEntry(ResourcePath.PAGE_LIMIT_QP_KEY, pageLimit));

	}

	@Test
	@Transactional
	public void parseFilterQueryParamSingleCondition() throws ReportingException {

		/*
		 * This query parameter value has an embedded double quote in the 
		 * comparison value:
		 * 
		 *   someField.ne."some\" value"
		 */
		String filterQueryParamText = "someField.ne.\"some\" value\"";

		List<List<Map<String, String>>> filterConditions = RestUtils.parseFilterQueryParam(filterQueryParamText);

		assertThat(filterConditions, hasSize(1));
		assertThat(filterConditions.get(0), hasSize(1));
		/*
		 * This Map represents the single condition.
		 */
		Map<String, String> filterConditionMap = filterConditions.get(0).get(0);
		//assertThat(filterConditionMap, isMapHasSize(3)); // <- Hamcrest v1.4?
		assertThat(filterConditionMap.size(), is(3));
		assertThat(filterConditionMap, hasEntry(RestUtils.CONDITION_ATTR_NAME, "someField"));
		assertThat(filterConditionMap, hasEntry(RestUtils.CONDITION_OPERATOR, "ne"));
		assertThat(filterConditionMap, hasEntry(RestUtils.CONDITION_VALUE, "some\" value"));

	}

	@Test(expected = ResourceFilterParseException.class)
	@Transactional
	public void parseFilterQueryParamNoClosingQuote() throws ReportingException {

		/*
		 * This query parameter value has an embedded double quote in the 
		 * comparison value, but no closing quote on the comparison value:
		 * 
		 *   someField.ne."some\" value
		 * 
		 * Since there is no closing quote, parseFilterQueryParam should throw
		 * a ResourceFilterParseException.
		 */
		String filterQueryParamText = "someField.ne.\"some\" value";

		List<List<Map<String, String>>> filterConditions = RestUtils.parseFilterQueryParam(filterQueryParamText);

		//	assertThat(filterConditions, hasSize(0));

	}

	@Test
	@Transactional
	public void parseFilterQueryParam() throws ReportingException {

		String filterQueryParamText = "jobStatus.eq.\"FAI\"L\"ED\".or.jobStatus.eq.\"COMPLETED\".and.someField.ne.\"some\" value\".and.annotherField.gt.\"10\"";

		/*
		 * These are the 3 filter conditions that should be parsed from 
		 * filterQueryParamText (the conditions that are separated by ".and.".
		 * They are associated with the following 3 sub-strings:
		 * 
		 *   1. jobStatus.eq."FAI"L"ED".or.jobStatus.eq."COMPLETED"
		 *   2. someField.ne."some" value"
		 *   3. annotherField.gt."10"
		 * 
		 * However, the lists filterCondition1, filterCondition2 and
		 * filterCondition3 hold the representation of these conditions as a
		 * List of Map's. This is how RestUtils.parseFilterQueryParam should 
		 * parse filterQueryParamText.
		 */
		//	List<Map<String, String>> filterCondition1 = new ArrayList<>(2);
		//	Map<String, String> orCondition1 = new HashMap<>();
		//	orCondition1.put(RestUtils.CONDITION_ATTR_NAME, "jobStatus");
		//	orCondition1.put(RestUtils.CONDITION_OPERATOR, "eq");
		//	orCondition1.put(RestUtils.CONDITION_VALUE, "FAI\"L\"ED");
		//	Map<String, String> orCondition2 = new HashMap<>();
		//	orCondition2.put(RestUtils.CONDITION_ATTR_NAME, "jobStatus");
		//	orCondition2.put(RestUtils.CONDITION_OPERATOR, "eq");
		//	orCondition2.put(RestUtils.CONDITION_VALUE, "COMPLETED");
		//	filterCondition1.add(orCondition1);
		//	filterCondition1.add(orCondition2);
		//	//
		//	List<Map<String, String>> filterCondition2 = new ArrayList<>(1);
		//	Map<String, String> orCondition3 = new HashMap<>();
		//	orCondition3.put(RestUtils.CONDITION_ATTR_NAME, "someField");
		//	orCondition3.put(RestUtils.CONDITION_OPERATOR, "ne");
		//	orCondition3.put(RestUtils.CONDITION_VALUE, "some\" value");
		//	filterCondition2.add(orCondition3);
		//	//
		//	List<Map<String, String>> filterCondition3 = new ArrayList<>(1);
		//	Map<String, String> orCondition4 = new HashMap<>();
		//	orCondition4.put(RestUtils.CONDITION_ATTR_NAME, "annotherField");
		//	orCondition4.put(RestUtils.CONDITION_OPERATOR, "gt");
		//	orCondition4.put(RestUtils.CONDITION_VALUE, "10");
		//	filterCondition3.add(orCondition4);

		List<List<Map<String, String>>> filterConditions = RestUtils.parseFilterQueryParam(filterQueryParamText);

		assertThat(filterConditions, hasSize(3));

		assertThat(filterConditions.get(0), hasSize(2));
		assertThat(filterConditions.get(1), hasSize(1));
		assertThat(filterConditions.get(2), hasSize(1));

		/*
		 * 1st condition to be AND'ed. It consists of two conditions to be OR'ed
		 * with each other:
		 */
		Map<String, String> filterCondition1Map1 = filterConditions.get(0).get(0);
		//assertThat(filterCondition1Map1, isMapHasSize(3)); // <- Hamcrest v1.4?
		assertThat(filterCondition1Map1.size(), is(3));
		assertThat(filterCondition1Map1, hasEntry(RestUtils.CONDITION_ATTR_NAME, "jobStatus"));
		assertThat(filterCondition1Map1, hasEntry(RestUtils.CONDITION_OPERATOR, "eq"));
		assertThat(filterCondition1Map1, hasEntry(RestUtils.CONDITION_VALUE, "FAI\"L\"ED"));
		//
		Map<String, String> filterCondition1Map2 = filterConditions.get(0).get(1);
		//assertThat(filterCondition1Map2, isMapHasSize(3)); // <- Hamcrest v1.4?
		assertThat(filterCondition1Map2.size(), is(3));
		assertThat(filterCondition1Map2, hasEntry(RestUtils.CONDITION_ATTR_NAME, "jobStatus"));
		assertThat(filterCondition1Map2, hasEntry(RestUtils.CONDITION_OPERATOR, "eq"));
		assertThat(filterCondition1Map2, hasEntry(RestUtils.CONDITION_VALUE, "COMPLETED"));

		/*
		 * 2nd condition to be AND'ed. It consists of a single condition.
		 */
		Map<String, String> filterCondition2Map1 = filterConditions.get(1).get(0);
		//assertThat(filterCondition2Map1, isMapHasSize(3)); // <- Hamcrest v1.4?
		assertThat(filterCondition2Map1.size(), is(3));
		assertThat(filterCondition2Map1, hasEntry(RestUtils.CONDITION_ATTR_NAME, "someField"));
		assertThat(filterCondition2Map1, hasEntry(RestUtils.CONDITION_OPERATOR, "ne"));
		assertThat(filterCondition2Map1, hasEntry(RestUtils.CONDITION_VALUE, "some\" value"));

		/*
		 * 3rd condition to be AND'ed. It consists of a single condition.
		 */
		Map<String, String> filterCondition3Map1 = filterConditions.get(2).get(0);
		//assertThat(filterCondition3Map1, isMapHasSize(3)); // <- Hamcrest v1.4?
		assertThat(filterCondition3Map1.size(), is(3));
		assertThat(filterCondition3Map1, hasEntry(RestUtils.CONDITION_ATTR_NAME, "annotherField"));
		assertThat(filterCondition3Map1, hasEntry(RestUtils.CONDITION_OPERATOR, "gt"));
		assertThat(filterCondition3Map1, hasEntry(RestUtils.CONDITION_VALUE, "10"));

	}

}
