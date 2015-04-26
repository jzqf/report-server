package com.qfree.obo.report.resource;

import java.nio.file.Path;
import java.nio.file.Paths;

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
public abstract class AbstractResource {

	private static final Logger logger = LoggerFactory.getLogger(AbstractResource.class);

	private static final String PATH_SEPARATOR = "/";

	/*
	 * These paths are used in @Path annotations (and elsewhere). Arguments to
	 * annotations must be constant expressions. Therefore these paths must be
	 * declared as constants. It does not seem possible to, e.g., declare them 
	 * within the ResourcePath enum because that enum is not considered to be a 
	 * constant expression. As a result, each path that is defined here must
	 * also be used in ResourcePath to create a new enum element.
	 */
	public static final String CONFIGURATIONS_PATH = PATH_SEPARATOR + "configurations";
	public static final String REPORTS_PATH = PATH_SEPARATOR + "reports";
	public static final String REPORTCATEGORIES_PATH = PATH_SEPARATOR + "reportcategories";

	@XmlElement
	protected String href;

	public AbstractResource() {
		super();
	}

	public AbstractResource(UriInfo uriInfo, Class<?> entityClass, Object id) {
		super();
		//		this.href = createHref(getFullyQualifiedContextPath(uriInfo), entityClass, id);
		this.href = createHref(uriInfo, entityClass, id);
	}

	//	public String getHref() {
	//		return href;
	//	}
	//
	//	public void setHref(String href) {
	//		this.href = href;
	//	}

	protected static String createHref(UriInfo uriInfo, Class<?> entityClass, Object id) {
		logger.info("entityClass.getName() = {}", entityClass.getName());
		/*
		 * The fully-qualified path is obtained from the UriInfo object that is
		 * passed as a parameter to a JAX-RS controller method using:
		 * 
		 *     Context UriInfo uriInfo}
		 * 
		 * It includes the entire request URL except for path components that  
		 * are specific to an endpoint. For example, this could be:
		 * 
		 *     http://localhost:8080/report-server/rest/
		 */
		String fqBasePath = uriInfo.getBaseUri().toString();
		logger.info("fqBasePath 1 = {}", fqBasePath);
		if (fqBasePath.endsWith("/")) {
			fqBasePath = fqBasePath.substring(0, fqBasePath.length() - 1);
			logger.info("fqBasePath 2 = {}", fqBasePath);
		}
		//		String fqBasePath = getFullyQualifiedContextPath(uriInfo);

		//		StringBuilder sb = new StringBuilder(fqBasePath);
		ResourcePath resourcePath = ResourcePath.forEntity(entityClass);
		logger.info("resourcePath = {}", resourcePath);
		//		sb.append(resourcePath.getPath()).append(PATH_SEPARATOR).append(id);
		Path p1 = Paths.get(fqBasePath, resourcePath.getPath(), id.toString());
		logger.info("p1.toString() = {}", p1.toString());
		//		logger.info("sb.toString() = {}", sb.toString());
		//		return sb.toString();
		return p1.toString();
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
