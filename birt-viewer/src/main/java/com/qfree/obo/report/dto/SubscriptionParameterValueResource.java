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

import com.qfree.obo.report.domain.SubscriptionParameter;
import com.qfree.obo.report.domain.SubscriptionParameterValue;
import com.qfree.obo.report.rest.server.RestUtils.RestApiVersion;

@XmlRootElement
public class SubscriptionParameterValueResource extends AbstractBaseResource {

	private static final Logger logger = LoggerFactory.getLogger(SubscriptionParameterValueResource.class);

	@XmlElement
	@XmlJavaTypeAdapter(UuidAdapter.class)
	private UUID subscriptionParameterValueId;

	@XmlElement(name = "subscriptionParameter")
	private SubscriptionParameterResource subscriptionParameterResource;

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

			this.subscriptionParameterResource = new SubscriptionParameterResource(
					subscriptionParameterValue.getSubscriptionParameter(),
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

	public static List<SubscriptionParameterValueResource> listFromSubscriptionParameter(SubscriptionParameter subscriptionParameter,
			UriInfo uriInfo, Map<String, List<String>> queryParams, RestApiVersion apiVersion) {
		if (subscriptionParameter.getSubscriptionParameterValues() != null) {
			List<SubscriptionParameterValue> subscriptionParameterValues = subscriptionParameter
					.getSubscriptionParameterValues();
			List<SubscriptionParameterValueResource> subscriptionParameterValueResources = new ArrayList<>(
					subscriptionParameterValues.size());
			for (SubscriptionParameterValue subscriptionParameterValue : subscriptionParameterValues) {
				// List<String> showAll =
				// queryParams.get(ResourcePath.SHOWALL_QP_KEY);
				// if (subscriptionParameterValue.isActive() ||
				// RestUtils.FILTER_INACTIVE_RECORDS == false ||
				// ResourcePath.showAll(SubscriptionParameterValue.class,
				// showAll)) {
				subscriptionParameterValueResources.add(
						new SubscriptionParameterValueResource(subscriptionParameterValue, uriInfo, queryParams,
								apiVersion));
				// }
			}
			return subscriptionParameterValueResources;
		} else {
			return null;
		}
	}

	public UUID getSubscriptionParameterValueId() {
		return subscriptionParameterValueId;
	}

	public void setSubscriptionParameterValueId(UUID subscriptionParameterValueId) {
		this.subscriptionParameterValueId = subscriptionParameterValueId;
	}

	public SubscriptionParameterResource getSubscriptionParameterResource() {
		return subscriptionParameterResource;
	}

	public void setSubscriptionParameterResource(SubscriptionParameterResource subscriptionParameterResource) {
		this.subscriptionParameterResource = subscriptionParameterResource;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getDatetimeValue() {
		return datetimeValue;
	}

	public void setDatetimeValue(Date datetimeValue) {
		this.datetimeValue = datetimeValue;
	}

	public Double getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public Integer getYearNumber() {
		return yearNumber;
	}

	public void setYearNumber(Integer yearNumber) {
		this.yearNumber = yearNumber;
	}

	public Integer getYearsAgo() {
		return yearsAgo;
	}

	public void setYearsAgo(Integer yearsAgo) {
		this.yearsAgo = yearsAgo;
	}

	public Integer getMonthNumber() {
		return monthNumber;
	}

	public void setMonthNumber(Integer monthNumber) {
		this.monthNumber = monthNumber;
	}

	public Integer getMonthsAgo() {
		return monthsAgo;
	}

	public void setMonthsAgo(Integer monthsAgo) {
		this.monthsAgo = monthsAgo;
	}

	public Integer getWeeksAgo() {
		return weeksAgo;
	}

	public void setWeeksAgo(Integer weeksAgo) {
		this.weeksAgo = weeksAgo;
	}

	public Integer getDayOfWeekInMonthOrdinal() {
		return dayOfWeekInMonthOrdinal;
	}

	public void setDayOfWeekInMonthOrdinal(Integer dayOfWeekInMonthOrdinal) {
		this.dayOfWeekInMonthOrdinal = dayOfWeekInMonthOrdinal;
	}

	public Integer getDayOfWeekInMonthNumber() {
		return dayOfWeekInMonthNumber;
	}

	public void setDayOfWeekInMonthNumber(Integer dayOfWeekInMonthNumber) {
		this.dayOfWeekInMonthNumber = dayOfWeekInMonthNumber;
	}

	public Integer getDayOfWeekNumber() {
		return dayOfWeekNumber;
	}

	public void setDayOfWeekNumber(Integer dayOfWeekNumber) {
		this.dayOfWeekNumber = dayOfWeekNumber;
	}

	public Integer getDayOfMonthNumber() {
		return dayOfMonthNumber;
	}

	public void setDayOfMonthNumber(Integer dayOfMonthNumber) {
		this.dayOfMonthNumber = dayOfMonthNumber;
	}

	public Integer getDaysAgo() {
		return daysAgo;
	}

	public void setDaysAgo(Integer daysAgo) {
		this.daysAgo = daysAgo;
	}

	public Integer getDurationToAddYears() {
		return durationToAddYears;
	}

	public void setDurationToAddYears(Integer durationToAddYears) {
		this.durationToAddYears = durationToAddYears;
	}

	public Integer getDurationToAddMonths() {
		return durationToAddMonths;
	}

	public void setDurationToAddMonths(Integer durationToAddMonths) {
		this.durationToAddMonths = durationToAddMonths;
	}

	public Integer getDurationToAddWeeks() {
		return durationToAddWeeks;
	}

	public void setDurationToAddWeeks(Integer durationToAddWeeks) {
		this.durationToAddWeeks = durationToAddWeeks;
	}

	public Integer getDurationToAddDays() {
		return durationToAddDays;
	}

	public void setDurationToAddDays(Integer durationToAddDays) {
		this.durationToAddDays = durationToAddDays;
	}

	public Integer getDurationToAddHours() {
		return durationToAddHours;
	}

	public void setDurationToAddHours(Integer durationToAddHours) {
		this.durationToAddHours = durationToAddHours;
	}

	public Integer getDurationToAddMinutes() {
		return durationToAddMinutes;
	}

	public void setDurationToAddMinutes(Integer durationToAddMinutes) {
		this.durationToAddMinutes = durationToAddMinutes;
	}

	public Integer getDurationToAddSeconds() {
		return durationToAddSeconds;
	}

	public void setDurationToAddSeconds(Integer durationToAddSeconds) {
		this.durationToAddSeconds = durationToAddSeconds;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionParameterValueResource [subscriptionParameterValueId=");
		builder.append(subscriptionParameterValueId);
		builder.append(", subscriptionParameterResource=");
		builder.append(subscriptionParameterResource);
		builder.append(", booleanValue=");
		builder.append(booleanValue);
		builder.append(", dateValue=");
		builder.append(dateValue);
		builder.append(", datetimeValue=");
		builder.append(datetimeValue);
		builder.append(", floatValue=");
		builder.append(floatValue);
		builder.append(", integerValue=");
		builder.append(integerValue);
		builder.append(", stringValue=");
		builder.append(stringValue);
		builder.append(", timeValue=");
		builder.append(timeValue);
		builder.append(", yearNumber=");
		builder.append(yearNumber);
		builder.append(", yearsAgo=");
		builder.append(yearsAgo);
		builder.append(", monthNumber=");
		builder.append(monthNumber);
		builder.append(", monthsAgo=");
		builder.append(monthsAgo);
		builder.append(", weeksAgo=");
		builder.append(weeksAgo);
		builder.append(", dayOfWeekInMonthOrdinal=");
		builder.append(dayOfWeekInMonthOrdinal);
		builder.append(", dayOfWeekInMonthNumber=");
		builder.append(dayOfWeekInMonthNumber);
		builder.append(", dayOfWeekNumber=");
		builder.append(dayOfWeekNumber);
		builder.append(", dayOfMonthNumber=");
		builder.append(dayOfMonthNumber);
		builder.append(", daysAgo=");
		builder.append(daysAgo);
		builder.append(", durationToAddYears=");
		builder.append(durationToAddYears);
		builder.append(", durationToAddMonths=");
		builder.append(durationToAddMonths);
		builder.append(", durationToAddWeeks=");
		builder.append(durationToAddWeeks);
		builder.append(", durationToAddDays=");
		builder.append(durationToAddDays);
		builder.append(", durationToAddHours=");
		builder.append(durationToAddHours);
		builder.append(", durationToAddMinutes=");
		builder.append(durationToAddMinutes);
		builder.append(", durationToAddSeconds=");
		builder.append(durationToAddSeconds);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
