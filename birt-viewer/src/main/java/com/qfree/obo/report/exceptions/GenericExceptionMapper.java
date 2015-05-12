package com.qfree.obo.report.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.dto.RestErrorResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Logger logger = LoggerFactory.getLogger(GenericExceptionMapper.class);

	public Response toResponse(Throwable ex) {
		/*
		 * Note: It appears that exceptions that inherit from 
		 *       WebApplicationException, such as this application's
		 *       RestApiException, are not processed by this exception mapper.
		 *       This is good, because otherwise we would need to check for that
		 *       exception here and treat it specially. Another approach would
		 *       be to implement the ExtendedExceptionMapper interface here,
		 *       instead of ExceptionMapper, which has an isMappable method that
		 *       can be used to reject specific exception classes from being 
		 *       mapped at all. 
		 */

		logger.error("An exception was thrown. Exception = ", ex);

		RestErrorResource restErrorResource = new RestErrorResource(RestError.INTERNAL_SERVER_ERROR,
				ex.getMessage(), ex);

		return Response.status(RestError.INTERNAL_SERVER_ERROR.getResponseStatus().getStatusCode()).
				entity(restErrorResource).
				type(MediaType.APPLICATION_JSON_TYPE).
				build();

		//return Response.status(404).
		//		entity(ex.getMessage()).
		//		type("text/plain").
		//		build();
	}
}