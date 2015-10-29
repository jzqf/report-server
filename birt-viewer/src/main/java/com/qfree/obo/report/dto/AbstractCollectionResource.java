package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

/*
 * An "xsi:type" JSON attribute normally appears whenever an instance of a 
 * subclass is marshalled. This @XmlTransient annotation applied at the class 
 * level stops that element from being generated.
 */
@XmlTransient
public abstract class AbstractCollectionResource<T extends AbstractBaseResource, U> extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCollectionResource.class);

	//	@XmlElement
	//	protected List<T> items;

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
	 * resources that would be returned if there were no pagination.
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
			List<U> entities,
			Class<U> entityClass,
			String baseResourceUri,
			String collectionPath,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {
		super(baseResourceUri, collectionPath, entityClass, null, uriInfo, queryParams, apiVersion);

		//	List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);
		//	if (ResourcePath.expand(entityClass, expand)) {
		//		/*
		//		 * We pass null for apiVersion since the version used in the 
		//		 * original request does not necessarily apply here.
		//		 */
		//		apiVersion = null;
		//		this.items = (List<T>) JobResource.jobResourceListPageFromJobs((List<Job>) entities, uriInfo, queryParams,
		//				apiVersion);
		//		logger.info("this.items = {}", this.items);
		//	}

		this.offset = RestUtils.paginationOffsetQueryParam(queryParams);
		this.limit = RestUtils.paginationLimitQueryParam(queryParams);
		logger.info("this.offset, this.limit = {}, {}", this.offset, this.limit);
		if (this.offset != null && this.limit != null) {

			this.size = entities.size();

			/*
			 * Remember so I can restore below.
			 */
			List<String> currentPageOffset = queryParams.get(ResourcePath.PAGE_OFFSET_QP_KEY);

			List<String> pageOffset = new ArrayList<>();
			Integer offset;

			offset = 0;
			pageOffset.add(offset.toString());
			logger.info("pageOffset (first) = {}", pageOffset);
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
			logger.info("queryParams (first) = {}", queryParams);
			this.first = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);

			if (this.offset > 0 && this.offset < this.size + this.limit) {
				/*
				 * There is at least one *previous* page that can be requested.
				 */
				offset = this.offset - this.limit;
				if (offset < 0) {
					offset = 0;
				}
				pageOffset.clear(); // reuse list
				pageOffset.add(offset.toString());
				logger.info("pageOffset (previous) = {}", pageOffset);
				queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
				logger.info("queryParams (previous) = {}", queryParams);
				this.previous = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
			}

			if (this.offset < this.size - this.limit) {
				/*
				 * There is at least one more page that can be requested.
				 */
				offset = this.offset + this.limit;
				pageOffset.clear(); // reuse list
				pageOffset.add(offset.toString());
				logger.info("pageOffset (next) = {}", pageOffset);
				queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
				logger.info("queryParams (next) = {}", queryParams);
				this.next = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);
			}

			offset = (this.size / this.limit) * this.limit;
			if (offset.equals(this.size)) {
				/*
				 * If the size of the list of entities is an exact multiple
				 * of this.limit, then we reduce "offset" by this.limit; 
				 * otherwise, the "last" URI will have no items in it.
				 */
				offset -= this.limit;
			}
			if (offset < 0) {
				offset = 0;
			}
			pageOffset.clear(); // reuse list
			pageOffset.add(offset.toString());
			logger.info("pageOffset (last) = {}", pageOffset);
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, pageOffset); // replace current value
			logger.info("queryParams (last) = {}", queryParams);
			this.last = createHref(baseResourceUri, collectionPath, uriInfo, entityClass, null, queryParams);

			/*
			 * Restore.
			 */
			queryParams.put(ResourcePath.PAGE_OFFSET_QP_KEY, currentPageOffset);
		}

	}

	//	public List<T> getItems() {
	//		return items;
	//	}
	//
	//	public void setItems(List<T> items) {
	//		this.items = items;
	//	}

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
