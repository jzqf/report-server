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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.JobParameterRepository;
import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.dto.JobParameterResource;
import com.qfree.obo.report.dto.JobParameterValueCollectionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.JOBPARAMETERS_PATH)
public class JobParameterController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(JobParameterController.class);

	private final JobParameterRepository jobParameterRepository;

	@Autowired
	public JobParameterController(
			JobParameterRepository jobParameterRepository) {
		this.jobParameterRepository = jobParameterRepository;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/jobParameters/2?expand=jobParameters\&expand=jobParameterValues
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_JOBS + "')")
	public JobParameterResource getById(
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
			addToExpandList(expand, JobParameter.class);
		}
		JobParameter jobParameter = jobParameterRepository.findOne(id);
		RestUtils.ifNullThen404(jobParameter, JobParameter.class, "jobParameterId",
				id.toString());

		JobParameterResource jobParameterResource = new JobParameterResource(
				jobParameter, uriInfo, queryParams, apiVersion);

		return jobParameterResource;
	}


	/*
	 * Return the JobParameterValue entities associated with a single 
	 * JobParameter that is specified by its id. This endpoint can be tested 
	 * with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -X GET -iH "Accept: application/json;v=1" \
	 *   http://localhost:8080/rest/jobParameters/2/jobParameterValues?expand=jobParameterValues
	 * 
	 * @Transactional is used to avoid org.hibernate.LazyInitializationException
	 * being thrown.
	 */
	@Transactional
	@Path("/{id}" + ResourcePath.JOBPARAMETERVALUES_PATH)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@PreAuthorize("hasAuthority('" + Authority.AUTHORITY_NAME_MANAGE_JOBS + "')")
	public JobParameterValueCollectionResource getJobParameterValuesByJobParameterId(
			@PathParam("id") final Long id,
			@HeaderParam("Accept") final String acceptHeader,
			@QueryParam(ResourcePath.EXPAND_QP_NAME) final List<String> expand,
			@QueryParam(ResourcePath.SHOWALL_QP_NAME) final List<String> showAll,
			@Context final UriInfo uriInfo) {
		Map<String, List<String>> queryParams = new HashMap<>();
		queryParams.put(ResourcePath.EXPAND_QP_KEY, expand);
		queryParams.put(ResourcePath.SHOWALL_QP_KEY, showAll);
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		JobParameter jobParameter = jobParameterRepository.findOne(id);
		RestUtils.ifNullThen404(jobParameter, JobParameter.class, "jobParameterId", id.toString());
		return new JobParameterValueCollectionResource(jobParameter, uriInfo, queryParams, apiVersion);
	}
}
