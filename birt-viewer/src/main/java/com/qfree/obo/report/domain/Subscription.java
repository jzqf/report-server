package com.qfree.obo.report.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.dto.SubscriptionResource;
import com.qfree.obo.report.util.DateUtils;

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
	 * runOnceAt, which is used to schedule a single delivery at a specified
	 * date and time.
	 */
	@Column(name = "cron_schedule", nullable = true, length = 80)
	private String cronSchedule;

	/**
	 * Used to associate a time zone with the cron schedule.
	 * 
	 * This string must be a legal value to pass to
	 * {@link java.time.ZoneId#of(String)}.
	 */
	@Column(name = "cron_schedule_zone_id", nullable = true, length = 80)
	private String cronScheduleZoneId;

	/**
	 * Date and time for running the subscription once at a particular date and
	 * time. This can be specified instead of cronSchedule, which is used to
	 * specify a recurring delivery schedule.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "run_once_at", nullable = true)
	private Date runOnceAt;

	/**
	 * E-mail address to which the rendered report will be sent. This allows a
	 * subscription to be set up that delivers reports to other than a role's
	 * primary e-mail address store with the Role entity.
	 */
	// @NotBlank
	@Column(name = "email", nullable = true, length = 160)
	private String email;

	/**
	 * Optional short description of the subscription. This can be useful if the
	 * same report is used with multiple subscriptions but with different
	 * schedules or different report parameters.
	 */
	@Column(name = "description", nullable = true, length = 1024)
	private String description;

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
	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
	private List<Job> jobs;

	private Subscription() {
	}

	public Subscription(
			Role role,
			ReportVersion reportVersion,
			DocumentFormat documentFormat,
			String cronSchedule,
			String cronScheduleZoneId,
			Date runOnceAt,
			String email,
			String description,
			Boolean enabled,
			Boolean active) {
		this(
				null,
				role,
				reportVersion,
				documentFormat,
				cronSchedule,
				cronScheduleZoneId,
				runOnceAt,
				email,
				description,
				enabled,
				active,
				DateUtils.nowUtc());
	}

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
				subscriptionResource.getCronSchedule(),
				subscriptionResource.getCronScheduleZoneId(),
				subscriptionResource.getRunOnceAt(),
				subscriptionResource.getEmail(),
				subscriptionResource.getDescription(),
				subscriptionResource.getEnabled(),
				subscriptionResource.getActive(),
				subscriptionResource.getCreatedOn());
	}

	public Subscription(
			UUID subscriptionId,
			Role role,
			ReportVersion reportVersion,
			DocumentFormat documentFormat,
			String cronSchedule,
			String cronScheduleZoneId,
			Date runOnceAt,
			String email,
			String description,
			Boolean enabled,
			Boolean active,
			Date createdOn) {
		super();
		this.subscriptionId = subscriptionId;
		this.role = role;
		this.reportVersion = reportVersion;
		this.documentFormat = documentFormat;
		this.cronSchedule = cronSchedule;
		this.cronScheduleZoneId = cronScheduleZoneId;
		this.runOnceAt = runOnceAt;
		this.email = email;
		this.description = description;
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

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
	}

	public String getCronScheduleZoneId() {
		return cronScheduleZoneId;
	}

	public void setCronScheduleZoneId(String cronScheduleZoneId) {
		this.cronScheduleZoneId = cronScheduleZoneId;
	}

	public Date getRunOnceAt() {
		return runOnceAt;
	}

	public void setRunOnceAt(Date runOnceAt) {
		this.runOnceAt = runOnceAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		builder.append(", cronSchedule=");
		builder.append(cronSchedule);
		builder.append(", cronScheduleZoneId=");
		builder.append(cronScheduleZoneId);
		builder.append(", runOnceAt=");
		builder.append(runOnceAt);
		builder.append(", email=");
		builder.append(email);
		builder.append(", description=");
		builder.append(description);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append(", active=");
		builder.append(active);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
