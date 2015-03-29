package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PersistenceConfigTestEnv.class)
public class ReportRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTest.class);

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(5, reportRepository.count());
	}

	@Test
	@Transactional
	public void findRecent() {
		// default case
		//		{
		//			List<Report> recent = reportRepository.findRecentlyCreated();
		//			assertRecent(recent, 10);
		//		}
		
		// specific count case
		{
			List<Report> recent = reportRepository.findRecentlyCreated(4);
			assertRecent(recent, 4);
		}
	}

	@Test
	@Transactional
	public void findOne() {
		UUID uuidOf4thReportRow = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report fourthReport = reportRepository.findOne(uuidOf4thReportRow);
		assertEquals(uuidOf4thReportRow.toString(), fourthReport.getReportId().toString());
		assertEquals("Report name #04", fourthReport.getName());

		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse("2012-03-25T12:15:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//		logger.info("date = {}", date);
		//		logger.info("fourthReport.getCreatedOn() = {}", fourthReport.getCreatedOn());
		//		logger.info("fourthReport.getCreatedOn().getTime() = {}", fourthReport.getCreatedOn().getTime());

		assertEquals(date.getTime(), fourthReport.getCreatedOn().getTime());
		assertEquals("bb2bc482-c19a-4c19-a087-e68ffc62b5a0",
				fourthReport.getReportCategory().getReportCategoryId().toString());
		assertEquals("Q-Free internal", fourthReport.getReportCategory().getDescription());
		assertEquals("QFREE", fourthReport.getReportCategory().getAbbreviation());
		assertTrue(fourthReport.getReportCategory().isActive());
	}

	@Test
	@Transactional
	public void findByReportCategory() {
		UUID uuidOf1stReportCategoryRow = UUID.fromString("7a482694-51d2-42d0-b0e2-19dd13bbbc64");
		List<Report> reports = reportRepository
				.findByReportCategoryReportCategoryId(uuidOf1stReportCategoryRow);
		assertEquals(3, reports.size());
		//		for (int i = 0; i < 11; i++) {
		//			assertEquals(i+5, reports.get(i).getReportId().longValue());
		//		}
	}
	
	@Test
	@Transactional
	public void save() {
		assertEquals(5, reportRepository.count());
		UUID uuidOf3rdReportRow = UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d");
		ReportCategory reportCategoryOf3rdReport = reportRepository.findOne(uuidOf3rdReportRow).getReportCategory();
		String rptdesign = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+
				"<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">\n"
				+
				"    <property name=\"createdBy\">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>\n"
				+
				"    <property name=\"units\">in</property>\n" +
				"    <property name=\"iconFile\">/templates/blank_report.gif</property>\n" +
				"    <property name=\"bidiLayoutOrientation\">ltr</property>\n" +
				"    <property name=\"imageDPI\">96</property>\n" +
				"    <page-setup>\n" +
				"        <simple-master-page name=\"Simple MasterPage\" id=\"2\"/>\n" +
				"    </page-setup>\n" +
				"</report>";
		Report report = new Report(reportCategoryOf3rdReport, "Some report title", rptdesign, new Date());
		Report saved = reportRepository.save(report);
		assertNotNull(saved.getReportId());    // Check that id was created.
		assertEquals(6, reportRepository.count());
		//		assertNewReport(saved);
		//		assertNewReport(reportRepository.findOne(16L));
		Report foundReport = reportRepository.findOne(saved.getReportId());
		assertEquals(foundReport, saved);
		assertEquals("Some report title", foundReport.getName());
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(5, reportRepository.count());
		assertEquals(4, reportCategoryRepository.count());
		UUID uuidOf2ndRow = UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca");
		assertNotNull(reportRepository.findOne(uuidOf2ndRow));
		//		ReportCategory reportCategoryOf2ndReport = reportRepository.findOne(uuidOf2ndRow).getReportCategory();
		reportRepository.delete(uuidOf2ndRow);
		assertEquals(4, reportRepository.count());
		assertNull(reportRepository.findOne(uuidOf2ndRow));
		assertEquals(4, reportCategoryRepository.count());
	}
	
	private void assertRecent(List<Report> recent, int count) {
		assertEquals(count, recent.size());
		for (int i = 0; i < count; i++) {
			assertTrue(recentReportIds.contains(recent.get(i).getReportId()));
		}
	}
	
	//	private void assertNewReport(Report report) {
	//		assertEquals(16, report.getReportId().longValue());
	//	}
	
	private static List<UUID> recentReportIds;

	@BeforeClass
	public static void before() {
		recentReportIds = new ArrayList<>();
		recentReportIds.add(UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d"));
		recentReportIds.add(UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca"));
		recentReportIds.add(UUID.fromString("d65f3d9c-f67d-4beb-9936-9dfa19aa1407"));
		recentReportIds.add(UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6"));
		recentReportIds.add(UUID.fromString("f1f06b15-c0b6-488d-9eed-74e867a47d5a"));
		logger.info("recentReportIds.size() = ", recentReportIds.size());
	}
}
