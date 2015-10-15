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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.NotBlank;

import com.qfree.obo.report.dto.ParameterGroupResource;
import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "parameter_group" database table.
 * 
 * Specifies a "group" that a report parameter can be a member of.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "parameter_group", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class ParameterGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//	@Column(name = "parameter_group_id", unique = true, nullable = false)
	//	private Long parameterGroupId;
	@NotNull
	@Type(type = "uuid-custom")
	//	@Type(groupType = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "parameter_group_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID parameterGroupId;

	@NotBlank
	@Column(name = "name", nullable = false, length = 80)
	private String name;

	@NotBlank
	@Column(name = "prompt_text", nullable = false, length = 132)
	private String promptText;

	/*
	 * Possible values for "groupType" are:
	 * 
	 *     IParameterDefnBase.SCALAR_PARAMETER = 0
	 *     IParameterDefnBase.FILTER_PARAMETER = 1
	 *     IParameterDefnBase.LIST_PARAMETER = 2
	 *     IParameterDefnBase.TABLE_PARAMETER = 3
	 *     IParameterDefnBase.PARAMETER_GROUP = 4
	 *     IParameterDefnBase.CASCADING_PARAMETER_GROUP = 5
	 * 
	 * Some of these values will never appear here since these constants
	 * are also used in other contexts. For example, see 
	 * scalarParameter.getParameterType() below.
	 */
	@NotNull
	@Column(name = "group_type", nullable = false)
	private Integer groupType;

	@OneToMany(targetEntity = ReportParameter.class, mappedBy = "parameterGroup")
	private List<ReportParameter> reportParameters;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private ParameterGroup() {
	}

	public ParameterGroup(String name, String promptText, Integer groupType) {
		this(name, promptText, groupType, DateUtils.nowUtc());
	}

	public ParameterGroup(ParameterGroupResource parameterGroupResource) {
		this(
				parameterGroupResource.getParameterGroupId(),
				parameterGroupResource.getName(),
				parameterGroupResource.getPromptText(),
				parameterGroupResource.getGroupType(),
				parameterGroupResource.getCreatedOn());
	}

	public ParameterGroup(String name, String promptText, Integer groupType, Date createdOn) {
		this.name = name;
		/*
		 * This defines a sensible value promptText where promptText is 
		 * null. Unfortunately, the "GroupPromptText" value seems to always 
		 * be null via the BIRT API for normal parameter groups (not cascading
		 * parameter groups).
		 */
		this.promptText = (promptText == null) ? name : promptText;
		this.groupType = groupType;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public ParameterGroup(UUID parameterGroupId, String name, String promptText, Integer groupType, Date createdOn) {
		this.parameterGroupId = parameterGroupId;
		this.name = name;
		/*
		 * This defines a sensible value promptText where promptText is 
		 * null. Unfortunately, the "GroupPromptText" value seems to always 
		 * be null via the BIRT API for normal parameter groups (not cascading
		 * parameter groups).
		 */
		this.promptText = (promptText == null) ? name : promptText;
		this.groupType = groupType;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getParameterGroupId() {
		return parameterGroupId;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGroupType() {
		return groupType;
	}

	public void setGroupType(Integer groupType) {
		this.groupType = groupType;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public List<ReportParameter> getReportParameters() {
		return reportParameters;
	}

	public void setReportParameters(List<ReportParameter> reportParameters) {
		this.reportParameters = reportParameters;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterGroup [parameterGroupId=");
		builder.append(parameterGroupId);
		builder.append(", promptText=");
		builder.append(promptText);
		builder.append(", name=");
		builder.append(name);
		builder.append(", groupType=");
		builder.append(groupType);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}
}
