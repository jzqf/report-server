package com.qfree.bo.report.rest.client;

public class CachedData {

	/*
	 * System.currentTimeMillis() value at the time the response entity was
	 * received from the ReSTful server.
	 */
	private long validAt;

	/*
	 * Java object containing parsed JSON entity data. This object contains no
	 * JSON-formatted data. It is a Java bean that contains both standard 
	 * Java types (primitive and non-primitive) as well as custom Java beans
	 * for holding OBO monitoring data.
	 */
	private Object restUriData;

	public long getValidAt() {
		return validAt;
	}

	public void setValidAt(long validAt) {
		this.validAt = validAt;
	}

	public Object getRestUriData() {
		return restUriData;
	}

	public void setRestUriData(Object restUriData) {
		this.restUriData = restUriData;
	}

}
