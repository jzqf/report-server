package com.qfree.obo.report.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import com.qfree.obo.report.db.SelectionListValueRepository;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.util.ReportUtils;

@Component
@Transactional
public class ReportParameterService {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterService.class);

	//	private final ReportVersionRepository reportVersionRepository;
	//	private final ReportRepository reportRepository;
	private final ReportParameterRepository reportParameterRepository;
	private SelectionListValueRepository selectionListValueRepository;
	private final ParameterGroupRepository parameterGroupRepository;

	@Autowired
	public ReportParameterService(
			//			ReportVersionRepository reportVersionRepository,
			//			ReportRepository reportRepository,
			ReportParameterRepository reportParameterRepository,
			SelectionListValueRepository selectionListValueRepository,
			ParameterGroupRepository parameterGroupRepository) {
		//		this.reportVersionRepository = reportVersionRepository;
		//		this.reportRepository = reportRepository;
		this.reportParameterRepository = reportParameterRepository;
		this.selectionListValueRepository = selectionListValueRepository;
		this.parameterGroupRepository = parameterGroupRepository;
	}

	@Transactional
	public Map<String, Map<String, Serializable>> createParametersForReport(ReportVersion reportVersion)
			throws IOException, BirtException {

		/*
		 * Extract all parameters and their metadata from the rptdesign.
		 */
		Map<String, Map<String, Serializable>> parameters = ReportUtils.parseReportParams(reportVersion.getRptdesign());

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
}