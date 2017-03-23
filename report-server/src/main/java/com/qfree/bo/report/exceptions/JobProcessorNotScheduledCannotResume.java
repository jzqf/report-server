package com.qfree.bo.report.exceptions;

/**
 * Exception thrown when an attempt is made to resume the subscription job
 * processor when it is not currently registered with the scheduler.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorNotScheduledCannotResume extends ReportingException {

	private static final long serialVersionUID = 1L;

	public JobProcessorNotScheduledCannotResume() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotResume(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotResume(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotResume(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorNotScheduledCannotResume(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
