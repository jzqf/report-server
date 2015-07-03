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
import org.hibernate.validator.constraints.NotBlank;

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
	//	@Type(type = "pg-uuid")
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
			foreignKey = @ForeignKey(name = "fk_subscription_role"),
			columnDefinition = "uuid")
	private Role role;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	//	@NotNull
	//	@JoinColumn(name = "report_id", nullable = false,
	//			foreignKey = @ForeignKey(name = "fk_subscription_report"),
	//			columnDefinition = "uuid")
	//	private Report report;
	@NotNull
	@JoinColumn(name = "report_version_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscription_reportversion"),
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
			foreignKey = @ForeignKey(name = "fk_subscription_documentformat"),
			columnDefinition = "uuid")
	private DocumentFormat documentFormat;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a Role will delete all of its SubscriptionParameterValue's.
	 */
	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL)
	private List<SubscriptionParameterValue> subscriptionParameterValues;

	/**
	 * "cron" expression used to specify the delivery schedule for the 
	 * subscription. This can be specified instead of (or in addition to?) 
	 * runOnceAt, which is used to schedule a single delivery at a specified 
	 * date and time.
	 */
	@Column(name = "cron_schedule", nullable = true, length = 80)
	private String cronSchedule;

	/**
	 * Date and time for running the subscription once at a particular date and
	 * time. This can be specified instead of (or in addition to?) cronSchedule,
	 * which is used to specify a recurring delivery schedule.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "run_once_at", nullable = true)
	private Date runOnceAt;

	/**
	 * E-mail address to which the rendered report will be sent. This allows a
	 * subscription to be set up that delivers reports to other than a role's
	 * primary e-mail address store with the Role entity.
	 */
	@NotBlank
	@Column(name = "email", nullable = false, length = 80)
	private String email;

	/**
	 * Optional short description of the subscription. This can be useful if the
	 * same report is used with multiple subscriptions but with different 
	 * schedules or different report parameters.
	 */
	@Column(name = "description", nullable = true, length = 80)
	private String description;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private Subscription() {
	}

	public Subscription(Role role, ReportVersion reportVersion, DocumentFormat documentFormat, String cronSchedule,
			Date runOnceAt,
			String email, String description) {
		this(role, reportVersion, documentFormat, cronSchedule, runOnceAt, email, description, DateUtils.nowUtc());
	}

	public Subscription(Role role, ReportVersion reportVersion, DocumentFormat documentFormat, String cronSchedule,
			Date runOnceAt,
			String email, String description, Date createdOn) {
		this.role = role;
		this.reportVersion = reportVersion;
		this.documentFormat = documentFormat;
		this.cronSchedule = cronSchedule;
		this.runOnceAt = runOnceAt;
		this.email = email;
		this.description = description;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public UUID getSubscriptionId() {
		return this.subscriptionId;
	}

	public Date getCreatedOn() {
		return this.createdOn;
	}

	public ReportVersion getReportVersion() {
		return this.reportVersion;
	}

	public void setReportVersion(ReportVersion reportVersion) {
		this.reportVersion = reportVersion;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public DocumentFormat getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(DocumentFormat documentFormat) {
		this.documentFormat = documentFormat;
	}

	public List<SubscriptionParameterValue> getSubscriptionParameterValues() {
		return subscriptionParameterValues;
	}

	public void setSubscriptionParameterValues(List<SubscriptionParameterValue> subscriptionParameterValues) {
		this.subscriptionParameterValues = subscriptionParameterValues;
	}

	public String getCronSchedule() {
		return cronSchedule;
	}

	public void setCronSchedule(String cronSchedule) {
		this.cronSchedule = cronSchedule;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RoleReport [role=");
		builder.append(role);
		builder.append(", reportVersion=");
		builder.append(reportVersion);
		builder.append("]");
		return builder.toString();
	}

}
