package com.qfree.obo.report.resource;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;

public enum ResourcePath {

	REPORTS("/reports", Report.class),
	REPORTCATEGORIES("/reportcategories", ReportCategory.class);

    final String path;
	final Class<?> entityClass;

	private ResourcePath(String path, Class<?> clazz) {
		this.path = path;
		this.entityClass = clazz;
    }

	public static ResourcePath forEntity(Class<?> clazz) {
		for (ResourcePath resourcePath : ResourcePath.values()) {
			/*
			 * Cannot use equals because object may be proxied by Hibernate.
			 * Cannot use instanceof because type not fixed at compile time?
			 */
			if (resourcePath.entityClass.isAssignableFrom(clazz)) {
				return resourcePath;
            }
        }
		throw new IllegalArgumentException("No ResourcePath for entity class '" + clazz.getName() + "'");
    }

    public String getPath() {
        return path;
    }

	public Class<?> getEntityClass() {
		return entityClass;
    }

}
