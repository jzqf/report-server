package com.qfree.obo.report.resource;

import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.domain.Role;

@XmlRootElement
public class ConfigurationResource {

	@XmlElement
	private String href;

	@XmlElement
	//(name = "someOverriddenName")
	private UUID configurationId;

	@XmlElement
	private Date createdOn;

	@XmlElement
	private Role role;

	@XmlElement
	private ParamName paramName;

	@XmlElement
	private String value;

	/*
	 * Required by JAXB
	 */
	public ConfigurationResource() {
	}

	public ConfigurationResource(UUID configurationId, Date createdOn, Role role, ParamName paramName, String value) {
		this.configurationId = configurationId;
		this.createdOn = createdOn;
		this.role = role;
		this.paramName = paramName;
		this.value = value;
	}

	public ConfigurationResource(Configuration configuration) {
		this.configurationId = configuration.getConfigurationId();
		this.createdOn = configuration.getCreatedOn();
		this.role = configuration.getRole();
		this.paramName = configuration.getParamName();
		/*
		 * Set "value" from appropriate field based on this.paramName.paramType()????????????????????????????????????????????????????????
		 * No, set "value from configuration.getStringValue(), but this will not work for data type "bytea", 
		 * so I will need to check this.paramName.paramType() for this case??????????????????????????????????????????????????????????????
		 * 
		 * At the moment, I am not sure how to proceed. Just come back to this when I have fleshed out other
		 * resources and controller. It might be more obvious then. :-)
		 */
		//		this.value = configuration.getStringValue();
	}

}