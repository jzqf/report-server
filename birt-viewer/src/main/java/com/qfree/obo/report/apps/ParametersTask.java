package com.qfree.obo.report.apps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

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

public class ParametersTask {

	private static final Logger logger = LoggerFactory.getLogger(ParametersTask.class);

	public static void executeReport() throws EngineException {

		HashMap<String, HashMap<String, Serializable>> parmDetails = new HashMap<>();

		IReportEngine engine = null;
		EngineConfig config = null;
		try {

			config = new EngineConfig();
			/*
			 * This does not seem to be necessary, at least for a web app that
			 * is packaged as a WAR file.
			 */
			//config.setEngineHome("/home/jeffreyz/workspace-4.5-jee/birt-runtime-history/birt-runtime-4_4_2/ReportEngine");
			config.setLogConfig(null, Level.FINE);

			Platform.startup(config);
			IReportEngineFactory factory = (IReportEngineFactory) Platform
					.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			engine = factory.createReportEngine(config);
			engine.changeLogLevel(Level.WARNING);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		logger.info("Initialized, I think..");

		IReportRunnable design = null;

		//Open a report design 
		design = engine.openReportDesign(
				"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");

		//Create Parameter Definition Task and retrieve parameter definitions
		IGetParameterDefinitionTask task = engine.createGetParameterDefinitionTask(design);
		Collection<IParameterDefnBase> params = (Collection<IParameterDefnBase>) task.getParameterDefns(true);

		//Iterate over each parameter
		for (IParameterDefnBase param : params) {
			if (param instanceof IParameterGroupDefn) {

				IParameterGroupDefn group = (IParameterGroupDefn) param;
				logger.info("Parameter group: {}", group.getName());

				// Iterate over the parameter group contents.
				//for (Iterator i2 = group.getContents().iterator(); i2.hasNext();) {
				for (Iterator<ArrayList> i2 = group.getContents().iterator(); i2.hasNext();) {
					IScalarParameterDefn scalar = (IScalarParameterDefn) i2.next();
					logger.info("\t{}", scalar.getName());
					//Get details about the parameter.
					parmDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, group));
				}

			} else {

				IScalarParameterDefn scalar = (IScalarParameterDefn) param;
				logger.info("Scalar parameter: {}", scalar.getName());
				// Get details about the parameter.
				parmDetails.put(scalar.getName(), loadParameterDetails(task, scalar, design, null));

			}
		}

		//Destroy the engine and shutdown the Platform
		//Note - If the program stays resident do not shutdown the Platform or the Engine  
		// engine.shutdown();	<- deprecated
		engine.destroy();
		Platform.shutdown();
		logger.info("Finished");
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
		parameter.put("Default Value", parameterHandle.getDefaultValue());
		parameter.put("Prompt Text", parameterHandle.getPromptText());
		parameter.put("Data Set Expression", parameterHandle.getValueExpr());

		if (scalar.getControlType() != IScalarParameterDefn.TEXT_BOX) {
			//retrieve selection list for cascaded parameter
			if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {
				if (parameterHandle.getContainer() instanceof CascadingParameterGroupHandle) {
					int index = parameterHandle.getContainerSlotHandle().findPosn(parameterHandle);
					Object[] keyValue = new Object[index];
					for (int i = 0; i < index; i++) {
						ScalarParameterHandle handle = (ScalarParameterHandle) ((CascadingParameterGroupHandle) parameterHandle
								.getContainer()).getParameters().get(i);
						//Use parameter default values
						keyValue[i] = handle.getDefaultValue();
					}
					String groupName = parameterHandle.getContainer().getName();
					task.evaluateQuery(groupName);

					Collection<IParameterSelectionChoice> sList = (Collection<IParameterSelectionChoice>) task
							.getSelectionListForCascadingGroup(groupName, keyValue);
					HashMap<Object, String> dynamicList = new HashMap<Object, String>();

					for (IParameterSelectionChoice sI : sList) {
						Object value = sI.getValue();
						Object label = sI.getLabel();
						//System.out.println(label + "--" + value);
						logger.info(label + "--{}", value);
						dynamicList.put(value, (String) label);

					}
					parameter.put("Selection List", dynamicList);

				}
			} else {
				//retrieve selection list
				Collection<IParameterSelectionChoice> selectionList = (Collection<IParameterSelectionChoice>) task
						.getSelectionList(scalar.getName());

				if (selectionList != null) {
					HashMap<Object, String> dynamicList = new HashMap<Object, String>();

					for (IParameterSelectionChoice selectionItem : selectionList) {
						Object value = selectionItem.getValue();
						String label = selectionItem.getLabel();
						//System.out.println( label + "--" + value);
						dynamicList.put(value, label);

					}
					parameter.put("Selection List", dynamicList);
				}
			}

		}

		//Print out results
		//System.out.println("====================== Parameter = " + scalar.getName());
		logger.info("");
		logger.info("Parameter = {}", scalar.getName());
		for (Map.Entry<String, Serializable> parameterEntry : parameter.entrySet()) {
			String name = parameterEntry.getKey();
			if (name.equals("Selection List")) {
				HashMap<?, ?> selList = (HashMap<?, ?>) parameterEntry.getValue();
				for (Map.Entry<?, ?> selListEntry : selList.entrySet()) {
					//System.out.println("Selection List Entry ===== Key = " + selListEntry.getKey() + " Value = "+ selListEntry.getValue());
					logger.info("  Selection List Entry ===== Key = {} Value = {}",
							selListEntry.getKey(), selListEntry.getValue());
				}

			} else {
				//System.out.println(name + " = " + parameterEntry.getValue());
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
			executeReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}