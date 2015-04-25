package com.qfree.obo.report.resource;

import static com.qfree.obo.report.resource.AbstractResource.PATH_SEPARATOR;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.RoleReport;

@XmlRootElement
public class ReportResource {

	private static final Logger logger = LoggerFactory.getLogger(ReportResource.class);

	@XmlElement
	private String href;

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
		this(createHref(getFullyQualifiedContextPath(uriInfo), Report.class, report.getReportId()), report);
	}

	public ReportResource(String href, Report report) {
		this(
				href,
				report.getReportId(),
				new ReportCategoryResource(report.getReportCategory()),
				report.getName(),
				report.getNumber(),
				report.getReportVersions(),
				report.getRoleReports(),
				report.isActive(),
				report.getCreatedOn());
	}

	public ReportResource(String href, UUID reportId, ReportCategoryResource reportCategoryResource, String name,
			Integer number,
			List<ReportVersion> reportVersions, List<RoleReport> roleReports, boolean active, Date createdOn) {

		logger.info("href = {}", href);
		this.href = href;

		logger.info("reportId = {}", reportId);
		this.reportId = reportId;
		//		this.reportCategoryResource = reportCategoryResource;
		this.name = name;
		this.number = number;
		//		this.reportVersions = reportVersions;
		//		this.roleReports = roleReports;
		this.active = active;
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "ReportResource [href=" + href + "]";
	}

	//PLACE IN ABSTRACT SUPERCLASS???:
	//SUPERCLASS MUST ALSO HAVE AN "href" field, instead of explicitly declaring it at the top of this class???????????????
	//CAN WE THE SET THE "href"	CLASS IN THE CONSTRUCTOR OF THAT SUPERCLASS?
	//IF THIS WORKS, I MAY HAVE TO INCLUDE "Uuid" IN THE NAME OF THE CLASS SO THAT WE CAN ALSO HAVE ONE THAT WORKS WITH "Bigserial" ids????

	protected static String getFullyQualifiedContextPath(UriInfo info) {
		String fq = info.getBaseUri().toString();
		logger.info("fq = {}", fq);
		if (fq.endsWith("/")) {
			return fq.substring(0, fq.length() - 1);
		}
		return fq;
	}

	protected static String createHref(String fqBasePath, Class<?> entityClass, Object id) {
		StringBuilder sb = new StringBuilder(fqBasePath);
		ResourcePath resourcePath = ResourcePath.forEntity(entityClass);
		sb.append(resourcePath.getPath()).append(PATH_SEPARATOR).append(id);
		return sb.toString();
	}

}
