package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ParameterGroupResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(ParameterGroupResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID parameterGroupId;

	@XmlElement
	private String name;

	@XmlElement
	private String promptText;

	@XmlElement(name = "groupType")
	private Integer groupType;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date createdOn;

	public ParameterGroupResource() {
	}

	/**
	 * Create new {@link ParameterGroupResource} instance from a 
	 * {@link ParameterGroup} instance.
	 * 
	 * @param parameterGroup
	 * @param uriInfo
	 * @param expand
	 * @param apiVersion
	 */
	public ParameterGroupResource(ParameterGroup parameterGroup, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		super(ParameterGroup.class, parameterGroup.getParameterGroupId(), uriInfo, queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(ParameterGroup.class).getExpandParam();
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
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

			this.parameterGroupId = parameterGroup.getParameterGroupId();
			this.name = parameterGroup.getName();
			this.promptText = parameterGroup.getPromptText();
			this.groupType = parameterGroup.getType();
			this.createdOn = parameterGroup.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public UUID getParameterGroupId() {
		return parameterGroupId;
	}

	public void setParameterGroupId(UUID parameterGroupId) {
		this.parameterGroupId = parameterGroupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPromptText() {
		return promptText;
	}

	public void setPromptText(String promptText) {
		this.promptText = promptText;
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

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterGroupResource [parameterGroupId=");
		builder.append(parameterGroupId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", promptText=");
		builder.append(promptText);
		builder.append(", groupType=");
		builder.append(groupType);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
