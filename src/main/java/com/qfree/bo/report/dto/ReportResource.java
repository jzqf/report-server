package com.qfree.bo.report.dto;

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

import com.qfree.bo.report.domain.Report;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

@XmlRootElement
public class ReportResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID reportId;

	@XmlElement(name = "reportCategory")
	private ReportCategoryResource reportCategoryResource;

	@XmlElement
	private String name;

	@XmlElement
	private Integer number;

	@XmlElement
	private Integer sortOrder;

	@XmlElement(name = "reportVersions")
	//	private List<ReportVersionResource> reportVersions;
	private ReportVersionCollectionResource reportVersions;

	//	@XmlElement
	//	private List<RoleReport> roleReports;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public ReportResource() {
	}

	public ReportResource(Report report, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(Report.class, report.getReportId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(Report.class).getExpandParam();
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
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			this.reportId = report.getReportId();
			this.reportCategoryResource = new ReportCategoryResource(report.getReportCategory(),
					uriInfo, newQueryParams, apiVersion);
			this.name = report.getName();
			this.number = report.getNumber();
			this.sortOrder = report.getSortOrder();
			this.active = report.isActive();
			this.createdOn = report.getCreatedOn();

			logger.debug("report = {}", report);
			logger.debug("report.getReportVersions() = {}", report.getReportVersions());

			this.reportVersions = new ReportVersionCollectionResource(report, uriInfo, newQueryParams, apiVersion);

			//		this.roleReports = report.getRoleReports();
		}
	}

	public static List<ReportResource> reportResourceListPageFromReports(List<Report> reports, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		if (reports != null) {

			/*
			 * The Report has an "active" field. In order to return REST 
			 * resources that correspond to only active entities, it is 
			 * necessary to do one of two things *before* we extract a page of 
			 * Report entities below. Either:
			 * 
			 *   1. Filter the list "reports" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "reports" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of Report entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "reports"; 
			 * otherwise, we use the whole list.
			 */
			List<Report> pageOfReports = RestUtils.getPageOfList(reports, queryParams);

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

			List<ReportResource> reportResources = new ArrayList<>(pageOfReports.size());
			for (Report report : pageOfReports) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				reportResources.add(new ReportResource(report, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return reportResources;
		} else {
			return null;
		}
	}

	public UUID getReportId() {
		return reportId;
	}

	public void setReportId(UUID reportId) {
		this.reportId = reportId;
	}

	public ReportCategoryResource getReportCategoryResource() {
		return reportCategoryResource;
	}

	public void setReportCategoryResource(ReportCategoryResource reportCategoryResource) {
		this.reportCategoryResource = reportCategoryResource;
	}

	//	public List<ReportVersionResource> getReportVersions() {
	//		return reportVersions;
	//	}
	//
	//	public void setReportVersions(List<ReportVersionResource> reportVersions) {
	//		this.reportVersions = reportVersions;
	//	}

	public ReportVersionCollectionResource getReportVersions() {
		return reportVersions;
	}

	public void setReportVersions(ReportVersionCollectionResource reportVersions) {
		this.reportVersions = reportVersions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setNumber(Integer number) {
		this.number = number;
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
		builder.append("ReportResource [reportId=");
		builder.append(reportId);
		builder.append(", reportCategoryResource=");
		builder.append(reportCategoryResource);
		builder.append(", name=");
		builder.append(name);
		builder.append(", number=");
		builder.append(number);
		builder.append(", reportVersions=");
		builder.append(reportVersions);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append(", href=");
		builder.append(href);
		builder.append(", mediaType=");
		builder.append(mediaType);
		builder.append("]");
		return builder.toString();
	}
}
