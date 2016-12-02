package com.qfree.bo.report.dto;

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

import com.qfree.bo.report.domain.ParameterGroup;
import com.qfree.bo.report.util.RestUtils;
import com.qfree.bo.report.util.RestUtils.RestApiVersion;

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

	@XmlElement
	private Integer groupType;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
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
	public ParameterGroupResource(
			ParameterGroup parameterGroup,
			UriInfo uriInfo,
			Map<String, List<String>> queryParams,
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
			this.groupType = parameterGroup.getGroupType();
			this.createdOn = parameterGroup.getCreatedOn();
		}
		logger.debug("this = {}", this);
	}

	public static List<ParameterGroupResource> parameterGroupResourceListPageFromParameterGroups(
			List<ParameterGroup> parameterGroups,
			UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion) {

		if (parameterGroups != null) {

			/*
			 * The Job entity does not have an "active" field, but if it did and
			 * if we wanted to return REST resources that correspond to only
			 * active entities, it would be necessary to do one of two things
			 * *before* we extract a page of Job entities below. Either:
			 * 
			 *   1. Filter the list "parameterGroups" here to eliminate inactive 
			 *      entities, or:
			 *   
			 *   2. Ensure that the list "parameterGroups" was passed to this 
			 *      method was *already* filtered to remove inactive entities.
			 */

			/*
			 * Create a List of ParameterGroup entities to return as REST 
			 * resources. If the "offset" & "limit" query parameters are 
			 * specified, we extract a sublist of the List "parameterGroups"; 
			 * otherwise, we use the whole list.
			 */
			List<ParameterGroup> pageOfParameterGroups = RestUtils.getPageOfList(parameterGroups, queryParams);

			/*
			 * Create a copy of the query parameters map and remove the
			 * pagination query parameters from it because they do not apply 
			 * to resources created from this point onwards from this method.
			 * If "queryParams" does not contain these pagination query 
			 * parameters, this will still work OK.
			 */
			Map<String, List<String>> queryParamsWOPagination = new HashMap<>(queryParams);
			queryParamsWOPagination.remove(ResourcePath.PAGE_OFFSET_QP_KEY);
			queryParamsWOPagination.remove(ResourcePath.PAGE_LIMIT_QP_KEY);

			List<ParameterGroupResource> parameterGroupResources = new ArrayList<>(pageOfParameterGroups.size());
			for (ParameterGroup parameterGroup : pageOfParameterGroups) {
				/*
				 * We cannot filter out entities here because then the page size
				 * will be variable. Instead, it is necessary to filter out
				 * entities *before* the page of entities is created above.
				 */
				parameterGroupResources.add(
						new ParameterGroupResource(parameterGroup, uriInfo, queryParamsWOPagination, apiVersion));
			}
			return parameterGroupResources;
		} else {
			return null;
		}
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
