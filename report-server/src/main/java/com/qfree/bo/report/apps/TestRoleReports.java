package com.qfree.bo.report.apps;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.bo.report.ApplicationConfig;
import com.qfree.bo.report.db.ReportRepository;
import com.qfree.bo.report.db.ReportVersionRepository;
import com.qfree.bo.report.db.RoleReportRepository;
import com.qfree.bo.report.db.RoleRepository;
import com.qfree.bo.report.domain.Report;
import com.qfree.bo.report.domain.Role;
import com.qfree.bo.report.domain.RoleReport;

public class TestRoleReports {

	private static final Logger logger = LoggerFactory.getLogger(TestRoleReports.class);

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);

		logger.info("\n\n\n");

		ReportRepository reportRepository = context.getBean(ReportRepository.class);
		ReportVersionRepository reportVersionRepository = context.getBean(ReportVersionRepository.class);
		RoleRepository roleRepository = context.getBean(RoleRepository.class);
		RoleReportRepository roleReportRepository = context.getBean(RoleReportRepository.class);

		UUID reportadmin_uuid=UUID.fromString("54aa1d35-f67d-47e6-8bea-cadd6085796e");
		UUID user4_uuid=UUID.fromString("46e477dc-085f-4714-a24f-742428579fcc");
		
		UUID report100_cascade_uuid=UUID.fromString("d65f3d9c-f67d-4beb-9936-9dfa19aa1407");
		UUID report400_allparams_uuid=UUID.fromString("2bcc090c-0e33-4e62-9127-7856f3fefd87");
		
		//	UUID reportversion_cascade_uuid=UUID.fromString("");
		//	UUID reportversion_allparams_uuid=UUID.fromString("");

		Boolean activeReportsOnly = true;
		Boolean activeInheritedRolesOnly = true;
		
		List<String> availableReportVersions = null;

		availableReportVersions = reportVersionRepository
				.findReportVersionFilenamesByRoleIdRecursive(reportadmin_uuid.toString(), activeReportsOnly, activeInheritedRolesOnly);
		logger.info("findReportVersionFilenamesByRoleIdRecursive(reportadmin_uuid.toString(), activeReportsOnly, activeInheritedRolesOnly) = {}",
				availableReportVersions);

		availableReportVersions = reportVersionRepository
				.findReportVersionFilenamesByRoleIdRecursive(user4_uuid.toString(), activeReportsOnly, activeInheritedRolesOnly);
		logger.info("findReportVersionFilenamesByRoleIdRecursive(user4_uuid.toString(), activeReportsOnly, activeInheritedRolesOnly) = {}",
				availableReportVersions);

		RoleReport roleReport = null;

		roleReport = roleReportRepository.findByRoleRoleIdAndReportReportId(reportadmin_uuid, report100_cascade_uuid);
		logger.info(
				"roleReportRepository.findByRoleRoleIdAndReportReportId(reportadmin_uuid, report100_cascade_uuid) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleRoleIdAndReportReportId(reportadmin_uuid, report400_allparams_uuid);
		logger.info(
				"roleReportRepository.findByRoleRoleIdAndReportReportId(reportadmin_uuid, report400_allparams_uuid) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleRoleIdAndReportReportId(user4_uuid, report100_cascade_uuid);
		logger.info("roleReportRepository.findByRoleRoleIdAndReportReportId(user4_uuid, report100_cascade_uuid) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleRoleIdAndReportReportId(user4_uuid, report400_allparams_uuid);
		logger.info("roleReportRepository.findByRoleRoleIdAndReportReportId(user4_uuid, report400_allparams_uuid) = {}",
				roleReport);
		
		Role reportadmin = roleRepository.findOne(reportadmin_uuid);
		Role user4 = roleRepository.findOne(user4_uuid);

		Report report100_cascade = reportRepository.findOne(report100_cascade_uuid);
		Report report400_allparams = reportRepository.findOne(report400_allparams_uuid);

		roleReport = roleReportRepository.findByRoleAndReport(reportadmin, report100_cascade);
		logger.info(
				"roleReportRepository.findByRoleAndReport(reportadmin, report100_cascade) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleAndReport(reportadmin, report400_allparams);
		logger.info(
				"roleReportRepository.findByRoleAndReport(reportadmin, report400_allparams) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleAndReport(user4, report100_cascade);
		logger.info("roleReportRepository.findByRoleAndReport(user4, report100_cascade) = {}",
				roleReport);

		roleReport = roleReportRepository.findByRoleAndReport(user4, report400_allparams);
		logger.info("roleReportRepository.findByRoleAndReport(user4, report400_allparams) = {}",
				roleReport);

		logger.info("\n\n\n");

		context.close();
	}

}