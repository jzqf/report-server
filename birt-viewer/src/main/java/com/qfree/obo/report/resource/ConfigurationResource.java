package com.qfree.obo.report.resource;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConfigurationResource {

	@XmlElement(name = "someOverriddenName")
	public String name;
	public int age;

	/**
	 * Required by JAXB
	 */
	public ConfigurationResource() {
	}

	public ConfigurationResource(String name, int age) {
		this.name = name;
		this.age = age;
	}
}