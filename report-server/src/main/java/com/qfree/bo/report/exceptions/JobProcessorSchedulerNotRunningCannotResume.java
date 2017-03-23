package com.qfree.bo.report.exceptions;

/**
 * Exception thrown when an attempt is made to resume the subscription job
 * processor when the scheduler is not currently running.
 * 
 * @author jeffreyz
 *
 */
public class JobProcessorSchedulerNotRunningCannotResume extends ReportingException {

	private static final long serialVersionUID = 1L;

	public JobProcessorSchedulerNotRunningCannotResume() {
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotResume(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotResume(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotResume(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public JobProcessorSchedulerNotRunningCannotResume(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
