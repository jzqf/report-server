package com.qfree.obo.report.domain;

import java.sql.Types;

/**
 * Currently not used. This class is not useful in its current form. It was
 * created to possibly improve the treatment of native SQL queries with Spring
 * Data JPA. However, it has not proved to be useful.
 */
import org.hibernate.dialect.PostgreSQL9Dialect;

public class UuidCustomPostgreSQL9Dialect extends PostgreSQL9Dialect {

	public UuidCustomPostgreSQL9Dialect() {
		super();
		registerColumnType(Types.OTHER, "uuid");
		registerHibernateType(Types.OTHER, "string");
	}
}
