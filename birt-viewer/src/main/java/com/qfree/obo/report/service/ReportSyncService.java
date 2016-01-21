package com.qfree.obo.report.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import com.qfree.obo.report.domain.ReportVersionsSortByVersionCode;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.util.ReportUtils;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;
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
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
		Boolean showInactiveReports = ResourcePath.showAll(Report.class, showAll);

		return syncReportsWithFileSystem(Paths.get(servletContext.getRealPath("")), showInactiveReports);
	}

	public ReportSyncResource syncReportsWithFileSystem(Path absoluteContextPath, Boolean showInactiveReports) {

		List<String> reportsDeleted = new ArrayList<>();
		List<String> reportsNotDeleted = new ArrayList<>();
		List<String> reportsCreated = new ArrayList<>();
		List<String> reportsNotCreated = new ArrayList<>(); // not currently used

		try {
			if (ReportUtils.reportSyncSemaphore.tryAcquire(ReportUtils.MAX_WAIT_ACQUIRE_REPORTSYNCSEMAPHORE,
					TimeUnit.SECONDS)) {
				try {

					File reportsDirectory = absoluteContextPath
							.resolve(ReportUtils.BIRT_VIEWER_WORKING_FOLDER).toFile();
					if (!reportsDirectory.isDirectory()) {
						throw new InvalidPathException(reportsDirectory.toString(),
								"\"reports\" directory does not exist");
					}

					/*
					 * Delete all existing "rptdesign" files in the "reports" directory.
					 */
					File[] files = reportsDirectory.listFiles(new RptdesignFileFilter());
					if (files != null) {
						for (File file : files) {
							if (file.delete()) {
								reportsDeleted.add(file.getAbsolutePath());
								logger.info("Deleted file \"{}\"", file.getAbsolutePath());
							} else {
								reportsNotDeleted.add(file.getAbsolutePath());
								logger.warn("Unable to delete file \"{}\"", file.getAbsolutePath());
							}
						}
					}

					/*
					* Write out all "rptdesign" files from the report server database 
					* that correspond to active reports and active report versions. 
					*/
					List<Report> reports = null;
					if (RestUtils.FILTER_INACTIVE_RECORDS
							&& !(showInactiveReports != null ? showInactiveReports : false)) {
						reports = reportRepository.findByActiveTrue();
					} else {
						reports = reportRepository.findAll();
					}
					for (Report report : reports) {
						/*
						 * Order the report versions for the report being treated
						 * by "versionCode". In this way, the older versions are
						 * written to the file system before the more recent
						 * versions (assuming that htere are multiple enabled
						 * reports versions for a report). This is important to
						 * handle the case where rptdesign file names are *not*
						 * unique. Under these conditions, this approach will 
						 * ensure that the most recent version will be accessible.
						 */
						List<ReportVersion> reportVersions = report.getReportVersions();
						Collections.sort(reportVersions, new ReportVersionsSortByVersionCode());
						for (ReportVersion reportVersion : reportVersions) {
							/*
							 * Only write out active report versions. If a report 
							 * version has been made inactive then it may no longer 
							 * work correctly or it may display data that should 
							 * no longer be viewed.
							 */
							if (reportVersion.isActive()) {
								logger.info("Writing report version = {}", reportVersion);
								logger.debug("report version = \"{}\", number of bytes = {}",
										reportVersion.getReportVersionId(), reportVersion.getRptdesign().length());
								/*
								 * Write uploaded rptdesign file to the file system of the report 
								 * server, overwriting a file with the same name, if one exists.
								 */
								Path rptdesignFilePath = ReportUtils.writeRptdesignFile(reportVersion,
										absoluteContextPath.toString());
								reportsCreated.add(rptdesignFilePath.toAbsolutePath().toString());
							}
						}
					}
				} catch (InvalidPathException e) {
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_REPORT_FOLDER_MISSING, e);
				} catch (IOException e) {
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC, e);
				} finally {
					ReportUtils.reportSyncSemaphore.release();
				}
			} else {
				throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_NO_PERMIT);
			}
		} catch (InterruptedException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_INTERRUPT, e);
		}

		return new ReportSyncResource(reportsDeleted, reportsNotDeleted, reportsCreated, reportsNotCreated);
	}

	/**
	 * Write BIRT rptdesign file to the file system of the report server,
	 * overwriting a file with the same name if one exists.
	 * 
	 * @param reportVersion
	 * @param absoluteAppContextPath
	 * @return
	 */
	public Path writeRptdesignFile(ReportVersion reportVersion, String absoluteAppContextPath) {
		Path rptdesignFilePath = null;
		try {
			if (ReportUtils.reportSyncSemaphore.tryAcquire(ReportUtils.MAX_WAIT_ACQUIRE_REPORTSYNCSEMAPHORE,
					TimeUnit.SECONDS)) {
				try {

					/*
					 * Write uploaded rptdesign file to the file system of the report 
					 * server, overwriting a file with the same name, if one exists.
					 */
					rptdesignFilePath = ReportUtils.writeRptdesignFile(reportVersion, absoluteAppContextPath);

				} catch (InvalidPathException e) {
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_REPORT_FOLDER_MISSING, e);
				} catch (IOException e) {
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC, e);
				} finally {
					ReportUtils.reportSyncSemaphore.release();
				}
			} else {
				throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_NO_PERMIT);
			}
		} catch (InterruptedException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_INTERRUPT, e);
		}

		return rptdesignFilePath;
	}

}
