package com.qfree.bo.report.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Subscription;

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

	/**
	 * The state of the Quartz trigger for the Subscription Job Processor.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	private String triggerState;

	public SchedulingStatusResource() {
	}

	public SchedulingStatusResource(
			Boolean scheduled,
			Date nextFireTime,
			String schedulingNotice,
			String triggerState) {
		this.scheduled = scheduled;
		this.nextFireTime = nextFireTime;
		this.schedulingNotice = schedulingNotice;
		this.triggerState = triggerState;
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

	public String getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
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
		builder.append(", triggerState=");
		builder.append(triggerState);
		builder.append("]");
		return builder.toString();
	}
}
