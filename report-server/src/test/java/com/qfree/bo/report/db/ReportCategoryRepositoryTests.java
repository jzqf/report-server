package com.qfree.bo.report.db;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ReportCategoryRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryRepositoryTests.class);

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

	@Autowired
	ReportRepository reportRepository;

	@Test
	@Transactional
	public void count() {
		assertThat(reportCategoryRepository.count(), is(4L));
	}

	@Test
	@Transactional
	public void findAll() {
		List<ReportCategory> reportCategories = reportCategoryRepository.findAll();
		assertThat(reportCategories.size(), is(4));
	}

	@Test
	@Transactional
	public void findByDescription() {
		assertReportCategory(0, reportCategoryRepository.findByDescription("Accounting"));
		assertReportCategory(1, reportCategoryRepository.findByDescription("Q-Free internal"));
		assertReportCategory(2, reportCategoryRepository.findByDescription("Manual validation"));
		assertReportCategory(3, reportCategoryRepository.findByDescription("Traffic"));
	}

	@Test
	@Transactional
	public void findByAbbreviation() {
		assertReportCategory(0, reportCategoryRepository.findByAbbreviation("ACCT"));
		assertReportCategory(1, reportCategoryRepository.findByAbbreviation("QFREE"));
		assertReportCategory(2, reportCategoryRepository.findByAbbreviation("MIR"));
		assertReportCategory(3, reportCategoryRepository.findByAbbreviation("TRA"));
	}

	@Test
	@Transactional
	public void findByDescriptionLikeOrAbbreviationLike() {
		List<ReportCategory> reportCategories;

		reportCategories = reportCategoryRepository.findByDescriptionLikeOrAbbreviationLike("Accoun%", "");
		assertThat(reportCategories.size(), is(1));
		assertReportCategory(0, reportCategories.get(0));

		reportCategories = reportCategoryRepository.findByDescriptionLikeOrAbbreviationLike("", "%FREE");
		assertThat(reportCategories.size(), is(1));
		assertReportCategory(1, reportCategories.get(0));

		reportCategories = reportCategoryRepository.findByDescriptionLikeOrAbbreviationLike("Accoun%", "%FREE");
		assertThat(reportCategories.size(), is(2));
	}

	@Test
	@Transactional
	public void findByActiveIsTrue() {
		List<ReportCategory> reportCategories = reportCategoryRepository.findByActiveIsTrue();
		assertThat(reportCategories.size(), is(2));
	}

	@Test
	@Transactional
	public void findOne() {
		assertReportCategory(0,
				reportCategoryRepository.findOne(UUID.fromString("7a482694-51d2-42d0-b0e2-19dd13bbbc64")));
		assertReportCategory(1,
				reportCategoryRepository.findOne(UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0")));
		assertReportCategory(2,
				reportCategoryRepository.findOne(UUID.fromString("5c3cc664-b685-4f6e-8d9a-2927c6bcffdc")));
		assertReportCategory(3,
				reportCategoryRepository.findOne(UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76")));
	}

	@Test
	@Transactional
	public void save_newReportCategory() {
		assertThat(reportCategoryRepository.count(), is(4L));
		//		ReportCategory reportCategory = new ReportCategory(
		//				UUID.fromString("b9a6301b-07ac-48d2-8de6-1fc6330c0cb0"), "Image processing", "OCR", true);
		ReportCategory reportCategory = new ReportCategory("Image processing", "OCR", true);
		ReportCategory saved = reportCategoryRepository.save(reportCategory);
		assertThat(reportCategoryRepository.count(), is(5L));
		assertReportCategory(4, saved);
		assertReportCategory(4, reportCategoryRepository
				.findOne(saved.getReportCategoryId()));
	}

	@Test
	@Transactional
	public void save_existingReportCategory() {
		assertThat(reportCategoryRepository.count(), is(4L));
		ReportCategory found = reportCategoryRepository
				.findOne(UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0"));
		assertReportCategory(1, found);
		ReportCategory saved = reportCategoryRepository.save(found);
		assertThat(reportCategoryRepository.count(), is(4L));
	}

	@Test
	@Ignore
	@Transactional
	public void delete() {
		assertThat(reportCategoryRepository.count(), is(4L));
		assertThat(reportRepository.count(), is(15L));
		UUID uuidOfTrafficeCategory = UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76");
		assertThat(reportCategoryRepository.findOne(uuidOfTrafficeCategory), is(not(nullValue())));
		reportCategoryRepository.delete(uuidOfTrafficeCategory);
		assertThat(reportCategoryRepository.count(), is(3L));
		assertThat(reportRepository.count(), is(4L));	// only for cascade=CascadeType.REMOVE in ReportCategory
		assertThat(reportCategoryRepository.findOne(uuidOfTrafficeCategory), is(nullValue()));
	}

	/**
	 * Checks that the {@link ReportCategory} instance passed to this method is 
	 * found at a specified index within the REPORT_CATEGORIES array.
	 * 
	 * @param expectedReportCategoryIndex
	 * @param reportCategory
	 */
	private static void assertReportCategory(int expectedReportCategoryIndex, ReportCategory reportCategory) {
		assertThat(reportCategory, is(not(nullValue())));
		ReportCategory expected = REPORT_CATEGORIES[expectedReportCategoryIndex];
		//		ReportCategory expected = REPORT_CATEGORIES.get(expectedReportCategoryIndex);
		//		assertThat(reportCategory.getReportCategoryId(), is(expected.getReportCategoryId()));
		assertThat(reportCategory.getDescription(), is(expected.getDescription()));
		assertThat(reportCategory.getAbbreviation(), is(expected.getAbbreviation()));
		assertThat(reportCategory.isActive(), is(expected.isActive()));
	}

	private static ReportCategory[] REPORT_CATEGORIES = new ReportCategory[5];

	@BeforeClass
	public static void before() {
		REPORT_CATEGORIES[0] = new ReportCategory("Accounting", "ACCT", false);
		REPORT_CATEGORIES[1] = new ReportCategory("Q-Free internal", "QFREE", true);
		REPORT_CATEGORIES[2] = new ReportCategory("Manual validation", "MIR", false);
		REPORT_CATEGORIES[3] = new ReportCategory("Traffic", "TRA", true);
		REPORT_CATEGORIES[4] = new ReportCategory("Image processing", "OCR", true);
		//		REPORT_CATEGORIES[5] = new ReportCategory("description", "abbreviation", false);
		//		REPORT_CATEGORIES[0] = new ReportCategory(
		//				UUID.fromString("7a482694-51d2-42d0-b0e2-19dd13bbbc64"), "Accounting", "ACCT", false);
		//		REPORT_CATEGORIES[1] = new ReportCategory(
		//				UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0"), "Q-Free internal", "QFREE", true);
		//		REPORT_CATEGORIES[2] = new ReportCategory(
		//				UUID.fromString("5c3cc664-b685-4f6e-8d9a-2927c6bcffdc"), "Manual validation", "MIR", false);
		//		REPORT_CATEGORIES[3] = new ReportCategory(
		//				UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76"), "Traffic", "TRA", true);
		//		REPORT_CATEGORIES[4] = new ReportCategory(
		//				UUID.fromString("b9a6301b-07ac-48d2-8de6-1fc6330c0cb0"), "Image processing", "OCR", true);
		//		//		REPORT_CATEGORIES[5] = new ReportCategory(4L, "description", "abbreviation", false);
	}

}
