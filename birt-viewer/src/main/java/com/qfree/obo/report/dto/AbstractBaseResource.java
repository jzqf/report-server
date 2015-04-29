package com.qfree.obo.report.dto;

import java.util.List;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public AbstractBaseResource() {
		super();
	}

	public AbstractBaseResource(Class<?> entityClass, Object id, UriInfo uriInfo, List<String> expand) {
		super();
		//		this.href = createHref(getFullyQualifiedContextPath(uriInfo), entityClass, id);
		this.href = createHref(uriInfo, entityClass, id, expand);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	protected static String createHref(UriInfo uriInfo, Class<?> entityClass, Object id, List<String> expand) {
		//		logger.info("entityClass.getName() = {}", entityClass.getName());
		//		logger.info("uriInfo.getRequestUri() = {}", uriInfo.getRequestUri());
		//		logger.info("uriInfo.getQueryParameters() = {}", uriInfo.getQueryParameters());

		ResourcePath resourcePath = ResourcePath.forEntity(entityClass);
		//		logger.info("resourcePath = {}", resourcePath);

		//		StringBuilder queryParameters = new StringBuilder();
		//		if (expand.size() > 0) {
		//			queryParameters.append("?");
		//			for (int i = 0; i < expand.size(); i++) {
		//				if (i == 0) {
		//					queryParameters.append("expand=" + expand.get(0));
		//				} else {
		//					queryParameters.append("&expand=" + expand.get(0));
		//				}
		//			}
		//		}

		/*
		 * uriInfo.getBaseUriBuilder() returns a UriBuilder initialized with the
		 * base URI of the application. This "base URI" includes the entire 
		 * request URL except for path components that are specific to an 
		 * endpoint. For example, this could be:
		 * 
		 *     http://localhost:8080/report-server/rest/
		 */
		return uriInfo.getBaseUriBuilder()
				.path(resourcePath.getPath())
				.path(id.toString())
				.queryParam("expand", expand.toArray())
				.toString();
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
