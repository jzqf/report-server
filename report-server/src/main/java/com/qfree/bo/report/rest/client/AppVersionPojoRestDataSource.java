package com.qfree.bo.report.rest.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppVersionPojoRestDataSource extends AbstractPojoRestDataSource {

	private static final Logger logger = LoggerFactory.getLogger(AppVersionPojoRestDataSource.class);

	private static final String RESOURCE_PATH = "appversion";
	//private static final String API_VERSION = "1";

	private Iterator<AppVersionResource> iterator;

	private List<AppVersionResource> getAppVersion(String baseUri, String resourcePath) {

		logger.info("baseUri = {}", baseUri);

		/*
		 * Retrieve the application version as a String via HTTP GET.
		 */
		WebTarget baseUriWebTarget = getBaseUriWebTarget(baseUri);
		Response response = baseUriWebTarget.path(resourcePath)
				.request()
				.header("Accept", MediaType.TEXT_PLAIN)
				.get();

		logger.info("response = {}", response);

		int status = response.getStatus();
		logger.info("HttpStatus = {}", status);

		String appVersion = response.readEntity(String.class);
		logger.info("appVersion = {}", appVersion);
		List<AppVersionResource> appVersions = new ArrayList<>(1);
		appVersions.add(new AppVersionResource(appVersion));
		logger.info("appVersions = {}", appVersions);

		return appVersions;
	}

	/* The "open" method will be called by BIRT engine once when the report 
	 * is invoked. It is a mandatory method.
	 */
	public void open(Object appContext, Map<String, Object> dataSetParamValues) {
		List<AppVersionResource> appVersions = getAppVersion(BASE_REST_URI, RESOURCE_PATH);
		this.iterator = appVersions.iterator();
	}

	/* The "next" method is called by the BIRT engine once for each row of the
	 * data set . It is a mandatory method.
	 */
	public Object next() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}

	/* The "close" method is called by the BIRT engine once at the end of the 
	 * report. It is a mandatory method.
	 */
	public void close() {
		this.iterator = null;
	}

	/**
	 * Run this class in Eclipse as a Java application to test this data source.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		AppVersionPojoRestDataSource appVersionPojoRestDataSource = new AppVersionPojoRestDataSource();

		appVersionPojoRestDataSource.open(null, new HashMap<String, Object>());
		while (appVersionPojoRestDataSource.iterator.hasNext()) {
			AppVersionResource appVersionResource = (AppVersionResource) appVersionPojoRestDataSource.next();
			System.out.println("appVersionResource = " + appVersionResource);
		}

		appVersionPojoRestDataSource.close();

	}
} 