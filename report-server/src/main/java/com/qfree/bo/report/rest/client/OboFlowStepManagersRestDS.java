package com.qfree.bo.report.rest.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a BIRT POJO data source using the ReSTful monitoring API exposed 
 * by the Q-Free OBO. 
 * 
 * This data source exposes monitoring data for both the main flow manager as
 * as well as each step manager.
 * 
 * @author Jeffrey Zelt
 *
 */
public class OboFlowStepManagersRestDS {

	private static final Logger logger = LoggerFactory.getLogger(OboFlowStepManagersRestDS.class);

	private OboFlowManager oboFlowManager;
	private Iterator<OboStepManager> iterator;

	/**
	 * Called by BIRT engine once when the report is invoked. It is a mandatory 
	 * method.
	 * 
	 * @param appContext
	 * @param dataSetParamValues Map of data set parameters passed to this 
	 * method. These can be used, e.g., for filtering, or for any other purpose.
	 */
	public void open(Object appContext, Map<String, Object> dataSetParamValues) {
		//TODO Can dataSetParamValues be used to select a single step manager from all step managers?
		List<OboStepManager> oboStepManagers = new ArrayList<>();

		try {
			URL restURI = new URL(
					OboMonitoringData.getREST_SCHEME(),
					OboMonitoringData.getREST_HOST(),
					OboMonitoringData.getREST_PORT(),
					OboMonitoringData.getFLOW_MANAGER_REST_PATH());
			OboMonitoringData oboMonitoringData = OboMonitoringData.getInstance();
			oboFlowManager = (OboFlowManager) oboMonitoringData.getRestData(restURI);
			oboStepManagers = oboFlowManager.getSubStatistics();
		} catch (MalformedURLException e) {
			logger.error("Exception thrown:", e);
		}

		this.iterator = oboStepManagers.iterator();
	}

	/**
	 * Called by the BIRT engine once for each row of the data set . It is a 
	 * mandatory method.
	 * 
	 * @return next row of data
	 */
	public Object next() {
		if (iterator.hasNext()) {
			OboFlowStepManager oboFlowStepManager = new OboFlowStepManager();
			/*
			 * The flow manager data is repeated for each step manager returned.
			 * A BIRT report that uses this data source can choose to ignore the
			 * flow manager data or it can group by it to present a more
			 * hierarchical view of the data (all step managers under a single
			 * flow manager).
			 * 
			 * The BIRT POJO data set implementation is good enough to allow us
			 * to peer inside both of the two objects stored in the
			 * oboFlowStepManager object and define data set columns
			 * based on getters of these objects. In other words, it is not 
			 * necessary for the data set to be constructed only from the 
			 * top-level getters of the oboFlowStepManager object. This means 
			 * that we can return an object here using composition, which 
			 * reduces the code maintenance if either of the child objects 
			 * change.
			 */
			oboFlowStepManager.setOboFlowManager(oboFlowManager);	// parent flow manager
			oboFlowStepManager.setOboStepManager(iterator.next());	// child step manager
			return oboFlowStepManager;
		}
		return null;
	}

	/**
	 * Called by the BIRT engine once at the end of the report. It is a 
	 * mandatory method.
	 */
	public void close() {
		this.iterator = null;
	}

	public static void main(String[] args) {

		OboFlowStepManagersRestDS oboFlowStepManagersRestDS = new OboFlowStepManagersRestDS();

		oboFlowStepManagersRestDS.open(null, new HashMap<String, Object>());
		while (oboFlowStepManagersRestDS.iterator.hasNext()) {
			OboFlowStepManager oboFlowStepManager = (OboFlowStepManager) oboFlowStepManagersRestDS.next();
			System.out.println("oboFlowStepManager = " + oboFlowStepManager);
		}

		oboFlowStepManagersRestDS.close();
	}

}
