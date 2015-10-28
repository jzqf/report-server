package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
//@XmlJavaTypeAdapter(value = UuidAdapter.class, type = UUID.class) <- doesn't work
public class ReportCategoryResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID reportCategoryId;

	@XmlElement
	private String abbreviation;

	@XmlElement
	private String description;

	//	@XmlElement
	//	private List<Report> reports;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public ReportCategoryResource() {
	}

	public ReportCategoryResource(ReportCategory reportCategory, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(ReportCategory.class, reportCategory.getReportCategoryId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(ReportCategory.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

			this.reportCategoryId = reportCategory.getReportCategoryId();
			this.abbreviation = reportCategory.getAbbreviation();
			this.description = reportCategory.getDescription();
			//		this.reports=reports;
			this.active = reportCategory.isActive();
			this.createdOn = reportCategory.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public static List<ReportCategoryResource> reportCategoryResourceListPageFromReportCategories(
			List<ReportCategory> reportCategories,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (reportCategories != null) {

			/*
			 * The ReportCategory has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * ReportCategory entities below. Either:
			 * 
			 *   1. Filter the list "reportCategories" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "reportCategories" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of ReportCategory entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "reportCategories"; 
			 * otherwise, we use the whole list.
			 */
			List<ReportCategory> pageOfReportCategories = RestUtils.getPageOfList(reportCategories, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<ReportCategoryResource> reportCategoryResources = new ArrayList<>(pageOfReportCategories.size());
			for (ReportCategory reportCategory : pageOfReportCategories) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				reportCategoryResources
						.add(new ReportCategoryResource(reportCategory, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return reportCategoryResources;
		} else {
			return null;
		}
	}

	public UUID getReportCategoryId() {
		return reportCategoryId;
	}

	public void setReportCategoryId(UUID reportCategoryId) {
		this.reportCategoryId = reportCategoryId;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportCategoryResource [reportCategoryId=");
		builder.append(reportCategoryId);
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
