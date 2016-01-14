package com.qfree.obo.report.rest.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.dto.AbstractBaseResource;
import com.qfree.obo.report.dto.ResourcePath;

public class AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AbstractBaseController.class);

	/**
	 * Returns an HTTP 200 {@link Response}.
	 * 
	 * @param resource
	 *            to return with the response.
	 * @return HTTP 201 response
	 */
	protected Response ok(AbstractBaseResource resource) {
		return Response.ok().entity(resource).build();
	}

	/**
	 * Returns an HTTP 201 {@link Response} appropriate for after a new resource
	 * has been successfully created (probably via an HTTP POST). This includes
	 * setting the HTTP "Location" response header with the URI of the created
	 * resource.
	 * 
	 * @param resource
	 *            that was created
	 * @return HTTP 201 response
	 */
	protected Response created(AbstractBaseResource resource) {
		URI uri = URI.create(resource.getHref());
		//		logger.info("resource.getHref() = {}", resource.getHref());
		//		logger.info("uri = {}", uri);
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
		//		logger.info("resource = {}", resource);
		return Response.created(uri).entity(resource).build();
	}

	/**
	 * Returns a new "expand" list containing an an entry corresponding to the 
	 * specified entity class.
	 * 
	 * @param entityClass
	 * @return "expand" list
	 */
	protected List<String> newExpandList(Class<?> entityClass) {
		return new ArrayList<>(Arrays.asList(ResourcePath.forEntity(entityClass).getExpandParam()));
	}

	/**
	 * Adds a new "expand" list entry corresponding to the specified entity 
	 * class to the provided list, but only if an entry for this class does not 
	 * already exist in the list.
	 * 
	 * @param expand
	 * @param entityClass
	 */
	protected void addToExpandList(List<String> expand, Class<?> entityClass) {
		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		/*
		 * Only add new value if it does not already appear in the list.
		 */
		if (!expand.contains(expandParam)) {
			expand.add(expandParam);
		}
	}

}
