package com.qfree.obo.report.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.db.ReportCategoryRepository;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.resource.AbstractResource;
import com.qfree.obo.report.resource.ReportCategoryResource;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@Component
@Path(AbstractResource.REPORTCATEGORIES_PATH)
public class ReportCategoryController {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryController.class);

	private final ReportCategoryRepository reportCategoryRepository;

	//	private final ReportCategoryService reportCategoryService;

	@Autowired
	public ReportCategoryController(ReportCategoryRepository reportCategoryRepository) {
		this.reportCategoryRepository = reportCategoryRepository;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReportCategoryResource> getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<ReportCategory> reportCategories = reportCategoryRepository.findByActiveTrue();
		List<ReportCategoryResource> reportCategoryResources = new ArrayList<>();
		for (ReportCategory reportCategory : reportCategories) {
			reportCategoryResources.add(new ReportCategoryResource(reportCategory, uriInfo, expand));
		}
		return reportCategoryResources;
	}

	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ReportCategoryResource getById(
			@PathParam("id") final UUID id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam("expand") final List<String> expand,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		//TODO Do not hardwire "reportcategory" here.
		if (!expand.contains("reportcategory")) {
			expand.add("reportcategory");	// Always expand primary resource.
		}

		ReportCategory reportCategory = reportCategoryRepository.findOne(id);
		ReportCategoryResource reportCategoryResource = new ReportCategoryResource(reportCategory, uriInfo, expand);
		return reportCategoryResource;
	}

}
