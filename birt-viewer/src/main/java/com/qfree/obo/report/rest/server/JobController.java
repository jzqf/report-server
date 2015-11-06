package com.qfree.obo.report.rest.server;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.JobRepository;
import com.qfree.obo.report.db.JobStatusRepository;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.dto.JobParameterCollectionResource;
import com.qfree.obo.report.dto.JobResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.JOBS_PATH)
public class JobController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(JobController.class);

	private final JobRepository jobRepository;
	//	private final JobService jobService;
	//	private final DocumentFormatRepository documentFormatRepository;
	//	private final ReportVersionRepository reportVersionRepository;
	//	private final RoleRepository roleRepository;
	//	private final RoleParameterRepository roleParameterRepository;
	//	private final RoleParameterValueRepository roleParameterValueRepository;
	private final JobStatusRepository jobStatusRepository;

	@Autowired
	public JobController(
			JobRepository jobRepository,
			//	JobService jobService,
			//	DocumentFormatRepository documentFormatRepository,
			//	ReportVersionRepository reportVersionRepository,
			//	RoleRepository roleRepository,
			//	RoleParameterRepository roleParameterRepository,
			//	RoleParameterValueRepository roleParameterValueRepository,
			JobStatusRepository jobStatusRepository) {
		this.jobRepository = jobRepository;
		//	this.jobService = jobService;
		//	this.documentFormatRepository = documentFormatRepository;
		//	this.reportVersionRepository = reportVersionRepository;
		//	this.roleRepository = roleRepository;
		//	this.roleParameterRepository = roleParameterRepository;
		//	this.roleParameterValueRepository = roleParameterValueRepository;
		this.jobStatusRepository = jobStatusRepository;
	}

	/*
	 * This endpoint can possibly be enabled one day, but only for administration
	 * purposes since it displays all Jobs, regardless of Role.
	 * 
	 * This enpoint also needs to be extended to handle paging, since over time
	 * the will be many, many Job entities.
	 */
	//	/*
	//	 * This endpoint can be tested with:
	//	 * 
	//	 *   $ mvn clean spring-boot:run
	//	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/jobs?expand=jobs
	//	 * 
	//	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	//	 * being thrown.
	//	 */
	//	@GET
	//	@Transactional
	//	@Produces(MediaType.APPLICATION_JSON)
	//	public JobCollectionResource getList(
	//			@HeaderParam("Accept") final String acceptHeader,
	//			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		Map<String, List<String>> queryParams = new HashMap<>();
	//		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		List<Job> jobs = null;
	//		//	if (RestUtils.FILTER_INACTIVE_RECORDS && !ResourcePath.showAll(Job.class, showAll)) {
	//		//		jobs = jobRepository.findByActiveTrue();
	//		//	} else {
	//		jobs = jobRepository.findAll();
	//		//	}
	//		List<JobResource> jobResources = new ArrayList<>(jobs.size());
	//		for (Job job : jobs) {
	//			jobResources.add(new JobResource(job, uriInfo, queryParams, apiVersion));
	//		}
	//		return new JobCollectionResource(jobResources, Job.class, uriInfo, queryParams, apiVersion);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/jobs/4?expand=jobs
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown when evaluating job.get...s().
	 */
	@Path("/{id}")
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public JobResource getById(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
			addToExpandList(expand, Job.class);
		}
		Job job = jobRepository.findOne(id);
		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());
		JobResource jobResource = new JobResource(job, uriInfo, queryParams, apiVersion);
		return jobResource;
	}

	/**
	 * This endpoint returns the render report document for a specified
	 * {@link Job} entity.
	 * 
	 * <p>
	 * A representation of this document is stored in the {@link Job#document}
	 * field.
	 * <p>
	 * This endpoint can be tested with (replace the value "19" with an id that
	 * is valid for a {@link Job} entity in your own database):
	 * 
	 * <pre>
	 * <code>$ mvn clean spring-boot:run
	 * $ curl -X GET -iH "Accept: application/pdf;v=1" http://localhost:8080/rest/jobs/19/document</code>
	 * </pre>
	 * <p>
	 * This assumes, of course, that the document is of type PDF.
	 * 
	 * @param id
	 * @param acceptHeader
	 * @param uriInfo
	 * @return
	 */
	@Path("/{id}/document")
	@GET
	@Transactional
	/*
	 * MediaType.APPLICATION_JSON seems to be needed here in order to be able
	 * to return a RestErrorResource from JobDocumentOutputStream if a problem
	 * is encountered.
	 */
	@Produces({ "application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"text/html",
			"application/vnd.oasis.opendocument.presentation",
			"application/vnd.oasis.opendocument.spreadsheet",
			"application/vnd.oasis.opendocument.text",
			"application/pdf",
			"application/vnd.ms-powerpoint",
			"application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/vnd.ms-excel",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			MediaType.APPLICATION_JSON })
	public Response geJobDocumentByJobId(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			//	@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			//	@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		//	Map<String, List<String>> queryParams = new HashMap<>();
		//	queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		//	queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Job job = jobRepository.findOne(id);
		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());

		return Response.status(Response.Status.OK)
				.entity(new JobDocumentOutputStream(job))
				.type(job.getDocumentFormat().getInternetMediaType())
				.header("content-disposition", String.format("attachment; filename = \"%s\"", job.getFileName()))
				.build();
	}
	//	public StreamingOutput getDocumentByJobId(
	//			@PathParam("id") final Long id,
	//			@HeaderParam("Accept") final String acceptHeader,
	//			//	@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
	//			//	@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
	//			@Context final UriInfo uriInfo) {
	//		//	Map<String, List<String>> queryParams = new HashMap<>();
	//		//	queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
	//		//	queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
	//		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
	//
	//		Job job = jobRepository.findOne(id);
	//		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());
	//
	//		return new JobDocumentOutputStream(job);
	//	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ curl -X PUT -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobs/4/cancelRequests
	 *   
	 * This cancels the job with id = 4. This will only succeed if the current
	 * status for the job is one of:
	 * 
	 *   QUEUED
	 * 
	 * If the current status is any other status, an error is returned.
	 */
	@Path("/{id}/cancelRequests")
	@PUT
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelJob(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final ServletContext servletContext,
			@Context final UriInfo uriInfo)
					throws ClassNotFoundException, NoSuchMethodException, SchedulerException, ParseException {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve Job entity to be updated.
		 */
		Job job = jobRepository.findOne(id);
		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());

		JobStatus jobStatus = job.getJobStatus();
		UUID currentJobStatusId = jobStatus.getJobStatusId();
		if (currentJobStatusId.equals(JobStatus.QUEUED_ID)) {
			JobStatus canceledJobStatus = jobStatusRepository.findOne(JobStatus.CANCELED_ID);
			RestUtils.ifNullThen404(canceledJobStatus, JobStatus.class, "jobStatusId",
					JobStatus.CANCELED_ID.toString());
			job.setJobStatus(canceledJobStatus);
			/*
			 * Record some details about the cancellation.
			 */
			job.setJobStatusRemarks(String.format("Job canceled at: %s", (new Date())));
			job = jobRepository.save(job);
		} else {
			String errorMessage = String.format(
					"Attempt to cancel a Job with JobStatus = \"%s\"."
							+ " Only Job resources with status \"QUEUED\" may be canceled.",
					jobStatus.getAbbreviation());
			throw new RestApiException(RestError.FORBIDDEN_CANCEL_JOB_WRONG_STATUS, errorMessage, Job.class);
		}

		return Response.status(Response.Status.OK).build();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X DELETE -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobs/4
	 */
	@Path("/{id}")
	@DELETE
	@Transactional
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobResource deleteById(
			//public Response updateById(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * Retrieve Job entity to be deleted.
		 */
		Job job = jobRepository.findOne(id);
		logger.debug("job = {}", job);
		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());

		/*
		 * If the Job entity is successfully deleted, it is 
		 * returned as the entity body so it is clear to the caller precisely
		 * which entity was deleted. Here, the resource to be returned is 
		 * created before the entity is deleted.
		 */
		//	if (RestUtils.AUTO_EXPAND_PRIMARY_RESOURCES) {
		addToExpandList(expand, Job.class); // Force primary resource to be "expanded"
		//	}
		JobResource jobResource = new JobResource(job, uriInfo, queryParams, apiVersion);
		logger.debug("jobResource = {}", jobResource);
		/*
		 * Delete entity.
		 */
		jobRepository.delete(job);
		logger.info("job (after deletion) = {}", job);

		//	/*
		//	 * Confirm that the entity was, indeed, deleted. job here
		//	 * should be null. Currently, I don't do anything based on this check.
		//	 * I assume that the delete call above with throw some sort of 
		//	 * RuntimeException if that happens, or some other exception will be
		//	 * thrown by the back-end database (PostgreSQL) code when the 
		//	 * transaction is eventually committed. I don't have the time to look 
		//	 * into this at the moment.
		//	 */
		//	job = jobRepository.findOne(jobResource.getJobId());
		//	logger.info("job (after find()) = {}", job); // job is null here

		//return Response.status(Response.Status.OK).build();
		return jobResource;
	}

	/*
	 * Return the JobParameter entities associated with a single 
	 * Job that is specified by its id. This endpoint can be tested 
	 * with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/jobs/4/jobParameters
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Path("/{id}" + ResourcePath.JOBPARAMETERS_PATH)
	@GET
	@Transactional
	@Produces(MediaType.APPLICATION_JSON)
	public JobParameterCollectionResource getJobParametersByJobId(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Job job = jobRepository.findOne(id);
		RestUtils.ifNullThen404(job, Job.class, "jobId", id.toString());
		return new JobParameterCollectionResource(job, uriInfo, queryParams, apiVersion);
	}
}
