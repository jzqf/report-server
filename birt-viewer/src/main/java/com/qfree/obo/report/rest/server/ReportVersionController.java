package com.qfree.obo.report.rest.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.birt.core.exception.BirtException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ReportRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.dto.ReportParameterCollectionResource;
import com.qfree.obo.report.dto.ReportResource;
import com.qfree.obo.report.dto.ReportSyncResource;
import com.qfree.obo.report.dto.ReportVersionCollectionResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.SubscriptionCollectionResource;
import com.qfree.obo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.obo.report.exceptions.ResourceFilterParseException;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;
import com.qfree.obo.report.service.ReportParameterService;
import com.qfree.obo.report.service.ReportSyncService;
import com.qfree.obo.report.service.ReportVersionService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.REPORTVERSIONS_PATH)
public class ReportVersionController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReportVersionController.class);

	private final ReportVersionRepository reportVersionRepository;
	private final ReportVersionService reportVersionService;
	private final ReportRepository reportRepository;
	private final ReportSyncService reportSyncService;
	private final ReportParameterService reportParameterService;

	@Autowired
	public ReportVersionController(
			ReportVersionRepository reportVersionRepository,
			ReportVersionService reportVersionService,
			ReportRepository reportRepository,
			ReportSyncService reportSyncService,
			ReportParameterService reportParameterService) {
		this.reportVersionRepository = reportVersionRepository;
		this.reportVersionService = reportVersionService;
		this.reportRepository = reportRepository;
		this.reportSyncService = reportSyncService;
		this.reportParameterService = reportParameterService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X -iH "Accept: application/json;v=1" GET http://localhost:8080/rest/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportParameters().
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportVersionCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<ReportVersion> reportVersions = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(ReportVersion.class, showAll)) {
			reportVersions = reportVersionRepository.findByActiveTrue();
		} else {
			reportVersions = reportVersionRepository.findAll();
		}
		return new ReportVersionCollectionResource(reportVersions, ReportVersion.class,
				uriInfo, queryParams, apiVersion);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	 *   '{"report":{"reportId":"702d5daa-e23d-4f00-b32b-67b44c06d8f6"},\
	 *   "fileName":"400-SomeReport_v3.9.rptdesign",\
	 *   "rptdesign":"<?xml version=\"1.0\" encoding=\"UTF-8\"?><report></report>",\
	 *   "versionName":"3.9","versionCode":7,"active":true}' \
	 *   http://localhost:8080/rest/reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportVersions(), as well
	 * as potentially other lazy-evaluated attributes..
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response createByPost(
			ReportVersionResource reportVersionResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) throws IOException, BirtException, RptdesignOpenFromStreamException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * If no value for versionCode was provided, we compute a value for 
		 * it. To do this, we first need to ensure that a value of reportId was
		 * submitted.
		 */
		if (reportVersionResource.getVersionCode() == null) {
			ReportResource reportResource = reportVersionResource.getReportResource();
			UUID reportId = reportResource.getReportId();
			if (reportId != null) {
				Report report = reportRepository.findOne(reportId);
				RestUtils.ifNullThen404(report, Report.class, "reportId", reportId.toString());
				reportVersionResource.setVersionCode(reportVersionService.nextVersionCode(report));
			} else {
				throw new RestApiException(RestError.FORBIDDEN_REPORTVERSION_REPORT_NULL, ReportVersion.class,
						"reportId");
			}
		}

		RestUtils.ifAttrNullOrBlankThen403(reportVersionResource.getRptdesign(), ReportVersion.class, "rptdesign");
		RestUtils.ifAttrNullOrBlankThen403(reportVersionResource.getFileName(), ReportVersion.class, "fileName");
		RestUtils.ifAttrNullOrBlankThen403(reportVersionResource.getVersionName(), ReportVersion.class, "versionName");

		RestUtils.ifNotValidXmlThen403(reportVersionResource.getRptdesign(),
				String.format("The report definition '%s' is not valid XML", reportVersionResource.getFileName()),
				ReportVersion.class, "rptdesign", null);

		ReportVersion reportVersion = reportVersionService.saveNewFromResource(reportVersionResource);
		// if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, ReportVersion.class);// Force primary resource
														// to be "expanded"
		// }

		/*
		 * Parse report parameters here and for each report parameter, create a 
		 * ReportParameter entity which is stored in the report server database.
		 */
		Map<String, Map<String, Serializable>> parameters = reportParameterService
				.createParametersForReport(reportVersion);

		/*
		 * Write uploaded rptdesign file to the file system of the report 
		 * server, overwriting a file with the same name if one exists.
		 */
		java.nio.file.Path rptdesignFilePath = reportSyncService.writeRptdesignFile(reportVersion,
				servletContext.getRealPath(""));

		ReportVersionResource newReportVersionResource = new ReportVersionResource(reportVersion, uriInfo, queryParams,
				apiVersion);

		return created(newReportVersionResource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   1. $ mvn clean spring-boot:run
	 *   
	 *   2. Open the following URL in a web browser:
	 *   
	 *         http://localhost:8080/upload_report.html
	 *   
	 *   3: Specify the following:
	 *   
	 *         Report id:        702d5daa-e23d-4f00-b32b-67b44c06d8f6   
	 *             *or*
	 *         Report name:      Test Report #04
	 *         
	 *         Version name:     3.9.5
	 *         .rptdesign file:  (select one from the file system)
	 * 
	 * Only *one* of reportId and reportName need be defined, as long as a 
	 * they can be used to locate a parent Report. If both are defined, reportId
	 * will be used.
	 */
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_UPLOAD_REPORTS + "')")
	public Response createByUpload(
			@FormDataParam("reportId") UUID reportId,
			@FormDataParam("reportName") String reportName,
			@FormDataParam("versionName") String versionName,
			@FormDataParam("versionCode") Integer versionCode,
			@FormDataParam("rptdesignfile") InputStream uploadedInputStream,
			@FormDataParam("rptdesignfile") FormDataContentDisposition fileDetail,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final ServletConfig servletConfig,
			@Context final UriInfo uriInfo) throws BirtException, RptdesignOpenFromStreamException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.info("reportId    = {}", reportId);
		logger.info("reportName  = {}", reportName);
		logger.info("versionName = {}", versionName);
		logger.info("versionCode = {}", versionCode);

		logger.debug("servletContext.getContextPath() = {}", servletContext.getContextPath());
		logger.debug("servletContext.getRealPath(\"\") = {}", servletContext.getRealPath(""));
		logger.debug("servletContext.getRealPath(\"/\") = {}", servletContext.getRealPath("/"));

		Enumeration<String> servletContextinitParamEnum = servletContext.getInitParameterNames();
		while (servletContextinitParamEnum.hasMoreElements()) {
			String servletContextInitParamName = servletContextinitParamEnum.nextElement();
			logger.debug("servletContextInitParamName = {}", servletContextInitParamName);
		}
		/*
		 * This returns null if this application ius run via:
		 *  
		 *     mvn clean spring-boot:run
		 */
		logger.debug("BIRT_VIEWER_WORKING_FOLDER = {}", servletContext.getInitParameter("BIRT_VIEWER_WORKING_FOLDER"));

		Enumeration<String> servletConfigInitParamEnum = servletConfig.getInitParameterNames();
		while (servletConfigInitParamEnum.hasMoreElements()) {
			String servletConfigInitParamName = servletConfigInitParamEnum.nextElement();
			logger.debug("servletConfigInitParamName = {}", servletConfigInitParamName);
		}
		logger.debug("javax.ws.rs.Application = {}", servletConfig.getInitParameter("javax.ws.rs.Application"));

		RestUtils.ifAttrNullOrBlankThen403(uploadedInputStream, ReportVersion.class, "rptdesignfile");

		ReportVersionResource reportVersionResource = null;
		try {

			String lineSeparator = System.getProperty("line.separator");
			StringBuilder rptdesignStringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedInputStream))) {
				String line;
				while ((line = reader.readLine()) != null) {
					rptdesignStringBuilder.append(line);
					rptdesignStringBuilder.append(lineSeparator);
				}
			}
			String rptdesign = rptdesignStringBuilder.toString();
			RestUtils.ifAttrNullOrBlankThen403(rptdesign, ReportVersion.class, "rptdesign");
			RestUtils.ifAttrNullOrBlankThen403(versionName, ReportVersion.class, "versionName");

			RestUtils.ifNotValidXmlThen403(rptdesign,
					String.format("The uploaded document '%s' is not valid XML", fileDetail.getFileName()),
					ReportVersion.class, "rptdesign", null);

			Report report = null;
			if (reportId != null) {
				report = reportRepository.findOne(reportId);
				RestUtils.ifNullThen404(report, Report.class, "reportId", reportId.toString());
			} else {
				report = reportRepository.findByName(reportName);
				RestUtils.ifNullThen404(report, Report.class, "reportName", reportName);
			}
			logger.info("report = {}", report);

			/*
			 * If no value for versionCode was provided, we compute a value for 
			 * it.
			 */
			if (versionCode == null) {
				versionCode = reportVersionService.nextVersionCode(report);
				logger.info("value computed for versionCode = {}", versionCode);
			}
			RestUtils.ifAttrNullOrBlankThen403(versionCode, ReportVersion.class, "versionCode");

			ReportVersion reportVersion = new ReportVersion(report, fileDetail.getFileName(),
					rptdesignStringBuilder.toString(), versionName, versionCode, true);
			reportVersion = reportVersionRepository.save(reportVersion);
			logger.info("reportVersion = {}", reportVersion);
			if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
				addToExpandList(expand, ReportVersion.class);
			}

			/*
			 * Parse report parameters here and for each report parameter, create a 
			 * ReportParameter entity which is stored in the report server database.
			 */
			Map<String, Map<String, Serializable>> parameters = reportParameterService
					.createParametersForReport(reportVersion);

			/*
			 * Write uploaded rptdesign file to the file system of the report 
			 * server, overwriting a file with the same name if one exists.
			 */
			//			ReportUtils.writeRptdesignFile(reportVersion, servletContext.getRealPath(""));
			java.nio.file.Path rptdesignFilePath = reportSyncService.writeRptdesignFile(reportVersion,
					servletContext.getRealPath(""));

			reportVersionResource = new ReportVersionResource(reportVersion, uriInfo, queryParams, apiVersion);
			logger.debug("reportVersionResource = {}", reportVersionResource);

			//} catch (InvalidPathException e) {
			//	throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_REPORT_FOLDER_MISSING, e);
		} catch (IOException e) {
			/*
			 * TODO Treat IOException better:
			 * This exception can be thrown either when working with the 
			 * BufferedReader above or from the call to 
			 * reportParameterService.createParametersForReport(reportVersion)
			 * above. Consider catching the IOException from 
			 * createParametersForReport in such a way that I can report a
			 * more detailed description of the error?
			 */
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_FILE_UPLOAD, e);
		}

		return created(reportVersionResource);
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d?expand=reportVersions
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating ReportVersion.getReportParameters(), as well
	 * as potentially other lazy-evaluated attributes.
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportVersionResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, ReportVersion.class);
		}
		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		ReportVersionResource reportVersionResource = new ReportVersionResource(reportVersion,
				uriInfo, queryParams, apiVersion);
		return reportVersionResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	 *   '{"report":{"reportId":"702d5daa-e23d-4f00-b32b-67b44c06d8f6"},\
	 *   "fileName":"400-TestReport04_v1.1.1.rptdesign","rptdesign":"Not a valid rptdesign, but this cannot be null",\
	 *   "versionName":"0.6.1","versionCode":3,"active":false}' \
	 *   http://localhost:8080/rest/reportVersions/bbd23109-e1e9-404e-913d-32150d8fd92f
	 *   
	 * This updates the ReportVersion with UUID 
	 * bbd23109-e1e9-404e-913d-32150d8fd92f with the following changes:
	 * 
	 * report:			Do not change - keep same parent report ("Test Report #04")
	 * fileName:		"400-TestReport04_v0.6.rptdesign"	->	"400-TestReport04_v1.1.1.rptdesign"
	 * rptdesign:							-> "Not a valid rptdesign, but this cannot be null"
	 * versionName:		"1.1"				-> "1.1.1"
	 * versionCode:		2					-> 3
	 * active:			true				-> false
	 * 
	 * @Transactional may be needed here to avoid future exceptions of the type
	 * org.hibernate.LazyInitializationException being thrown when evaluating 
	 * lazy-evaluated attributes. It is not actually needed as this comment is
	 * being written.
	 */
	@Path("/{id}")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public Response updateById(
			ReportVersionResource reportVersionResource,
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve ReportVersion entity to be updated.
		 */
		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());

		// TODO Uncomment and finish this (see ReportParameterController):

		// /*
		// * Treat attributes of reportVersionResource that are effectively
		// * required. These attributes can be omitted in the PUT data, but in
		// * that case they are then set here to the CURRENT values from the
		// * reportVersion entity. These are that attributes that are required,
		// * but if their value does not need to be changed, they do not need to
		// * be included in the PUT data.
		// */
		// if (reportVersionResource.getReportResource() == null) {
		// /*
		// * Construct a ReportResource to specify the CURRENTLY
		// * selected Report.
		// */
		// UUID currentReportId = reportVersion.getReport().getReportId();
		// ReportResource reportResource = new ReportResource();
		// reportResource.setReportId(currentReportId);
		// reportVersionResource.setReportResource(reportResource);
		// } else {
		// /*
		// * If a "reportVersionResource" attribute *is* supplied in the PUT
		// * data, it *must* have a value set for its "reportId"
		// * attribute...
		// */
		// RestUtils.ifAttrNullOrBlankThen403(reportVersionResource.getReportResource().getReportId(),
		// Report.class, "reportId");
		// /*
		// * ... and the value for "reportId" must correspond to an
		// * existing Report entity.
		// */
		// UUID
		// reportId=reportVersionResource.getReportResource().getReportId();
		// RestUtils.ifNullThen404(reportRepository.findOne(reportId),
		// Report.class,
		// "reportId", reportId.toString());
		// }

		/*
		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
		 */
		reportVersionResource.setReportVersionId(reportVersion.getReportVersionId());
		reportVersionResource.setCreatedOn(reportVersion.getCreatedOn());
		/*
		 * If the "rptdesign" XML report definition was not specified in 
		 * reportVersionResource, we use the current value stored in 
		 * reportVersion.
		 */
		if (reportVersionResource.getRptdesign() == null) {
			reportVersionResource.setRptdesign(reportVersion.getRptdesign());
		}
		/*
		 * If the "fileName" was not specified in 
		 * reportVersionResource, we use the current value stored in 
		 * reportVersion.
		 */
		if (reportVersionResource.getFileName() == null) {
			reportVersionResource.setFileName(reportVersion.getFileName());
		}
		/*
		 * If the "versionCode" (integer) was not specified in 
		 * reportVersionResource, we use the current value stored in 
		 * reportVersion.
		 */
		if (reportVersionResource.getVersionCode() == null) {
			reportVersionResource.setVersionCode(reportVersion.getVersionCode());
		}
		/*
		 * Save updated entity.
		 */
		reportVersion = reportVersionService.saveExistingFromResource(reportVersionResource);

		/*
		 * Synchronize "rptdesign" files in the report server's file system with
		 * the "rptdesign" definitions stored in the report server's database.
		 */
		ReportSyncResource reportSyncResource = reportSyncService.syncReportsWithFileSystem(servletContext,
				uriInfo, queryParams, apiVersion);

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * Return the ReportParameter's associated with a single ReportVersion that
	 * is specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d/reportParameters?expand=reportParameters
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating report.get...()?
	 */
	@Path("/{id}" + ResourcePath.REPORTPARAMETERS_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')")
	public ReportParameterCollectionResource getReportParametersByReportVersionId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		return new ReportParameterCollectionResource(reportVersion, uriInfo, queryParams, apiVersion);
	}

	/*
	 * Return the Subscription entities associated with a single ReportVersion 
	 * that is specified by its id. This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d/subscriptions\
	 *   ?expand=subscriptions
	 * 
	 * This endpoint supports filtering on roleId as follows:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/reportVersions/293abf69-1516-4e9b-84ae-241d25c13e8d/subscriptions\
	 *   ?expand=subscriptions\&filter=roleId.eq."b85fd129-17d9-40e7-ac11-7541040f8627"
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}" + ResourcePath.SUBSCRIPTIONS_PATH)
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_REPORTS + "')"
			+ " and hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_SUBSCRIPTIONS + "')")
	public SubscriptionCollectionResource getSubscriptionsByReportVersionId(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@QueryParam(ResourcePath.FILTER_QP_NAME) final List<String> filter,
			@QueryParam(ResourcePath.PAGE_OFFSET_QP_NAME) final List<String> pageOffset,
			@QueryParam(ResourcePath.PAGE_LIMIT_QP_NAME) final List<String> pageLimit,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		queryParams.put(ResourcePath.FILTER_QP_KEY, filter);
		RestUtils.checkPaginationQueryParams(pageOffset, pageLimit, queryParams);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		ReportVersion reportVersion = reportVersionRepository.findOne(id);
		RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId", id.toString());
		try {
			return new SubscriptionCollectionResource(reportVersion, uriInfo, queryParams, apiVersion);
		} catch (ResourceFilterExecutionException | ResourceFilterParseException e) {
			throw new RestApiException(RestError.FORBIDDEN_RESOURCE_FILTER_PROBLEM, e.getMessage(), e);
		}
	}

}
