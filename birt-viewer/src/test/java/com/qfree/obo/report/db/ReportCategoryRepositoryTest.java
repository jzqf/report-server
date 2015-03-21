package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ReportCategoryRepositoryTest {

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

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
		assertReportCategory(0, reportCategoryRepository.findOne(1L));
		assertReportCategory(1, reportCategoryRepository.findOne(2L));
		assertReportCategory(2, reportCategoryRepository.findOne(3L));
		assertReportCategory(3, reportCategoryRepository.findOne(4L));
	}

	@Test
	@Transactional
	public void save_newReportCategory() {
		assertEquals(4, reportCategoryRepository.count());
		ReportCategory reportCategory = new ReportCategory(null, "Image processing", "OCR", true);
		ReportCategory saved = reportCategoryRepository.save(reportCategory);
		assertEquals(5, reportCategoryRepository.count());
		assertReportCategory(4, saved);
		assertReportCategory(4, reportCategoryRepository.findOne(5L));
	}

	@Test
	@Transactional
	@Ignore
	public void save_existingReportCategory() {
		assertEquals(4, reportCategoryRepository.count());
		ReportCategory reportCategory = new ReportCategory(4L, "arthur", "Arthur Names", false);
		ReportCategory saved = reportCategoryRepository.save(reportCategory);
		assertReportCategory(5, saved);
		assertEquals(4, reportCategoryRepository.count());
		ReportCategory updated = reportCategoryRepository.findOne(4L);
		assertReportCategory(5, updated);
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
		assertEquals(expected.getReportCategoryId(), reportCategory.getReportCategoryId());
		assertEquals(expected.getDescription(), reportCategory.getDescription());
		assertEquals(expected.getAbbreviation(), reportCategory.getAbbreviation());
		assertEquals(expected.isActive(), reportCategory.isActive());
	}

	private static ReportCategory[] REPORT_CATEGORIES = new ReportCategory[5];

	@BeforeClass
	public static void before() {
		REPORT_CATEGORIES[0] = new ReportCategory(1L, "Accounting", "ACCT", false);
		REPORT_CATEGORIES[1] = new ReportCategory(2L, "Q-Free internal", "QFREE", true);
		REPORT_CATEGORIES[2] = new ReportCategory(3L, "Manual validation", "MIR", false);
		REPORT_CATEGORIES[3] = new ReportCategory(4L, "Traffic", "TRA", true);
		REPORT_CATEGORIES[4] = new ReportCategory(5L, "Image processing", "OCR", true);
		//		REPORT_CATEGORIES[5] = new ReportCategory(4L, "description", "abbreviation", false);
	}

}
