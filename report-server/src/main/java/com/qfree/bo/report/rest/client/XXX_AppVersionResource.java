package com.qfree.bo.report.rest.client;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Role;

@XmlRootElement
public class XXX_AppVersionResource {
	//public class XXX_AppVersionResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(XXX_AppVersionResource.class);

	@XmlElement
	private String appVersion;

	public XXX_AppVersionResource() {
	}

	/**
	 * Create new {@link XXX_AppVersionResource} instance from a {@link Role}
	 * instance.
	 * 
	 * @param role
	 * @param uriInfo
	 * @param expand
	 * @param apiVersion
	 */
	public XXX_AppVersionResource(String appVersion) {
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
		return "XXX_AppVersionResource [appVersion=" + appVersion + "]";
	}

}
