package com.qfree.obo.report.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.util.ReportUtils;
import com.qfree.obo.report.util.RptdesignFileFilter;

@Component
@Transactional
public class ReportSyncService {

	private static final Logger logger = LoggerFactory.getLogger(ReportSyncService.class);

	private final ReportRepository reportRepository;

	@Autowired
	public ReportSyncService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;
	}

	public ReportSyncResource syncReportsWithFileSystem(ServletContext servletContext, UriInfo uriInfo,
			List<String> expand, Map<String, List<String>> extraQueryParams, RestApiVersion apiVersion)
			throws UnsupportedEncodingException, IOException {

		File reportsDirectory = Paths.get(servletContext.getRealPath(""),
				ReportUtils.BIRT_VIEWER_WORKING_FOLDER).toFile();

		/*
		 * Delete all existing "rptdesign" files in the "reports" directory.
		 */
		List<String> reportsDeleted = new ArrayList<>();
		List<String> reportsNotDeleted = new ArrayList<>();
		for (File file : reportsDirectory.listFiles(new RptdesignFileFilter())) {
			if (file.delete()) {
				reportsDeleted.add(file.getAbsolutePath());
				logger.info("Deleted file \"{}\"", file.getAbsolutePath());
			} else {
				reportsNotDeleted.add(file.getAbsolutePath());
				logger.warn("Unable to delete file \"{}\"", file.getAbsolutePath());
			}
		}

		/*
		 * Write out all "rptdesign" files from the report server database 
		 * that correspond to active reports and active report versions. 
		 */
		List<Report> reports = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS) {
			reports = reportRepository.findByActiveTrue();
		} else {
			reports = reportRepository.findAll();
		}
		List<String> reportsCreated = new ArrayList<>();
		List<String> reportsNotCreated = new ArrayList<>();	// not currently used
		for (Report report : reports) {
			for (ReportVersion reportVersion : report.getReportVersions()) {
				/*
				 * Only write out active report versions. If a report version
				 * has been made inactive then it may no longer work 
				 * correctly or it may display data that should no longer be
				 * viewed.
				 */
				if (reportVersion.isActive()) {
					logger.debug("report version to write = {}", reportVersion);
					logger.debug("report version = \"{}\", number of bytes = {}",
							reportVersion.getReportVersionId(), reportVersion.getRptdesign().length());
					/*
					 * Write uploaded rptdesign file to the file system of the report 
					 * server, overwriting a file with the same name, if one exists.
					 */
					Path rptdesignFilePath = ReportUtils.writeRptdesignFile(reportVersion,
							servletContext.getRealPath(""));
					reportsCreated.add(rptdesignFilePath.toAbsolutePath().toString());
				}
			}
		}
		return new ReportSyncResource(reportsDeleted, reportsNotDeleted, reportsCreated, reportsNotCreated);
	}
}
