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

	//	public ReportResource(String href, Report report) {
	//		//		public ReportResource(Report report) {
	//		this(
	//				href,
	//				report.getReportId(),
	//				new ReportCategoryResource(report.getReportCategory()),
	//				report.getName(),
	//				report.getNumber(),
	//				report.getReportVersions(),
	//				report.getRoleReports(),
	//				report.isActive(),
	//				report.getCreatedOn());
	//	}
	//
	//	public ReportResource(String href, UUID reportId, ReportCategoryResource reportCategoryResource, String name,
	//			Integer number,
	//			List<ReportVersion> reportVersions, List<RoleReport> roleReports, boolean active, Date createdOn) {
	//
	//		logger.info("href = {}", href);
	//		this.href = href;
	//
	//		logger.info("reportId = {}", reportId);
	//		this.reportId = reportId;
	//		//		this.reportCategoryResource = reportCategoryResource;
	//		this.name = name;
	//		this.number = number;
	//		//		this.reportVersions = reportVersions;
	//		//		this.roleReports = roleReports;
	//		this.active = active;
	//		this.createdOn = createdOn;
	//	}

	@Override
	public String toString() {
		return "ReportResource [href=" + href + "]";
	}

}
