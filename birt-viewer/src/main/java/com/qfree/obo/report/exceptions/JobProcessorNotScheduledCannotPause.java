package com.qfree.obo.report.exceptions;

/**
 * Exception thrown when an attempt is made to pause the subscription job
 * processor when it is not currently registered with the scheduler.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorNotScheduledCannotPause extends ReportingException {

	public JobProcessorNotScheduledCannotPause() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotPause(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotPause(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotPause(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotPause(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
