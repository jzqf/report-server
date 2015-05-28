package com.qfree.obo.report.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class ReportSyncResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportSyncResource.class);

	//	@XmlElement
	//	@XmlJavaTypeAdapter(UuidAdapter.class)
	//	private UUID reportSyncId;

	//	@XmlElement
	//	private String name;
	//
	//	@XmlElement
	//	private Integer number;

	@XmlElement
	private List<String> reportsDeleted;

	@XmlElement
	private List<String> reportsNotDeleted;

	@XmlElement
	private List<String> reportsCreated;

	@XmlElement
	private List<String> reportsNotCreated;

	@XmlElement
	private Boolean active;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date createdOn;

	public ReportSyncResource() {
	}

	public ReportSyncResource(
			List<String> reportsDeleted,
			List<String> reportsNotDeleted,
			List<String> reportsCreated,
			List<String> reportsNotCreated) {
		super();
		this.reportsDeleted = reportsDeleted;
		this.reportsNotDeleted = reportsNotDeleted;
		this.reportsCreated = reportsCreated;
		this.reportsNotCreated = reportsNotCreated;
	}

	//	public ReportSyncResource(ReportSync reportSync, UriInfo uriInfo, List<String> expand, 
	//			Map<String, List<String>> extraQueryParams, RestApiVersion apiVersion) {
	//
	//		super(ReportSync.class, reportSync.getReportSyncId(), uriInfo, expand, apiVersion);
	//
	//		String expandParam = ResourcePath.forEntity(ReportSync.class).getExpandParam();
	//		if (expand.contains(expandParam)) {
	//			/*
	//			 * Make a copy of the "expand" list from which expandParam is
	//			 * removed. This list should be used when creating new resources
	//			 * here, instead of the original "expand" list. This is done to 
	//			 * avoid the unlikely event of a long list of chained expansions
	//			 * across relations.
	//			 */
	//			List<String> expandElementRemoved = new ArrayList<>(expand);
	//			expandElementRemoved.remove(expandParam);
	//
	//			/*
	//			 * Clear apiVersion since its current value is not necessarily
	//			 * applicable to any resources associated with fields of this class. 
	//			 * See ReportResource for a more detailed explanation.
	//			 */
	//			apiVersion = null;
	//
	//			this.reportSyncId = reportSync.getReportSyncId();
	//			this. = reportSync.;
	//			this. = reportSync.;
	//			this.active = reportSync.isActive();
	//			this.createdOn = reportSync.getCreatedOn();
	//
	//			logger.info("reportSync = {}", reportSync);
	//		}
	//	}

	public List<String> getReportsDeleted() {
		return reportsDeleted;
	}

	public void setReportsDeleted(List<String> reportsDeleted) {
		this.reportsDeleted = reportsDeleted;
	}

	public List<String> getReportsNotDeleted() {
		return reportsNotDeleted;
	}

	public void setReportsNotDeleted(List<String> reportsNotDeleted) {
		this.reportsNotDeleted = reportsNotDeleted;
	}

	public List<String> getReportsCreated() {
		return reportsCreated;
	}

	public void setReportsCreated(List<String> reportsCreated) {
		this.reportsCreated = reportsCreated;
	}

	public List<String> getReportsNotCreated() {
		return reportsNotCreated;
	}

	public void setReportsNotCreated(List<String> reportsNotCreated) {
		this.reportsNotCreated = reportsNotCreated;
	}

	public Boolean getActive() {
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
		builder.append("ReportSyncResource [reportsDeleted=");
		builder.append(reportsDeleted);
		builder.append(", reportsNotDeleted=");
		builder.append(reportsNotDeleted);
		builder.append(", reportsCreated=");
		builder.append(reportsCreated);
		builder.append(", reportsNotCreated=");
		builder.append(reportsNotCreated);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
