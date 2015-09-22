package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

/*
 * An "xsi:type" JSON attribute normally appears whenever an instance of a 
 * subclass is marshalled. This @XmlTransient annotation applied at the class 
 * level stops that element from being generated.
 */
@XmlTransient
public abstract class AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AbstractBaseResource.class);

	@XmlElement
	protected String href;

	@XmlElement
	protected String mediaType;

	public AbstractBaseResource() {
		super();
	}

	public AbstractBaseResource(Class<?> entityClass, Object id, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(null, null, entityClass, id, uriInfo, queryParams, apiVersion);
	}

	/**
	 * 
	 * @param baseResourceUri currently only used for collection resources
	 * @param collectionPath currently only used for collection resources
	 * @param extraQueryParams currently only used for collection resources
	 * @param entityClass
	 * @param id currently only used for instance resources
	 * @param uriInfo
	 * @param expand
	 * @param apiVersion
	 */
	public AbstractBaseResource(
			String baseResourceUri, String collectionPath,
			Class<?> entityClass, Object id, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		super();
		//		this.href = createHref(getFullyQualifiedContextPath(uriInfo), entityClass, id);
		this.href = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, id,queryParams);
		this.mediaType = createMediaType(apiVersion);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public static String createHref(
			UriInfo uriInfo, Class<?> entityClass, Object instanceId, Map<String, List<String>> queryParams) {
		return createHref(null, null, uriInfo, entityClass, instanceId, queryParams);
	}

	protected static String createHref(
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Class<?> entityClass, Object instanceId,
			Map<String, List<String>> queryParams) {

		//		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		UriBuilder uriBuilder = null;
		if (instanceId != null) {
			/*
			 * Instance resources:
			 * 
			 * uriInfo.getBaseUriBuilder() returns a UriBuilder initialized with the
			 * base URI of the application. This "base URI" includes the entire 
			 * request URL except for path components that are specific to an 
			 * endpoint. For example, this could be:
			 * 
			 *     http://localhost:8080/report-server/rest/
			 */
			ResourcePath resourcePath = ResourcePath.forEntity(entityClass);
			uriBuilder = uriInfo.getBaseUriBuilder()
					.path(resourcePath.getPath())
					.path(instanceId.toString());
		} else {
			/*
			 * Collection resources:
			 */
			if (baseResourceUri != null) {
				/*
				 * We are probably dealing with a collection resource that is an
				 * attribute of another resource.
				 */
				uriBuilder = UriBuilder.fromPath(baseResourceUri);
				if (collectionPath != null) {
					uriBuilder = uriBuilder.path(collectionPath);
				}
			} else {
				/*
				 * We are probably dealing with a top level collection resource..
				 */
				ResourcePath resourcePath = ResourcePath.forEntity(entityClass);
				uriBuilder = uriInfo.getBaseUriBuilder().path(resourcePath.getPath());
			}
		}

		if (queryParams != null) {
			/*
			 * Append any miscellaneous query parameters held in the Map 
			 * "queryParams". Each map key is the query parameter name, and 
			 * each map value is a list of query parameter values. A list is 
			 * used because there may be multiple values for each query 
			 * parameter.
			 */
			for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
				uriBuilder = uriBuilder.queryParam(entry.getKey(), entry.getValue().toArray());
			}
		}
		//		if (expand != null) {
		//			/*
		//			 * Append the "expand" query parameters.
		//			 */
		//			uriBuilder = uriBuilder.queryParam(ResourcePath.EXPAND_QP_NAME, expand.toArray());
		//		}
		return uriBuilder.toString();
	}

	private String createMediaType(RestApiVersion apiVersion) {
		if (apiVersion != null) {
			return "application/json;v=" + apiVersion.getVersion();
		} else {
			return "application/json";
		}
	}

	//	/**
	//	 * Returns the fully-qualified path given the UriInfo object passed as a 
	//	 * parameter to a JAX-RS controller method using:<p>
	//	 * <p>
	//	 * {@code Context UriInfo uriInfo}<p>
	//	 * <p>
	//	 * It includes the entire request URL except for path components that are 
	//	 * specific to an endpoint. For example, this could be:<p>
	//	 * <p>
	//	 * http://localhost:8080/report-server/rest/
	//	 * 
	//	 * @param uriInfo
	//	 * @return
	//	 */
	//	protected static String getFullyQualifiedContextPath(UriInfo uriInfo) {
	//		String fqBasePath = uriInfo.getBaseUri().toString();
	//		logger.info("fq = {}", fqBasePath);
	//		if (fqBasePath.endsWith("/")) {
	//			return fqBasePath.substring(0, fqBasePath.length() - 1);
	//		}
	//		return fqBasePath;
	//	}

}
