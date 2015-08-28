package com.qfree.obo.report.apps;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

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

public class ParseReportParameters {

	private static final Logger logger = LoggerFactory.getLogger(ParseReportParameters.class);

	public static Map<String, Map<String, Serializable>> parseReportParams(String rptdesignXml)
			throws EngineException, IOException {

		/*
		 * A LinkedHashMap is used here so that the order of the parameters 
		 * insterted into this map is preserved, i.e., iteration over the 
		 * entries in this map will always preserve the order that the entries
		 * were originally inserted into the map. 
		 */
		Map<String, Map<String, Serializable>> paramDetails = new LinkedHashMap<>();

		IReportEngine engine = null;
		EngineConfig config = null;
		try {

			config = new EngineConfig();
			config.setLogConfig(null, Level.FINE);

			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);

		} catch (BirtException ex) {
			logger.error("Exception starting up BIRT platform for parsing parameters in a report:",
					ex);
		}

		logger.info("Report engine is initialized");

		IReportRunnable design = null;

		/*
		 * Open the report design.
		 */
		try (InputStream rptdesignStream = new ByteArrayInputStream(rptdesignXml.getBytes(StandardCharsets.UTF_8))) {
			design = engine.openReportDesign(rptdesignStream);
		}

		/*
		 * One can also open a design by passing an absolute path to an 
		 * rptdesign file as shown here. This code is commented out and only 
		 * used for testing.
		 */
		//		design = engine.openReportDesign(
		//				"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
		//design = engine.openReportDesign("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");

		/*
		 * Create an engine task for obtaining report parameter definitions.
		 */
		IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
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
				for (IScalarParameterDefn scalar : (ArrayList<IScalarParameterDefn>) group.getContents()) {
					logger.info("Scalar parameter '{}' from group '{}'", scalar.getName(), group.getName());
					/*
					 * Get details about the parameter as a Map and then insert
					 * that Map into the paramDetails Map
					 */
					paramDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, group));
				}

			} else {

				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
				logger.info("Scalar parameter '{}'", scalar.getName());
				/*
				 * Get details about the parameter as a Map and then insert
				 * that Map into the paramDetails Map
				 */
				paramDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, null));

			}
		}

		//Destroy the engine and shutdown the Platform
		//Note - If the program stays resident do not shutdown the Platform or the Engine  
		// engine.shutdown();	<- deprecated
		engine.destroy();
		Platform.shutdown();
		logger.info("Finished");

		return paramDetails;
	}

	/**
	 * Returns a {@link Map<String, Serializable>} containing details for each
	 * parameter of a report.
	 * 
	 * @param task
	 * @param scalarParameter
	 * @param report
	 * @param parameterGroup
	 * @return
	 */
	private static Map<String, Serializable> loadParameterDetails(IGetParameterDefinitionTask task,
			IScalarParameterDefn scalarParameter, IReportRunnable report, IParameterGroupDefn parameterGroup) {

		Map<String, Serializable> parameter = new HashMap<>();

		parameter.put("GroupName", parameterGroup == null ? null : parameterGroup.getName());
		parameter.put("GroupPromptText", parameterGroup == null ? null : parameterGroup.getPromptText());
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
		parameter.put("GroupParameterType", parameterGroup == null ? null : parameterGroup.getParameterType());

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
				scalarParameter.getDefaultValue(), scalarParameter.getDefaultValue().getClass().getSimpleName());
		parameter.put("PromptText", scalarParameter.getPromptText());
		/*
		 * The locale-specific help text. The locale used is the locale in the 
		 * getParameterDefinition task.
		 */
		parameter.put("HelpText", scalarParameter.getHelpText());
		/*
		 * There are the formatting instructions for the parameter value within 
		 * the parameter prompt UI. It does not influence the value passed to
		 * the report .
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
		parameter.put("Hidden", scalarParameter.isHidden());
		/*
		 * allowBlank() and allowNull() are deprecated, so they are commented 
		 * out here. When isRequired() returns true, then both of these will 
		 * return false. When isRequired() returns false, then both of these 
		 * will return true. Hence, allowBlank() and allowNull() are no longer 
		 * needed.
		 */
		//	parameter.put("AllowBlank (deprecated)", scalar.allowBlank());
		//	parameter.put("AllowNull (deprecated)", scalar.allowNull());
		parameter.put("Required", scalarParameter.isRequired());
		/*
		 * Possible values for "ParameterType" are:
		 * 
		 *     IParameterDefnBase.SCALAR_0
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
		parameter.put("TypeName", scalarParameter.getTypeName());
		/*
		 * "simple", "multi-value" or "ad-hoc"
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
		 * as a list. This is only a UI directve. The BIRT engine does not 
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
		 * This might specify the data type of items *displayed* in the 
		 * selection list. If so, possible values will be:
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
		 * 
		 * If this is the case, then this data type can be different that the
		 * data type for the parameter (scalarParameter.getDataType()) because
		 * selecting an item in the selection list will assign an associated
		 * item to the parameter which might be of different type than the 
		 * selected displayed value.
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
					List defaultValues = handle.getDefaultValueList();
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
				 * inserted into the the "parameter"object below which is of 
				 * type Map<String, Serializable>. A HashMap is Serializable,
				 * but a Map is not.
				 */
				HashMap<Object, String> dynamicList = new HashMap<>();

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
					 * inserted into the the "parameter"object below which is of 
					 * type Map<String, Serializable>. A HashMap is Serializable,
					 * but a Map is not.
					 */
					HashMap<Object, String> dynamicList = new HashMap<>();

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
		 *     IScalarParameterDefn scalar.
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			/*
			 * Load rptdesign file into a String.
			 */

			Path rptdesignPath = Paths
					.get("/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
			//Path rptdesignPath = Paths
			//		.get("/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/cascade.rptdesign");
			//Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");
			//Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.6.rptdesign");

			List<String> rptdesignLines = null;
			try {
				rptdesignLines = Files.readAllLines(rptdesignPath);// assumes UTF-8 encoding
			} catch (IOException e) {
				e.printStackTrace();
			}
			String rptdesignXml = String.join("\n", rptdesignLines);
			//logger.info("rptdesignXml = \n{}", rptdesignXml);

			Map<String, Map<String, Serializable>> paramDetails = parseReportParams(rptdesignXml);

			/*
			 * Log all details for each parameter extracted from the report
			 * design.
			 */
			for (Map.Entry<String, Map<String, Serializable>> paramDetailsEntry : paramDetails.entrySet()) {
				logger.info("Parameter = {}", paramDetailsEntry.getKey());
				Map<String, Serializable> parameter = paramDetailsEntry.getValue();
				for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
					String name = parameterEntry.getKey();
					//if (name.equals("SelectionList")) {
					//	HashMap<?, ?> selList = (HashMap<?, ?>) parameterEntry.getValue();
					//	for (Map.Entry<?, ?> selListEntry : selList.entrySet()) {
					//		logger.debug("  Selection List Entry ===== Key = {} Value = {}",
					//				selListEntry.getKey(), selListEntry.getValue());
					//	}
					//} else {
					logger.info("  {} = {}", name, parameterEntry.getValue());
					//}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}