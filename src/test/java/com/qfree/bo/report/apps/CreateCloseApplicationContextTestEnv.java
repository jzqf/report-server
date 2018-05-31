package com.qfree.bo.report.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.qfree.bo.report.ApplicationConfig;

public class CreateCloseApplicationContextTestEnv {

	private static final Logger logger = LoggerFactory.getLogger(CreateCloseApplicationContextTestEnv.class);

	/**
	 * Creates an AnnotationConfigApplicationContext and then closes it. This 
	 * can be used to create or update tables in a database via Hibernate's
	 * "hibernate.hbm2ddl.auto" property.
	 */
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				ApplicationConfig.class);

		context.close();
	}

}
