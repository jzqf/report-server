package com.qfree.obo.report.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.annotation.PreDestroy;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IParameterGroupDefn;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.model.api.CascadingParameterGroupHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.qfree.obo.report.exceptions.DynamicSelectionListKeyException;
import com.qfree.obo.report.exceptions.RptdesignOpenFromStreamException;

@Component
public class BirtService {

	private static final Logger logger = LoggerFactory.getLogger(BirtService.class);

	private IReportEngine engine = null;

	public synchronized IReportEngine getBirtReportEngine() throws BirtException {

		if (engine == null) {
			EngineConfig config = new EngineConfig();
			config.setLogConfig(null, Level.FINE);

			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);

			logger.info("The BIRT report engine is initialized");
		}

		return engine;
	}

	public synchronized void shutdownBirt() {
		/*
		 * Destroy the engine and shutdown the Platform.
		 */
		if (engine != null) {
			engine.destroy();
			Platform.shutdown();
			engine = null;

			logger.info("The BIRT report engine has been destroyed and the platform is shut down");
		}
	}

	@PreDestroy
	public void shutdown() {
		shutdownBirt();
	}

	public Map<String, Map<String, Serializable>> parseReportParams(String rptdesignXml)
			throws IOException, BirtException, RptdesignOpenFromStreamException {

		/*
		 * A LinkedHashMap is used here so that the order of the parameters 
		 * inserted into this map is preserved, i.e., iteration over the 
		 * entries in this map will always preserve the order that the entries
		 * were originally inserted into the map. 
		 */
		Map<String, Map<String, Serializable>> parameters = new LinkedHashMap<>();

		IReportEngine engine = getBirtReportEngine();
		IReportRunnable runnableReportDesign = openReportDesign(rptdesignXml, engine);

		/*
		 * One can also open a design by passing an absolute path to an 
		 * rptdesign file as shown here. This code is commented out and only 
		 * used for testing.
		 */
		//runnableReportDesign = engine.openReportDesign(
		//	"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
		//runnableReportDesign = engine.openReportDesign("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");

		/*
		 * Create an engine task for obtaining report parameter definitions.
		 */
		IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(runnableReportDesign);
		Collection<IParameterDefnBase> params = (Collection<IParameterDefnBase>) task.getParameterDefns(true);

		/*
		 * Iterate through all parameter entries in "params". These can be 
		 * either IScalarParameterDefn or IParameterGroupDefn objects.
		 */
		for (IParameterDefnBase param : params) {
			if (param instanceof IParameterGroupDefn) {

				IParameterGroupDefn group = (IParameterGroupDefn) param;

				/*
				 *  Iterate over each parameter in the parameter group.
				 */
				for (IScalarParameterDefn scalarParameter : (ArrayList<IScalarParameterDefn>) group.getContents()) {
					logger.info("Scalar parameter '{}' from group '{}'", scalarParameter.getName(),
							group.getName());
					/*
					 * Get details about the parameter as a Map and then insert
					 * that Map into the paramDetails Map
					 */
					parameters.put(scalarParameter.getName(),
							BirtService.loadParameterDetails(task, scalarParameter, runnableReportDesign, group));
				}

			} else {

				IScalarParameterDefn scalarParameter = (IScalarParameterDefn) param;
				logger.info("Scalar parameter '{}'", scalarParameter.getName());
				/*
				 * Get details about the parameter as a Map and then insert
				 * that Map into the paramDetails Map
				 */
				parameters.put(scalarParameter.getName(),
						BirtService.loadParameterDetails(task, scalarParameter, runnableReportDesign, null));

			}
		}

		return parameters;
	}

	public HashMap<Object, String> getReportParameterDynamicSelectionList(
			String parameterName, List<String> parentParamValues, String rptdesignXml)
					throws RptdesignOpenFromStreamException, BirtException, DynamicSelectionListKeyException {

		IReportEngine engine = getBirtReportEngine();
		IReportRunnable runnableReportDesign = openReportDesign(rptdesignXml, engine);

		/*
		 * One can also open a design by passing an absolute path to an 
		 * rptdesign file as shown here. This code is commented out and only 
		 * used for testing.
		 */
		//runnableReportDesign = engine.openReportDesign(
		//	"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
		//runnableReportDesign = engine.openReportDesign("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");

		/*
		 * Create an engine task for obtaining the report parameter definition
		 * for the specified report parameter.
		 */
		IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(runnableReportDesign);
		IParameterDefnBase parameterDefn = task.getParameterDefn(parameterName);

		IScalarParameterDefn scalarParameter = (IScalarParameterDefn) parameterDefn;
		logger.debug("scalarParameter.getName() = '{}'", scalarParameter.getName());

		/*
		 * Get the design element handle that the design engine creates when 
		 * opening the report. From this, we obtain a ScalarParameterHandle
		 * that corresponds to the current parameter being processed. This 
		 * is used to extract more information about the parameter
		 */
		ReportDesignHandle reportHandle = (ReportDesignHandle) runnableReportDesign.getDesignHandle();
		ScalarParameterHandle parameterHandle = (ScalarParameterHandle) reportHandle
				.findParameter(scalarParameter.getName());

		/*
		 * dynamicList must be a HashMap, not a Map, because it is 
		 * inserted into the the "parameter" object below which is of 
		 * type Map<String, Serializable>. A HashMap is Serializable,
		 * but a Map is not. We use a LinkedHashMap implementation so
		 * that the selection list entries inserted into this map retain
		 * the order that they are inserted.
		 */
		HashMap<Object, String> dynamicList = new LinkedHashMap<>();

		/*
		 * Get selection list. 
		 * 
		 * Text boxes cannot have selection lists, so we only try to get a 
		 * selection list if we are *not* dealing with a text box.
		 */
		if (scalarParameter.getControlType() != IScalarParameterDefn.TEXT_BOX) {
			Collection<IParameterSelectionChoice> selectionList = null;
			if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {

				/*
				 * This identifies the cascading parameter group.
				 */
				String parameterGroupName = parameterHandle.getContainer().getName();

				/*
				 * We use the BIRT API to set positionInSlot, which specifies 
				 * the position of the parameter in the group. If it is the 
				 * first parameter in the group this will be zero, if it is the 
				 * second parameter in the group this will be one, as so on. 
				 * This value is used to check that the correct number of key
				 * values has been passed to this method in dynamicListKeys. If
				 * the wrong number of keys have been provided, we throw an 
				 * exception because there is no way that we can continue.
				 */
				int positionInSlot = parameterHandle.getContainerSlotHandle().findPosn(parameterHandle);
				logger.info("Parameter name = '{}', positionInSlot = {}, parentParamValues.size() = {}",
						parameterName, positionInSlot, parentParamValues.size());
				if (positionInSlot == parentParamValues.size()) {
					/*
					 * Create groupKeyValues array.
					 * 
					 * This is an array of the String representation of the values 
					 * for each parameter that comes earlier in the cascading group.
					 * 
					 * The values in this array must be ordered in the same order as
					 * these parameters that come earlier in the group. For the 
					 * first parameter in the group, this array will have zero 
					 * elements, for the second parameter in the group, this array 
					 * will have one element, and so on.
					 */
					String[] groupKeyValues = new String[parentParamValues.size()];
					groupKeyValues = parentParamValues.toArray(groupKeyValues);

					/*
					 * Get the selection list for the parameter, which is a 
					 * member of the cascading parameter group. The name of this
					 * group (not the parameter) is stored in parameterGroupName. 
					 */
					selectionList = (Collection<IParameterSelectionChoice>) task
							.getSelectionListForCascadingGroup(parameterGroupName, groupKeyValues);
					logger.info("Dynamic selection list for cascading parameter:");

				} else {
					throw new DynamicSelectionListKeyException(Integer.toString(parentParamValues.size())
							+ " keys passed but " + Integer.toString(positionInSlot) + " expected");
				}

			} else {
				//TODO Test this case! Add a parameter with a dynamic selection list to cascade.rptdesign and then upload it to the report server.
				/*
				 * Get selection list for a non-cascading parameter.
				 */
				selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionList(scalarParameter.getName());
				logger.info("Dynamic selection list for NON-cascading parameter:");
			}

			if (selectionList != null) {
				for (IParameterSelectionChoice selectionItem : selectionList) {
					Object value = selectionItem.getValue();
					Object label = selectionItem.getLabel();
					dynamicList.put(value, (String) label);
				}
			}

		}

		return dynamicList;
	}

	/**
	 * Opens a report design. 
	 * 
	 * The XML content of an rptdesign file is passed as a String, along with
	 * a report engine.
	 * 
	 * @param rptdesignXml
	 * @param engine
	 * @return
	 * @throws RptdesignOpenFromStreamException
	 */
	private IReportRunnable openReportDesign(String rptdesignXml, IReportEngine engine)
			throws RptdesignOpenFromStreamException {
		IReportRunnable runnableReportDesign = null;
		try (InputStream rptdesignStream = new ByteArrayInputStream(
				rptdesignXml.getBytes(StandardCharsets.UTF_8))) {
			runnableReportDesign = engine.openReportDesign(rptdesignStream);
		} catch (EngineException | IOException e) {
			throw new RptdesignOpenFromStreamException("rptdesign = " + rptdesignXml, e);
		}
		return runnableReportDesign;
	}

	/**
	 * Returns a {@link Map<String, Serializable>} containing attributes for 
	 * each parameter of a report.
	 * 
	 * @param task
	 * @param scalarParameter
	 * @param report
	 * @param parameterGroup
	 * @param groupCounter
	 * @return
	 */
	//private static Map<String, Serializable> loadParameterDetails(
	private static Map<String, Serializable> loadParameterDetails(
			IGetParameterDefinitionTask task,
			IScalarParameterDefn scalarParameter,
			IReportRunnable report,
			IParameterGroupDefn parameterGroup) {
	
		Map<String, Serializable> parameter = new HashMap<>();
	
		/*
		 * If the parameter is a member of a group (normal parameter group or a
		 * cascading parameter group), we insert here into "parameter" a HashMap 
		 * that contains details about the group; otherwise, we insert only 
		 * "null" to signal that this parameter is not a member of a group.
		 */
		if (parameterGroup == null) {
			parameter.put("GroupDetails", null);
		} else {
			/*
			 * groupDetails must be a HashMap, not a Map, because it is 
			 * inserted into the the "parameter" object below which is of 
			 * type Map<String, Serializable>. A HashMap is Serializable,
			 * but a Map is not.
			 */
			HashMap<String, Serializable> groupDetails = new HashMap<>();
			parameter.put("GroupDetails", groupDetails);
			/*
			 * Parameters that are members of the same group will have the same
			 * value for their "GroupName".
			 */
			groupDetails.put("GroupName", parameterGroup.getName());
			/*
			 * The "GroupPromptText" will be null for parameters that are not 
			 * members of a group. It also seems to be null for members of a normal
			 * parameter group (disappointingly). But it does seem to be properly
			 * set for members of *cascading" parameter groups. In this case, the
			 * same value of this prompt text will be defined for each member of the
			 * group.
			 */
			groupDetails.put("GroupPromptText", parameterGroup.getPromptText());
			/*
			 * Possible values for "GroupParameterType" are:
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
			groupDetails.put("GroupParameterType", new Integer(parameterGroup.getParameterType()));
		}
	
		parameter.put("Name", scalarParameter.getName());
		/*
		 * The locale-specific display name for the parameter. The locale used 
		 * is the locale in the getParameterDefinition task.
		 */
		parameter.put("DisplayName", scalarParameter.getDisplayName());
		/*
		 * Possible values for "ControlType" are:
		 * 
		 *     IScalarParameterDefn.TEXT_BOX     = 0  (default)
		 *     IScalarParameterDefn.LIST_BOX     = 1
		 *     IScalarParameterDefn.RADIO_BUTTON = 2
		 *     IScalarParameterDefn.CHECK_BOX    = 3
		 */
		parameter.put("ControlType", scalarParameter.getControlType());
		/*
		 * Possible values for "DataType" are:
		 * 
		 *     IParameterDefn.TYPE_ANY       = 0
		 *     IParameterDefn.TYPE_STRING    = 1
		 *     IParameterDefn.TYPE_FLOAT     = 2
		 *     IParameterDefn.TYPE_DECIMAL   = 3
		 *     IParameterDefn.TYPE_DATE_TIME = 4
		 *     IParameterDefn.TYPE_BOOLEAN   = 5
		 *     IParameterDefn.TYPE_INTEGER   = 6
		 *     IParameterDefn.TYPE_DATE      = 7
		 *     IParameterDefn.TYPE_TIME      = 8
		 */
		parameter.put("DataType", scalarParameter.getDataType());
		parameter.put("DefaultValue", scalarParameter.getDefaultValue());
		logger.debug("scalarParameter.getDefaultValue() = {}, class = {}",
				scalarParameter.getDefaultValue(),
				scalarParameter.getDefaultValue() != null ? scalarParameter.getDefaultValue().getClass().getSimpleName()
						: null);
		parameter.put("PromptText", scalarParameter.getPromptText());
		/*
		 * The locale-specific help text. The locale used is the locale in the 
		 * getParameterDefinition task.
		 */
		parameter.put("HelpText", scalarParameter.getHelpText());
		/*
		 * These are the formatting instructions for the parameter value within 
		 * the parameter prompt UI. It does not influence the value passed to
		 * the report.
		 */
		parameter.put("DisplayFormat", scalarParameter.getDisplayFormat());
		/*
		 * Possible values for "Alignment" are:
		 * 
		 *     IScalarParameterDefn.AUTO   = 0  (default)
		 *     IScalarParameterDefn.LEFT   = 1
		 *     IScalarParameterDefn.CENTER = 2
		 *     IScalarParameterDefn.RIGHT  = 3
		 */
		parameter.put("Alignment", scalarParameter.getAlignment());
		/*
		 * Specifies whether the parameter is a hidden parameter.
		 */
		parameter.put("Hidden", scalarParameter.isHidden());
		/*
		 * allowBlank() and allowNull() are deprecated, so they are commented 
		 * out here. When isRequired() returns true, then both of these will 
		 * return false. When isRequired() returns false, then both of these 
		 * will return true. Hence, allowBlank() and allowNull() are no longer 
		 * needed.
		 */
		//	parameter.put("AllowBlank (deprecated)", scalarParameter.allowBlank());
		//	parameter.put("AllowNull (deprecated)", scalarParameter.allowNull());
		parameter.put("Required", scalarParameter.isRequired());
		/*
		 * Possible values for "ParameterType" are:
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
		 * parameterGroup.getParameterType() above.
		 */
		parameter.put("ParameterType", scalarParameter.getParameterType());
		/*
		 * Possible values for "TypeName" are:
		 * 
		 *     "scalar", ... 
		 */
		parameter.put("TypeName", scalarParameter.getTypeName());
		/*
		 * Specifies the parameter type for this scalar parameter. The same data
		 * is available also below from parameterHandle.getParamType(), but it 
		 * is commented out there. Possible values are:
		 * 
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_SIMPLE = "simple"
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_MULTI_VALUE = "multi-value"
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_AD_HOC = "ad-hoc"
		 */
		parameter.put("ScalarParameterType", scalarParameter.getScalarParameterType());
		/*
		 * isValueConcealed() should be true for passwords, possibly for  bank 
		 * account numbers, ...
		 */
		parameter.put("ValueConcealed", scalarParameter.isValueConcealed());
		/*
		 * Applies only to parameters with a selection list. Specifies whether 
		 * the user can enter a value different from values in a selection list. 
		 * Usually, a parameter with allowNewValue=true is displayed as a 
		 * combo-box, while a parameter with allowNewValue=false is displayed 
		 * as a list. This is only a UI directive. The BIRT engine does not 
		 * validate whether the value passed in is in the list.
		 */
		parameter.put("AllowNewValues", scalarParameter.allowNewValues());
		/*
		 * Specifies whether the UI should display the selection list in a fixed
		 * order. Only applies to parameters with a selection list.
		 */
		parameter.put("DisplayInFixedOrder", scalarParameter.displayInFixedOrder());
		/*
		 * The number of values that a picklist could have. Not clear what this 
		 * could be used for.
		 */
		parameter.put("AutoSuggestThreshold", scalarParameter.getAutoSuggestThreshold());
		/*
		 * Specifies the type of the parameter selection list.		 * 
		 *     IParameterDefn.SELECTION_LIST_NONE    = 0
		 *     IParameterDefn.SELECTION_LIST_DYNAMIC = 1
		 *     IParameterDefn.SELECTION_LIST_STATIC  = 2
		 */
		parameter.put("SelectionListType", scalarParameter.getSelectionListType());
	
		/*
		 * Get the design element handle that the design engine creates when 
		 * opening the report. From this, we obtain a ScalarParameterHandle
		 * that corresponds to the current parameter being processed. This 
		 * is used to extract more information about the parameter
		 */
		ReportDesignHandle reportHandle = (ReportDesignHandle) report.getDesignHandle();
		ScalarParameterHandle parameterHandle = (ScalarParameterHandle) reportHandle.findParameter(scalarParameter.getName());
	
		/*
		 * Specifies the parameter type for this scalar parameter. The same data
		 * is available also above from 
		 * scalarParameter.getScalarParameterType(). Possible values are:
		 * 
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_SIMPLE = "simple"
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_MULTI_VALUE = "multi-value"
		 *     DesignChoiceConstants.SCALAR_PARAM_TYPE_AD_HOC = "ad-hoc"
		 */
		//parameter.put("ScalarParameterType", parameterHandle.getParamType());
	
		/*
		 * This is an expression on the data row from the dynamic list data set 
		 * that returns the value for the choice.
		 */
		parameter.put("ValueExpr", parameterHandle.getValueExpr());
	
		/*
		 * Get selection list, if any. 
		 * 
		 * Text boxes cannot have selection lists, so we only try to get a 
		 * selection list if we are *not* dealing with a text box.
		 */
		if (scalarParameter.getControlType() != IScalarParameterDefn.TEXT_BOX) {
			if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {
	
				/*
				 * Get selection list for a cascading parameter. We use the
				 * default values for each of the lists that are earlier in
				 * the cascade group.
				 */
				int positionInSlot = parameterHandle.getContainerSlotHandle().findPosn(parameterHandle);
				logger.debug("Parameter '{}': positionInSlot = {}", parameterHandle.getName(), positionInSlot);
				Object[] groupKeyValues = new Object[positionInSlot];
				for (int i = 0; i < positionInSlot; i++) {
					/*
					 * Place the default value for the parameter associated with
					 * index "i" in the array "groupKeyValues".
					 */
					ScalarParameterHandle handle = (ScalarParameterHandle) ((CascadingParameterGroupHandle) parameterHandle
							.getContainer()).getParameters().get(i);
					/*
					 * ScalarParameterHandle.getDefaultValue()
					 * is deprecated. Since version 2.5 it has been replaced by:
					 *     org.eclipse.birt.report.model.api.AbstractScalarParameterHandleImpl.getDefaultValueList()
					 * which returns a List. We use the first value in the list
					 * as the default value here.
					 */
					//groupKeyValues[i] = handle.getDefaultValue();//<-- deprecated
					List<Object> defaultValues = handle.getDefaultValueList();
					/*
					 * the ".toString()" here is necessary:
					 */
					groupKeyValues[i] = defaultValues == null ? "" : defaultValues.get(0).toString();
	
					logger.debug("i = {}, handle.getDefaultValueList().get(0) = {}, class = {}", i,
							handle.getDefaultValueList().get(0),
							handle.getDefaultValueList().get(0).getClass().getSimpleName());
					logger.debug("i = {}, handle.getDefaultValueList().get(0).toString() = {}, class = {}", i,
							handle.getDefaultValueList().get(0).toString(),
							handle.getDefaultValueList().get(0).toString().getClass().getSimpleName());
					logger.debug("groupKeyValues[{}] = {}, class = {}", i,
							groupKeyValues[i], groupKeyValues[i].getClass().getSimpleName());
				}
				String parameterGroupName = parameterHandle.getContainer().getName();
				//task.evaluateQuery(groupName); <-- Deprecated. Apparently, it has no use.
	
				/*
				 * getSelectionListForCascadingGroup(parameterGroupName, groupKeyValues)
				 * returns the selection list for the current parameter being
				 * processed, which is a member of a cascading parameter group
				 * of lists, i.e., it is one of the lists in the group of lists
				 * that make up the cascading group of lists. 
				 * 
				 * The first argument here, parameterGroupName, identifies the
				 * cascading parameter group of lists.
				 * 
				 * The second argument here, groupKeyValues, is an array of
				 * values that has two purposes:
				 *   1. Each value in the array provides the value selected 
				 *      for from each selection list from the cascade group
				 *      that comes earlier than the parameter being processed
				 *      here. The first value (index 0) is for the first list
				 *      in the group, etc.
				 *   2. Indirectly, it identifies the current parameter being
				 *      processed because the the selection list returned will
				 *      be for the next list in the cascade group, i.e.,
				 *      number positionInSlot (using a zero-based index).
				 */
				Collection<IParameterSelectionChoice> selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionListForCascadingGroup(parameterGroupName, groupKeyValues);
				/*
				 * dynamicList must be a HashMap, not a Map, because it is 
				 * inserted into the the "parameter" object below which is of 
				 * type Map<String, Serializable>. A HashMap is Serializable,
				 * but a Map is not. We use a LinkedHashMap implementation so
				 * that the selection list entries inserted into this map retain
				 * the order that they are inserted.
				 */
				HashMap<Object, String> dynamicList = new LinkedHashMap<>();
	
				for (IParameterSelectionChoice sI : selectionList) {
					Object value = sI.getValue();
					Object label = sI.getLabel();
					dynamicList.put(value, (String) label);
				}
				parameter.put("SelectionList", dynamicList);
	
			} else {
	
				/*
				 * Get selection list for a non-cascading parameter.
				 */
				Collection<IParameterSelectionChoice> selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionList(scalarParameter.getName());
	
				if (selectionList != null) {
					/*
					 * dynamicList must be a HashMap, not a Map, because it is 
					 * inserted into the the "parameter" object below which is of 
					 * type Map<String, Serializable>. A HashMap is Serializable,
					 * but a Map is not. We use a LinkedHashMap implementation so
					 * that the selection list entries inserted into this map retain
					 * the order that they are inserted.
					 */
					HashMap<Object, String> dynamicList = new LinkedHashMap<>();
	
					for (IParameterSelectionChoice selectionItem : selectionList) {
						Object value = selectionItem.getValue();
						String label = selectionItem.getLabel();
						dynamicList.put(value, label);
					}
					parameter.put("SelectionList", dynamicList);
				}
			}
		} else {
			parameter.put("SelectionList", null);
		}
	
		/*
		 * Log all details for the report parameter associated with with the
		 * object:
		 *     IScalarParameterDefn scalarParameter.
		 */
		logger.debug("Parameter = {}", scalarParameter.getName());
		for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
			String name = parameterEntry.getKey();
			//if (name.equals("SelectionList")) {
			//	HashMap<?, ?> selList = (HashMap<?, ?>) parameterEntry.getValue();
			//	for (Map.Entry<?, ?> selListEntry : selList.entrySet()) {
			//		logger.info("  Selection List Entry ===== Key = {} Value = {}",
			//				selListEntry.getKey(), selListEntry.getValue());
			//	}
			//} else {
			logger.debug("  {} = {}", name, parameterEntry.getValue());
			//}
		}
	
		return parameter;
	}

}
