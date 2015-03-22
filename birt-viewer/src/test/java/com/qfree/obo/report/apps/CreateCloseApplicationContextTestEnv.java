package com.qfree.obo.report.apps;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CreateCloseApplicationContextTestEnv {

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This 
	 * can be used to create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RootConfigDesktopAppTestEnv.class);
		context.close();
	}


}
