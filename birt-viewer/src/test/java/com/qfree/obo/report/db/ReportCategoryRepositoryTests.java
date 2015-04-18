package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

import com.qfree.obo.report.PersistenceConfigTestEnv;
import com.qfree.obo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ReportCategoryRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTests.class);

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

	@Autowired
	ReportRepository reportRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(4, reportCategoryRepository.count());
	}

	@Test
	@Transactional
	public void findAll() {
		List<ReportCategory> reportCategories = reportCategoryRepository.findAll();
		assertEquals(4, reportCategories.size());
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
		assertEquals(1, reportCategories.size());
		assertReportCategory(0, reportCategories.get(0));

		reportCategories = reportCategoryRepository.findByDescriptionLikeOrAbbreviationLike("", "%FREE");
		assertEquals(1, reportCategories.size());
		assertReportCategory(1, reportCategories.get(0));

		reportCategories = reportCategoryRepository.findByDescriptionLikeOrAbbreviationLike("Accoun%", "%FREE");
		assertEquals(2, reportCategories.size());
	}

	@Test
	@Transactional
	public void findByActiveIsTrue() {
		List<ReportCategory> reportCategories = reportCategoryRepository.findByActiveIsTrue();
		assertEquals(2, reportCategories.size());
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
		assertEquals(4, reportCategoryRepository.count());
		//		ReportCategory reportCategory = new ReportCategory(
		//				UUID.fromString("b9a6301b-07ac-48d2-8de6-1fc6330c0cb0"), "Image processing", "OCR", true);
		ReportCategory reportCategory = new ReportCategory("Image processing", "OCR", true);
		ReportCategory saved = reportCategoryRepository.save(reportCategory);
		assertEquals(5, reportCategoryRepository.count());
		assertReportCategory(4, saved);
		assertReportCategory(4, reportCategoryRepository
				.findOne(saved.getReportCategoryId()));
	}

	@Test
	@Transactional
	public void save_existingReportCategory() {
		assertEquals(4, reportCategoryRepository.count());
		ReportCategory found = reportCategoryRepository
				.findOne(UUID.fromString("bb2bc482-c19a-4c19-a087-e68ffc62b5a0"));
		assertReportCategory(1, found);
		ReportCategory saved = reportCategoryRepository.save(found);
		assertEquals(4, reportCategoryRepository.count());
	}

	@Test
	@Ignore
	@Transactional
	public void delete() {
		assertEquals(4, reportCategoryRepository.count());
		assertEquals(15, reportRepository.count());
		UUID uuidOfTrafficeCategory = UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76");
		assertNotNull(reportCategoryRepository.findOne(uuidOfTrafficeCategory));
		reportCategoryRepository.delete(uuidOfTrafficeCategory);
		assertEquals(3, reportCategoryRepository.count());
		assertEquals(4, reportRepository.count());	// only for cascade=CascadeType.REMOVE in ReportCategory
		assertNull(reportCategoryRepository.findOne(uuidOfTrafficeCategory));
	}

	/**
	 * Checks that the {@link ReportCategory} instance passed to this method is 
	 * found at a specified index within the REPORT_CATEGORIES array.
	 * 
	 * @param expectedReportCategoryIndex
	 * @param reportCategory
	 */
	private static void assertReportCategory(int expectedReportCategoryIndex, ReportCategory reportCategory) {
		assertNotNull(reportCategory);
		ReportCategory expected = REPORT_CATEGORIES[expectedReportCategoryIndex];
		//		ReportCategory expected = REPORT_CATEGORIES.get(expectedReportCategoryIndex);
		//		assertEquals(expected.getReportCategoryId(), reportCategory.getReportCategoryId());
		assertEquals(expected.getDescription(), reportCategory.getDescription());
		assertEquals(expected.getAbbreviation(), reportCategory.getAbbreviation());
		assertEquals(expected.isActive(), reportCategory.isActive());
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
