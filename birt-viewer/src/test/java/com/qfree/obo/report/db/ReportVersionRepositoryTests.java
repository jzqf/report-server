package com.qfree.obo.report.db;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hamcrest.core.IsCollectionContaining;
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
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.util.DateUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfig.class)
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ReportVersionRepositoryTests {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionRepositoryTests.class);

	@Autowired
	ReportVersionRepository reportVersionRepository;

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	ReportParameterRepository reportParameterRepository;

	@Test
	@Transactional
	public void count() {
		assertThat(reportVersionRepository.count(), is(equalTo(7L)));
	}

	@Test
	@Transactional
	public void findOne() {
		UUID uuidOfReport04Version2 = UUID.fromString("80b14b11-45c7-4a05-99ed-972050f2338f");
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		UUID reportParameterUuid_1 = UUID.fromString("458c5619-5f0e-4218-b0b0-ae02f2174be0");
		UUID reportParameterUuid_2 = UUID.fromString("fc7bcff1-2234-460f-a0cf-d832d5933eaf");
		UUID reportParameterUuid_3 = UUID.fromString("48583e0f-490b-46f3-9fa5-d35dc3ffdc90");
		UUID reportParameterUuid_4 = UUID.fromString("e7d61d7d-c2d2-477c-9abb-0d56d172f392");
		UUID reportParameterUuid_5 = UUID.fromString("bcab7172-a063-46ba-857d-2acaa5e498df");
		UUID reportParameterUuid_6 = UUID.fromString("72478fe3-cb7a-4df9-b8e1-fc05b71195c5");
		UUID reportParameterUuid_7 = UUID.fromString("be77549d-bf1a-4aee-b9aa-78ba633a8358");
		String versionName = "0.6";
		Integer versionCode = 2;
		Boolean active = true;
		Date createdOn = DateUtils.dateUtcFromIso8601String("2015-05-06T16:59:00.000Z");

		ReportVersion report04Version2 = reportVersionRepository.findOne(uuidOfReport04Version2);
		assertThat(report04Version2, is(not(nullValue())));
		assertThat(report04Version2.getReportVersionId().toString(), is(equalTo(uuidOfReport04Version2.toString())));
		assertThat(report04Version2.getReport().getReportId().toString(), is(equalTo(uuidOfReport04.toString())));
		assertThat(report04Version2.getVersionName(), is(versionName));
		assertThat(report04Version2.getVersionCode(), is(versionCode));
		assertThat(report04Version2.getReport().isActive(), is(true));
		assertThat(DateUtils.entityTimestampToNormalDate(report04Version2.getCreatedOn()), is(createdOn));
		assertThat(report04Version2.getReportParameters(), is(not(nullValue())));
		assertThat(report04Version2.getReportParameters(), hasSize(7));
		List<UUID> reportParameterUuids = new ArrayList<>(7);
		for (ReportParameter reportParameter : report04Version2.getReportParameters()) {
			reportParameterUuids.add(reportParameter.getReportParameterId());
		}
		assertThat(reportParameterUuids, IsCollectionContaining.hasItems(
				reportParameterUuid_1, reportParameterUuid_2, reportParameterUuid_3, reportParameterUuid_4,
				reportParameterUuid_5, reportParameterUuid_6, reportParameterUuid_7));
	}

	@Test
	@Transactional
	public void findByReport() {
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		List<ReportVersion> reportVersions = null;
		reportVersions = reportVersionRepository.findByReportId(uuidOfReport04);
		assertThat(reportVersions, is(not(nullValue())));
		assertThat(reportVersions.size(), is(2));
		reportVersions = reportVersionRepository.findActiveByReportId(uuidOfReport04);
		assertThat(reportVersions, is(not(nullValue())));
		assertThat(reportVersions.size(), is(2));
	}

	@Test
	@Transactional
	public void maxVersionCodeForReport() {
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));
		assertThat(report04.getReportId().toString(), is(equalTo(uuidOfReport04.toString())));
		assertThat(reportVersionRepository.maxVersionCodeForReport(report04), is(2));
	}

	@Test
	@Transactional
	public void saveNew() {
		/*
		 * ReportVersion attributes for creating a new ReportVersion.
		 */
		UUID uuidOfReport04 = UUID.fromString("702d5daa-e23d-4f00-b32b-67b44c06d8f6");
		Report report04 = reportRepository.findOne(uuidOfReport04);
		assertThat(report04, is(not(nullValue())));
		String newFilename = "400-TestReport04_v2.1-CreatedByIntegrationTesting.rptdesign";
		String newRptdesign = new StringBuilder()
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
				.append("<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">\n")
				.append("<property name=\"createdBy\">Eclipse BIRT Designer Version 4.4.2.v201410272105 Build &lt;4.4.2.v20150217-1805></property>\n")
				.append("<property name=\"units\">in</property>\n")
				.append("<property name=\"iconFile\">/templates/blank_report.gif</property>\n")
				.append("<property name=\"bidiLayoutOrientation\">ltr</property>\n")
				.append("<property name=\"imageDPI\">96</property>\n")
				.append("<page-setup>\n")
				.append("<simple-master-page name=\"Simple MasterPage\" id=\"2\"/>\n")
				.append("</page-setup>\n")
				.append("<body>\n")
				.append("<label id=\"3\">\n")
				.append("<text-property name=\"text\">Report created by ReportVersionRepositoryTests.saveNew() integration test</text-property>\n")
				.append("</label>\n")
				.append("</body>\n")
				.append("</report>").toString();
		String newVersionName = "2.1";
		Integer newVersionCode = 4;
		Boolean newActive = true;
		Date newCreatedOn = null;	// this will be filled in when the ReportVersion is created

		long currentTotalNumberOfReportVersions = 7;
		int currentNumberOfReport04ReportVersions = 2;

		assertThat(reportVersionRepository.count(), is(currentTotalNumberOfReportVersions));
		assertThat(report04.getReportVersions(),is(not(nullValue())));
		assertThat(report04.getReportVersions(), hasSize(currentNumberOfReport04ReportVersions));

		ReportVersion reportVersionToCreate = new ReportVersion(report04, newFilename, newRptdesign,
				newVersionName, newVersionCode, newActive, newCreatedOn);
		ReportVersion saved = reportVersionRepository.save(reportVersionToCreate);
		assertThat(saved, is(not(nullValue())));
		assertThat(saved.getReportVersionId(), is(not(nullValue())));    // Check that id was created.
		assertThat(reportVersionRepository.count(), is(currentTotalNumberOfReportVersions + 1));
		report04 = reportRepository.refresh(report04);  // force report04.reportVersions to be updated.
		assertThat(report04.getReportVersions(), is(not(nullValue())));
		assertThat(report04.getReportVersions(), hasSize(currentNumberOfReport04ReportVersions + 1));

		ReportVersion foundReportVersion = reportVersionRepository.findOne(saved.getReportVersionId());
		assertThat(foundReportVersion, is(saved));
		assertThat(foundReportVersion.getFileName(), is(equalTo(newFilename)));
		assertThat(foundReportVersion.getRptdesign(), is(equalTo(newRptdesign)));
		assertThat(foundReportVersion.getVersionName(), is(equalTo(newVersionName)));
		assertThat(foundReportVersion.getVersionCode(), is(equalTo(newVersionCode)));
		assertThat(foundReportVersion.isActive(), is(equalTo(newActive)));
		/*
		 * Assert that the "CreatedOn" datetime is within 5 minutes of the
		 * current time in this process. Ideally,they should be much, much
		 * closer, but this at least is a sanity check that the "CreatedOn"
		 * datetime is actually getting set. We don't want this to fail unless
		 * there is a significant difference; otherwise, this could cause 
		 * problems with continuous integration and automatic builds.
		 */
		long millisecondsSinceCreated = (DateUtils.nowUtc()).getTime() - foundReportVersion.getCreatedOn().getTime();
		assertThat(Math.abs(millisecondsSinceCreated), is(lessThan(5L * 60L * 1000L)));
	}

	@Test
	@Transactional
	public void delete() {
		UUID uuidOfReport04Version2 = UUID.fromString("80b14b11-45c7-4a05-99ed-972050f2338f");
		long currentNumberOfReportVersions = 7;
		long numberOfReportVersionReportParameters = 7;
		long currentTotalNumberOfReportParameters = 13;
		long currentTotalNumberOfReports = 6;
		assertThat(reportVersionRepository.count(), is(currentNumberOfReportVersions));
		assertThat(reportParameterRepository.count(), is(currentTotalNumberOfReportParameters));
		assertThat(reportRepository.count(), is(currentTotalNumberOfReports));
		assertThat(reportVersionRepository.findOne(uuidOfReport04Version2), is(not(nullValue())));
		//ReportVersion Report04Version2 = reportVersionRepository.findOne(uuidOfReport04Version2);
		reportVersionRepository.delete(uuidOfReport04Version2);
		assertThat(reportVersionRepository.count(), is(currentNumberOfReportVersions - 1));
		assertThat(reportParameterRepository.count(), is(currentTotalNumberOfReportParameters
				- numberOfReportVersionReportParameters));
		assertThat(reportRepository.count(), is(currentTotalNumberOfReports));
	}
}
