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
 * This data source exposes monitoring data for both the main flow manager.
 * 
 * @author Jeffrey Zelt
 *
 */
public class OboFlowManagerRestDS {

	private static final Logger logger = LoggerFactory.getLogger(OboFlowManagerRestDS.class);

	private Iterator<OboFlowManager> iterator;

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
		List<OboFlowManager> oboFlowManagers = new ArrayList<>();

		try {
			URL restURI = new URL(
					OboMonitoringData.getREST_SCHEME(),
					OboMonitoringData.getREST_HOST(),
					OboMonitoringData.getREST_PORT(),
					OboMonitoringData.getFLOW_MANAGER_REST_PATH());
			OboMonitoringData oboMonitoringData = OboMonitoringData.getInstance();
			OboFlowManager oboFlowManager = (OboFlowManager) oboMonitoringData.getRestData(restURI);
			oboFlowManagers.add(oboFlowManager);
		} catch (MalformedURLException e) {
			logger.error("Exception thrown:", e);
		}

		this.iterator = oboFlowManagers.iterator();
	}

	/**
	 * Called by the BIRT engine once for each row of the data set . It is a 
	 * mandatory method.
	 * 
	 * @return next row of data
	 */
	public Object next() {
		if (iterator.hasNext()) {
			return iterator.next();
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

		OboFlowManagerRestDS oboFlowManagerRestDS = new OboFlowManagerRestDS();

		oboFlowManagerRestDS.open(null, new HashMap<String, Object>());
		while (oboFlowManagerRestDS.iterator.hasNext()) {
			OboFlowManager oboFlowManager = (OboFlowManager) oboFlowManagerRestDS.next();
			System.out.println("oboFlowManager = " + oboFlowManager);
		}

		oboFlowManagerRestDS.close();
	}

}
