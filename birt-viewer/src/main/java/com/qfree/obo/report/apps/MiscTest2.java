package com.qfree.obo.report.apps;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiscTest2 {

	private static final Logger logger = LoggerFactory.getLogger(MiscTest2.class);

	public static void main(String[] args) {

		String baseURI = "http:localhost:8080/report-server/rest";

		UriBuilder uriBuilder = UriBuilder.fromPath(baseURI).path("reportVersions");

		System.out.println("uriBuilder.toString() = " + uriBuilder.toString());
	}
}