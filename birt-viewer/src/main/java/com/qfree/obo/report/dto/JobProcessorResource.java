package com.qfree.obo.report.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class JobProcessorResource extends AbstractBaseResource {
	//public class JobProcessorResource {

	private static final Logger logger = LoggerFactory.getLogger(JobProcessorResource.class);

	//	@XmlElement
	//	@XmlJavaTypeAdapter(UuidAdapter.class)
	//	private UUID jobProcessorId;

	/**
	 * <code>true</code> if the Subscription Job Processor is currently
	 * registered with the Quartz scheduler; <code>false</code> otherwise.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	private Boolean scheduled;

	/**
	 * The next time the Subscription Job Processor will run.
	 * 
	 * <p>
	 * This will only be present if the Subscription Job Processor is scheduled,
	 * i.e., <code>{@link #scheduled}=true</code>.
	 * 
	 * <p>
	 * This is a dynamic, "read-only" field that is computed when an instance is
	 * constructed.
	 */
	@XmlElement
	@XmlJavaTypeAdapter(DatetimeAdapter.class)
	private Date nextFireTime;

	/**
	 * A message that provides information about the state of the Subscription
	 * Job Processor.
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

	/**
	 * The repeat interval in seconds of the Subscription Job Processor. This is
	 * when scheduling theSubscription Job Processor with the
	 * <a href="https://quartz-scheduler.org/">Quartz</a> scheduler.
	 * 
	 * <p>
	 * This attribute is used only to specify the repeat interval in seconds of
	 * the Subscription Job Processor in the POST data of the "startRequests"
	 * endpoint for this resource, {@link JobProcessor#startRequests}. Since
	 * this data is not persisted (there is currently no JobProcessor entity
	 * class), this value is never <i>returned</i> in a JobProcessorResource
	 * resource.
	 */
	@XmlElement
	private Long repeatInterval;

	public JobProcessorResource() {
	}

	public JobProcessorResource(
			Boolean scheduled,
			Date nextFireTime,
			String schedulingNotice,
			String triggerState,
			Long repeatInterval) {
		this.scheduled = scheduled;
		this.nextFireTime = nextFireTime;
		this.schedulingNotice = schedulingNotice;
		this.triggerState = triggerState;
		this.repeatInterval = repeatInterval;
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

	public Long getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(Long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JobProcessorResource [scheduled=");
		builder.append(scheduled);
		builder.append(", nextFireTime=");
		builder.append(nextFireTime);
		builder.append(", schedulingNotice=");
		builder.append(schedulingNotice);
		builder.append(", triggerState=");
		builder.append(triggerState);
		builder.append(", repeatInterval=");
		builder.append(repeatInterval);
		builder.append("]");
		return builder.toString();
	}
}
