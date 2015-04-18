package com.qfree.obo.report.db;

//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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

import com.qfree.obo.report.ApplicationConfig;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
public class ReportRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportRepositoryTests.class);

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	ReportCategoryRepository reportCategoryRepository;

	@Test
	@Transactional
	public void count() {
		//		assertEquals(6, reportRepository.count());
		assertThat(reportRepository.count(), is(equalTo(6L)));
		//		assertThat(reportRepository.count(), is(not(equalTo(6L))));  //import static org.hamcrest.Matchers.not;
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
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertEquals(uuidOfReport04.toString(), report04.getReportId().toString());
		assertEquals("Report name #04", report04.getName());

		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse("2014-03-25T12:15:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//		logger.info("date = {}", date);
		//		logger.info("report04.getCreatedOn() = {}", report04.getCreatedOn());
		//		logger.info("report04.getCreatedOn().getTime() = {}", report04.getCreatedOn().getTime());

		assertEquals(date.getTime(), report04.getCreatedOn().getTime());
		assertEquals("bb2bc482-c19a-4c19-a087-e68ffc62b5a0",
				report04.getReportCategory().getReportCategoryId().toString());
		assertEquals("Q-Free internal", report04.getReportCategory().getDescription());
		assertEquals("QFREE", report04.getReportCategory().getAbbreviation());
		assertTrue(report04.getReportCategory().isActive());
	}

	@Test
	@Transactional
	public void findByReportCategory() {
		UUID uuidOf1stReportCategoryRow = UUID.fromString("7a482694-51d2-42d0-b0e2-19dd13bbbc64");
		List<Report> reports = reportRepository
				.findByReportCategoryReportCategoryId(uuidOf1stReportCategoryRow);
		assertEquals(3, reports.size());
	}

	@Test
	@Transactional
	public void save() {
		assertEquals(6, reportRepository.count());
		UUID uuidOfReport03 = UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d");
		ReportCategory reportCategoryOfReport03 = reportRepository.findOne(uuidOfReport03).getReportCategory();
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
		Report report = new Report(reportCategoryOfReport03, "Some report title", 666, true);
		Report saved = reportRepository.save(report);
		assertNotNull(saved.getReportId());    // Check that id was created.
		assertEquals(7, reportRepository.count());
		//		assertNewReport(saved);
		//		assertNewReport(reportRepository.findOne(16L));
		Report foundReport = reportRepository.findOne(saved.getReportId());
		assertEquals(foundReport, saved);
		assertEquals("Some report title", foundReport.getName());
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(6, reportRepository.count());
		assertEquals(4, reportCategoryRepository.count());
		UUID uuidOfReport02 = UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca");
		assertNotNull(reportRepository.findOne(uuidOfReport02));
		//		ReportCategory reportCategoryOfReport02 = reportRepository.findOne(uuidOf2ndRow).getReportCategory();
		reportRepository.delete(uuidOfReport02);
		assertEquals(5, reportRepository.count());
		assertNull(reportRepository.findOne(uuidOfReport02));
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
		UUID uuidOfReport01 = UUID.fromString("d65f3d9c-f67d-4beb-9936-9dfa19aa1407");
		UUID uuidOfReport02 = UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca");
		UUID uuidOfReport03 = UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d");
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		UUID uuidOfReport05 = UUID.fromString("f1f06b15-c0b6-488d-9eed-74e867a47d5a");
		UUID uuidOfReport06 = UUID.fromString("adc50b28-cb84-4ede-9759-43f467ac22ec");
		recentReportIds = new ArrayList<>();
		// These are in reverse chronological order, from most recent to oldest.
		recentReportIds.add(uuidOfReport06);
		recentReportIds.add(uuidOfReport03);
		recentReportIds.add(uuidOfReport02);
		recentReportIds.add(uuidOfReport01);
		recentReportIds.add(uuidOfReport04);
		recentReportIds.add(uuidOfReport05);
		//		logger.info("recentReportIds.size() = ", recentReportIds.size());
	}
}
