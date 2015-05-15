package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Configuration.ParamName;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class ConfigurationResource extends AbstractBaseResource {

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID configurationId;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
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

	public ConfigurationResource(Configuration configuration, UriInfo uriInfo, List<String> expand,
			RestApiVersion apiVersion) {

		super(Configuration.class, configuration.getConfigurationId(), uriInfo, expand, apiVersion);

		String expandParam = ResourcePath.forEntity(Configuration.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);

			/*
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

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
	}

	@Override
	public String toString() {
		return "ConfigurationResource [href=" + href + "]";
	}

}