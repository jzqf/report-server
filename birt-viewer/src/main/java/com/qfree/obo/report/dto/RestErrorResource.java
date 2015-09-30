package com.qfree.obo.report.dto;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RestErrorResource {

	//	private static final String STATUS_PROP_NAME = "status";
	//	private static final String CODE_PROP_NAME = "code";
	//	private static final String MESSAGE_PROP_NAME = "message";
	//	private static final String DEVELOPER_MESSAGE_PROP_NAME = "developerMessage";
	//	private static final String MORE_INFO_PROP_NAME = "moreInfo";
	//
	//	private static final String DEFAULT_MORE_INFO_URL = "mailto:support@stormpath.com";

	public enum RestError {

		// 1xx Informational:

		//		/**
		//		 * {@code 100 Continue}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.1.1">HTTP/1.1</a>
		//		 */
		//		CONTINUE(),
		//		/**
		//		 * {@code 101 Switching Protocols}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.1.2">HTTP/1.1</a>
		//		 */
		//		SWITCHING_PROTOCOLS(),
		//		/**
		//		 * {@code 102 Processing}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2518#section-10.1">WebDAV</a>
		//		 */
		//		PROCESSING(),

		// 2xx Success:

		/**
		 * {@code 200 OK}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.1">HTTP/1.1</a>
		 */
		OK(Response.Status.OK, null, null, null),
		/**
		 * {@code 201 Created}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.2">HTTP/1.1</a>
		 */
		CREATED(Response.Status.CREATED, null, null, null),
		/**
		 * {@code 202 Accepted}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.3">HTTP/1.1</a>
		 */
		ACCEPTED(Response.Status.ACCEPTED, null, null, null),
		//		/**
		//		 * {@code 203 Non-Authoritative Information}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.4">HTTP/1.1</a>
		//		 */
		//		NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
		/**
		 * {@code 204 No Content}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.5">HTTP/1.1</a>
		 */
		NO_CONTENT(Response.Status.NO_CONTENT, null, null, null),
		/**
		 * {@code 205 Reset Content}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.6">HTTP/1.1</a>
		 */
		RESET_CONTENT(Response.Status.RESET_CONTENT, null, null, null),
		/**
		 * {@code 206 Partial Content}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.2.7">HTTP/1.1</a>
		 */
		PARTIAL_CONTENT(Response.Status.PARTIAL_CONTENT, null, null, null),
		//		/**
		//		 * {@code 207 Multi-Status}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc4918#section-13">WebDAV</a>
		//		 */
		//		MULTI_STATUS(207, "Multi-Status"),
		//		/**
		//		 * {@code 208 Already Reported}.
		//		 * @see <a href="http://tools.ietf.org/html/draft-ietf-webdav-bind-27#section-7.1">WebDAV Binding Extensions</a>
		//		 */
		//		ALREADY_REPORTED(208, "Already Reported"),
		//		/**
		//		 * {@code 226 IM Used}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc3229#section-10.4.1">Delta encoding in HTTP</a>
		//		 */
		//		IM_USED(226, "IM Used"),

		// 3xx Redirection

		//		/**
		//		 * {@code 300 Multiple Choices}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.1">HTTP/1.1</a>
		//		 */
		//		MULTIPLE_CHOICES(300, "Multiple Choices"),
		/**
		 * {@code 301 Moved Permanently}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.2">HTTP/1.1</a>
		 */
		MOVED_PERMANENTLY(Response.Status.MOVED_PERMANENTLY, null, null, null),
		/**
		 * {@code 302 Found} (Moved Temporarily).
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.3">HTTP/1.1</a>
		 */
		FOUND(Response.Status.FOUND, null, null, null),
		/**
		 * {@code 303 See Other}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.4">HTTP/1.1</a>
		 */
		SEE_OTHER(Response.Status.SEE_OTHER, null, null, null),
		/**
		 * {@code 304 Not Modified}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.5">HTTP/1.1</a>
		 */
		NOT_MODIFIED(Response.Status.NOT_MODIFIED, null, null, null),
		/**
		 * {@code 305 Use Proxy}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.6">HTTP/1.1</a>
		 */
		USE_PROXY(Response.Status.USE_PROXY, null, null, null),
		/**
		 * {@code 307 Temporary Redirect}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.3.8">HTTP/1.1</a>
		 */
		TEMPORARY_REDIRECT(Response.Status.TEMPORARY_REDIRECT, null, null, null),

		// --- 4xx Client Error ---

		/**
		 * {@code 400 Bad Request}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.1">HTTP/1.1</a>
		 */
		BAD_REQUEST(Response.Status.BAD_REQUEST, null, null, null),
		/**
		 * {@code 401 Unauthorized}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.2">HTTP/1.1</a>
		 */
		UNAUTHORIZED(Response.Status.UNAUTHORIZED, null, null, null),
		/**
		 * {@code 402 Payment Required}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.3">HTTP/1.1</a>
		 */
		PAYMENT_REQUIRED(Response.Status.PAYMENT_REQUIRED, null, null, null),
		/**
		 * {@code 403 Forbidden}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.4">HTTP/1.1</a>
		 */
		FORBIDDEN(Response.Status.FORBIDDEN, "403.0", null, null),
		FORBIDDEN_NOT_LOGIN_ROLE(Response.Status.FORBIDDEN, "403.1", "Role does not have 'login' privilege", null),
		FORBIDDEN_BAD_ROLE_PASSWORD(Response.Status.FORBIDDEN, "403.2", "Wrong password for role", null),
		FORBIDDEN_REPORT_CATEGORY_NULL(Response.Status.FORBIDDEN, "403.3",
				"reportCategoryId is null for a report being saved", null),
		FORBIDDEN_NEW_RESOUCE_ID_NOTNULL(Response.Status.FORBIDDEN, "403.4",
				"When creating a new entity from an instance of a ReST resource, "
						+ "the id from the resource instance must be null because "
						+ "it will be generated for the new entity by the Entity Manager",
				null),
		FORBIDDEN_REPORTVERSION_REPORT_NULL(Response.Status.FORBIDDEN, "403.5",
				"reportId is null for a report version being saved", null),
		FORBIDDEN_XML_NOT_VALID(Response.Status.FORBIDDEN, "403.6", "The XML is not well formed", null),
		FORBIDDEN_ATTRIBUTE_NULL(Response.Status.FORBIDDEN, "403.7",
				"An attribute is null when it should not be", null),
		FORBIDDEN_VALIDATION_ERROR(Response.Status.FORBIDDEN, "403.8",
				"An attribute violates a validation constraint", null),
		FORBIDDEN_SELECTIONLISTVALUE_REPORTPARAMETER_NULL(
				Response.Status.FORBIDDEN, "403.9",
				"reportParameterId is null for a selection list value being saved", null),
		FORBIDDEN_REPORTPARAMETER_REPORTVERSION_NULL(
				Response.Status.FORBIDDEN, "403.10",
				"reportVersionId is null for a report parameter being saved", null),
		FORBIDDEN_DYN_SEL_LIST_PARENT_KEY_COUNT(
				Response.Status.FORBIDDEN, "403.11",
				"Wrong number of parent key values passed for a cascading parameter dynamic list", null),
		FORBIDDEN_ATTRIBUTE_BLANK(Response.Status.FORBIDDEN, "403.12",
				"An attribute is blank when it should not be", null),
		FORBIDDEN_SUBSCRIPTION_DOCUMENTFORMAT_NULL(
				Response.Status.FORBIDDEN, "403.13",
				"documentFormatId is null for a subscription being saved", null),
		FORBIDDEN_SUBSCRIPTION_REPORTVERSION_NULL(
				Response.Status.FORBIDDEN, "403.14",
				"reportVersionId is null for a subscription being saved", null),
		FORBIDDEN_SUBSCRIPTION_ROLE_NULL(
				Response.Status.FORBIDDEN, "403.15",
				"roleId is null for a subscription being saved", null),
		FORBIDDEN_NEW_SUBSCRIPTION_ENABLED(
				Response.Status.FORBIDDEN, "403.16",
				"A new subscription cannot be created with enabled=true", null),
		FORBIDDEN_MULTIPLE_VALUES_FOR_PARAM(
				Response.Status.FORBIDDEN, "403.17",
				"Multiple values provided for a single-valued report parameter", null),
		/**
		 * {@code 404 Not Found}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.5">HTTP/1.1</a>
		 */
		NOT_FOUND(Response.Status.NOT_FOUND, "404.0", null, null),
		NOT_FOUND_RESOUCE(Response.Status.NOT_FOUND, "404.1", "A resource could not be located", null),
		NOT_FOUND_ROLE_TO_AUTHENTICATE(Response.Status.NOT_FOUND, "404.2",
				"Both a username and encoded password must be submitted", null),
		/**
		 * {@code 405 Method Not Allowed}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.6">HTTP/1.1</a>
		 */
		METHOD_NOT_ALLOWED(Response.Status.METHOD_NOT_ALLOWED, null, null, null),
		/**
		 * {@code 406 Not Acceptable}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.7">HTTP/1.1</a>
		 */
		NOT_ACCEPTABLE(Response.Status.NOT_ACCEPTABLE, null, null, null),
		/**
		 * {@code 407 Proxy Authentication Required}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.8">HTTP/1.1</a>
		 */
		PROXY_AUTHENTICATION_REQUIRED(Response.Status.PROXY_AUTHENTICATION_REQUIRED, null, null, null),
		/**
		 * {@code 408 Request Timeout}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.9">HTTP/1.1</a>
		 */
		REQUEST_TIMEOUT(Response.Status.REQUEST_TIMEOUT, null, null, null),
		/**
		 * {@code 409 Conflict}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.10">HTTP/1.1</a>
		 */
		CONFLICT(Response.Status.CONFLICT, null, null, null),
		/**
		 * {@code 410 Gone}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.11">HTTP/1.1</a>
		 */
		GONE(Response.Status.GONE, null, null, null),
		/**
		 * {@code 411 Length Required}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.12">HTTP/1.1</a>
		 */
		LENGTH_REQUIRED(Response.Status.LENGTH_REQUIRED, null, null, null),
		/**
		 * {@code 412 Precondition failed}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.13">HTTP/1.1</a>
		 */
		PRECONDITION_FAILED(Response.Status.PRECONDITION_FAILED, null, null, null),
		/**
		 * {@code 413 Request Entity Too Large}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.14">HTTP/1.1</a>
		 */
		REQUEST_ENTITY_TOO_LARGE(Response.Status.REQUEST_ENTITY_TOO_LARGE, null, null, null),
		/**
		 * {@code 414 Request-URI Too Long}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.15">HTTP/1.1</a>
		 */
		REQUEST_URI_TOO_LONG(Response.Status.REQUEST_URI_TOO_LONG, null, null, null),
		/**
		 * {@code 415 Unsupported Media Type}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.16">HTTP/1.1</a>
		 */
		UNSUPPORTED_MEDIA_TYPE(Response.Status.UNSUPPORTED_MEDIA_TYPE, null, null, null),
		/**
		 * {@code 416 Requested Range Not Satisfiable}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.17">HTTP/1.1</a>
		 */
		REQUESTED_RANGE_NOT_SATISFIABLE(Response.Status.REQUESTED_RANGE_NOT_SATISFIABLE, null, null, null),
		/**
		 * {@code 417 Expectation Failed}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.4.18">HTTP/1.1</a>
		 */
		EXPECTATION_FAILED(Response.Status.EXPECTATION_FAILED, null, null, null),
		//		/**
		//		 * {@code 419 Insufficient Space on Resource}.
		//		 * @see <a href="http://tools.ietf.org/html/draft-ietf-webdav-protocol-05#section-10.4">WebDAV Draft</a>
		//		 */
		//		INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"),
		//		/**
		//		 * {@code 420 Method Failure}.
		//		 * @see <a href="http://tools.ietf.org/html/draft-ietf-webdav-protocol-05#section-10.5">WebDAV Draft</a>
		//		 */
		//		METHOD_FAILURE(420, "Method Failure"),
		//		/**
		//		 * {@code 421 Destination Locked}.
		//		 * @see <a href="http://tools.ietf.org/html/draft-ietf-webdav-protocol-05#section-10.6">WebDAV Draft</a>
		//		 */
		//		DESTINATION_LOCKED(421, "Destination Locked"),
		//		/**
		//		 * {@code 422 Unprocessable Entity}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.2">WebDAV</a>
		//		 */
		//		UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
		//		/**
		//		 * {@code 423 Locked}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.3">WebDAV</a>
		//		 */
		//		LOCKED(423, "Locked"),
		//		/**
		//		 * {@code 424 Failed Dependency}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.4">WebDAV</a>
		//		 */
		//		FAILED_DEPENDENCY(424, "Failed Dependency"),
		//		/**
		//		 * {@code 426 Upgrade Required}.
		//		 * @see <a href="http://tools.ietf.org/html/rfc2817#section-6">Upgrading to TLS Within HTTP/1.1</a>
		//		 */
		//		UPGRADE_REQUIRED(426, "Upgrade Required"),

		// --- 5xx Server Error ---

		/**
		 * {@code 500 Internal Server Error}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.1">HTTP/1.1</a>
		 */
		INTERNAL_SERVER_ERROR(Response.Status.INTERNAL_SERVER_ERROR, "500.0", null, null),
		INTERNAL_SERVER_ERROR_REPORT_FOLDER_MISSING(Response.Status.INTERNAL_SERVER_ERROR, "500.1",
				"BIRT Viewer working folder cannot be accessed", null),
		INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC(Response.Status.INTERNAL_SERVER_ERROR, "500.2",
				"Error syncing rptdesign files between file system and database", null),
		INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_NO_PERMIT(
				Response.Status.INTERNAL_SERVER_ERROR,
				"500.3",
				"Unable to acquire semaphore permit for synchronizing rptdesign files between file system and database",
				null),
		INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_INTERRUPT(
				Response.Status.INTERNAL_SERVER_ERROR,
				"500.4",
				"InterruptedException thrown while waiting to acquire semaphore permit.",
				null),
		INTERNAL_SERVER_ERROR_UNTREATED_CASE(Response.Status.INTERNAL_SERVER_ERROR, "500.5",
				"Untreated case", null),
		/**
		 * {@code 501 Not Implemented}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.2">HTTP/1.1</a>
		 */
		NOT_IMPLEMENTED(Response.Status.NOT_IMPLEMENTED, null, null, null),
		/**
		 * {@code 502 Bad Gateway}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.3">HTTP/1.1</a>
		 */
		BAD_GATEWAY(Response.Status.BAD_GATEWAY, null, null, null),
		/**
		 * {@code 503 Service Unavailable}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.4">HTTP/1.1</a>
		 */
		SERVICE_UNAVAILABLE(Response.Status.SERVICE_UNAVAILABLE, null, null, null),
		/**
		 * {@code 504 Gateway Timeout}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.5">HTTP/1.1</a>
		 */
		GATEWAY_TIMEOUT(Response.Status.GATEWAY_TIMEOUT, null, null, null),
		/**
		 * {@code 505 HTTP Version Not Supported}.
		 * @see <a href="http://tools.ietf.org/html/rfc2616#section-10.5.6">HTTP/1.1</a>
		 */
		HTTP_VERSION_NOT_SUPPORTED(Response.Status.HTTP_VERSION_NOT_SUPPORTED, null, null, null);
		//		/**
		//		 * {@code 506 Variant Also Negotiates}
		//		 * @see <a href="http://tools.ietf.org/html/rfc2295#section-8.1">Transparent Content Negotiation</a>
		//		 */
		//		VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
		//		/**
		//		 * {@code 507 Insufficient Storage}
		//		 * @see <a href="http://tools.ietf.org/html/rfc4918#section-11.5">WebDAV</a>
		//		 */
		//		INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
		//		/**
		//		 * {@code 508 Loop Detected}
		//		 * @see <a href="http://tools.ietf.org/html/draft-ietf-webdav-bind-27#section-7.2">WebDAV Binding Extensions</a>
		//		 */
		//		LOOP_DETECTED(508, "Loop Detected"),
		//		/**
		//		 * {@code 510 Not Extended}
		//		 * @see <a href="http://tools.ietf.org/html/rfc2774#section-7">HTTP Extension Framework</a>
		//		 */
		//		NOT_EXTENDED(510, "Not Extended");

		private final Response.Status responseStatus;
		private final String errorCode;
		private final String errorMessage;
		private final String moreInfoUrl;

		private RestError(Response.Status responseStatus, String errorCode, String errorMessage, String moreInfoUrl) {
			this.responseStatus = responseStatus;
			this.errorCode = errorCode;
			this.errorMessage = errorMessage;
			this.moreInfoUrl = moreInfoUrl;
		}

		public Response.Status getResponseStatus() {
			return responseStatus;
		}

		public String getErrorCode() {
			return errorCode;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public String getMoreInfoUrl() {
			return moreInfoUrl;
		}

		/**
		 * Return the enum constant of this type with the specified numeric 
		 * value.
		 * 
		 * @param statusCode the numeric value of the HTTP status code for the 
		 *                   enum to be returned
		 * @return the enum  constant with the specified numeric value of the
		 *                   HTTP status code
		 * @throws IllegalArgumentException if this enum has no constant for the
		 *                                  specified numeric value
		 */
		public static RestError fromStatusCode(int statusCode) {
			for (RestError restError : values()) {
				if (restError.responseStatus.getStatusCode() == statusCode) {
					return restError;
				}
			}
			throw new IllegalArgumentException("No matching enum constant for status code [" + statusCode + "]");
		}

	}

	@XmlElement
	private int httpStatus;
	@XmlElement
	private String errorCode;
	@XmlElement
	private String errorMessage;
	@XmlElement //@XmlTransient
	private String resourceName;// @XmlTransient means this will not be serialized to JSON
	@XmlElement //@XmlTransient
	private String attrName;// @XmlTransient means this will not be serialized to JSON
	@XmlElement //@XmlTransient
	private String attrValue;// @XmlTransient means this will not be serialized to JSON
	@XmlElement
	private String moreInfoUrl;
	@XmlTransient
	//	@XmlElement
	private Throwable cause;// @XmlTransient means this will not be serialized to JSON

	public RestErrorResource() {
	}

	public RestErrorResource(
			RestError restError,
			String errorMessage) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				errorMessage,
				null,
				null,
				null,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			RestError restError,
			String errorMessage,
			Throwable throwable) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				errorMessage,
				null,
				null,
				null,
				restError.getMoreInfoUrl(),
				throwable);
	}

	public RestErrorResource(
			RestError restError,
			Class<?> resourceClass) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				restError.getErrorMessage(),
				resourceClass.getSimpleName(),
				null,
				null,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			RestError restError,
			String errorMessage,
			Class<?> resourceClass) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				errorMessage,
				resourceClass.getSimpleName(),
				null,
				null,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			RestError restError,
			Class<?> resourceClass,
			String attrName) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				restError.getErrorMessage(),
				resourceClass.getSimpleName(),
				attrName,
				null,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			RestError restError,
			Class<?> resourceClass,
			String attrName,
			String attrValue) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				restError.getErrorMessage(),
				resourceClass.getSimpleName(),
				attrName,
				attrValue,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			RestError restError,
			String errorMessage,
			Class<?> resourceClass,
			String attrName,
			String attrValue) {
		this(
				restError.getResponseStatus(),
				restError.getErrorCode(),
				errorMessage,
				resourceClass.getSimpleName(),
				attrName,
				attrValue,
				restError.getMoreInfoUrl(),
				null);
	}

	public RestErrorResource(
			Response.Status responseStatus,
			String errorCode,
			String errorMessage,
			String resourceName,
			String attrName,
			String attrValue,
			String moreInfoUrl,
			Throwable throwable) {
		if (responseStatus == null) {
			throw new NullPointerException("responseStatus argument cannot be null.");
		}
		this.httpStatus = responseStatus.getStatusCode();
		this.errorCode = errorCode;
		if (errorMessage != null) {
			this.errorMessage = errorMessage;
		} else {
			this.errorMessage = responseStatus.getReasonPhrase();
		}
		this.resourceName = resourceName;
		this.attrName = attrName;
		this.attrValue = attrValue;
		this.moreInfoUrl = moreInfoUrl;
		this.cause = throwable;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public String getMoreInfoUrl() {
		return moreInfoUrl;
	}

	public void setMoreInfoUrl(String moreInfoUrl) {
		this.moreInfoUrl = moreInfoUrl;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable throwable) {
		this.cause = throwable;
	}

}
