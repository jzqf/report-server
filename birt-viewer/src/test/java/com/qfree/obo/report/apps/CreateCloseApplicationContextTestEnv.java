package com.qfree.obo.report.apps;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.obo.report.db.PersistenceConfigTestEnv;

public class CreateCloseApplicationContextTestEnv {

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This 
	 * can be used to create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				PersistenceConfigTestEnv.class);
		//		RootConfigDesktopAppTestEnv.class);

		//		URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
		//		ResourceConfig config = new JAXRSConfiguration();
		//		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);//, false);
		//		server.shutdownNow();

		//		HttpServer server = (HttpServer) context.getBean(HttpServer.class);
		//		server.shutdownNow();

		//		// Initialize Grizzly HttpServer
		//		HttpServer server = new HttpServer();
		//		NetworkListener listener = new NetworkListener("grizzly2", "localhost", 3388);
		//		server.addListener(listener);
		//		// Initialize and add Spring-aware Jersey resource
		//		WebappContext ctx = new WebappContext("ctx", "/api");
		//		//		final ServletRegistration reg = ctx.addServlet("jersey-servlet", new ServletContainer());
		//		final ServletRegistration reg = ctx.addServlet("jersey-servlet", new ServletContainer());
		//		reg.addMapping("/rest/*");
		//		ctx.addContextInitParameter("contextClass",
		//				"org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
		//		ctx.addContextInitParameter("contextConfigLocation", "com.qfree.obo.report.apps.RootConfigDesktopAppTestEnv");//TODO Derive this from the class object!
		//		ctx.addContextInitParameter("javax.ws.rs.Application", "com.qfree.obo.report.rest.server.JAXRSConfiguration");
		//		ctx.addListener("org.springframework.web.context.ContextLoaderListener");
		//		ctx.addListener("org.springframework.web.context.request.RequestContextListener");
		//		ctx.deploy(server);
		//		try {
		//			server.start();
		//			System.out.println("In order to test the server please try the following urls:");
		//			System.out.println("http://localhost:3388/...");
		//			System.out.println();
		//			System.out.println("Press enter to stop the server...");
		//			System.in.read();
		//		} finally {
		//			server.shutdownNow();
		//		}

		context.close();
	}

}
