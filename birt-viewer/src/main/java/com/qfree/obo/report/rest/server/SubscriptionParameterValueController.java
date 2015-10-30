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

import com.qfree.obo.report.db.SubscriptionParameterValueRepository;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.SubscriptionParameterValueResource;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Path(ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH)
public class SubscriptionParameterValueController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterValueController.class);

	private final SubscriptionParameterValueRepository subscriptionParameterValueRepository;

	@Autowired
	public SubscriptionParameterValueController(
			SubscriptionParameterValueRepository subscriptionParameterValueRepository) {
		this.subscriptionParameterValueRepository = subscriptionParameterValueRepository;
	}

	/*
	 * This endpoint can be tested with:
	 * 
	 *   $ mvn clean spring-boot:run
	 *   $ curl -i -H "Accept: application/json;v=1" -X GET \
	 *   http://localhost:8080/rest/subscriptionParameterValues/7a482694-51d2-42d0-b0e2-19dd13bbbc64\
	 *   ?expand=subscriptionParameterValues
	 */
	@Path("/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SubscriptionParameterValueResource getById(
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
			addToExpandList(expand, SubscriptionParameterValue.class);
		}
		SubscriptionParameterValue subscriptionParameterValue = subscriptionParameterValueRepository.findOne(id);
		RestUtils.ifNullThen404(subscriptionParameterValue, SubscriptionParameterValue.class,
				"subscriptionParameterValueId", id.toString());
		SubscriptionParameterValueResource subscriptionParameterValueResource = new SubscriptionParameterValueResource(
				subscriptionParameterValue, uriInfo, queryParams, apiVersion);
		return subscriptionParameterValueResource;
	}
}
