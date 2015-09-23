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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.qfree.obo.report.util.DateUtils;

/**
 * The persistent class for the "subscription_parameter" database table.
 * 
 * Instances/rows represent report parameters for a report subscription, and
 * they are used to associate one or more values with a parameter..
 * 
 * @author Jeffrey Zelt
 * 
 */
@Entity
@Table(name = "subscription_parameter", schema = "reporting",
		uniqueConstraints = { @UniqueConstraint(columnNames = { "subscription_id", "report_parameter_id" },
				name = "uc_subscriptionparameter_subscription_parameter") })
@TypeDef(name = "uuid-custom", defaultForType = UUID.class, typeClass = UuidCustomType.class)
public class SubscriptionParameter implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Type(type = "uuid-custom")
	//	@Type(type = "pg-uuid")
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(name = "subscription_parameter_id", unique = true, nullable = false,
			columnDefinition = "uuid DEFAULT uuid_generate_v4()")
	private UUID subscriptionParameterId;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "subscription_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscriptionparameter_subscription") ,
			columnDefinition = "uuid")
	private Subscription subscription;

	@ManyToOne
	/*
	 * If columnDefinition="uuid" is omitted here and the database schema is 
	 * created by Hibernate (via hibernate.hbm2ddl.auto="create"), then the 
	 * PostgreSQL column definition includes "DEFAULT uuid_generate_v4()", which
	 * is not what is wanted.
	 */
	@NotNull
	@JoinColumn(name = "report_parameter_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_subscriptionparameter_reportparameter") ,
			columnDefinition = "uuid")
	private ReportParameter reportParameter;

	/*
	 * cascade = CascadeType.ALL:
	 *     Deleting a SubscriptionParameter will delete all of its 
	 *     SubscriptionParameterValue's.
	 */
	@OneToMany(mappedBy = "subscriptionParameter", cascade = CascadeType.ALL)
	private List<SubscriptionParameterValue> subscriptionParameterValues;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on", nullable = false)
	private Date createdOn;

	private SubscriptionParameter() {
	}

	public SubscriptionParameter(Subscription subscription, ReportParameter reportParameter) {
		this(subscription, reportParameter, DateUtils.nowUtc());
	}

	public SubscriptionParameter(Subscription subscription, ReportParameter reportParameter, Date createdOn) {
		this.subscription = subscription;
		this.reportParameter = reportParameter;
		this.createdOn = (createdOn != null) ? createdOn : DateUtils.nowUtc();
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public List<SubscriptionParameterValue> getSubscriptionParameterValues() {
		return subscriptionParameterValues;
	}

	public void setSubscriptionParameterValues(List<SubscriptionParameterValue> subscriptionParameterValues) {
		this.subscriptionParameterValues = subscriptionParameterValues;
	}

	public UUID getSubscriptionParameterId() {
		return subscriptionParameterId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionParameter [subscriptionParameterId=");
		builder.append(subscriptionParameterId);
		builder.append(", subscription=");
		builder.append(subscription);
		builder.append(", reportParameter=");
		builder.append(reportParameter);
		builder.append(", subscriptionParameterValues=");
		builder.append(subscriptionParameterValues);
		builder.append(", createdOn=");
		builder.append(createdOn);
		builder.append("]");
		return builder.toString();
	}

}
