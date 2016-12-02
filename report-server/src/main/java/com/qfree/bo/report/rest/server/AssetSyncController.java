package com.qfree.bo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.AssetRepository;
import com.qfree.bo.report.domain.Authority;
import com.qfree.bo.report.dto.AssetSyncResource;
import com.qfree.bo.report.dto.ResourcePath;
import com.qfree.bo.report.service.AssetSyncService;
import com.qfree.bo.report.util.ReportUtils;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path("assetSyncs")
public class AssetSyncController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AssetSyncController.class);

	private final AssetRepository assetRepository;
	private final AssetSyncService assetSyncService;

	@Autowired
	public AssetSyncController(
			AssetRepository assetRepository, AssetSyncService assetSyncService) {
		this.assetRepository = assetRepository;
		this.assetSyncService = assetSyncService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -u reportserver-restadmin:ReportServer*RESTADMIN -iH "Accept: text/plain;v=1" \
	 *   http://localhost:8080/report-server/rest/assetSyncs/availablePermits
	 * 
	 */
	@Path("/availablePermits")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_FILESYNCING + "')")
	public int getAvailablePermits(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		return ReportUtils.assetSyncSemaphore.availablePermits();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -u reportserver-restadmin:ReportServer*RESTADMIN \
	 *   -iH "Accept: application/json;v=1" http://localhost:8080/report-server/rest/assetSyncs
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@POST
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_FILESYNCING + "')")
	public AssetSyncResource syncAssetsWithFileSystem(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		return assetSyncService.syncAssetsWithFileSystem(servletContext, uriInfo, queryParams, apiVersion);
	}
}
