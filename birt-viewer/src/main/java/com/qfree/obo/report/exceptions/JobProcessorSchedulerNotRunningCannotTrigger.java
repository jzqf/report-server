package com.qfree.obo.report.exceptions;

/**
 * Exception thrown when an attempt is made to trigger the subscription job
 * processor when the scheduler is not currently running.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorSchedulerNotRunningCannotTrigger extends ReportingException {

	public JobProcessorSchedulerNotRunningCannotTrigger() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotTrigger(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotTrigger(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotTrigger(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotTrigger(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
