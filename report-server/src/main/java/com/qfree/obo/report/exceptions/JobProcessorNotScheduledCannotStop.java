package com.qfree.obo.report.exceptions;

/**
 * Exception thrown when an attempt is made to stop/unschedule the subscription
 * job processor when it is not currently registered with the scheduler.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorNotScheduledCannotStop extends ReportingException {

	private static final long serialVersionUID = 1L;

	public JobProcessorNotScheduledCannotStop() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotStop(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotStop(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotStop(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotStop(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
