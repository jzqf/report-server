package com.qfree.obo.report.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IParameterDefn;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ParameterGroupRepository;
import com.qfree.obo.report.db.ReportParameterRepository;
import com.qfree.obo.report.db.ReportVersionRepository;
import com.qfree.obo.report.db.SelectionListValueRepository;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.dto.AbstractBaseResource;
import com.qfree.obo.report.dto.ParameterGroupResource;
import com.qfree.obo.report.dto.ReportParameterResource;
import com.qfree.obo.report.dto.ReportVersionResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.dto.SelectionListValueCollectionResource;
import com.qfree.obo.report.dto.SelectionListValueResource;
import com.qfree.obo.report.exceptions.DynamicSelectionListKeyException;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;
import com.qfree.obo.report.rest.server.RestUtils;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@Component
@Transactional
public class ReportParameterService {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterService.class);

	//	private final ReportVersionRepository reportVersionRepository;
	//	private final ReportRepository reportRepository;
	private final ReportParameterRepository reportParameterRepository;
	private final ReportVersionRepository reportVersionRepository;
	private SelectionListValueRepository selectionListValueRepository;
	private final ParameterGroupRepository parameterGroupRepository;
	private final BirtService birtService;

	@Autowired
	public ReportParameterService(
			ReportParameterRepository reportParameterRepository,
			ReportVersionRepository reportVersionRepository,
			SelectionListValueRepository selectionListValueRepository,
			ParameterGroupRepository parameterGroupRepository,
			BirtService birtService) {
		this.reportParameterRepository = reportParameterRepository;
		this.reportVersionRepository = reportVersionRepository;
		this.selectionListValueRepository = selectionListValueRepository;
		this.parameterGroupRepository = parameterGroupRepository;
		this.birtService = birtService;
	}

	@Transactional
	public ReportParameter saveNewFromResource(ReportParameterResource reportParameterResource) {
		RestUtils.ifNewResourceIdNotNullThen403(reportParameterResource.getReportParameterId(), ReportParameter.class,
				"reportParameterId", reportParameterResource.getReportParameterId());
		return saveOrUpdateFromResource(reportParameterResource);
	}

	@Transactional
	public ReportParameter saveExistingFromResource(ReportParameterResource reportParameterResource) {
		return saveOrUpdateFromResource(reportParameterResource);
	}

	@Transactional
	public ReportParameter saveOrUpdateFromResource(ReportParameterResource reportParameterResource) {
		logger.debug("reportParameterResource = {}", reportParameterResource);

		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the ReportVersionResource from reportParameterResource 
		 * We assume here that the reportVersionId attribute of this object is
		 * set to the id of the ReportVersion that will we associated with the
		 * ReportParameter entity that is be saved/created below. It is not 
		 * necessary for any on the other ReportVersionResource attributes to 
		 * have non-null values.
		 * 
		 * If reportVersionId is not provided here, we throw a custom 
		 * exception. 
		 */
		ReportVersionResource reportVersionResource = reportParameterResource.getReportVersionResource();
		logger.debug("reportVersionResource = {}", reportVersionResource);
		UUID reportVersionId = null;
		if (reportVersionResource != null) {
			reportVersionId = reportVersionResource.getReportVersionId();
		}
		logger.debug("reportVersionId = {}", reportVersionId);
		ReportVersion reportVersion = null;
		if (reportVersionId != null) {
			reportVersion = reportVersionRepository.findOne(reportVersionId);
			RestUtils.ifNullThen404(reportVersion, ReportVersion.class, "reportVersionId",
					reportVersionId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_REPORTPARAMETER_REPORTVERSION_NULL, ReportParameter.class,
					"reportVersionId");
		}
		/*
		 * IMPORTANT:
		 * 
		 * Retrieve the ParameterGroupResource from reportParameterResource. If 
		 * the report parameter is not linked to a parameter group, then 
		 * ParameterGroupResource will be null).
		 * We assume here that the parameterGroupId attribute of this object is
		 * set to the id of the ParameterGroup that will we associated with the
		 * ReportParameter entity that is be saved/created below. It is not 
		 * necessary for any on the other ParameterGroupResource attributes to 
		 * have non-null values.
		 * 
		 * If parameterGroupId is not provided here, we throw a custom 
		 * exception. 
		 */
		ParameterGroupResource parameterGroupResource = reportParameterResource.getParameterGroupResource();
		logger.debug("parameterGroupResource = {}", parameterGroupResource);
		UUID parameterGroupId = null;
		if (parameterGroupResource != null) {
			parameterGroupId = parameterGroupResource.getParameterGroupId();
		}
		logger.debug("parameterGroupId = {}", parameterGroupId);
		ParameterGroup parameterGroup = null;
		if (parameterGroupId != null) {
			parameterGroup = parameterGroupRepository.findOne(parameterGroupId);
			RestUtils.ifNullThen404(parameterGroup, ParameterGroup.class, "parameterGroupId",
					parameterGroupId.toString());
		}

		ReportParameter reportParameter = new ReportParameter(reportParameterResource, reportVersion, parameterGroup);
		logger.debug("reportParameter = {}", reportParameter);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * ReportParameter.
		 */
		reportParameter = reportParameterRepository.save(reportParameter);
		logger.debug("reportParameter (created/updated) = {}", reportParameter);

		return reportParameter;
	}

	@Transactional
	public Map<String, Map<String, Serializable>> createParametersForReport(ReportVersion reportVersion)
			throws IOException, BirtException, RptdesignOpenFromStreamException {

		/*
		 * Extract all parameters and their metadata from the rptdesign.
		 */
		//Map<String, Map<String, Serializable>> parameters = ReportUtils.parseReportParams(reportVersion.getRptdesign());
		Map<String, Map<String, Serializable>> parameters = birtService.parseReportParams(reportVersion.getRptdesign());

		/*
		 * This is used to keep trakc of ParameterGroup entities that have been
		 * created here. A new ParameterGroup is created the first time a new
		 * group is encountered. For those parameters that below to a 
		 * ParameterGroup that has already been created, the id of that entity
		 * is extracted from this map (instead of creating another 
		 * ParameterGroup). The parameter group name is used as the key in this
		 * map because it will always be unique - it is not allowed to have two
		 * parameter groups in the same report with the same name.
		 */
		Map<String, UUID> parameterGroups = new HashMap<>();

		/*
		 * For each report parameter, create a ReportParameter entity which is
		 * stored in the report server database. "parameters" is a LinkedHashMap
		 * so this "for" loop will iterate through the parameters in the order
		 * that they are defined in the rptdesign file.
		 */
		Integer orderIndex = 0;
		for (Map.Entry<String, Map<String, Serializable>> parametersEntry : parameters.entrySet()) {
			logger.debug("Parameter = {}", parametersEntry.getKey());
			Map<String, Serializable> parameter = parametersEntry.getValue();
			//for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
			//	String parameterAttrName = parameterEntry.getKey();
			//	logger.info("  {} = {}", parameterAttrName, parameterEntry.getValue());
			//}

			orderIndex += 1;
			logger.info("parameter #{}. Name = {}", orderIndex, parameter.get("Name"));

			Boolean multivalued = Boolean.FALSE;
			if (parameter.get("ScalarParameterType") != null) {
				if (parameter.get("ScalarParameterType").equals(DesignChoiceConstants.SCALAR_PARAM_TYPE_SIMPLE)) {
					multivalued = false;
				} else if (parameter.get("ScalarParameterType")
						.equals(DesignChoiceConstants.SCALAR_PARAM_TYPE_MULTI_VALUE)) {
					multivalued = true;
					//} else if (parameter.get("ScalarParameterType").equals(DesignChoiceConstants.SCALAR_PARAM_TYPE_AD_HOC)) {
					//	// I hope this case does not occur because I don't know how to handle it.
				}
			}

			//			String promptText;
			//			if (parameter.get("PromptText") != null && ((String) parameter.get("PromptText")).isEmpty()) {
			//				promptText = (String) parameter.get("PromptText");
			//			} else {
			//				promptText = (String) parameter.get("Name") + ":";// sensible default value
			//			}

			/*
			 * This is the ParameterGroup to associate with the current 
			 * parameter being processed. A parameter is not necessarily 
			 * associated with a parameter group, so this can be null.
			 */
			ParameterGroup parameterGroup = null;
			//			String groupName = null;// need to be able to store SQL NULL if the parameter is not part of a group?
			//			String groupPromptText = null;// need to be able to store SQL NULL if the parameter is not part of a group?
			//			Integer groupParameterType = null;// need to be able to store SQL NULL if the parameter is not part of a group?
			if (parameter.get("GroupDetails") != null) {
				HashMap<String, Serializable> groupDetails = (HashMap<String, Serializable>) parameter
						.get("GroupDetails");
				/*
				 * If this is the first time the parameter group has been seen
				 * in this loop, a new ParameterGroup is created; otherwise,
				 * the UUID of an existing ParameterGroup that was created on an
				 * earlier trip through this loop is used.
				 */
				String parameterGroupKey = (String) groupDetails.get("GroupName");
				if (parameterGroups.containsKey(parameterGroupKey)) {
					/*
					 * Retrieve existing ParameterGroup entity from Map.
					 */
					parameterGroup = parameterGroupRepository.findOne(parameterGroups.get(parameterGroupKey));
					logger.debug("parameterGroup (retrieved from parameterGroups)={}", parameterGroup);
				} else {
					/*
					 * Create new ParameterGroup entity.
					 * 
					 * Unfortunately, the "GroupPromptText" value seems to 
					 * always be null via the BIRT API for normal parameter 
					 * groups (not cascading parameter groups), so it is treated
					 * specially here.
					 */
					String groupPromptText = groupDetails.get("GroupPromptText") != null
							? (String) groupDetails.get("GroupPromptText") : null;

					parameterGroup = new ParameterGroup(
							(String) groupDetails.get("GroupName"),
							groupPromptText,
							(Integer) groupDetails.get("GroupParameterType"));
					parameterGroup = parameterGroupRepository.save(parameterGroup);
					logger.debug("parameterGroup (created)={}", parameterGroup);
					/*
					 * Insert the id of the ParameterGroup just created into the
					 * map that is used to keep track of all ParameterGroup's
					 * that have been created. So that we only create *one*
					 * ParameterGroup per parameter group that we encounter.
					 */
					parameterGroups.put(parameterGroupKey, parameterGroup.getParameterGroupId());
				}
			}

			ReportParameter reportParameter = new ReportParameter(
					reportVersion,
					orderIndex,
					(Integer) parameter.get("DataType"),
					(Integer) parameter.get("ControlType"),
					(String) parameter.get("Name"),
					parameter.get("PromptText") != null ? (String) parameter.get("PromptText") : null,
					(Boolean) parameter.get("Required"),
					multivalued,
					parameter.get("DefaultValue") != null ? (String) parameter.get("DefaultValue") : null,
					parameter.get("DisplayName") != null ? (String) parameter.get("DisplayName") : null,
					parameter.get("HelpText") != null ? (String) parameter.get("HelpText") : null,
					parameter.get("DisplayFormat") != null ? (String) parameter.get("DisplayFormat") : null,
					(Integer) parameter.get("Alignment"),
					(Boolean) parameter.get("Hidden"),
					(Boolean) parameter.get("ValueConcealed"),
					(Boolean) parameter.get("AllowNewValues"),
					(Boolean) parameter.get("DisplayInFixedOrder"),
					(Integer) parameter.get("ParameterType"),
					(Integer) parameter.get("AutoSuggestThreshold"),
					(Integer) parameter.get("SelectionListType"),
					//parameter.get("TypeName") != null ? (String) parameter.get("TypeName") : null,
					parameter.get("ValueExpr") != null ? (String) parameter.get("ValueExpr") : null,
					parameterGroup);
			reportParameter = reportParameterRepository.save(reportParameter);

			/*
			 * If the ReportParameter just saved has a static selection list,
			 * we persist is data here.
			 */
			if (parameter.get("SelectionList") != null
					&& ((Integer) parameter.get("SelectionListType")).equals(IParameterDefn.SELECTION_LIST_STATIC)) {
				HashMap<Object, String> selectionList = (HashMap<Object, String>) parameter.get("SelectionList");
				Integer selectionListValueOrderIndex = 0;
				for (Map.Entry<Object, String> selectionListEntry : selectionList.entrySet()) {
					selectionListValueOrderIndex += 1;
					logger.info("selectionListEntry:  {}: {}", selectionListEntry.getKey(),
							selectionListEntry.getValue());
					SelectionListValue selectionListValue = new SelectionListValue(
							reportParameter, selectionListValueOrderIndex,
							selectionListEntry.getKey().toString(), selectionListEntry.getValue());
					selectionListValue = selectionListValueRepository.save(selectionListValue);
				}
			}
		}

		return parameters;
	}

	/**
	 * Returns a SelectionListValueCollectionResource representing the dynamic 
	 * selection list for a report parameter.
	 * 
	 * @param reportParameter
	 * @param parentParamValues
	 * @param rptdesign
	 * @param uriInfo
	 * @param queryParams
	 * @param apiVersion
	 * @return
	 * @throws BirtException 
	 * @throws RptdesignOpenFromStreamException 
	 * @throws DynamicSelectionListKeyException 
	 */
	public SelectionListValueCollectionResource getDynamicSelectionList(ReportParameter reportParameter,
			List<String> parentParamValues, String rptdesign, UriInfo uriInfo, Map<String, List<String>> queryParams,
			RestApiVersion apiVersion)
					throws RptdesignOpenFromStreamException, BirtException, DynamicSelectionListKeyException {

		Map<Object, String> dynamicList = birtService.getReportParameterDynamicSelectionList(
				reportParameter.getName(), parentParamValues, rptdesign);
		logger.debug("dynamicList = {}", dynamicList);

		/*
		 * Create a list of SelectionListValueResource's from the Map 
		 * dynamicList. This list is used to create a 
		 * SelectionListValueCollectionResource to be returned by this method.
		 */
		Integer orderIndex = 0;
		SelectionListValue selectionListValue = null;
		List<SelectionListValueResource> selectionListValueResources = new ArrayList<>(dynamicList.size());
		for (Entry<Object, String> entry : dynamicList.entrySet()) {
			orderIndex += 1;
			/*
			 * IMPORTANT: This SelectionListValue is not saved/persisted to the
			 *            report server database. As a result, it's id,
			 *            selectionListValueId will be null. This is the 
			 *            intended behaviour because these list values that will
			 *            be returned as a SelectionListValueCollectionResource
			 *            are never persisted in the report server database. The
			 *            only reason for creating a SelectionListValue here is 
			 *            to use it to construct a SelectionListValueResource.
			 */
			selectionListValue = new SelectionListValue(reportParameter, orderIndex,
					entry.getKey().toString(), entry.getValue());
			SelectionListValueResource selectionListValueResource = new SelectionListValueResource(selectionListValue,
					uriInfo, queryParams, apiVersion);
			/*
			 * selectionListValueResource will have a value for its "href" 
			 * attribute, but it will not be usable. So I set it to null here.
			 */
			logger.debug("selectionListValueResource.getHref() = {}", selectionListValueResource.getHref());
			selectionListValueResource.setHref(null);
			selectionListValueResources.add(selectionListValueResource);
		}
		logger.debug("selectionListValueResources = {}", selectionListValueResources);

		// SelectionListValueCollectionResource
		// selectionListValueCollectionResource = new
		// SelectionListValueCollectionResource(
		// selectionListValueResources,
		// SelectionListValue.class,
		// uriInfo, queryParams, apiVersion);
		SelectionListValueCollectionResource selectionListValueCollectionResource = new SelectionListValueCollectionResource(
				selectionListValueResources,
				SelectionListValue.class,
				AbstractBaseResource.createHref(uriInfo, ReportParameter.class, reportParameter.getReportParameterId(),
						null),
				ResourcePath.SELECTIONLISTVALUES_PATH,
				uriInfo, queryParams, apiVersion);

		/*
		 * selectionListValueCollectionResource will have a value for its "href" 
		 * attribute, but it will not be usable. So I set it to null here.
		 */
		// selectionListValueCollectionResource.setHref(null);

		return selectionListValueCollectionResource;
	}
}
