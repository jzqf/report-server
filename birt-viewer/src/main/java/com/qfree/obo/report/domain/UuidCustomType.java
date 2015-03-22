package com.qfree.obo.report.domain;

import java.io.IOException;
import java.io.InputStream;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UuidCustomType extends AbstractSingleColumnStandardBasicType {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(UuidCustomType.class);

	private static final SqlTypeDescriptor SQL_DESCRIPTOR;
	private static final JavaTypeDescriptor TYPE_DESCRIPTOR;

	static {

		//Properties properties = new Properties();
		boolean runningTests = true;
		try (InputStream in = UuidCustomType.class.getResourceAsStream("/test-env.properties")) {
			/*
			 * "in" will be null if the file "test-env.properties" is not found 
			 * at the root of the classpath. This file is located in this project
			 * here:
			 *     /src/test/resources/test-env.properties
			 * It will be placed on the classpath if we run a class from Eclipse
			 * that is stored in the src/test/java/ tree.  Testing shows that 
			 * this covers both cases:
			 * 
			 *     Run As > JUnit Test         (for a JUnit test class)
			 *     Run As > Java Application   (for a Java class with a "main" method)
			 */
			runningTests = (in == null) ? false : true;
		} catch (IOException e) {
			runningTests = false;
		}

		//runningTests = false;	// to *force* @Type(type = "pg-uuid")	

		logger.info("runningTests = {}", runningTests);
		if (runningTests) {
			SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;	// for H2 database
		} else {
			SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
		}

		//		String dialect = "org.hibernate.dialect.H2Dialect";
		//		String dialect = "org.hibernate.dialect.PostgreSQLDialect";
		//		if (dialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
		//			SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
		//		} else if (dialect.equals("org.hibernate.dialect.H2Dialect")) {
		//			SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;
		//		} else {
		//			throw new UnsupportedOperationException("Unsupported database!");
		//		}

		TYPE_DESCRIPTOR = UUIDTypeDescriptor.INSTANCE;
	}

	public UuidCustomType() {
		super(SQL_DESCRIPTOR, TYPE_DESCRIPTOR);
	}

	@Override
	public String getName() {
		return "uuid-custom";
	}

}
