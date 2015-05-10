package com.qfree.obo.report.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.qfree.obo.report.dto.RestErrorResource;
import com.qfree.obo.report.dto.RestErrorResource.RestError;

public class ReportingException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public ReportingException(
			RestError restError,
			String errorMessage,
			Class<?> notFoundClass,
			String attributeName,
			String attributeValue) {
		super(Response.status(restError.getResponseStatus())
				.entity(new RestErrorResource(restError, errorMessage, notFoundClass, attributeName, attributeValue))
				.build());
	}

	/**
	 * Create an HTTP 500 (Internal Server Error) exception.
	 */
	public ReportingException() {
		super();
	}

	/**
	 * Create an HTTP 500 (Internal Server Error) exception with a specified 
	 * message.
	 * 
	 * @param message
	 */
	public ReportingException(String message) {
		super(message);
	}

	/**
	 * Construct a new exception using the supplied response and a default 
	 * message generated from the response's HTTP status code and the associated
	 * HTTP status reason phrase.
	 *
	 * @param response the response that will be returned to the client. A value
	 *                 of null will be replaced with an internal server error 
	 *                 response (status code 500).
	 */
	public ReportingException(final Response response) {
		super(response);
	}

	/**
	 * Construct a new exception using the supplied message and response.
	 *
	 * @param message  the detail message (which is saved for later retrieval
	 *                 by the {@link #getMessage()} method), but not displayed
	 *                 in the HTTP response.
	 * @param response the response that will be returned to the client, a value
	 *                 of null will be replaced with an internal server error 
	 *                 response (status code 500).
	 */
	public ReportingException(final String message, final Response response) {
		super(message, response);
	}

	/**
	 * Construct a new exception with a supplied message and HTTP status code 
	 * and a default message generated from the HTTP status code and the 
	 * associated HTTP status reason phrase.
	 *
	 * @param intStatus the HTTP status code that will be returned to the 
	 *                  client.
	 */
	public ReportingException(final int intStatus) {
		super(intStatus);
	}

	/**
	 * Construct a new exception with a supplied message and HTTP status code.
	 *
	 * @param message    the detail message, which is saved for later retrieval
	 *                   by the {@link #getMessage()} method, but not displayed
	 *                   in the HTTP response.
	 * @param intStatus  the HTTP status code that will be returned to the 
	 *                   client.
	 */
	public ReportingException(String message, final int intStatus) {
		super(message, intStatus);
	}

	/**
	 * Construct a new exception with the supplied HTTP status and a default 
	 * message generated from the HTTP status code and the associated HTTP 
	 * status reason phrase.
	 *
	 * @param responseStatus the HTTP response status that will be returned to 
	 *                       the client.
	 * @throws IllegalArgumentException if responseStatus is {@code null}.
	 */
	public ReportingException(final Response.Status responseStatus) {
		super(responseStatus);
	}

	/**
	 * Construct a new exception with the supplied message and HTTP status.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method).
	 * @param responseStatus the HTTP response status that will be returned to 
	 *                       the client.
	 * @throws IllegalArgumentException if status is {@code null}.
	 */
	public ReportingException(final String message, final Response.Status responseStatus) {
		super(message, responseStatus);
	}

	/**
	 * Construct a new exception with the supplied root cause, default HTTP 
	 * status code of 500 and a default message generated from the HTTP status 
	 * code and the associated HTTP status reason phrase.
	 *
	 * @param cause the underlying cause of the exception.
	 */
	public ReportingException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new exception with the supplied message, root cause and 
	 * default HTTP status code of 500.
	 *
	 * @param message the detail message (which is saved for later retrieval
	 *                by the {@link #getMessage()} method).
	 * @param cause   the underlying cause of the exception.
	 */
	public ReportingException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new exception with the supplied root cause, response and a 
	 * default message generated from the response's HTTP status code and the 
	 * associated HTTP status reason phrase.
	 *
	 * @param response the response that will be returned to the client. A value
	 *                 of null will be replaced with an internal server error 
	 *                 response (status code 500).
	 * @param cause    the underlying cause of the exception.
	 */
	public ReportingException(final Throwable cause, final Response response) {
		super(cause, response);
	}

	/**
	 * Construct a new exception with the supplied message, root cause and 
	 * response.
	 *
	 * @param message  the detail message (which is saved for later retrieval
	 *                 by the {@link #getMessage()} method).
	 * @param response the response that will be returned to the client. A value
	 *                 of null will be replaced with an internal server error 
	 *                 response (status code 500).
	 * @param cause    the underlying cause of the exception.
	 */
	public ReportingException(final String message, final Throwable cause, final Response response) {
		super(message, cause, response);
	}

	/**
	 * Construct a new exception with the supplied root cause, HTTP status code
	 * and a default message generated from the HTTP status code and the 
	 * associated HTTP status reason phrase.
	 *
	 * @param intStatus the HTTP status code that will be returned to the 
	 *                  client.
	 * @param cause     the underlying cause of the exception.
	 */
	public ReportingException(final Throwable cause, final int intStatus) {
		super(cause, intStatus);
	}

	/**
	 * Construct a new exception with the supplied message, root cause and HTTP 
	 * status code.
	 *
	 * @param message   the detail message (which is saved for later retrieval
	 *                  by the {@link #getMessage()} method).
	 * @param intStatus the HTTP status code that will be returned to the 
	 *                  client.
	 * @param cause     the underlying cause of the exception.
	 */
	public ReportingException(final String message, final Throwable cause, final int intStatus) {
		super(message, cause, intStatus);
	}

	/**
	 * Construct a new instance with the supplied root cause, HTTP status code
	 * and a default message generated from the HTTP status code and the 
	 * associated HTTP status reason phrase.
	 *
	 * @param responseStatus the HTTP response status that will be returned to 
	 *                       the client.
	 * @param cause          the underlying cause of the exception.
	 * @throws IllegalArgumentException if status is {@code null}.
	 */
	public ReportingException(final Throwable cause, final Response.Status responseStatus)
			throws IllegalArgumentException {
		super(cause, responseStatus);
	}

	/**
	 * Construct a new instance with a the supplied message, root cause and HTTP status code.
	 *
	 * @param message        the detail message (which is saved for later 
	 *                       retrieval by the {@link #getMessage()} method).
	 * @param responseStatus the HTTP response status that will be returned to 
	 *                       the client.
	 * @param cause          the underlying cause of the exception.
	 * @since 2.0
	 */
	public ReportingException(final String message, final Throwable cause, final Response.Status responseStatus)
			throws IllegalArgumentException {
		super(message, cause, responseStatus);
	}

}
