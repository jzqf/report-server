package com.qfree.obo.report.resource;

import java.util.Date;
import java.util.List;
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
	@XmlElement(name = "reportCategory")
	private ReportCategoryResource reportCategoryResource;

	@XmlElement
	private String name;

	@XmlElement
	private Integer number;

	//	@XmlElement
	//	private List<ReportVersion> reportVersions;

	//	@XmlElement
	//	private List<RoleReport> roleReports;

	@XmlElement
	private Boolean active;

	@XmlElement
	private Date createdOn;

	public ReportResource() {
	}

	public ReportResource(Report report, UriInfo uriInfo, List<String> expand) {

		super(Report.class, report.getReportId(), uriInfo, expand);

		boolean expandHere = false;
		//TODO Do not hardwire "report" here.
		if (expand.contains("report")) {
			expandHere = true;
			expand.remove("report");
		}

		if (expandHere) {
			this.reportId = report.getReportId();
			this.reportCategoryResource = new ReportCategoryResource(report.getReportCategory(), uriInfo, expand);
			this.name = report.getName();
			this.number = report.getNumber();
			//		this.reportVersions = report.getReportVersions();
			//		this.roleReports = report.getRoleReports();
			this.active = report.isActive();
			this.createdOn = report.getCreatedOn();
		}
	}

	@Override
	public String toString() {
		return "ReportResource [href=" + href + "]";
	}

}
