package com.qfree.obo.report.rest.server;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);

	@GET
	@Produces("text/plain")
	public String numConsumers(@HeaderParam("Accept") String acceptHeader) {
		String apiVersion = ReST.extractAPIVersion(acceptHeader);
		return "test";
	}

	//	@GET
	//	@Path("/state")
	//	@Produces("text/plain")
	//	public String state(@HeaderParam("Accept") String acceptHeader) {
	//		String apiVersion = ReST.extractAPIVersion(acceptHeader);
	//		return "/test/state";
	//	}

}
