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
public class SpitterRepositoryTest {

	@Autowired
	SpitterRepository spitterRepository;
	
	@Test
	@Transactional
	public void count() {
		assertEquals(4, spitterRepository.count());
	}
	
	@Test
	@Transactional
	public void findAll() {
		List<ReportCategory> spitters = spitterRepository.findAll();
		assertEquals(4, spitters.size());
		assertSpitter(0, spitters.get(0));
		assertSpitter(1, spitters.get(1));
		assertSpitter(2, spitters.get(2));
		assertSpitter(3, spitters.get(3));
	}
	
	@Test
	@Transactional
	public void findByDescription() {
		assertSpitter(0, spitterRepository.findByDescription("habuma"));
		assertSpitter(1, spitterRepository.findByDescription("mwalls"));
		assertSpitter(2, spitterRepository.findByDescription("chuck"));
		assertSpitter(3, spitterRepository.findByDescription("artnames"));
	}
	
	@Test
	@Transactional
	public void findOne() {
		assertSpitter(0, spitterRepository.findOne(1L));
		assertSpitter(1, spitterRepository.findOne(2L));
		assertSpitter(2, spitterRepository.findOne(3L));
		assertSpitter(3, spitterRepository.findOne(4L));
	}
	
	@Test
	@Transactional
	public void save_newSpitter() {
		assertEquals(4, spitterRepository.count());
		ReportCategory spitter = new ReportCategory(null, "newbee", "letmein", "New Bee", "newbee@habuma.com", true);
		ReportCategory saved = spitterRepository.save(spitter);
		assertEquals(5, spitterRepository.count());
		assertSpitter(4, saved);
		assertSpitter(4, spitterRepository.findOne(5L));
	}

	@Test
	@Transactional
	@Ignore
	public void save_existingSpitter() {
		assertEquals(4, spitterRepository.count());
		ReportCategory spitter = new ReportCategory(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", false);
		ReportCategory saved = spitterRepository.save(spitter);
		assertSpitter(5, saved);
		assertEquals(4, spitterRepository.count());
		ReportCategory updated = spitterRepository.findOne(4L);
		assertSpitter(5, updated);
	}

	private static void assertSpitter(int expectedSpitterIndex, ReportCategory actual) {
		assertSpitter(expectedSpitterIndex, actual, "Newbie");
	}
	
	private static void assertSpitter(int expectedSpitterIndex, ReportCategory actual, String expectedUnusedReportCategoryField3) {
		ReportCategory expected = SPITTERS[expectedSpitterIndex];
		assertEquals(expected.getReportCategoryId(), actual.getReportCategoryId());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getUnusedReportCategoryField1(), actual.getUnusedReportCategoryField1());
		assertEquals(expected.getAbbreviation(), actual.getAbbreviation());
		assertEquals(expected.getUnusedReportCategoryField2(), actual.getUnusedReportCategoryField2());
		assertEquals(expected.isUpdateByEmail(), actual.isUpdateByEmail());
	}
	
	private static ReportCategory[] SPITTERS = new ReportCategory[6];
	
	@BeforeClass
	public static void before() {
		SPITTERS[0] = new ReportCategory(1L, "habuma", "password", "Craig Walls", "craig@habuma.com", false);
		SPITTERS[1] = new ReportCategory(2L, "mwalls", "password", "Michael Walls", "mwalls@habuma.com", true);
		SPITTERS[2] = new ReportCategory(3L, "chuck", "password", "Chuck Wagon", "chuck@habuma.com", false);
		SPITTERS[3] = new ReportCategory(4L, "artnames", "password", "Art Names", "art@habuma.com", true);
		SPITTERS[4] = new ReportCategory(5L, "newbee", "letmein", "New Bee", "newbee@habuma.com", true);		
		SPITTERS[5] = new ReportCategory(4L, "arthur", "letmein", "Arthur Names", "arthur@habuma.com", false);		
	}
	
}
