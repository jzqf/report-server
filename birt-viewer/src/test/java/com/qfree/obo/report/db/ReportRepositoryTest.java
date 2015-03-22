package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
//@ContextConfiguration(classes = com.qfree.obo.report.apps.RootConfigDesktopApp.class)
public class ReportRepositoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterRepositoryTest.class);

	@Autowired
	ReportRepository reportRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(15, reportRepository.count());
	}

	@Test
	@Transactional
	public void findRecent() {
		// default case
		{
			List<Report> recent = reportRepository.findRecentlyCreated();
			assertRecent(recent, 10);
		}
		
		// specific count case
		{
			List<Report> recent = reportRepository.findRecentlyCreated(5);
			assertRecent(recent, 5);
		}
	}

	@Test
	@Transactional
	public void findOne() {
		UUID uuidOf13thRow = UUID.fromString("75162a48-3031-4e46-a1b5-5fb09248e01e");
		Report thirteen = reportRepository.findOne(uuidOf13thRow);
		assertEquals("75162a48-3031-4e46-a1b5-5fb09248e01e", thirteen.getReportId().toString());
		assertEquals("Report name #13", thirteen.getName());

		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		//		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		dateFormat.setLenient(false);
		Date date = null;
		try {
			date = dateFormat.parse("2012-03-25T13:35:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//		logger.info("date = {}", date);
		//		logger.info("thirteen.getCreatedOn() = {}", thirteen.getCreatedOn());
		//		logger.info("thirteen.getCreatedOn().getTime() = {}", thirteen.getCreatedOn().getTime());

		assertEquals(date.getTime(), thirteen.getCreatedOn().getTime());
		//		assertEquals(1332682500000L, thirteen.getCreatedOn().getTime());
		assertEquals("72d7cb27-1770-4cc7-b301-44d39ccf1e76",
				thirteen.getReportCategory().getReportCategoryId().toString());
		assertEquals("Traffic", thirteen.getReportCategory().getDescription());
		assertEquals("TRA", thirteen.getReportCategory().getAbbreviation());
		assertTrue(thirteen.getReportCategory().isActive());
	}

	@Test
	@Transactional
	public void findByReportCategory() {
		List<Report> reports = reportRepository
				.findByReportCategoryReportCategoryId(UUID.fromString("72d7cb27-1770-4cc7-b301-44d39ccf1e76"));
		assertEquals(11, reports.size());
		//		for (int i = 0; i < 11; i++) {
		//			assertEquals(i+5, reports.get(i).getReportId().longValue());
		//		}
	}
	
	@Test
	@Transactional
	public void save() {
		assertEquals(15, reportRepository.count());
		ReportCategory reportCategory = reportRepository
				.findOne(UUID.fromString("75162a48-3031-4e46-a1b5-5fb09248e01e")).getReportCategory();
		Report report = new Report(reportCategory, "Some report title", new Date());
		Report saved = reportRepository.save(report);
		assertNotNull(saved.getReportId());    // Check that id was created.
		assertEquals(16, reportRepository.count());
		//		assertNewReport(saved);
		//		assertNewReport(reportRepository.findOne(16L));
		Report foundReport = reportRepository.findOne(saved.getReportId());
		assertEquals(foundReport, saved);
		assertEquals("Some report title", foundReport.getName());
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(15, reportRepository.count());
		UUID uuidOf13thRow = UUID.fromString("75162a48-3031-4e46-a1b5-5fb09248e01e");
		assertNotNull(reportRepository.findOne(uuidOf13thRow));
		reportRepository.delete(uuidOf13thRow);
		assertEquals(14, reportRepository.count());
		assertNull(reportRepository.findOne(uuidOf13thRow));
	}
	
	private void assertRecent(List<Report> recent, int count) {
		assertEquals(count, recent.size());
		UUID[] recentIds = new UUID[] {
				UUID.fromString("fe718314-5b39-40e7-aed2-279354c04a9d"),
				UUID.fromString("c7f1d394-9814-4ede-bb01-2700187d79ca"),
				UUID.fromString("d65f3d9c-f67d-4beb-9936-9dfa19aa1407"),
				UUID.fromString("f8b6f20e-626a-4039-b385-7106c15d0204"),
				UUID.fromString("0308594d-081f-4adf-8ead-33f8e1e02217"),
				UUID.fromString("75162a48-3031-4e46-a1b5-5fb09248e01e"),
				UUID.fromString("c606d7f3-9c62-4db3-bba7-52fa58e31327"),
				UUID.fromString("8b42eeef-928a-49cb-b827-4177b5ad6f6d"),
				UUID.fromString("2c2b279b-8d8d-4b15-a795-6de0a0796a03"),
				UUID.fromString("8d9a517b-4628-4a17-af26-21252fafcb5f") };
		for (int i = 0; i < count; i++) {
			assertEquals(recentIds[i], recent.get(i).getReportId());
		}
	}
	
	//	private void assertNewReport(Report report) {
	//		assertEquals(16, report.getReportId().longValue());
	//	}
	
}
