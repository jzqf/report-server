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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.dto.JobProcessorResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;
import com.qfree.obo.report.scheduling.schedulers.SubscriptionJobProcessorScheduler;

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

		return subscriptionJobProcessorScheduler.getJobProcessorResource();
	}
}
