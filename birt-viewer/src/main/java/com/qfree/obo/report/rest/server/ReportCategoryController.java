package com.qfree.obo.report.rest.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
import com.qfree.obo.report.service.ReportCategoryService;

@Component
@Path(AbstractResource.REPORTCATEGORIES_PATH)
public class ReportCategoryController {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryController.class);

	private final ReportCategoryRepository reportCategoryRepository;
	private final ReportCategoryService reportCategoryService;

	//	private final ReportCategoryService reportCategoryService;

	@Autowired
	public ReportCategoryController(
			ReportCategoryRepository reportCategoryRepository,
			ReportCategoryService reportCategoryService) {
		this.reportCategoryRepository = reportCategoryRepository;
		this.reportCategoryService = reportCategoryService;
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

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(
			ReportCategoryResource reportCategoryResource,
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		logger.info("reportCategoryResource = {}", reportCategoryResource);
		System.out.println("ReportCategoryController.create: reportCategoryResource = " + reportCategoryResource);

		ReportCategory reportCategory = reportCategoryService.saveFromResource(reportCategoryResource);
		System.out.println("ReportCategoryController.create: reportCategory = " + reportCategory);

		List<String> expand = new ArrayList<>();
		//TODO Do not hardwire "reportcategory" here.
		expand.add("reportcategory");
		ReportCategoryResource resource = new ReportCategoryResource(reportCategory, uriInfo, expand);
		System.out.println("ReportCategoryController.create: resource = " + resource);


		ReportCategoryResource bogusResourceForTesting = new ReportCategoryResource();
		bogusResourceForTesting.setHref("http://BOGUS.COM!");
		bogusResourceForTesting.setReportCategoryId(UUID.randomUUID());
		bogusResourceForTesting.setAbbreviation("BOGUS");
		bogusResourceForTesting.setDescription("***** Bogus description*****");
		bogusResourceForTesting.setActive(true);
		bogusResourceForTesting.setCreatedOn(new Date());
		return created(bogusResourceForTesting);

		//		return created(resource);
	}

	//IF USEFUL, PLACE IN "BaseController"?
	protected Response created(ReportCategoryResource resource) {
		//	protected Response created(AbstractResource resource) {
		URI uri = URI.create(resource.getHref());
		/*
		 * .created(uri):		sets the Location header for a "201 Created"
		 * 						response.
		 * 
		 * .entity(resource):	sets the response entity that will be returned 
		 * 						in the response, i.e., "resource" is the payload.
		 * 
		 * .build():			builds the response instance from the current 
		 * 						ResponseBuilder. Jersey will pass this off to 
		 * 						the JAXB-based JSON binding provider, which will
		 * 						perform the conversion to JSON. In our case, it
		 * 						is MOXy.
		 */
		System.out.println("ReportCategoryController.created: resource = " + resource);
		return Response.created(uri).entity(resource).build();
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
