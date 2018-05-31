package com.qfree.bo.report.exceptions;

public class NoScheduleForSubscriptionException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public NoScheduleForSubscriptionException() {
	}

	public NoScheduleForSubscriptionException(String message) {
		super(message);
	}

	public NoScheduleForSubscriptionException(Throwable cause) {
		super(cause);
	}

	public NoScheduleForSubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoScheduleForSubscriptionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
