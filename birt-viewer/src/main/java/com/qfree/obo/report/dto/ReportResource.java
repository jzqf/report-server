package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

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

	@XmlElement(name = "reportVersions")
	//	private List<ReportVersionResource> reportVersions;
	private ReportVersionCollectionResource reportVersions;

	//	@XmlElement
	//	private List<RoleReport> roleReports;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date createdOn;

	public ReportResource() {
	}

	public ReportResource(Report report, UriInfo uriInfo, List<String> expand, RestApiVersion apiVersion) {

		super(Report.class, report.getReportId(), uriInfo, expand, apiVersion);

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
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

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
					uriInfo, expandElementRemoved, apiVersion);
			this.name = report.getName();
			this.number = report.getNumber();
			this.active = report.isActive();
			this.createdOn = report.getCreatedOn();

			if (report.getReportVersions() != null) {

				List<ReportVersion> reportVersions = report.getReportVersions();
				List<ReportVersionResource> reportVersionResources = new ArrayList<>(reportVersions.size());
				for (ReportVersion reportVersion : reportVersions) {
					/*
					 * TODO Add a query parameter to disable filtering on *active* for ReportVersion's?
					 * 		How about ...&nofilter=active or ... What if we want to see only
					 * 		active Report's but unfiltered ReportVersion's (active or not)?
					 */
					if (reportVersion.isActive() || RestUtils.FILTER_INACTIVE_RECORDS == false) {
						reportVersionResources.add(
								new ReportVersionResource(reportVersion, uriInfo, expandElementRemoved, apiVersion));
					}
				}
				//this.reportVersions = reportVersionResources;
				this.reportVersions = new ReportVersionCollectionResource(reportVersionResources, ReportVersion.class,
						uriInfo, expand, apiVersion);
			}

			//		this.roleReports = report.getRoleReports();
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
