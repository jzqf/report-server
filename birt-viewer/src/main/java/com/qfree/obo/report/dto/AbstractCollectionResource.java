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
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {
		this(items, entityClass, null, null, null, uriInfo, expand, apiVersion);
	}

	public AbstractCollectionResource(List<T> items, Class<?> entityClass,
			String baseResourceUri, String collectionPath, Map<String, List<String>> extraQueryParams,
			UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {
		super(baseResourceUri, collectionPath, extraQueryParams, entityClass, null, uriInfo, expand, apiVersion);
		//		String expandParam = ResourcePath.forEntity(entityClass).getExpandParam();
		//		if (expand.contains(expandParam)) {
		//			this.items = items;
		//		}
	}

}
