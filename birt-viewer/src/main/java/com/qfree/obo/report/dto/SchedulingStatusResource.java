package com.qfree.obo.report.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Subscription;

@XmlRootElement
//public class SchedulingStatusResource extends AbstractBaseResource {
public class SchedulingStatusResource {

	private static final Logger logger = LoggerFactory.getLogger(SchedulingStatusResource.class);

	//	@XmlElement
	//	@XmlJavaTypeAdapter(UuidAdapter.class)
	//	private UUID schedulingStatusId;

	/**
	 * <code>true</code> if the {@link Subscription} is currently registered
	 * with the Quartz scheduler; <code>false</code> otherwise.
	 * 
	 * <p>
	 * This could be <code>false</code> even if <code>enabled=true</code> if
	 * there is a problem and it was not possible to schedule the
	 * {@link Subscription}.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	private Boolean scheduled;

	/**
	 * The next time the subscription will run.
	 * 
	 * <p>
	 * This will only be present if the subscription is scheduled, i.e.,
	 * <code>{@link #scheduled}=true</code>.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date nextFireTime;

	/**
	 * A message that provides information about the state of a scheduled
	 * subscription.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	private String schedulingNotice;

	public SchedulingStatusResource() {
	}

	public SchedulingStatusResource(
			Boolean scheduled,
			Date nextFireTime,
			String schedulingNotice) {
		this.scheduled = scheduled;
		this.nextFireTime = nextFireTime;
		this.schedulingNotice = schedulingNotice;
	}

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
	}

	public Date getNextFireTime() {
		return nextFireTime;
	}

	public void setNextFireTime(Date nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public String getSchedulingNotice() {
		return schedulingNotice;
	}

	public void setSchedulingNotice(String schedulingNotice) {
		this.schedulingNotice = schedulingNotice;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SchedulingStatusResource [scheduled=");
		builder.append(scheduled);
		builder.append(", nextFireTime=");
		builder.append(nextFireTime);
		builder.append(", schedulingNotice=");
		builder.append(schedulingNotice);
		builder.append("]");
		return builder.toString();
	}
}
