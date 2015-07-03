package com.qfree.obo.report.dto;

import java.util.List;
import java.util.Map;

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
public abstract class AbstractCollectionResource<T extends AbstractBaseResource> extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCollectionResource.class);

	//	@XmlElement
	//	private List<RoleResource> items;

	@XmlElement
	private Integer offset;	// not used yet

	@XmlElement
	private Integer limit;	// not used yet

	//	@XmlElement
	//	private ? first;
	//
	//	@XmlElement
	//	private ? previous;
	//
	//	@XmlElement
	//	private ? next;
	//
	//	@XmlElement
	//	private ? last;

	public AbstractCollectionResource() {
	}

	public AbstractCollectionResource(List<T> items, Class<?> entityClass,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public AbstractCollectionResource(List<T> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		super(baseResourceUri, collectionPath, entityClass, null, uriInfo, queryParams, apiVersion);
		//	List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		//	if (ResourcePath.expand(entityClass, expand)) {
		//		this.items = items;
		//	}
	}

}
