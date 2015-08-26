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

		Map<String, Map<String, Serializable>> paramDetails = new HashMap<>();

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

		//Create Parameter Definition Task and retrieve parameter definitions
		IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
		Collection<IParameterDefnBase> params = (Collection<IParameterDefnBase>) task.getParameterDefns(true);

		/*
		 * Iterate through all parameter entries in "params". These can be 
		 * either IScalarParameterDefn or IParameterGroupDefn objects.
		 */
		for (IParameterDefnBase param : params) {
			if (param instanceof IParameterGroupDefn) {

				IParameterGroupDefn group = (IParameterGroupDefn) param;
				logger.info("*** Parameter group: {} ***", group.getName());

				/*
				 *  Iterate over the parameter group contents.
				 */
				////for (Iterator i2 = group.getContents().iterator(); i2.hasNext();) {
				////	IScalarParameterDefn scalar = (IScalarParameterDefn) i2.next();
				//for (Iterator<IScalarParameterDefn> i2 = group.getContents().iterator(); i2.hasNext();) {
				//	IScalarParameterDefn scalar = i2.next();
				//	logger.info("\t{}", scalar.getName());
				//	//Get details about the parameter.
				//	paramDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, group));
				//}
				for (IScalarParameterDefn scalar : (ArrayList<IScalarParameterDefn>) group.getContents()) {
					logger.info("Group scalar parameter: {}", scalar.getName());
					//Get details about the parameter.
					paramDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, group));
				}

			} else {

				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
				logger.info("Scalar parameter: {}", scalar.getName());
				// Get details about the parameter.
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

	//Function to load parameter details in a map.
	private static HashMap<String, Serializable> loadParameterDetails(IGetParameterDefinitionTask task,
			IScalarParameterDefn scalar, IReportRunnable report, IParameterGroupDefn group) {

		HashMap<String, Serializable> parameter = new HashMap<>();

		parameter.put("Parameter Group", group == null ? "Default" : group.getName());
		parameter.put("Name", scalar.getName());
		parameter.put("Help Text", scalar.getHelpText());
		parameter.put("Display Name", scalar.getDisplayName());
		//this is a format code such as  > for UPPERCASE
		parameter.put("Display Format", scalar.getDisplayFormat());

		parameter.put("Hidden", scalar.isHidden() ? "Yes" : "No");
		parameter.put("Required", scalar.isRequired() ? "Yes" : "No");
		/*
		 * allowBlank() and allowNull() are deprecated, so they are commented 
		 * out here. When isRequired() returns true, then both of these will 
		 * return false. When isRequired() returns false, then both of these 
		 * will return true. Hence, allowBlank() and allowNull() are no longer 
		 * needed.
		 */
		//	parameter.put("Allow Blank (deprecated)", scalar.allowBlank() ? "Yes" : "No");
		//	parameter.put("Allow Null (deprecated)", scalar.allowNull() ? "Yes" : "No");
		parameter.put("Conceal Entry", scalar.isValueConcealed() ? "Yes" : "No");// e.g., for passwords, etc.

		/*
		 * These seem to always be null, even for cascading parameters, so there 
		 * is no point including them:
		 */
		//parameter.put("Prompt Text (scalar)", scalar.getPromptText());
		//if (group != null) {
		//	parameter.put("Help Text (group)", group.getHelpText());
		//	parameter.put("Display Name (group)", group.getDisplayName());
		//	parameter.put("Prompt Text (group)", group.getPromptText());
		//}

		switch (scalar.getControlType()) {
		case IScalarParameterDefn.TEXT_BOX:
			parameter.put("Type", "Text Box");
			break;
		case IScalarParameterDefn.LIST_BOX:
			parameter.put("Type", "List Box");
			break;
		case IScalarParameterDefn.RADIO_BUTTON:
			parameter.put("Type", "List Box");
			break;
		case IScalarParameterDefn.CHECK_BOX:
			parameter.put("Type", "List Box");
			break;
		default:
			parameter.put("Type", "Text Box");
			break;
		}

		switch (scalar.getDataType()) {
		case IScalarParameterDefn.TYPE_STRING:
			parameter.put("Data Type", "String");
			break;
		case IScalarParameterDefn.TYPE_FLOAT:
			parameter.put("Data Type", "Float");
			break;
		case IScalarParameterDefn.TYPE_DECIMAL:
			parameter.put("Data Type", "Decimal");
			break;
		case IScalarParameterDefn.TYPE_DATE_TIME:
			parameter.put("Data Type", "Date Time");
			break;
		case IScalarParameterDefn.TYPE_BOOLEAN:
			parameter.put("Data Type", "Boolean");
			break;
		default:
			parameter.put("Data Type", "Any");
			break;
		}

		//Get report design and find default value, prompt text and data set expression using the DE API
		ReportDesignHandle reportHandle = (ReportDesignHandle) report.getDesignHandle();
		ScalarParameterHandle parameterHandle = (ScalarParameterHandle) reportHandle.findParameter(scalar.getName());
		/*
		 * ScalarParameterHandle.getDefaultValue()
		 * is deprecated. Since version 2.5 it has been replaced by:
		 *     AbstractScalarParameterHandleImpl.getDefaultValueList()
		 * which returns a List, not a String. A List is not serializable and
		 * therefore cannot be put in "parameter".
		 */
		parameter.put("Default Value", parameterHandle.getDefaultValue());
		List defaultValues = parameterHandle.getDefaultValueList();
		if (defaultValues != null) {
			//List<String> defaultValues = parameterHandle.getDefaultValueList();
			for (int i = 0; i < defaultValues.size(); i++) {
				parameter.put("  *** Default Value " + i, defaultValues.get(i).toString());//TODO Should I enter null here if blank, or blank if null?
			}
		} else {
			parameter.put("  *** Default Value 0", "");//TODO Should I enter blank or null here?
		}
		parameter.put("Prompt Text", parameterHandle.getPromptText());
		parameter.put("Data Set Expression", parameterHandle.getValueExpr());

		/*
		 * Get selection list, if any. 
		 * 
		 * Text boxes cannot have selection lists, so we only try to get a 
		 * selection list if we are *not* dealing with a text box.
		 */
		if (scalar.getControlType() != IScalarParameterDefn.TEXT_BOX) {
			if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {

				/*
				 * Get selection list for a cascading parameter.
				 */
				int index = parameterHandle.getContainerSlotHandle().findPosn(parameterHandle);
				Object[] keyValue = new Object[index];
				for (int i = 0; i < index; i++) {
					ScalarParameterHandle handle = (ScalarParameterHandle) ((CascadingParameterGroupHandle) parameterHandle
							.getContainer()).getParameters().get(i);
					//Use parameter default values
					keyValue[i] = handle.getDefaultValue();
				}
				String groupName = parameterHandle.getContainer().getName();
				//task.evaluateQuery(groupName); <-- Deprecated. Apparently, it has no use.

				Collection<IParameterSelectionChoice> selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionListForCascadingGroup(groupName, keyValue);
				HashMap<Object, String> dynamicList = new HashMap<>();

				for (IParameterSelectionChoice sI : selectionList) {
					Object value = sI.getValue();
					Object label = sI.getLabel();
					//logger.info(label + "--{}", value);
					dynamicList.put(value, (String) label);
				}
				parameter.put("Selection List", dynamicList);

			} else {

				/*
				 * Get selection list for a non-cascading parameter.
				 */
				Collection<IParameterSelectionChoice> selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionList(scalar.getName());

				if (selectionList != null) {
					HashMap<Object, String> dynamicList = new HashMap<>();

					for (IParameterSelectionChoice selectionItem : selectionList) {
						Object value = selectionItem.getValue();
						String label = selectionItem.getLabel();
						//logger.info(label + "--{}", value);
						dynamicList.put(value, label);
					}
					parameter.put("Selection List", dynamicList);
				}
			}
		}

		/*
		 * Log all details for the report parameter associated with with the
		 * object:
		 *     IScalarParameterDefn scalar.
		 */
		logger.info("");
		logger.info("Parameter = {}", scalar.getName());
		for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
			String name = parameterEntry.getKey();
			if (name.equals("Selection List")) {
				HashMap<?, ?> selList = (HashMap<?, ?>) parameterEntry.getValue();
				for (Map.Entry<?, ?> selListEntry : selList.entrySet()) {
					logger.info("  Selection List Entry ===== Key = {} Value = {}",
							selListEntry.getKey(), selListEntry.getValue());
				}
			} else {
				logger.info("  {} = {}", name, parameterEntry.getValue());
			}
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
			//Path rptdesignPath = Paths
			//		.get("/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
			Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");
			//Path rptdesignPath = Paths.get("/home/jeffreyz/Desktop/cascade_v3.2.6.rptdesign");
			List<String> rptdesignLines = null;
			try {
				rptdesignLines = Files.readAllLines(rptdesignPath);// assumes UTF-8 encoding
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String rptdesignXml = String.join("\n", rptdesignLines);
			//logger.info("rptdesignXml = \n{}", rptdesignXml);

			parseReportParams(rptdesignXml);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}