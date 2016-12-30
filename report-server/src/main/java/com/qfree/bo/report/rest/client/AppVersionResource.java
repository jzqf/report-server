package com.qfree.bo.report.rest.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Role;

@XmlRootElement
public class AppVersionResource {
	//public class AppVersionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(AppVersionResource.class);

	@XmlElement
	private String appVersion;

	public AppVersionResource() {
	}

	/**
	 * Create new {@link AppVersionResource} instance from a {@link Role}
	 * instance.
	 * 
	 * @param role
	 * @param uriInfo
	 * @param expand
	 * @param apiVersion
	 */
	public AppVersionResource(String appVersion) {
		super();
		this.appVersion = appVersion;
		logger.debug("this = {}", this);
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	@Override
	public String toString() {
		return "AppVersionResource [appVersion=" + appVersion + "]";
	}

}
