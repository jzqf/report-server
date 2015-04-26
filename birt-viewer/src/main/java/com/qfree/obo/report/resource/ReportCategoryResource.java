package com.qfree.obo.report.resource;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ReportCategory;

@XmlRootElement
public class ReportCategoryResource extends AbstractResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportCategoryResource.class);

	//	@XmlElement
	//	private String href;

	@XmlElement
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
	private Date createdOn;

	public ReportCategoryResource() {
	}

	public ReportCategoryResource(ReportCategory reportCategory, UriInfo uriInfo, List<String> expand) {

		super(uriInfo, ReportCategory.class, reportCategory.getReportCategoryId());

		boolean expandHere = false;
		//TODO Do not hardwire "reportcategory" here.
		if (expand.contains("reportcategory")) {
			expandHere = true;
			expand.remove("reportcategory");
		}

		if (expandHere) {
			this.reportCategoryId = reportCategory.getReportCategoryId();
			this.abbreviation = reportCategory.getAbbreviation();
			this.description = reportCategory.getDescription();
			//		this.reports=reports;
			this.active = reportCategory.isActive();
			this.createdOn = reportCategory.getCreatedOn();
		}
	}

	@Override
	public String toString() {
		return "ReportCategoryResource [href=" + href + "]";
	}

}
