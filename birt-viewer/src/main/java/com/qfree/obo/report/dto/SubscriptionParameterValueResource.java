package com.qfree.obo.report.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterValueResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterValueResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID subscriptionParameterValueId;

	@XmlElement(name = "subscription")
	private SubscriptionResource subscriptionResource;

	@XmlElement(name = "reportParameter")
	private ReportParameterResource reportParameterResource;

	@XmlElement
	private Boolean booleanValue;

	@XmlElement
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date dateValue;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date datetimeValue;

	@XmlElement
	private Double floatValue;

	@XmlElement
	private Integer integerValue;

	@XmlElement
	private String stringValue;

	@XmlElement
	@XmlJavaTypeAdapter(TimeAdapter.class)
	private Date timeValue;

	@XmlElement
	private Integer yearNumber;

	@XmlElement
	private Integer yearsAgo;

	@XmlElement
	private Integer monthNumber;

	@XmlElement
	private Integer monthsAgo;

	@XmlElement
	private Integer weeksAgo;

	@XmlElement
	private Integer dayOfWeekInMonthOrdinal;

	@XmlElement
	private Integer dayOfWeekInMonthNumber;

	@XmlElement
	private Integer dayOfWeekNumber;

	@XmlElement
	private Integer dayOfMonthNumber;

	@XmlElement
	private Integer daysAgo;

	@XmlElement
	private Integer durationToAddYears;

	@XmlElement
	private Integer durationToAddMonths;

	@XmlElement
	private Integer durationToAddWeeks;

	@XmlElement
	private Integer durationToAddDays;

	@XmlElement
	private Integer durationToAddHours;

	@XmlElement
	private Integer durationToAddMinutes;

	@XmlElement
	private Integer durationToAddSeconds;

	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date createdOn;

	public SubscriptionParameterValueResource() {
	}

	public SubscriptionParameterValueResource(SubscriptionParameterValue subscriptionParameterValue, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		super(SubscriptionParameterValue.class, subscriptionParameterValue.getSubscriptionParameterValueId(), uriInfo,
				queryParams, apiVersion);

		List<String> expand = queryParams.get(ResourcePath.EXPAND_QP_KEY);

		String expandParam = ResourcePath.forEntity(SubscriptionParameterValue.class).getExpandParam();
		if (expand.contains(expandParam)) {
			/*
			 * Make a copy of the "expand" list from which expandParam is
			 * removed. This list should be used when creating new resources
			 * here, instead of the original "expand" list. This is done to 
			 * avoid the unlikely event of a long list of chained expansions
			 * across relations.
			 */
			List<String> expandElementRemoved = new ArrayList<>(expand);
			expandElementRemoved.remove(expandParam);
			/*
			 * Make a copy of the original queryParams Map and then replace the 
			 * "expand" array with expandElementRemoved.
			 */
			Map<String, List<String>> newQueryParams = new HashMap<>(queryParams);
			newQueryParams.put(ResourcePath.EXPAND_QP_KEY, expandElementRemoved);

			/*
			 * Clear apiVersion since its current value is not necessarily
			 * applicable to any resources associated with fields of this class. 
			 * See ReportResource for a more detailed explanation.
			 */
			apiVersion = null;

			/*
			 * Set the API version to null for any/all constructors for 
			 * resources associated with fields of this class. Passing null
			 * means that we want to use the DEFAULT ReST API version for the
			 * "href" attribute value. There is no reason why the ReST endpoint
			 * version associated with these fields should be the same as the 
			 * version specified for this particular resource class. We could 
			 * simply pass null below where apiVersion appears, but this is more 
			 * explicit and therefore clearer to the reader of this code.
			 */
			apiVersion = null;

			this.subscriptionParameterValueId = subscriptionParameterValue.getSubscriptionParameterValueId();

			this.subscriptionResource = new SubscriptionResource(subscriptionParameterValue.getSubscription(),
					uriInfo, newQueryParams, apiVersion);

			this.reportParameterResource = new ReportParameterResource(subscriptionParameterValue.getReportParameter(),
					uriInfo, newQueryParams, apiVersion);

			this.booleanValue = subscriptionParameterValue.getBooleanValue();
			this.dateValue = subscriptionParameterValue.getDateValue();
			this.datetimeValue = subscriptionParameterValue.getDatetimeValue();
			this.floatValue = subscriptionParameterValue.getFloatValue();
			this.integerValue = subscriptionParameterValue.getIntegerValue();
			this.stringValue = subscriptionParameterValue.getStringValue();
			this.timeValue = subscriptionParameterValue.getTimeValue();
			this.yearNumber = subscriptionParameterValue.getYearNumber();
			this.yearsAgo = subscriptionParameterValue.getYearsAgo();
			this.monthNumber = subscriptionParameterValue.getMonthNumber();
			this.monthsAgo = subscriptionParameterValue.getMonthsAgo();
			this.weeksAgo = subscriptionParameterValue.getWeeksAgo();
			this.dayOfWeekInMonthOrdinal = subscriptionParameterValue.getDayOfWeekInMonthOrdinal();
			this.dayOfWeekInMonthNumber = subscriptionParameterValue.getDayOfWeekInMonthNumber();
			this.dayOfWeekNumber = subscriptionParameterValue.getDayOfWeekNumber();
			this.dayOfMonthNumber = subscriptionParameterValue.getDayOfMonthNumber();
			this.daysAgo = subscriptionParameterValue.getDaysAgo();
			this.durationToAddYears = subscriptionParameterValue.getDurationToAddYears();
			this.durationToAddMonths = subscriptionParameterValue.getDurationToAddMonths();
			this.durationToAddWeeks = subscriptionParameterValue.getDurationToAddWeeks();
			this.durationToAddDays = subscriptionParameterValue.getDurationToAddDays();
			this.durationToAddHours = subscriptionParameterValue.getDurationToAddHours();
			this.durationToAddMinutes = subscriptionParameterValue.getDurationToAddMinutes();
			this.durationToAddSeconds = subscriptionParameterValue.getDurationToAddSeconds();
			this.createdOn = subscriptionParameterValue.getCreatedOn();
		}
	}
}
