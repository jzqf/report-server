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

import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

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
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date createdOn;

	public ReportCategoryResource() {
	}

	public ReportCategoryResource(ReportCategory reportCategory, UriInfo uriInfo, List<String> expand,
			RestApiVersion apiVersion) {

		super(ReportCategory.class, reportCategory.getReportCategoryId(), uriInfo, expand, apiVersion);
		logger.debug("After super(ReportCategory.class, ...  this = {}", this);

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
		builder.append(", href=");
		builder.append(href);
		builder.append("]");
		return builder.toString();
	}

	//	@Override
	//	public String toString() {
	//		return "ReportCategoryResource [href=" + href + "]";
	//	}

}
