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

		//	this.offset = RestUtils.paginationOffsetQueryParam(queryParams);
		//	this.limit = RestUtils.paginationLimitQueryParam(queryParams);
		//	if (this.offset != null && this.limit != null) {
		//
		//		/*
		//		 * Remember.
		//		 */
		//		List<String> currentPageOffset=queryParams.get(ResourcePath.PAGE_OFFSET_QP_KEY);
		//		
		//		// MUST OVERRIDE "OFFSET" & "LIMIT" or perhaps only "offset"?
		//		List<String> pageOffset = new ArrayList<>();
		//		Integer offset;
		//
		//		offset = 0;
		//		pageOffset.add(offset.toString());
		//		queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
		//		this.first = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
		//
		//		if (this.offset > 0) {
		//			offset = this.offset - this.limit;
		//			if (offset < 0) {
		//				offset = 0;
		//			}
		//			pageOffset.clear(); // reuse list
		//			pageOffset.add(offset.toString());
		//			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
		//			this.previous = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
		//		}
		//		
		//		//	private String next;
		//		
		//		//	offset = (items.size()/this.limit)*this.limit	NO THIS WILL NOT WORK BECUASE ITEMS IS ONLY ONE PAGE OF RESOURCES!
		//		//	pageOffset.clear(); // reuse list
		//		//	pageOffset.add(offset.toString());
		//		//	queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
		//		//	this.last = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
		//		
		//		/*
		//		 * Restore.
		//		 */
		//		queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY,currentPageOffset);
		//	}

	}

}
