package com.qfree.obo.report.exceptions;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.postgresql.util.PSQLException;
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

		/*
		 * Work through the chain of exceptions to reach the "root" cause.
		 */
		Throwable cause = ex.getCause();
		Throwable rootCause = cause;
		while (cause != null) {
			rootCause = cause;
			logger.debug("cause.getMessage() = {}", cause.getMessage());
			logger.debug("cause.getClass() = {}", cause.getClass());
			cause = cause.getCause();
		}

		/*
		 * If we recognize the root cause of the exception, we return a 
		 * RestErrorResource that is customized for it; otherwise, we return
		 * a generic "500.0" error code.
		 */
		RestErrorResource restErrorResource = null;
		RestError restError = null;
		if ((rootCause != null) && (rootCause.getClass().equals(ConstraintViolationException.class))) {
			restError = RestError.FORBIDDEN_VALIDATION_ERROR;
			restErrorResource = new RestErrorResource(restError, rootCause.getMessage(), ex);
		} else if ((rootCause != null) && (rootCause.getClass().equals(PSQLException.class))) {
			/*
			 * This can happen if, e.g., 
			 * 
			 *   1. An attempt is made to store a string in a VARCHAR column 
			 *      that is too short to hold the string. In this case, the 
			 *      message could be something like:
			 *      
			 *        "value too long for type character varying(32)"
			 *        
			 *   2. ...
			 */
			restError = RestError.INTERNAL_SERVER_ERROR;
			restErrorResource = new RestErrorResource(restError, rootCause.getMessage(), ex);
		}else {
			restError = RestError.INTERNAL_SERVER_ERROR;
			restErrorResource = new RestErrorResource(restError, ex.getMessage(), ex);
		}

		return Response.status(restError.getResponseStatus().getStatusCode()).
				entity(restErrorResource).
				type(MediaType.APPLICATION_JSON_TYPE).
				build();
	}
}