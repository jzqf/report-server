package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;

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
@ContextConfiguration(classes = JpaConfig.class)
public class ReportCategoryRepositoryTest {

	@Autowired
	SpitterRepository reportCategoryRepository;
	
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
		assertReportCategory(0, reportCategories.get(0));
		assertReportCategory(1, reportCategories.get(1));
		assertReportCategory(2, reportCategories.get(2));
		assertReportCategory(3, reportCategories.get(3));
	}
	
	@Test
	@Transactional
	public void findByDescription() {
		assertReportCategory(0, reportCategoryRepository.findByDescription("habuma"));
		assertReportCategory(1, reportCategoryRepository.findByDescription("mwalls"));
		assertReportCategory(2, reportCategoryRepository.findByDescription("chuck"));
		assertReportCategory(3, reportCategoryRepository.findByDescription("artnames"));
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
		ReportCategory reportCategory = new ReportCategory(null, "newbee", "letmein", "New Bee", "newbee@habuma.com", true);
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
		ReportCategory reportCategory = new ReportCategory(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", false);
		ReportCategory saved = reportCategoryRepository.save(reportCategory);
		assertReportCategory(5, saved);
		assertEquals(4, reportCategoryRepository.count());
		ReportCategory updated = reportCategoryRepository.findOne(4L);
		assertReportCategory(5, updated);
	}

	private static void assertReportCategory(int expectedReportCategoryIndex, ReportCategory actual) {
		assertReportCategory(expectedReportCategoryIndex, actual, "Newbie");
	}
	
	private static void assertReportCategory(int expectedReportCategoryIndex, ReportCategory actual, String expectedUnusedReportCategoryField3) {
		ReportCategory expected = REPORT_CATEGORIES[expectedReportCategoryIndex];
		assertEquals(expected.getReportCategoryId(), actual.getReportCategoryId());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getUnusedReportCategoryField1(), actual.getUnusedReportCategoryField1());
		assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
		assertEquals(expected.getUnusedReportCategoryField2(), actual.getUnusedReportCategoryField2());
		assertEquals(expected.isActive(), actual.isActive());
	}
	
	private static ReportCategory[] REPORT_CATEGORIES = new ReportCategory[6];
	
	@BeforeClass
	public static void before() {
		REPORT_CATEGORIES[0] = new ReportCategory(1L, "habuma", "password", "Craig Walls", "craig@habuma.com", false);
		REPORT_CATEGORIES[1] = new ReportCategory(2L, "mwalls", "password", "Michael Walls", "mwalls@habuma.com", true);
		REPORT_CATEGORIES[2] = new ReportCategory(3L, "chuck", "password", "Chuck Wagon", "chuck@habuma.com", false);
		REPORT_CATEGORIES[3] = new ReportCategory(4L, "artnames", "password", "Art Names", "art@habuma.com", true);
		REPORT_CATEGORIES[4] = new ReportCategory(5L, "newbee", "letmein", "New Bee", "newbee@habuma.com", true);		
		REPORT_CATEGORIES[5] = new ReportCategory(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", false);		
	}
	
}
