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
@ContextConfiguration(classes=JpaConfig.class)
public class SpittleRepositoryTest {
	
	@Autowired
	SpittleRepository spittleRepository;

	@Test
	@Transactional
	public void count() {
		assertEquals(15, spittleRepository.count());
	}

	@Test
	@Transactional
	public void findRecent() {
		// default case
		{
			List<Report> recent = spittleRepository.findRecent();
			assertRecent(recent, 10);
		}
		
		// specific count case
		{
			List<Report> recent = spittleRepository.findRecent(5);
			assertRecent(recent, 5);
		}
	}

	@Test
	@Transactional
	public void findOne() {
		Report thirteen = spittleRepository.findOne(13L);
		assertEquals(13, thirteen.getId().longValue());
		assertEquals("Bonjour from Art!", thirteen.getMessage());
		assertEquals(1332682500000L, thirteen.getPostedTime().getTime());
		assertEquals(4, thirteen.getSpitter().getReportCategoryId().longValue());
		assertEquals("artnames", thirteen.getSpitter().getDescription());
		assertEquals("password", thirteen.getSpitter().getUnusedReportCategoryField1());
		assertEquals("Art Names", thirteen.getSpitter().getAbbreviation());
		assertEquals("art@habuma.com", thirteen.getSpitter().getUnusedReportCategoryField2());
		assertTrue(thirteen.getSpitter().isUpdateByEmail());
	}

	@Test
	@Transactional
	public void findBySpitter() {
		List<Report> spittles = spittleRepository.findBySpitterReportCategoryId(4L);
		assertEquals(11, spittles.size());
		for (int i = 0; i < 11; i++) {
			assertEquals(i+5, spittles.get(i).getId().longValue());
		}
	}
	
	@Test
	@Transactional
	public void save() {
		assertEquals(15, spittleRepository.count());
		ReportCategory spitter = spittleRepository.findOne(13L).getSpitter();
		Report spittle = new Report(null, spitter, "Un Nuevo Spittle from Art", new Date());
		Report saved = spittleRepository.save(spittle);
		assertEquals(16, spittleRepository.count());
		assertNewSpittle(saved);
		assertNewSpittle(spittleRepository.findOne(16L));
	}

	@Test
	@Transactional
	public void delete() {
		assertEquals(15, spittleRepository.count());
		assertNotNull(spittleRepository.findOne(13L));
		spittleRepository.delete(13L);
		assertEquals(14, spittleRepository.count());
		assertNull(spittleRepository.findOne(13L));
	}
	
	private void assertRecent(List<Report> recent, int count) {
		long[] recentIds = new long[] {3,2,1,15,14,13,12,11,10,9};
		assertEquals(count, recent.size());
		for (int i = 0; i < count; i++) {
			assertEquals(recentIds[i], recent.get(i).getId().longValue());
		}
	}
	
	private void assertNewSpittle(Report spittle) {
		assertEquals(16, spittle.getId().longValue());
	}
	
}
