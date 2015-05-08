package com.qfree.obo.report.dto;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.domain.Role;

public enum ResourcePath {

	CONFIGURATIONS(ResourcePath.CONFIGURATIONS_PATH, ResourcePath.CONFIGURATION_EXPAND_PARAM, Configuration.class),
	REPORTS(ResourcePath.REPORTS_PATH, ResourcePath.REPORT_EXPAND_PARAM, Report.class),
	REPORTCATEGORIES(ResourcePath.REPORTCATEGORIES_PATH, ResourcePath.REPORTCATEGORY_EXPAND_PARAM, ReportCategory.class),
	ROLES(ResourcePath.ROLES_PATH, ResourcePath.ROLE_EXPAND_PARAM, Role.class);

	private static final String PATH_SEPARATOR = "/";

	/*
	 * These paths are used in @Path annotations (and elsewhere). Arguments to
	 * annotations must be constant expressions. Therefore these paths must be
	 * declared as constants. It does not seem possible to, e.g., declare them 
	 * within the ResourcePath enum because that enum is not considered to be a 
	 * constant expression.
	 */
	public static final String CONFIGURATIONS_PATH = ResourcePath.PATH_SEPARATOR + "configurations";
	public static final String REPORTCATEGORIES_PATH = ResourcePath.PATH_SEPARATOR + "reportCategories";
	public static final String REPORTS_PATH = ResourcePath.PATH_SEPARATOR + "reports";
	public static final String ROLES_PATH = ResourcePath.PATH_SEPARATOR + "roles";

	/*
	 * These are the values for each resource class that can be assigned to the
	 * "expand" query parameter.
	 */
	private static final String CONFIGURATION_EXPAND_PARAM = "configuration";
	private static final String REPORTCATEGORY_EXPAND_PARAM = "reportCategory";
	private static final String REPORT_EXPAND_PARAM = "report";
	private static final String ROLE_EXPAND_PARAM = "role";

	private final String path;
	private final String expandParam;
	private final Class<?> entityClass;

	private ResourcePath(String path, String expandParam, Class<?> entityClass) {
		this.path = path;
		this.expandParam = expandParam;
		this.entityClass = entityClass;
    }

	public static ResourcePath forEntity(Class<?> entityClass) {
		for (ResourcePath resourcePath : ResourcePath.values()) {
			/*
			 * Cannot use equals because object may be proxied by Hibernate.
			 * Cannot use instanceof because type not fixed at compile time?
			 */
			if (resourcePath.entityClass.isAssignableFrom(entityClass)) {
				return resourcePath;
            }
        }
		throw new IllegalArgumentException("No ResourcePath for entity class '" + entityClass.getName() + "'");
    }

    public String getPath() {
        return path;
    }

	public String getExpandParam() {
		return expandParam;
	}

	public Class<?> getEntityClass() {
		return entityClass;
    }

}
