package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.dto.JobProcessorResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.JobProcessorAlreadyScheduledException;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotPause;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotResume;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotStop;
import com.qfree.obo.report.exceptions.JobProcessorNotScheduledCannotTrigger;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotPause;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotResume;
import com.qfree.obo.report.exceptions.JobProcessorSchedulerNotRunningCannotTrigger;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionJobProcessorScheduler;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.JOBPROCESSOR_PATH)
public class JobProcessorController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(JobProcessorController.class);

	private final SubscriptionJobProcessorScheduler subscriptionJobProcessorScheduler;

	@Autowired
	public JobProcessorController(
			SubscriptionJobProcessorScheduler subscriptionJobProcessorScheduler) {
		this.subscriptionJobProcessorScheduler = subscriptionJobProcessorScheduler;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" http://localhost:8080/rest/jobProcessor/status
	 */
	//@Transactional
	@Path("/status")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource status(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		JobProcessorResource jobProcessorResource = subscriptionJobProcessorScheduler.getJobProcessorResource();
		return jobProcessorResource;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" -d \
	 *   '{"repeatInterval":60}' http://localhost:8080/rest/jobProcessor/start
	 */
	@POST
	@Path("/start")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource start(
			JobProcessorResource jobProcessorResource,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		/*
		 * If a value for "repeatInterval" has not been provided, we use the 
		 * default value set by a configuration parameter.
		 */
		Long repeatIntervalSeconds = null;
		if (jobProcessorResource != null) {
			repeatIntervalSeconds = jobProcessorResource.getRepeatInterval();
		}
		if (repeatIntervalSeconds != null) {
			/*
			 * Sanity check. Adjust value, if necessary.
			 */
			if (repeatIntervalSeconds < 30L) {
				repeatIntervalSeconds = 30L;
			}
		}

		try {
			subscriptionJobProcessorScheduler.scheduleJob(repeatIntervalSeconds, 0L);
		} catch (JobProcessorAlreadyScheduledException e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_ALREADY_SCHEDULED);
		} catch (ClassNotFoundException | NoSuchMethodException | SchedulerException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_SCHEDULER, e);
		}

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobProcessor/trigger
	 */
	@POST
	@Path("/trigger")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource trigger(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		try {
			subscriptionJobProcessorScheduler.triggerJob();
		} catch (JobProcessorNotScheduledCannotTrigger e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_SCHEDULED_CANNOT_TRIGGER);
		} catch (JobProcessorSchedulerNotRunningCannotTrigger e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_RUNNING_CANNOT_TRIGGER);
		} catch (SchedulerException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_SCHEDULER, e);
		}

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobProcessor/pause
	 */
	@POST
	@Path("/pause")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource pause(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		try {
			subscriptionJobProcessorScheduler.pauseJob();
		} catch (JobProcessorNotScheduledCannotPause e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_SCHEDULED_CANNOT_PAUSE);
		} catch (JobProcessorSchedulerNotRunningCannotPause e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_RUNNING_CANNOT_PAUSE);
		} catch (SchedulerException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_SCHEDULER, e);
		}

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobProcessor/resume
	 */
	@POST
	@Path("/resume")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource resume(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		try {
			subscriptionJobProcessorScheduler.resumeJob();
		} catch (JobProcessorNotScheduledCannotResume e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_SCHEDULED_CANNOT_RESUME);
		} catch (JobProcessorSchedulerNotRunningCannotResume e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_RUNNING_CANNOT_RESUME);
		} catch (SchedulerException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_SCHEDULER, e);
		}

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X POST -iH "Accept: application/json;v=1" -H "Content-Type: application/json" \
	 *   http://localhost:8080/rest/jobProcessor/stop
	 */
	@POST
	@Path("/stop")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JobProcessorResource stop(
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		try {
			subscriptionJobProcessorScheduler.unscheduleJob();
		} catch (JobProcessorNotScheduledCannotStop e) {
			throw new RestApiException(RestError.FORBIDDEN_JOB_PROCESSOR_NOT_SCHEDULED_CANNOT_UNSCHEDULE);
		} catch (SchedulerException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_SCHEDULER, e);
		}

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}
}
