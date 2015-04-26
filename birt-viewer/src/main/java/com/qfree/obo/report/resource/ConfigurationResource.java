package com.qfree.obo.report.resource;

import java.util.Date;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Configuration.ParamName;

@XmlRootElement
public class ConfigurationResource extends AbstractResource {

	//	@XmlElement
	//	private String href;

	@XmlElement
	//(name = "someOverriddenName")
	private UUID configurationId;

	@XmlElement
	private Date createdOn;

	//	@XmlElement
	//	private RoleResource roleResource;

	@XmlElement
	private ParamName paramName;

	@XmlElement
	private String value;

	/*
	 * Required by JAXB
	 */
	public ConfigurationResource() {
	}

	public ConfigurationResource(UriInfo uriInfo, Configuration configuration) {

		super(uriInfo, Configuration.class, configuration.getConfigurationId());

		this.configurationId = configuration.getConfigurationId();
		this.createdOn = configuration.getCreatedOn();
		//		this.role = configuration.getRole();
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

	@Override
	public String toString() {
		return "ConfigurationResource [href=" + href + "]";
	}

}