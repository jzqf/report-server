package com.qfree.bo.report.rest.server;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.qfree.bo.report.db.ConfigurationRepository;
import com.qfree.bo.report.domain.Configuration;
import com.qfree.bo.report.service.ConfigurationService;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@Component
@Path("/")
@PropertySource("classpath:config.properties")
public class RootController extends AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(RootController.class);

	/*
	 * The injected "env" object here will contain key/value pairs for each 
	 * property in the properties files specified above in the @PropertySource
	 * annotation above.
	 */
	@Autowired
	private Environment env;

	private final ConfigurationRepository configurationRepository;
	private final ConfigurationService configurationService;

	@Autowired
	public RootController(
			ConfigurationRepository configurationRepository,
			ConfigurationService configurationService) {
		this.configurationRepository = configurationRepository;
		this.configurationService = configurationService;
	}

	@GET
	@Path("/appversion")
	@Produces(MediaType.TEXT_PLAIN)
	public String getAppversion(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		return env.getProperty("app.version");
	}

	@GET
	@Path("/dbversion")
	@Produces(MediaType.TEXT_PLAIN)
	public Integer getDbversion(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);
		
		Configuration configuration = configurationRepository.dbversion();
		logger.debug("configuration (dbversion) = ", configuration);
		Integer dbversion = -1;  // -1: undefined version number
		if (configuration != null) {
			dbversion = configuration.getIntegerValue() != null ? configuration.getIntegerValue() : -1;
		}
		return dbversion;
	}

	@GET
	@Path("/subscriptionTimeZones")
	@Produces(MediaType.TEXT_PLAIN)
	//	@Produces(MediaType.APPLICATION_JSON)
	public String getSubscriptionTimeZones(
			@HeaderParam("Accept") final String acceptHeader,
			@Context final UriInfo uriInfo) {
		RestApiVersion apiVersion = RestUtils.extractAPIVersion(acceptHeader, RestApiVersion.v1);

		Set<String> zoneIds = ZoneId.getAvailableZoneIds();
		String[] zoneIdsArray = zoneIds.toArray(new String[] {});
		Arrays.sort(zoneIdsArray);
		String subscriptionTimeZones = String.join("\n", zoneIdsArray);

		return subscriptionTimeZones;
	}

}
