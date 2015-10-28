package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.dto.JobStatusCollectionResource;
import com.qfree.obo.report.dto.JobStatusResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.service.JobStatusService;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.JOBSTATUSES_PATH)
public class JobStatusController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(JobStatusController.class);

	private final JobStatusRepository jobStatusRepository;
	private final JobStatusService jobStatusService;

	@Autowired
	public JobStatusController(
			JobStatusRepository jobStatusRepository,
			JobStatusService jobStatusService) {
		this.jobStatusRepository = jobStatusRepository;
		this.jobStatusService = jobStatusService;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/jobStatuses?expand=jobStatuses
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JobStatusCollectionResource getList(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		List<JobStatus> jobStatuses = null;
		if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(JobStatus.class, showAll)) {
			jobStatuses = jobStatusRepository.findByActiveTrue();
		} else {
			jobStatuses = jobStatusRepository.findAll();
		}
		return new JobStatusCollectionResource(jobStatuses, JobStatus.class, uriInfo, queryParams, apiVersion);
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	//	 *   '{"abbreviation":"JSABBREV","description":"JobStatus description",\
	//	 *   "active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	//	 *   http://localhost:8080/rest/jobStatuses
	//	 * 
	//	 * This endpoint will throw a "403 Forbidden" error because an id for the 
	//	 * JobStatus to create is given:
	//	 * 
	//	 *	curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X POST -d \
	//	 *	'{"jobStatusId":"71b3e8ae-bba8-45b7-a85f-12546bcc95b2",\
	//	 *	'"abbreviation":"JSABBREV","description":"JobStatus description",\
	//	 *	'"active":true, "createdOn":"1958-05-06T12:00:00.000Z"}' \
	//	 *	http://localhost:8080/rest/jobStatuses
	//	 */
	//	@POST
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Transactional
	//	public Response create(
	//			JobStatusResource jobStatusResource,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		JobStatus jobStatus = jobStatusService.saveNewFromResource(jobStatusResource);
	//		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
	//		addToExpandList(expand, JobStatus.class); // Force primary resource to be "expanded"
	//		//	}
	//		JobStatusResource resource = new JobStatusResource(jobStatus, uriInfo, queryParams, apiVersion);
	//		return created(resource);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/jobStatuses/7a482694-51d2-42d0-b0e2-19dd13bbbc64
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JobStatusResource getById(
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
			addToExpandList(expand, JobStatus.class);
		}
		JobStatus jobStatus = jobStatusRepository.findOne(id);
		RestUtils.ifNullThen404(jobStatus, JobStatus.class, "jobStatusId", id.toString());
		JobStatusResource jobStatusResource = new JobStatusResource(jobStatus, uriInfo, queryParams, apiVersion);
		return jobStatusResource;
	}

	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -X PUT -d \
	//	 *   '{"abbreviation":"QFREE-MOD","description":"Q-Free internal (modified)","active":false}' \
	//	 *   http://localhost:8080/rest/jobStatuses/bb2bc482-c19a-4c19-a087-e68ffc62b5a0
	//	 */
	//	@Path("/{id}")
	//	@PUT
	//	@Consumes(MediaType.APPLICATION_JSON)
	//	@Produces(MediaType.APPLICATION_JSON)
	//	@Transactional
	//	public Response updateById(
	//			JobStatusResource jobStatusResource,
	//			@PathParam("id") final UUID id,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		/*
	//		 * Retrieve JobStatus entity to be updated.
	//		 */
	//		JobStatus jobStatus = jobStatusRepository.findOne(id);
	//		RestUtils.ifNullThen404(jobStatus, JobStatus.class, "jobStatusId", id.toString());
	//		/*
	//		 * Ensure that the entity's "id" and "CreatedOn" are not changed.
	//		 */
	//		jobStatusResource.setJobStatusId(jobStatus.getJobStatusId());
	//		jobStatusResource.setCreatedOn(jobStatus.getCreatedOn());
	//		/*
	//		 * Save updated entity.
	//		 */
	//		jobStatus = jobStatusService.saveExistingFromResource(jobStatusResource);
	//		return Response.status(Response.Status.OK).build();
	//	}

}
