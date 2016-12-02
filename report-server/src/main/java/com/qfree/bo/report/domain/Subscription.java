package com.qfree.bo.report.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.dto.SubscriptionResource;
import com.qfree.bo.report.exceptions.ResourceFilterExecutionException;
import com.qfree.bo.report.util.DateUtils;
import com.qfree.bo.report.util.RestUtils;

/**
 * The persistent class for the "subscription" database table.
 * 
 * Instances/rows specify which reports are available to each role.
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "subscription", schema = "reporting")
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class Subscription implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(Subscription.class);

	@Id
	@Type(type = "uuid-custom")
	// @Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "subscription_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID subscriptionId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "role_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscription_role") ,
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	// @NotNull
	// @JoinColumn(name = "report_id", nullable = false,
	// foreignKey = @ForeignKey(name = "fk_subscription_report"),
	// columnDefinition = "uuid")
	// private Report report;
	@NotNull
	@JoinColumn(name = "report_version_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscription_reportversion") ,
			columnDefinition = "uuid")
	private ReportVersion reportVersion;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "document_format_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscription_documentformat") ,
			columnDefinition = "uuid")
	private DocumentFormat documentFormat;

	/**
	 * "cron" expression used to specify the delivery schedule for the
	 * subscription. This can be specified instead of (or in addition to?)
	 * deliveryDatetimeRunAt, which is used to schedule a single delivery at a specified
	 * date and time.
	 */
	@Column(name = "delivery_cron_schedule", nullable = true, length = 80)
	private String deliveryCronSchedule;

	/**
	 * Date and time for running the subscription once at a particular date and
	 * time. This can be specified instead of deliveryCronSchedule, which is used to
	 * specify a recurring delivery schedule.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "delivery_datetime_run_at", nullable = true)
	private Date deliveryDatetimeRunAt;

	/**
	 * Used to associate a time zone with the cron schedule.
	 * 
	 * This string must be a legal value to pass to
	 * {@link java.time.ZoneId#of(String)}.
	 */
	@Column(name = "delivery_time_zone_id", nullable = true, length = 80)
	private String deliveryTimeZoneId;

	/**
	 * E-mail address to which the rendered report will be sent. This allows a
	 * subscription to be set up that delivers reports to other than a role's
	 * primary e-mail address store with the Role entity.
	 */
	// @NotBlank
	@Column(name = "email_address", nullable = true, length = 160)
	private String emailAddress;

	/**
	 * Optional short description of the subscription. This can be useful if the
	 * same report is used with multiple subscriptions but with different
	 * schedules or different report parameters.
	 */
	@Column(name = "description", nullable = true, length = 1024)
	private String description;

	@Column(name = "custom_report_parameter_1_name", nullable = true, length = 64)
	private String customReportParameter1_name;

	@Column(name = "custom_report_parameter_1_value", nullable = true, length = 64)
	private String customReportParameter1_value;

	@Column(name = "custom_report_parameter_2_name", nullable = true, length = 64)
	private String customReportParameter2_name;

	@Column(name = "custom_report_parameter_2_value", nullable = true, length = 64)
	private String customReportParameter2_value;

	@Column(name = "custom_report_parameter_3_name", nullable = true, length = 64)
	private String customReportParameter3_name;

	@Column(name = "custom_report_parameter_3_value", nullable = true, length = 64)
	private String customReportParameter3_value;

	@Column(name = "custom_report_parameter_4_name", nullable = true, length = 64)
	private String customReportParameter4_name;

	@Column(name = "custom_report_parameter_4_value", nullable = true, length = 64)
	private String customReportParameter4_value;

	@Column(name = "custom_report_parameter_5_name", nullable = true, length = 64)
	private String customReportParameter5_name;

	@Column(name = "custom_report_parameter_5_value", nullable = true, length = 64)
	private String customReportParameter5_value;

	@Column(name = "custom_report_parameter_6_name", nullable = true, length = 64)
	private String customReportParameter6_name;

	@Column(name = "custom_report_parameter_6_value", nullable = true, length = 64)
	private String customReportParameter6_value;

	@Column(name = "custom_report_parameter_7_name", nullable = true, length = 64)
	private String customReportParameter7_name;

	@Column(name = "custom_report_parameter_7_value", nullable = true, length = 64)
	private String customReportParameter7_value;

	@Column(name = "custom_report_parameter_8_name", nullable = true, length = 64)
	private String customReportParameter8_name;

	@Column(name = "custom_report_parameter_8_value", nullable = true, length = 64)
	private String customReportParameter8_value;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private Boolean enabled;

	@NotNull
	@Column(name = "active", nullable = false)
	private Boolean active;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Subscription will delete all of its 
	 *     SubscriptionParameter's.
	 */
	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
	private List<SubscriptionParameter> subscriptionParameters;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Subscription will delete all of its 
	 *     Job's.
	 */
	@OrderBy("jobId ASC")
	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
	private List<Job> jobs;

	private Subscription() {
	}

	//	public Subscription(
	//			Role role,
	//			ReportVersion reportVersion,
	//			DocumentFormat documentFormat,
	//			String deliveryCronSchedule,
	//			String deliveryTimeZoneId,
	//			Date deliveryDatetimeRunAt,
	//			String emailAddress,
	//			String description,
	//			Boolean enabled,
	//			Boolean active) {
	//		this(
	//				null,
	//				role,
	//				reportVersion,
	//				documentFormat,
	//				deliveryCronSchedule,
	//				deliveryTimeZoneId,
	//				deliveryDatetimeRunAt,
	//				emailAddress,
	//				description,
	//				enabled,
	//				active,
	//				DateUtils.nowUtc());
	//	}

	public Subscription(
			SubscriptionResource subscriptionResource,
			DocumentFormat documentFormat,
			ReportVersion reportVersion,
			Role role) {
		this(
				subscriptionResource.getSubscriptionId(),
				role,
				reportVersion,
				documentFormat,
				subscriptionResource.getDeliveryCronSchedule(),
				subscriptionResource.getDeliveryTimeZoneId(),
				subscriptionResource.getDeliveryDatetimeRunAt(),
				subscriptionResource.getEmailAddress(),
				subscriptionResource.getDescription(),
				subscriptionResource.getCustomReportParameter1_name(),
				subscriptionResource.getCustomReportParameter1_value(),
				subscriptionResource.getCustomReportParameter2_name(),
				subscriptionResource.getCustomReportParameter2_value(),
				subscriptionResource.getCustomReportParameter3_name(),
				subscriptionResource.getCustomReportParameter3_value(),
				subscriptionResource.getCustomReportParameter4_name(),
				subscriptionResource.getCustomReportParameter4_value(),
				subscriptionResource.getCustomReportParameter5_name(),
				subscriptionResource.getCustomReportParameter5_value(),
				subscriptionResource.getCustomReportParameter6_name(),
				subscriptionResource.getCustomReportParameter6_value(),
				subscriptionResource.getCustomReportParameter7_name(),
				subscriptionResource.getCustomReportParameter7_value(),
				subscriptionResource.getCustomReportParameter8_name(),
				subscriptionResource.getCustomReportParameter8_value(),
				subscriptionResource.getEnabled(),
				subscriptionResource.getActive(),
				subscriptionResource.getCreatedOn());
	}

	public Subscription(
			UUID subscriptionId,
			Role role,
			ReportVersion reportVersion,
			DocumentFormat documentFormat,
			String deliveryCronSchedule,
			String deliveryTimeZoneId,
			Date deliveryDatetimeRunAt,
			String emailAddress,
			String description,
			String customReportParameter1_name,
			String customReportParameter1_value,
			String customReportParameter2_name,
			String customReportParameter2_value,
			String customReportParameter3_name,
			String customReportParameter3_value,
			String customReportParameter4_name,
			String customReportParameter4_value,
			String customReportParameter5_name,
			String customReportParameter5_value,
			String customReportParameter6_name,
			String customReportParameter6_value,
			String customReportParameter7_name,
			String customReportParameter7_value,
			String customReportParameter8_name,
			String customReportParameter8_value,
			Boolean enabled,
			Boolean active,
			Date createdOn) {
		super();
		this.subscriptionId = subscriptionId;
		this.role = role;
		this.reportVersion = reportVersion;
		this.documentFormat = documentFormat;
		this.deliveryCronSchedule = deliveryCronSchedule;
		this.deliveryTimeZoneId = deliveryTimeZoneId;
		this.deliveryDatetimeRunAt = deliveryDatetimeRunAt;
		this.emailAddress = emailAddress;
		this.description = description;
		this.customReportParameter1_name = customReportParameter1_name;
		this.customReportParameter1_value = customReportParameter1_value;
		this.customReportParameter2_name = customReportParameter2_name;
		this.customReportParameter2_value = customReportParameter2_value;
		this.customReportParameter3_name = customReportParameter3_name;
		this.customReportParameter3_value = customReportParameter3_value;
		this.customReportParameter4_name = customReportParameter4_name;
		this.customReportParameter4_value = customReportParameter4_value;
		this.customReportParameter5_name = customReportParameter5_name;
		this.customReportParameter5_value = customReportParameter5_value;
		this.customReportParameter6_name = customReportParameter6_name;
		this.customReportParameter6_value = customReportParameter6_value;
		this.customReportParameter7_name = customReportParameter7_name;
		this.customReportParameter7_value = customReportParameter7_value;
		this.customReportParameter8_name = customReportParameter8_name;
		this.customReportParameter8_value = customReportParameter8_value;
		this.enabled = (enabled != null) ? enabled : false;
		this.active = (active != null) ? active : true;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ReportVersion getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(ReportVersion reportVersion) {
		this.reportVersion = reportVersion;
	}

	public DocumentFormat getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(DocumentFormat documentFormat) {
		this.documentFormat = documentFormat;
	}

	public String getDeliveryCronSchedule() {
		return deliveryCronSchedule;
	}

	public void setDeliveryCronSchedule(String deliveryCronSchedule) {
		this.deliveryCronSchedule = deliveryCronSchedule;
	}

	public String getDeliveryTimeZoneId() {
		return deliveryTimeZoneId;
	}

	public void setDeliveryTimeZoneId(String deliveryTimeZoneId) {
		this.deliveryTimeZoneId = deliveryTimeZoneId;
	}

	public Date getDeliveryDatetimeRunAt() {
		return deliveryDatetimeRunAt;
	}

	public void setDeliveryDatetimeRunAt(Date deliveryDatetimeRunAt) {
		this.deliveryDatetimeRunAt = deliveryDatetimeRunAt;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustomReportParameter1_name() {
		return customReportParameter1_name;
	}

	public void setCustomReportParameter1_name(String customReportParameter1_name) {
		this.customReportParameter1_name = customReportParameter1_name;
	}

	public String getCustomReportParameter1_value() {
		return customReportParameter1_value;
	}

	public void setCustomReportParameter1_value(String customReportParameter1_value) {
		this.customReportParameter1_value = customReportParameter1_value;
	}

	public String getCustomReportParameter2_name() {
		return customReportParameter2_name;
	}

	public void setCustomReportParameter2_name(String customReportParameter2_name) {
		this.customReportParameter2_name = customReportParameter2_name;
	}

	public String getCustomReportParameter2_value() {
		return customReportParameter2_value;
	}

	public void setCustomReportParameter2_value(String customReportParameter2_value) {
		this.customReportParameter2_value = customReportParameter2_value;
	}

	public String getCustomReportParameter3_name() {
		return customReportParameter3_name;
	}

	public void setCustomReportParameter3_name(String customReportParameter3_name) {
		this.customReportParameter3_name = customReportParameter3_name;
	}

	public String getCustomReportParameter3_value() {
		return customReportParameter3_value;
	}

	public void setCustomReportParameter3_value(String customReportParameter3_value) {
		this.customReportParameter3_value = customReportParameter3_value;
	}

	public String getCustomReportParameter4_name() {
		return customReportParameter4_name;
	}

	public void setCustomReportParameter4_name(String customReportParameter4_name) {
		this.customReportParameter4_name = customReportParameter4_name;
	}

	public String getCustomReportParameter4_value() {
		return customReportParameter4_value;
	}

	public void setCustomReportParameter4_value(String customReportParameter4_value) {
		this.customReportParameter4_value = customReportParameter4_value;
	}

	public String getCustomReportParameter5_name() {
		return customReportParameter5_name;
	}

	public void setCustomReportParameter5_name(String customReportParameter5_name) {
		this.customReportParameter5_name = customReportParameter5_name;
	}

	public String getCustomReportParameter5_value() {
		return customReportParameter5_value;
	}

	public void setCustomReportParameter5_value(String customReportParameter5_value) {
		this.customReportParameter5_value = customReportParameter5_value;
	}

	public String getCustomReportParameter6_name() {
		return customReportParameter6_name;
	}

	public void setCustomReportParameter6_name(String customReportParameter6_name) {
		this.customReportParameter6_name = customReportParameter6_name;
	}

	public String getCustomReportParameter6_value() {
		return customReportParameter6_value;
	}

	public void setCustomReportParameter6_value(String customReportParameter6_value) {
		this.customReportParameter6_value = customReportParameter6_value;
	}

	public String getCustomReportParameter7_name() {
		return customReportParameter7_name;
	}

	public void setCustomReportParameter7_name(String customReportParameter7_name) {
		this.customReportParameter7_name = customReportParameter7_name;
	}

	public String getCustomReportParameter7_value() {
		return customReportParameter7_value;
	}

	public void setCustomReportParameter7_value(String customReportParameter7_value) {
		this.customReportParameter7_value = customReportParameter7_value;
	}

	public String getCustomReportParameter8_name() {
		return customReportParameter8_name;
	}

	public void setCustomReportParameter8_name(String customReportParameter8_name) {
		this.customReportParameter8_name = customReportParameter8_name;
	}

	public String getCustomReportParameter8_value() {
		return customReportParameter8_value;
	}

	public void setCustomReportParameter8_value(String customReportParameter8_value) {
		this.customReportParameter8_value = customReportParameter8_value;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public List<SubscriptionParameter> getSubscriptionParameters() {
		return subscriptionParameters;
	}

	public void setSubscriptionParameters(List<SubscriptionParameter> subscriptionParameters) {
		this.subscriptionParameters = subscriptionParameters;
	}

	/**
	 * Returns {@link List} of {@link Job} entities associated with the
	 * {@link Subscription}.
	 * 
	 * Filtering can be performed on the set of all {@link Job} entities
	 * associated with the {@link Subscription}.
	 * 
	 * @param filterConditions
	 * @return
	 * @throws ResourceFilterExecutionException
	 */
	public List<Job> getJobs(List<List<Map<String, String>>> filterConditions) throws ResourceFilterExecutionException {
		return Job.getFilteredJobs(getJobs(), filterConditions);
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public UUID getSubscriptionId() {
		return subscriptionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Subscription [subscriptionId=");
		builder.append(subscriptionId);
		builder.append(", roleId=");
		builder.append(role.getRoleId());
		builder.append(", reportVersionId=");
		builder.append(reportVersion.getReportVersionId());
		builder.append(", documentFormat=");
		builder.append(documentFormat);
		builder.append(", deliveryCronSchedule=");
		builder.append(deliveryCronSchedule);
		builder.append(", deliveryTimeZoneId=");
		builder.append(deliveryTimeZoneId);
		builder.append(", deliveryDatetimeRunAt=");
		builder.append(deliveryDatetimeRunAt);
		builder.append(", emailAddress=");
		builder.append(emailAddress);
		builder.append(", description=");
		builder.append(description);
		builder.append(", customReportParameter1_name=");
		builder.append(customReportParameter1_name);
		builder.append(", customReportParameter1_value=");
		builder.append(customReportParameter1_value);
		builder.append(", customReportParameter2_name=");
		builder.append(customReportParameter2_name);
		builder.append(", customReportParameter2_value=");
		builder.append(customReportParameter2_value);
		builder.append(", customReportParameter3_name=");
		builder.append(customReportParameter3_name);
		builder.append(", customReportParameter3_value=");
		builder.append(customReportParameter3_value);
		builder.append(", customReportParameter4_name=");
		builder.append(customReportParameter4_name);
		builder.append(", customReportParameter4_value=");
		builder.append(customReportParameter4_value);
		builder.append(", customReportParameter5_name=");
		builder.append(customReportParameter5_name);
		builder.append(", customReportParameter5_value=");
		builder.append(customReportParameter5_value);
		builder.append(", customReportParameter6_name=");
		builder.append(customReportParameter6_name);
		builder.append(", customReportParameter6_value=");
		builder.append(customReportParameter6_value);
		builder.append(", customReportParameter7_name=");
		builder.append(customReportParameter7_name);
		builder.append(", customReportParameter7_value=");
		builder.append(customReportParameter7_value);
		builder.append(", customReportParameter8_name=");
		builder.append(customReportParameter8_name);
		builder.append(", customReportParameter8_value=");
		builder.append(customReportParameter8_value);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns a {@link List} of filtered {@link Subscription} entities given an
	 * unfiltered list and a set of filter conditions.
	 * 
	 * This method first sets up one list for each attribute on which the list
	 * of {@link Job} entities can be filtered on. Then the filtering is
	 * performed by a call to a generic static method.
	 * 
	 * @param filterConditions
	 * @return
	 * @throws ResourceFilterExecutionException
	 */
	public static List<Subscription> getFilteredSubscriptions(
			List<Subscription> unfilteredSubscriptions,
			List<List<Map<String, String>>> filterConditions)
					throws ResourceFilterExecutionException {

		/*
		 * The returned list must be ordered in case pagination is used for the 
		 * collection resource created from list of filtered entities. Even if
		 * it is not necessary to paginate the list (because there are too few
		 * list elements), we still sort the list for consistency. The only
		 * sensible order is chronological order. Since the Job entities has a 
		 * Long primary key, we could also sort by id.
		 */
		//Comparator<Subscription> chronological = (Subscription subscription1,
		//		Subscription subscription2) -> subscription1.getCreatedOn().compareTo(subscription2.getCreatedOn());
		Comparator<Subscription> chronologicalReversed = (Subscription subscription1,
				Subscription subscription2) -> subscription2.getCreatedOn().compareTo(subscription1.getCreatedOn());
		Comparator<Subscription> comparator = chronologicalReversed;

		if (filterConditions == null || filterConditions.size() == 0) {
			/*
			 * No filtering is required, but we still need to sort.
			 */
			unfilteredSubscriptions.sort(comparator);
			return unfilteredSubscriptions; // sorted, but not filtered
		}

		List<Object> roleIds = new ArrayList<>(unfilteredSubscriptions.size());
		for (Subscription subscription : unfilteredSubscriptions) {
			roleIds.add(subscription.getRole().getRoleId());
		}
		Map<String, List<Object>> filterableAttributes = new HashMap<>(1);
		/*
		 * Here, the Map keys used *must* agree with the filter attributes used
		 * in the value assigned to the "filter" query parameter in the resource
		 * URI.
		 */
		filterableAttributes.put("roleId", roleIds);

		return RestUtils.filterEntities(unfilteredSubscriptions, filterConditions, filterableAttributes, comparator,
				Subscription.class);
	}
}
