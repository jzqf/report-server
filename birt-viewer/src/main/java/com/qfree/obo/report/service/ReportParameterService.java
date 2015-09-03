package com.qfree.obo.report.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.ParameterGroupRepository;
import com.qfree.obo.report.db.ReportParameterRepository;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.util.ReportUtils;

@Component
@Transactional
public class ReportParameterService {

	private static final Logger logger = LoggerFactory.getLogger(ReportParameterService.class);

	//	private static final UUID DATA_TYPE_BOOLEAN = UUID.fromString("bfa09b13-ad55-481e-8c29-b047dc5d7f3e");
	//	private static final UUID DATA_TYPE_DATE = UUID.fromString("12d3f4f8-468d-4faf-be3a-5c15eaba4eb6");
	//	private static final UUID DATA_TYPE_DATETIME = UUID.fromString("abce5a38-b1e9-42a3-9962-19227d51dd4a");
	//	private static final UUID DATA_TYPE_DECIMAL = UUID.fromString("f2bfa3f9-f446-49dd-ad0e-6a02b3af1023");
	//	private static final UUID DATA_TYPE_FLOAT = UUID.fromString("8b0bfc37-5fb4-4dea-87fc-3e2c3313af17");
	//	private static final UUID DATA_TYPE_INTEGER = UUID.fromString("807c64b1-a59b-465c-998b-a399984b5ef4");
	//	private static final UUID DATA_TYPE_STRING = UUID.fromString("9b0af697-8bc9-49e2-b8b6-136ced83dbd8");
	//	private static final UUID DATA_TYPE_TIME = UUID.fromString("da575eee-e5a3-4149-8ea3-1fd86015bbb9");
	//	private static final UUID DATA_TYPE_ANY = UUID.fromString("2bc62461-6ddb-4e86-b46d-080cd5e9cf83");
	//
	//	private static final UUID WIDGET_TEXTBOX = UUID.fromString("e5b4cebb-1852-41a1-9fdf-bb4b8da82ef9");
	//	private static final UUID WIDGET_LISTBOX = UUID.fromString("4d0842e4-ba65-4064-8ab1-556e90e3953b");
	//	private static final UUID WIDGET_RADIO_BUTTON = UUID.fromString("864c60a8-6c48-4efb-84dd-fc79502899fe");
	//	private static final UUID WIDGET_CHECKBOX = UUID.fromString("b8e91527-8b0e-4ed2-8cba-8cb8989ba8e2");

	//	private final ReportVersionRepository reportVersionRepository;
	//	private final ReportRepository reportRepository;
	private final ReportParameterRepository reportParameterRepository;
	private final ParameterGroupRepository parameterGroupRepository;

	@Autowired
	public ReportParameterService(
			//			ReportVersionRepository reportVersionRepository,
			//			ReportRepository reportRepository,
			ReportParameterRepository reportParameterRepository,
			ParameterGroupRepository parameterGroupRepository) {
		//		this.reportVersionRepository = reportVersionRepository;
		//		this.reportRepository = reportRepository;
		this.reportParameterRepository = reportParameterRepository;
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

			/*
			//	 * Select a ParameterType that matches the parameter data type,
			//	 * parameter.get("DataType"):
			//	 */
			//	UUID parameterDataTypeId = DATA_TYPE_ANY;
			//	if (parameter.get("DataType").equals(IParameterDefn.TYPE_BOOLEAN)) {
			//		parameterDataTypeId = DATA_TYPE_BOOLEAN;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_DATE)) {
			//		parameterDataTypeId = DATA_TYPE_DATE;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_DATE_TIME)) {
			//		parameterDataTypeId = DATA_TYPE_DATETIME;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_DECIMAL)) {
			//		parameterDataTypeId = DATA_TYPE_DECIMAL;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_FLOAT)) {
			//		parameterDataTypeId = DATA_TYPE_FLOAT;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_INTEGER)) {
			//		parameterDataTypeId = DATA_TYPE_INTEGER;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_STRING)) {
			//		parameterDataTypeId = DATA_TYPE_STRING;
			//	} else if (parameter.get("DataType").equals(IParameterDefn.TYPE_TIME)) {
			//		parameterDataTypeId = DATA_TYPE_TIME;
			//	} else {
			//		parameterDataTypeId = DATA_TYPE_ANY;
			//	}
			//	logger.debug("parameterDataTypeId = {}", parameterDataTypeId);
			//	ParameterType parameterDataType = parameterTypeRepository.findOne(parameterDataTypeId);
			//	logger.debug("parameterDataType = {}", parameterDataType);

			//	/*
			//	 * Select a WidgetId that matches the "control" type,
			//	 * parameter.get("ControlType"):
			//	 */
			//	UUID widgetId = WIDGET_TEXTBOX;
			//	if (parameter.get("ControlType").equals(IScalarParameterDefn.LIST_BOX)) {
			//		widgetId = WIDGET_LISTBOX;
			//	} else if (parameter.get("ControlType").equals(IScalarParameterDefn.RADIO_BUTTON)) {
			//		widgetId = WIDGET_RADIO_BUTTON;
			//	} else if (parameter.get("ControlType").equals(IScalarParameterDefn.CHECK_BOX)) {
			//		widgetId = WIDGET_CHECKBOX;
			//	} else {// includes case: parameter.get("ControlType").equals(IScalarParameterDefn.TEXT_BOX)
			//		widgetId = WIDGET_TEXTBOX;
			//	}
			//	logger.debug("widgetId = {}", widgetId);
			//	Widget widget = widgetRepository.findOne(widgetId);
			//	logger.debug("widget = {}", widget);

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

			String promptText;
			if (parameter.get("PromptText") != null && ((String) parameter.get("PromptText")).isEmpty()) {
				promptText = (String) parameter.get("PromptText");
			} else {
				promptText = (String) parameter.get("Name") + ":";// sensible default value
			}

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

			String displayName = parameter.get("DisplayName") != null ? (String) parameter.get("DisplayName") : null;
			String helpText = parameter.get("HelpText") != null ? (String) parameter.get("HelpText") : null;
			if ("".equals(helpText)) {
				/*
				 * If helpText is blank, we store null instead since this is
				 * a better way to determine if help text has been provided or 
				 * not.
				 */
				helpText = null;//TODO place this logic in the setter/constructor!!!!!
			}
			String defaultValue = parameter.get("DefaultValue") != null ? (String) parameter.get("DefaultValue") : null;
			String displayFormat = parameter.get("DisplayFormat") != null ? (String) parameter.get("DisplayFormat")
					: null;
			Integer alignment = (Integer) parameter.get("Alignment");
			Boolean hidden = (Boolean) parameter.get("Hidden");
			Boolean valueConcealed = (Boolean) parameter.get("ValueConcealed");
			Boolean allowNewValues = (Boolean) parameter.get("AllowNewValues");
			Boolean displayInFixedOrder = (Boolean) parameter.get("DisplayInFixedOrder");
			Integer parameterType = (Integer) parameter.get("ParameterType");
			Integer autoSuggestThreshold = (Integer) parameter.get("AutoSuggestThreshold");
			Integer selectionListType = (Integer) parameter.get("SelectionListType");
			String typeName = parameter.get("TypeName") != null ? (String) parameter.get("TypeName") : null;
			if ("".equals(typeName)) {
				/*
				 * If typeName is blank, we store null instead since this is
				 * a better way to determine if a value has been provided or 
				 * not.
				 */
				typeName = null;//TODO place this logic in the setter/constructor!!!!!
			}
			String valueExpr = parameter.get("ValueExpr") != null ? (String) parameter.get("ValueExpr") : null;
			if ("".equals(valueExpr)) {
				/*
				 * If valueExpr is blank, we store null instead since this is
				 * a better way to determine if a value has been provided or 
				 * not.
				 */
				valueExpr = null;//TODO place this logic in the setter/constructor!!!!!
			}
			//========================================================================================================================

			//TODO These values should be added as arguments to the ReportParameter constructor:

			//ReportParameter reportParameter = new ReportParameter(
			//		reportVersion,
			//		orderIndex,
			//		parameterDataType,
			//		widget,
			//		(String) parameter.get("Name"),
			//		promptText,
			//		parameter.get("Required") != null ? (Boolean) parameter.get("Required") : Boolean.TRUE,
			//		multivalued);
			ReportParameter reportParameter = new ReportParameter(
					reportVersion,
					orderIndex,
					(Integer) parameter.get("DataType"),
					(Integer) parameter.get("ControlType"),
					(String) parameter.get("Name"),
					promptText,
					parameter.get("Required") != null ? (Boolean) parameter.get("Required") : Boolean.TRUE,
					multivalued,
					parameterGroup);

			reportParameter = reportParameterRepository.save(reportParameter);
		}

		return parameters;
	}
}
