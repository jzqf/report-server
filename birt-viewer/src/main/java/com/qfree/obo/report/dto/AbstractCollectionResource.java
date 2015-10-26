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

	/**
	 * Used for pagination to specify how many resources have been skipped to
	 * get to the current page.
	 */
	@XmlElement
	protected Integer offset;

	/**
	 * Used for pagination to specify the page size, i.e., the maximum number of
	 * resources to include in the "items" attribute.
	 */
	@XmlElement
	protected Integer limit;

	/**
	 * Total number of entities available in all pages, i,e., the number of
	 * resources that would be returned if there were no pagniation.
	 */
	@XmlElement
	protected Integer size;

	/**
	 * Used for pagination to specify the URI for the first page of resources.
	 */
	@XmlElement
	protected String first;

	/**
	 * Used for pagination to specify the URI for the previous page of
	 * resources.
	 */
	@XmlElement
	protected String previous;

	/**
	 * Used for pagination to specify the URI for the next page of resources.
	 */
	@XmlElement
	protected String next;

	/**
	 * Used for pagination to specify the URI for the last page of resources.
	 */
	@XmlElement
	protected String last;

	public AbstractCollectionResource() {
	}

	public AbstractCollectionResource(
			List<T> items, // <- This argument can be eliminated, since it is not used
			Class<?> entityClass,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		this(items, entityClass, null, null, uriInfo, queryParams, apiVersion);
	}

	public AbstractCollectionResource(
			List<T> items, // <- This argument can be eliminated, since it is not used
			Class<?> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		super(baseResourceUri, collectionPath, entityClass, null, uriInfo, queryParams, apiVersion);
		//	List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		//	if (ResourcePath.expand(entityClass, expand)) {
		//		this.items = items;
		//	}
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

}
