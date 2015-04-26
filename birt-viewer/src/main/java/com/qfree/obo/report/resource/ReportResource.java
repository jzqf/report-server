package com.qfree.obo.report.resource;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Report;

@XmlRootElement
public class ReportResource extends AbstractResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportResource.class);

	@XmlElement
	private UUID reportId;

	//TODO Expand or not based on query parameter!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	//	@XmlElement
	//	private ReportCategoryResource reportCategoryResource;

	@XmlElement
	private String name;

	@XmlElement
	private Integer number;

	//	@XmlElement
	//	private List<ReportVersion> reportVersions;

	//	@XmlElement
	//	private List<RoleReport> roleReports;

	@XmlElement
	private boolean active;

	@XmlElement
	private Date createdOn;

	public ReportResource() {
	}

	public ReportResource(UriInfo uriInfo, Report report) {

		super(uriInfo, Report.class, report.getReportId());

		this.reportId = report.getReportId();
		//				this.reportCategoryResource = new ReportCategoryResource(uriInfo, report.getReportCategory());
		this.name = report.getName();
		this.number = report.getNumber();
		//		this.reportVersions = report.getReportVersions();
		//		this.roleReports = report.getRoleReports();
		this.active = report.isActive();
		this.createdOn = report.getCreatedOn();
		//		this(createHref(getFullyQualifiedContextPath(uriInfo), Report.class, report.getReportId()), report);
	}

	@Override
	public String toString() {
		return "ReportResource [href=" + href + "]";
	}

}
