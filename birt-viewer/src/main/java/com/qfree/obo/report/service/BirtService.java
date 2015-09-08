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
import com.qfree.obo.report.util.ReportUtils;

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
			throws IOException, BirtException {

		/*
		 * A LinkedHashMap is used here so that the order of the parameters 
		 * inserted into this map is preserved, i.e., iteration over the 
		 * entries in this map will always preserve the order that the entries
		 * were originally inserted into the map. 
		 */
		Map<String, Map<String, Serializable>> parameters = new LinkedHashMap<>();

		IReportEngine engine = getBirtReportEngine();

		/*
		 * Open the report design.
		 */
		IReportRunnable design = null;
		try (InputStream rptdesignStream = new ByteArrayInputStream(
				rptdesignXml.getBytes(StandardCharsets.UTF_8))) {
			design = engine.openReportDesign(rptdesignStream);
		}

		/*
		 * One can also open a design by passing an absolute path to an 
		 * rptdesign file as shown here. This code is commented out and only 
		 * used for testing.
		 */
		//design = engine.openReportDesign(
		//	"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
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
				for (IScalarParameterDefn scalarParameter : (ArrayList<IScalarParameterDefn>) group.getContents()) {
					logger.info("Scalar parameter '{}' from group '{}'", scalarParameter.getName(),
							group.getName());
					/*
					 * Get details about the parameter as a Map and then insert
					 * that Map into the paramDetails Map
					 */
					parameters.put(scalarParameter.getName(),
							ReportUtils.loadParameterDetails(task, scalarParameter, design, group));
				}

			} else {

				IScalarParameterDefn scalarParameter = (IScalarParameterDefn) param;
				logger.info("Scalar parameter '{}'", scalarParameter.getName());
				/*
				 * Get details about the parameter as a Map and then insert
				 * that Map into the paramDetails Map
				 */
				parameters.put(scalarParameter.getName(),
						ReportUtils.loadParameterDetails(task, scalarParameter, design, null));

			}
		}

		return parameters;
	}

	public HashMap<Object, String> getReportParameterDynamicSelectionList(
			String parameterName, List<String> dynamicListKeys, String rptdesignXml)
					throws RptdesignOpenFromStreamException, BirtException, DynamicSelectionListKeyException {

		IReportEngine engine = getBirtReportEngine();

		/*
		 * Open the report design.
		 */
		IReportRunnable runnableReportDesign = null;
		try (InputStream rptdesignStream = new ByteArrayInputStream(
				rptdesignXml.getBytes(StandardCharsets.UTF_8))) {
			runnableReportDesign = engine.openReportDesign(rptdesignStream);
		} catch (EngineException | IOException e) {
			throw new RptdesignOpenFromStreamException("rptdesign = " + rptdesignXml, e);
		}

		/*
		 * One can also open a design by passing an absolute path to an 
		 * rptdesign file as shown here. This code is commented out and only 
		 * used for testing.
		 */
		//design = engine.openReportDesign(
		//	"/home/jeffreyz/git/obo-birt-reports/birt-reports/tests/400-TestReport04_v1.1.rptdesign");
		//design = engine.openReportDesign("/home/jeffreyz/Desktop/cascade_v3.2.23.rptdesign");

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
				logger.debug("Parameter name = '{}', positionInSlot = {}", parameterName, positionInSlot);
				if (positionInSlot == dynamicListKeys.size()) {
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
					String[] groupKeyValues = new String[dynamicListKeys.size()];
					groupKeyValues = dynamicListKeys.toArray(groupKeyValues);

					/*
					 * Get the selection list for the parameter, which is a 
					 * member of the cascading parameter group. The name of this
					 * group (not the parameter) is stored in parameterGroupName. 
					 */
					selectionList = (Collection<IParameterSelectionChoice>) task
							.getSelectionListForCascadingGroup(parameterGroupName, groupKeyValues);
					logger.info("Dynamic selection list for cascading parameter:");

				} else {
					throw new DynamicSelectionListKeyException(Integer.toString(dynamicListKeys.size())
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

}
