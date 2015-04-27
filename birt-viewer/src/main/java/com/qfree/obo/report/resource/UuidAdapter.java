package com.qfree.obo.report.resource;

import java.util.UUID;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UuidAdapter extends XmlAdapter<String, UUID> {

	private static final Logger logger = LoggerFactory.getLogger(UuidAdapter.class);

	/*
	 * Java => JSON/XML
	 * Given the unmappable Java UUID object, return the desired JSON/XML 
	 * representation.
	 */
	public String marshal(UUID uuid) throws Exception {
		//		logger.info("uuid = {}", uuid);
		if (uuid != null) {
			return uuid.toString();
		} else {
			return (String) null;
			//			return "";
		}
	}

	/*
	 * JSON/XML => Java
	 * Given a JSON/XML string, use it to build an instance of the unmappable 
	 * Java UUID class.
	 */
	public UUID unmarshal(String uuidAsString) throws Exception {
		//		logger.info("uuidAsString = {}", uuidAsString);
		if (uuidAsString != null && !uuidAsString.equals("")) {
			return UUID.fromString(uuidAsString);
		} else {
			return (UUID) null;
		}
	}

}
