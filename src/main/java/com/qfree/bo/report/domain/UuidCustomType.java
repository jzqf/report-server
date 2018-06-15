package com.qfree.bo.report.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

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
public class UuidCustomType extends AbstractSingleColumnStandardBasicType<UUID> {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(UuidCustomType.class);

	public static final String DB_VENDOR;
	public static final String H2_VENDOR = "H2";
	public static final String POSTGRESQL_VENDOR = "POSTGRESQL";

	private static final SqlTypeDescriptor SQL_DESCRIPTOR;
	private static final JavaTypeDescriptor<UUID> TYPE_DESCRIPTOR;

	static {

		Properties properties = new Properties();
		String vendor = null;
		String dialect = null;
		try (InputStream in = UuidCustomType.class.getResourceAsStream("/config.properties")) {
			/*
			 * If we are running tests, this will load:
			 *     /src/test/resources/config.properties
			 * Maven will ensure that this file will be placed on the classpath 
			 * if we run a class from Eclipse that is stored in the 
			 * src/test/java/ tree.  This seems to cover all cases:
			 * 
			 *     Run As > JUnit Test			(for a JUnit test class)
			 *     Run As > Java Application	(for a Java class with a "main" method)
			 *     $ mvn clean test				(from a bash shell outside Eclipse)
			 * 
			 * If we are *not* running tests, "getResourceAsStream" will load:
			 *     /src/main/resources/config.properties
			 */
			if (in != null) {
				properties.load(in);
				vendor = properties.getProperty("spring.database.vendor");
				dialect = properties.getProperty("hibernate.dialect");
			}
		} catch (IOException e) {
			logger.error("An IOException was thrown loading config.properties. Rethrowing...", e);
			try {
				throw e;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		/*
		 * This can be used elsewhere in the application to customize code that
		 * is dependent on whether we are using an H2 or POSTGRESQL dataase.
		 */
		DB_VENDOR = vendor;

		logger.info("dialect = {}", dialect);
		/*
		 * org.hibernate.dialect.PostgreSQLDialect is deprecated!
		 */
		//if (dialect != null && dialect.equals("org.hibernate.dialect.PostgreSQLDialect")) {
		//if (dialect != null && dialect.equals("com.qfree.bo.report.domain.UuidCustomPostgreSQL9Dialect")) {
		if (dialect != null && dialect.equals("org.hibernate.dialect.PostgreSQL9Dialect")) {
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
