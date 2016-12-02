package com.qfree.bo.report.exceptions;

public class NoValueForReportParameterException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public NoValueForReportParameterException() {
	}

	public NoValueForReportParameterException(String message) {
		super(message);
	}

	public NoValueForReportParameterException(Throwable cause) {
		super(cause);
	}

	public NoValueForReportParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoValueForReportParameterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
