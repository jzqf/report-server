package com.qfree.obo.report.rest.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.qfree.obo.report.db.JobParameterValueRepository;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.dto.JobParameterValueResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.JOBPARAMETERVALUES_PATH)
public class JobParameterValueController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterValueController.class);

	private final JobParameterValueRepository jobParameterValueRepository;

	@Autowired
	public JobParameterValueController(
			JobParameterValueRepository jobParameterValueRepository) {
		this.jobParameterValueRepository = jobParameterValueRepository;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/jobParameterValues/4?expand=jobParameterValues
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JobParameterValueResource getById(
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
			addToExpandList(expand, JobParameterValue.class);
		}

		logger.info("id = ", id);

		JobParameterValue jobParameterValue = jobParameterValueRepository.findOne(id);
		RestUtils.ifNullThen404(jobParameterValue, JobParameterValue.class, "jobParameterValueId", id.toString());
		JobParameterValueResource jobParameterValueResource = new JobParameterValueResource(
				jobParameterValue, uriInfo, queryParams, apiVersion);

		return jobParameterValueResource;
	}
}
