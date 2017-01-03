package com.qfree.bo.report.rest.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.dto.RoleCollectionResource;
import com.qfree.bo.report.dto.RoleResource;

public class XXX_RolesPojoRestDataSource extends XXX_AbstractPojoRestDataSource {

	private static final Logger logger = LoggerFactory.getLogger(XXX_RolesPojoRestDataSource.class);

	private static final String RESOURCE_PATH = "roles";
	private static final String API_VERSION = "1";

	private Iterator<RoleResource> iterator;

	private List<RoleResource> getRoles(String baseUri, String resourcePath) {

		logger.info("baseUri = {}", baseUri);

		/*
		 * Retrieve the RoleResource's as a RoleCollectionResource via HTTP GET.
		 * 
		 * IMPORTANT:
		 * 
		 * This request needs "expand" query parameter "roles"; otherwise, the
		 * "items" attribute of the RoleCollectionResource will be null.
		 */
		WebTarget baseUriWebTarget = getBaseUriWebTarget(baseUri);
		Response response = baseUriWebTarget.path(resourcePath)
				.queryParam("expand", "roles") // TODO This parameter and value should not be harwired here!
				.request()
				.header("Accept", MediaType.APPLICATION_JSON + ";v=" + API_VERSION)
				.get();

		logger.info("response = {}", response);

		int status = response.getStatus();
		logger.info("HttpStatus = {}", status);

		RoleCollectionResource roleCollectionResource = response.readEntity(RoleCollectionResource.class);
		List<RoleResource> roleResources = roleCollectionResource.getItems();
		logger.info("roleResources = {}", roleResources);

		return roleResources;
	}

	/* The "open" method will be called by BIRT engine once when the report 
	 * is invoked. It is a mandatory method.
	 */
	public void open(Object appContext, Map<String, Object> dataSetParamValues) {
		List<RoleResource> roleResources = getRoles(BASE_REST_URI, RESOURCE_PATH);
		this.iterator = roleResources.iterator();
	}

	/* The "next" method is called by the BIRT engine once for each row of the
	 * data set . It is a mandatory method.
	 */
	public Object next() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	/* The "close" method is called by the BIRT engine once at the end of the 
	 * report. It is a mandatory method.
	 */
	public void close() {
		this.iterator = null;
	}

	/**
	 * Run this class in Eclipse as a Java application to test this data source.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		XXX_RolesPojoRestDataSource rolesPojoRestDataSource = new XXX_RolesPojoRestDataSource();

		rolesPojoRestDataSource.open(null, new HashMap<String, Object>());
		while (rolesPojoRestDataSource.iterator.hasNext()) {
			RoleResource roleResource = (RoleResource) rolesPojoRestDataSource.next();
			System.out.println("roleResource = " + roleResource);
		}

		rolesPojoRestDataSource.close();

	}
} 