package com.qfree.obo.report.rest.server;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.dto.AbstractBaseResource;

public class AbstractBaseController {

	private static final Logger logger = LoggerFactory.getLogger(AbstractBaseController.class);

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
}
