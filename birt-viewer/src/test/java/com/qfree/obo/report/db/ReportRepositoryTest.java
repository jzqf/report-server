package com.qfree.obo.report.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=PersistenceConfigTestEnv.class)
public class ReportRepositoryTest {
	
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
		Report thirteen = reportRepository.findOne(13L);
		assertEquals(13, thirteen.getReportId().longValue());
		assertEquals("Report name #13", thirteen.getName());
		assertEquals(1332682500000L, thirteen.getCreatedOn().getTime());
		assertEquals(4, thirteen.getReportCategory().getReportCategoryId().longValue());
		assertEquals("Traffic", thirteen.getReportCategory().getDescription());
		assertEquals("TRA", thirteen.getReportCategory().getAbbreviation());
		assertTrue(thirteen.getReportCategory().isActive());
	}

	@Test
	@Transactional
	public void findByReportCategory() {
		List<Report> reports = reportRepository.findByReportCategoryReportCategoryId(4L);
		assertEquals(11, reports.size());
		for (int i = 0; i < 11; i++) {
			assertEquals(i+5, reports.get(i).getReportId().longValue());
		}
	}
	
	@Test
	@Transactional
	public void save() {
		assertEquals(15, reportRepository.count());
		ReportCategory reportCategory = reportRepository.findOne(13L).getReportCategory();
		Report report = new Report(null, reportCategory, "Un Nuevo Spittle from Art", new Date());
		Report saved = reportRepository.save(report);
		assertEquals(16, reportRepository.count());
		assertNewReport(saved);
		assertNewReport(reportRepository.findOne(16L));
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(15, reportRepository.count());
		assertNotNull(reportRepository.findOne(13L));
		reportRepository.delete(13L);
		assertEquals(14, reportRepository.count());
		assertNull(reportRepository.findOne(13L));
	}
	
	private void assertRecent(List<Report> recent, int count) {
		long[] recentIds = new long[] {3,2,1,15,14,13,12,11,10,9};
		assertEquals(count, recent.size());
		for (int i = 0; i < count; i++) {
			assertEquals(recentIds[i], recent.get(i).getReportId().longValue());
		}
	}
	
	private void assertNewReport(Report report) {
		assertEquals(16, report.getReportId().longValue());
	}
	
}
