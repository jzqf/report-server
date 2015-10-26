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
import com.qfree.obo.report.rest.server.RestUtils;

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

		RestUtils.checkPaginationQueryParams(pageOffset,pageLimit,queryParams);

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

}
