package com.qfree.obo.report.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a custom UUID column type "uuid-custom" that can be used with either
 * PostgreSQL or H2 databases.
 * <p/>
 * To this this type, annotate the entity class itself with:
 * <p/>
 * <code>@TypeDef(name="uuid-custom", defaultForType=UUID.class, typeClass=UuidCustomType.class)</code>,
 * <p/>
 * and annotate a UUID attribute/column in the entity class with:
 * <p/>
 * <code>@Type(type="uuid-custom")</code>
 * 
 * @author Jeffrey Zelt
 *
 */
public class UuidCustomType extends AbstractSingleColumnStandardBasicType {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(UuidCustomType.class);

	private static final SqlTypeDescriptor SQL_DESCRIPTOR;
	private static final JavaTypeDescriptor TYPE_DESCRIPTOR;

	static {

		Properties properties = new Properties();
		String dialect = null;
		//		boolean runningTests = true;
		try (InputStream in = UuidCustomType.class.getResourceAsStream("/config.properties")) {
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
			//			runningTests = (in == null) ? false : true;
			if (in != null) {
				properties.load(in);
				dialect = properties.getProperty("hibernate.dialect");
			}
		} catch (IOException e) {
			logger.error("An IOException was thrown loading config.properties. Rethrowing...", e);
			try {
				throw e;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//			runningTests = false;
		}

		//runningTests = false;	// to *force* @Type(type = "pg-uuid")	

		//		logger.info("runningTests = {}", runningTests);
		//		if (runningTests) {
		//			SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;	// for H2 database
		//		} else {
		//			SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
		//		}

		logger.info("dialect = {}", dialect);
		if (dialect != null && dialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
			SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
		} else if (dialect.equals("org.hibernate.dialect.H2Dialect")) {
			SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;
		} else {
			throw new UnsupportedOperationException("Unsupported database: " + dialect);
		}

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
