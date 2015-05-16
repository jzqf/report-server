package com.qfree.obo.report.rest.server;

import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;

public class RestUtils {

	public static final boolean AUTO_EXPAND_PRIMARY_RESOURCES = false;

	/**
	 * Version numbers for JAX-RS ReST endpoints exposed by this application.<p>
	 * 
	 * These version numbers are endpoint-specific, i.e., the version number for
	 * an endpoint is only changed if the API for that endpoint changes; other
	 * endpoints retain their current version numbers.
	 * 
	 * @author Jeffrey Zelt
	 *
	 */
	public enum RestApiVersion {
		v1("1"),
		v2("2"),
		v3("3"),
		v4("4"),
		v5("5");

		private String version;

		private RestApiVersion(String version) {
			this.version = version;
		}

		public String getVersion() {
			return version;
		}
	}

	/**
	 * 	 * Extracts and returns the ReST API version from the HTTP "Accept" header.
	 * 
	 * This method expects ReST API version to be embedded in the HTTP "Accept"
	 * header as a media type parameter with name "v". The accept header must
	 * therefore be of the form:
	 * 
	 *     ...;v=version...
	 * 
	 * Here "v" is the parameter name and "version" is the version that will be 
	 * returned.
	 * 
	 * @param httpAcceptHeader
	 * @param defaultVersion
	 * @return
	 */
	public static RestApiVersion extractAPIVersion(String httpAcceptHeader, RestApiVersion defaultVersion) {
		RestApiVersion apiVersion = defaultVersion;

		/*
		 * Support Accept headers of the form:
		 * 
		 *     <media-type>;v=<version>
		 *     <media-type>;morestuff&v=<version>
		 * 
		 * Therefore, the regex for the delimiter specifies ";" or "&".
		 */
		for (String token : httpAcceptHeader.split("[;&]")) {
			String[] potentialKeyValuePair = token.split("=");
			if (potentialKeyValuePair.length == 2) {
				String key = potentialKeyValuePair[0].trim();
				if (key.equalsIgnoreCase("v")) {
					String apiVersionString = potentialKeyValuePair[1].trim();
					try {
						apiVersion = RestApiVersion.valueOf("v" + apiVersionString);
					} catch (IllegalArgumentException | NullPointerException e) {
						/* '"v" + apiVersionString' is not a valid element of the
						 * RestApiVersion enum. Return default version instead.
						 */
					}
					break;
				}
			}
		}
		return apiVersion;
	}

	public static void ifNullThen404(Object object, Class<?> objectClass, String attrName, String attrValue) {
		if (object == null) {
			String message = String.format("No %s for %s '%s'", objectClass.getSimpleName(), attrName, attrValue);
			throw new RestApiException(RestError.NOT_FOUND_RESOUCE, message, objectClass, attrName, attrValue);
		}
	}

	public static void ifNewResourceIdNotNullThen403(Object id, Class<?> classToCreate, String attrName,
			Object attrValueObject) {
		if (id != null) {

			String attrValueString = "";
			if (attrValueObject != null) {
				attrValueString = attrValueObject.toString();
			}

			String message = String.format("%s = '%s'. The id must be null when creating a new %s. "
					+ RestError.FORBIDDEN_NEW_RESOUCE_ID_NOTNULL.getErrorMessage(), attrName, attrValueString,
					classToCreate.getSimpleName());
			throw new RestApiException(RestError.FORBIDDEN_NEW_RESOUCE_ID_NOTNULL, message, classToCreate, attrName,
					null);
		}
	}

}
