package com.qfree.obo.report.dto;

import java.util.List;

import com.qfree.obo.report.domain.Configuration;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.domain.JobParameter;
import com.qfree.obo.report.domain.JobParameterValue;
import com.qfree.obo.report.domain.JobStatus;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportCategory;
import com.qfree.obo.report.domain.ReportParameter;
import com.qfree.obo.report.domain.ReportVersion;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.SelectionListValue;
import com.qfree.obo.report.domain.Subscription;
import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;

/**
 * Enum to hold settings related to resource paths so that they are not
 * hardwired into the code.
 * 
 * @author jeffreyz
 *
 */
public enum ResourcePath {

	CONFIGURATIONS(
			ResourcePath.CONFIGURATIONS_PATH,
			ResourcePath.CONFIGURATION_EXPAND_PARAM,
			ResourcePath.CONFIGURATION_SHOWALL_PARAM,
			Configuration.class),
	DOCUMENTFORMATS(
			ResourcePath.DOCUMENTFORMATS_PATH,
			ResourcePath.DOCUMENTFORMAT_EXPAND_PARAM,
			ResourcePath.DOCUMENTFORMAT_SHOWALL_PARAM,
			DocumentFormat.class),
	/*
	 * Passing null here for entityClass causes problems. Do not uncoment this
	 * code here unless you are prepared to track down the issue. For example,
	 * it will cause a NullPointerException in forEntity(...) below, among
	 * other problems (some unit tests will fail). No big deal - we do not need
	 * to uncomment these lines here for any good reason.
	 */
	//	JOBPROCESSOR(
	//			ResourcePath.JOBPROCESSOR_PATH,
	//			ResourcePath.JOBPROCESSOR_EXPAND_PARAM,
	//			ResourcePath.JOBPROCESSOR_SHOWALL_PARAM,
	//			null),
	JOBS(
			ResourcePath.JOBS_PATH,
			ResourcePath.JOB_EXPAND_PARAM,
			ResourcePath.JOB_SHOWALL_PARAM,
			Job.class),
	JOBPARAMETERS(
			ResourcePath.JOBPARAMETERS_PATH,
			ResourcePath.JOBPARAMETER_EXPAND_PARAM,
			ResourcePath.JOBPARAMETER_SHOWALL_PARAM,
			JobParameter.class),
	JOBPARAMETERVALUES(
			ResourcePath.JOBPARAMETERVALUES_PATH,
			ResourcePath.JOBPARAMETERVALUE_EXPAND_PARAM,
			ResourcePath.JOBPARAMETERVALUE_SHOWALL_PARAM,
			JobParameterValue.class),
	JOBSTATUSES(
			ResourcePath.JOBSTATUSES_PATH,
			ResourcePath.JOBSTATUS_EXPAND_PARAM,
			ResourcePath.JOBSTATUS_SHOWALL_PARAM,
			JobStatus.class),
	PARAMETERGROUPS(
			ResourcePath.PARAMETERGROUPS_PATH,
			ResourcePath.PARAMETERGROUP_EXPAND_PARAM,
			ResourcePath.PARAMETERGROUP_SHOWALL_PARAM,
			ParameterGroup.class),
	REPORTS(
			ResourcePath.REPORTS_PATH,
			ResourcePath.REPORT_EXPAND_PARAM,
			ResourcePath.REPORT_SHOWALL_PARAM,
			Report.class),
	REPORTCATEGORIES(
			ResourcePath.REPORTCATEGORIES_PATH,
			ResourcePath.REPORTCATEGORY_EXPAND_PARAM,
			ResourcePath.REPORTCATEGORY_SHOWALL_PARAM,
			ReportCategory.class),
	REPORTPARAMETERS(
			ResourcePath.REPORTPARAMETERS_PATH,
			ResourcePath.REPORTPARAMETER_EXPAND_PARAM,
			ResourcePath.REPORTPARAMETER_SHOWALL_PARAM,
			ReportParameter.class),
	REPORTVERSIONS(
			ResourcePath.REPORTVERSIONS_PATH,
			ResourcePath.REPORTVERSION_EXPAND_PARAM,
			ResourcePath.REPORTVERSION_SHOWALL_PARAM,
			ReportVersion.class),
	ROLES(
			ResourcePath.ROLES_PATH,
			ResourcePath.ROLE_EXPAND_PARAM,
			ResourcePath.ROLE_SHOWALL_PARAM,
			Role.class),
	SELECTIONLISTVALUES(
			ResourcePath.SELECTIONLISTVALUES_PATH,
			ResourcePath.SELECTIONLISTVALUE_EXPAND_PARAM,
			ResourcePath.SELECTIONLISTVALUE_SHOWALL_PARAM,
			SelectionListValue.class),
	SUBSCRIPTIONS(
			ResourcePath.SUBSCRIPTIONS_PATH,
			ResourcePath.SUBSCRIPTION_EXPAND_PARAM,
			ResourcePath.SUBSCRIPTION_SHOWALL_PARAM,
			Subscription.class),
	SUBSCRIPTIONPARAMETERS(
			ResourcePath.SUBSCRIPTIONPARAMETERS_PATH,
			ResourcePath.SUBSCRIPTIONPARAMETER_EXPAND_PARAM,
			ResourcePath.SUBSCRIPTIONPARAMETER_SHOWALL_PARAM,
			SubscriptionParameter.class),
	SUBSCRIPTIONPARAMETERVALUES(
			ResourcePath.SUBSCRIPTIONPARAMETERVALUES_PATH,
			ResourcePath.SUBSCRIPTIONPARAMETERVALUE_EXPAND_PARAM,
			ResourcePath.SUBSCRIPTIONPARAMETERVALUE_SHOWALL_PARAM,
			SubscriptionParameterValue.class);

	private static final String PATH_SEPARATOR = "/";

	/*
	 * These paths are used in @Path annotations (and elsewhere). Arguments to
	 * annotations must be constant expressions. Therefore these paths must be
	 * declared as constants. It does not seem possible to, e.g., declare them 
	 * within the ResourcePath enum because that enum is not considered to be a 
	 * constant expression.
	 */
	public static final String CONFIGURATIONS_PATH = ResourcePath.PATH_SEPARATOR + "configurations";
	public static final String DOCUMENTFORMATS_PATH = ResourcePath.PATH_SEPARATOR + "documentFormats";
	public static final String JOBPROCESSOR_PATH = ResourcePath.PATH_SEPARATOR + "jobProcessor";
	public static final String JOBS_PATH = ResourcePath.PATH_SEPARATOR + "jobs";
	public static final String JOBPARAMETERS_PATH = ResourcePath.PATH_SEPARATOR + "jobParameters";
	public static final String JOBPARAMETERVALUES_PATH = ResourcePath.PATH_SEPARATOR + "jobParameterValues";
	public static final String JOBSTATUSES_PATH = ResourcePath.PATH_SEPARATOR + "jobStatuses";
	public static final String PARAMETERGROUPS_PATH = ResourcePath.PATH_SEPARATOR + "parameterGroups";
	public static final String REPORTCATEGORIES_PATH = ResourcePath.PATH_SEPARATOR + "reportCategories";
	public static final String REPORTPARAMETERS_PATH = ResourcePath.PATH_SEPARATOR + "reportParameters";
	public static final String REPORTVERSIONS_PATH = ResourcePath.PATH_SEPARATOR + "reportVersions";
	public static final String REPORTS_PATH = ResourcePath.PATH_SEPARATOR + "reports";
	public static final String ROLES_PATH = ResourcePath.PATH_SEPARATOR + "roles";
	public static final String SELECTIONLISTVALUES_PATH = ResourcePath.PATH_SEPARATOR + "selectionListValues";
	public static final String SUBSCRIPTIONS_PATH = ResourcePath.PATH_SEPARATOR + "subscriptions";
	public static final String SUBSCRIPTIONPARAMETERS_PATH = ResourcePath.PATH_SEPARATOR + "subscriptionParameters";
	public static final String SUBSCRIPTIONPARAMETERVALUES_PATH = ResourcePath.PATH_SEPARATOR
			+ "subscriptionParameterValues";

	public static final String LOGINATTEMPTS_PATH = ResourcePath.PATH_SEPARATOR + "loginAttempts";

	/*
	 * This is the name of "expand" query parameter used for all HTTP requests. 
	 */
	public static final String EXPAND_QP_NAME = "expand";
	/*
	 * This is the map key associated with the "expand" array that contains all
	 * of the "expand" query parameter values for an HTTP request. 
	 */
	public static final String EXPAND_QP_KEY = "expand";
	/*
	 * These are the values for each resource class that can be assigned to the
	 * "expand" query parameter.
	 */
	public static final String CONFIGURATION_EXPAND_PARAM = "configurations";
	public static final String DOCUMENTFORMAT_EXPAND_PARAM = "documentFormats";
	public static final String JOBPROCESSOR_EXPAND_PARAM = "jobProcessor";
	public static final String JOB_EXPAND_PARAM = "jobs";
	public static final String JOBPARAMETER_EXPAND_PARAM = "jobParameters";
	public static final String JOBPARAMETERVALUE_EXPAND_PARAM = "jobParameterValues";
	public static final String JOBSTATUS_EXPAND_PARAM = "jobStatuses";
	public static final String PARAMETERGROUP_EXPAND_PARAM = "parameterGroups";
	public static final String REPORT_EXPAND_PARAM = "reports";
	public static final String REPORTCATEGORY_EXPAND_PARAM = "reportCategories";
	public static final String REPORTPARAMETER_EXPAND_PARAM = "reportParameters";
	public static final String REPORTVERSION_EXPAND_PARAM = "reportVersions";
	public static final String ROLE_EXPAND_PARAM = "roles";
	public static final String SELECTIONLISTVALUE_EXPAND_PARAM = "selectionListValues";
	public static final String SUBSCRIPTION_EXPAND_PARAM = "subscriptions";
	public static final String SUBSCRIPTIONPARAMETER_EXPAND_PARAM = "subscriptionParameters";
	public static final String SUBSCRIPTIONPARAMETERVALUE_EXPAND_PARAM = "subscriptionParameterValues";
	/*
	 * Special "expand" parameter for the *field* ReportVersion.rptdesign.
	 */
	public static final String RPTDESIGN_EXPAND_PARAM = "rptdesign";

	/*
	 * This is the name of "showAll" query parameter used for all HTTP requests. 
	 */
	public static final String SHOWALL_QP_NAME = "showAll";
	/*
	 * This is the map key associated with the "showAll" array that contains all
	 * of the "showAll" query parameter values for an HTTP request. 
	 */
	public static final String SHOWALL_QP_KEY = "showAll";
	/*
	 * These are the values for each resource class that can be assigned to the
	 * "showall" query parameter.
	 */
	public static final String CONFIGURATION_SHOWALL_PARAM = CONFIGURATION_EXPAND_PARAM;
	public static final String DOCUMENTFORMAT_SHOWALL_PARAM = DOCUMENTFORMAT_EXPAND_PARAM;
	public static final String JOBPROCESSOR_SHOWALL_PARAM = JOBPROCESSOR_EXPAND_PARAM;
	public static final String JOB_SHOWALL_PARAM = JOB_EXPAND_PARAM;
	public static final String JOBPARAMETER_SHOWALL_PARAM = JOBPARAMETER_EXPAND_PARAM;
	public static final String JOBPARAMETERVALUE_SHOWALL_PARAM = JOBPARAMETERVALUE_EXPAND_PARAM;
	public static final String JOBSTATUS_SHOWALL_PARAM = JOBSTATUS_EXPAND_PARAM;
	public static final String PARAMETERGROUP_SHOWALL_PARAM = PARAMETERGROUP_EXPAND_PARAM;
	public static final String REPORT_SHOWALL_PARAM = REPORT_EXPAND_PARAM;
	public static final String REPORTCATEGORY_SHOWALL_PARAM = REPORTCATEGORY_EXPAND_PARAM;
	public static final String REPORTPARAMETER_SHOWALL_PARAM = REPORTPARAMETER_EXPAND_PARAM;
	public static final String REPORTVERSION_SHOWALL_PARAM = REPORTVERSION_EXPAND_PARAM;
	public static final String ROLE_SHOWALL_PARAM = ROLE_EXPAND_PARAM;
	public static final String SELECTIONLISTVALUE_SHOWALL_PARAM = SELECTIONLISTVALUE_EXPAND_PARAM;
	public static final String SUBSCRIPTION_SHOWALL_PARAM = SUBSCRIPTION_EXPAND_PARAM;
	public static final String SUBSCRIPTIONPARAMETER_SHOWALL_PARAM = SUBSCRIPTIONPARAMETER_EXPAND_PARAM;
	public static final String SUBSCRIPTIONPARAMETERVALUE_SHOWALL_PARAM = SUBSCRIPTIONPARAMETERVALUE_EXPAND_PARAM;

	/*
	 * This are the names of the collection resource pagination query parameter
	 * names for HTTP requests. 
	 */
	public static final String PAGE_OFFSET_QP_NAME = "offset";
	public static final String PAGE_LIMIT_QP_NAME = "limit";
	/*
	 * These are the map keys associated with the collection resource pagination
	 * query parameter names for HTTP requests. 
	 */
	public static final String PAGE_OFFSET_QP_KEY = PAGE_OFFSET_QP_NAME;
	public static final String PAGE_LIMIT_QP_KEY = PAGE_LIMIT_QP_NAME;

	/*
	 * This is the name of query parameter used to specify the value(s) of
	 * "parent" cascading parameters that are needed to fill in the dynamic
	 * selection list for the next parameter in the group.
	 */
	public static final String PARENTPARAMVALUE_QP_NAME = "parentParam";

	private final String path;
	private final String expandParam;
	private final String showAllParam;
	private final Class<?> entityClass;

	private ResourcePath(String path, String expandParam, String showAllParam, Class<?> entityClass) {
		this.path = path;
		this.expandParam = expandParam;
		this.showAllParam = showAllParam;
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

	/**
	 * Returns <tt>true</tt>  if the {@link List} <tt>expand</tt>contains an
	 * element corresponding to the resource class <tt>entityClass</tt>.
	 * 
	 * @param entityClass
	 * @param expand
	 * @return
	 */
	public static Boolean expand(Class<?> entityClass, List<String> expand) {
		if (expand == null) {
			return false;
		}
		for (ResourcePath resourcePath : ResourcePath.values()) {
			/*
			 * Cannot use equals because object may be proxied by Hibernate.
			 * Cannot use instanceof because type not fixed at compile time?
			 */
			if (resourcePath.entityClass.isAssignableFrom(entityClass)) {
				return expand.contains(resourcePath.expandParam);
			}
		}
		throw new IllegalArgumentException("No ResourcePath for entity class '" + entityClass.getName() + "'");
	}

	/**
	 * Returns <tt>true</tt>  if the {@link List} <tt>showAll</tt>contains an
	 * element corresponding to the resource class <tt>entityClass</tt>.
	 * 
	 * @param entityClass
	 * @param showAll
	 * @return
	 */
	public static Boolean showAll(Class<?> entityClass, List<String> showAll) {
		if (showAll == null) {
			return false;
		}
		for (ResourcePath resourcePath : ResourcePath.values()) {
			/*
			 * Cannot use equals because object may be proxied by Hibernate.
			 * Cannot use instanceof because type not fixed at compile time?
			 */
			if (resourcePath.entityClass.isAssignableFrom(entityClass)) {
				return showAll.contains(resourcePath.showAllParam);
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

	public String getShowAllParam() {
		return showAllParam;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

}
