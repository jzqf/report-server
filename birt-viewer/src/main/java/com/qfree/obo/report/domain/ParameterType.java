package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "parameter_type" database table.
 * 
 * Specifies a basic type for a report parameter, e.g.,
 * 
 *     boolean
 *     date
 *     datetime
 *     decimal
 *     float
 *     integer
 *     string
 *     time
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "parameter_type", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ParameterType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "parameter_type_id", unique = true, nullable = false)
	//	private Long reportCategoryId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "parameter_type_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID parameterTypeId;

	@Column(name = "abbreviation", nullable = false, length = 32)
	private String abbreviation;

	@Column(name = "description", nullable = false, length = 32)
	private String description;

	@OneToMany(targetEntity = ReportParameter.class, mappedBy = "parameterType")
	private List<ReportParameter> reportParameters;

	@Column(name = "active", nullable = false)
	private Boolean active;

	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private ParameterType() {
	}

	public ParameterType(String description, String abbreviation) {
		this(description, abbreviation, true, DateUtils.nowUtc());
	}

	public ParameterType(String description, String abbreviation, Boolean active) {
		this(description, abbreviation, active, DateUtils.nowUtc());
	}

	public ParameterType(String description, String abbreviation, Boolean active, Date createdOn) {
		this.description = description;
		this.abbreviation = abbreviation;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getParameterTypeId() {
		return parameterTypeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(", abbreviation=");
		builder.append(abbreviation);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}
}
