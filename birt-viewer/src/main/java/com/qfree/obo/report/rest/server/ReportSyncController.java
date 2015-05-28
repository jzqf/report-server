package com.qfree.obo.report.rest.server;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.service.ReportSyncService;

@Component
@Path("reportSyncs")
public class ReportSyncController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportSyncController.class);

	private final ReportRepository reportRepository;

	private final ReportSyncService reportSyncService;

	//	private final ReportService reportService;

	@Autowired
	public ReportSyncController(
			ReportRepository reportRepository, ReportSyncService reportSyncService) {
		this.reportRepository = reportRepository;
		this.reportSyncService = reportSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Content-Type: application/json;v=1" -X POST http://localhost:8080/rest/reportSyncs
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.getReportVersions().
	 */
	@Transactional
	@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void syncReportsWithFileSystem(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		Map<String, List<String>> extraQueryParams = new HashMap<>();

		//TODO MOVE THIS CODE TO A SERVICE CLASS! !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		try {
			reportSyncService.syncReportsWithFileSystem(servletContext, uriInfo, expand, extraQueryParams, apiVersion);
			
			//			File reportsDirectory = Paths.get(servletContext.getRealPath(""),
			//					ReportUtils.BIRT_VIEWER_WORKING_FOLDER).toFile();
			//
			//			/*
			//			 * Delete all existing "rptdesign" files in the "reports" directory.
			//			 */
			//			for (File file : reportsDirectory.listFiles(new RptdesignFileFilter())) {
			//				if (file.delete()) {
			//					logger.info("Deleted file \"{}\"", file.getAbsolutePath());
			//				} else {
			//					logger.warn("Unable to delete file \"{}\"", file.getAbsolutePath());
			//				}
			//			}
			//
			//			/*
			//			 * Write out all "rptdesign" files from the report server database 
			//			 * that correspond to active reports and active report versions. 
			//			 */
			//			List<Report> reports = null;
			//			if (RestUtils.FILTER_INACTIVE_RECORDS) {
			//				reports = reportRepository.findByActiveTrue();
			//			} else {
			//				reports = reportRepository.findAll();
			//			}
			//			for (Report report : reports) {
			//				for (ReportVersion reportVersion : report.getReportVersions()) {
			//					/*
			//					 * Only write out active report versions. If a report version
			//					 * has been made inactive then it may no longer work 
			//					 * correctly or it may display data that should no longer be
			//					 * viewed.
			//					 */
			//					if (reportVersion.isActive()) {
			//						logger.debug("report version to write = {}", reportVersion);
			//						logger.debug("report version = \"{}\", number of bytes = {}",
			//								reportVersion.getReportVersionId(), reportVersion.getRptdesign().length());
			//						/*
			//						 * Write uploaded rptdesign file to the file system of the report 
			//						 * server, overwriting a file with the same name, if one exists.
			//						 */
			//						ReportUtils.writeRptdesignFile(reportVersion, servletContext.getRealPath(""));
			//					}
			//				}
			//			}

		} catch (InvalidPathException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_REPORT_FOLDER_MISSING, e);	//TODO Test this!!!!!!!!!!!!!!!!!!!!!!
		} catch (IOException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC, e);	//TODO Test this!!!!!!!!!!!!!!!!!!!!!!
		}

		//TODO Eventually, return number of reports deleted, created?
	}
}
