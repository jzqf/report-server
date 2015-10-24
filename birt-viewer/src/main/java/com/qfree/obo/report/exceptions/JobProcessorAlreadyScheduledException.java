package com.qfree.obo.report.exceptions;

/**
 * Attempt to schedule the subscription job processor, but it is already
 * registered with the scheduler.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorAlreadyScheduledException extends ReportingException {

	public JobProcessorAlreadyScheduledException() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorAlreadyScheduledException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorAlreadyScheduledException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorAlreadyScheduledException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorAlreadyScheduledException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
