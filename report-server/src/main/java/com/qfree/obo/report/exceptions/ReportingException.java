package com.qfree.obo.report.exceptions;

public class ReportingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ReportingException() {
	}

	public ReportingException(String message) {
		super(message);
	}

	public ReportingException(Throwable cause) {
		super(cause);
	}

	public ReportingException(String message, Throwable cause) {
		super(message, cause);
	}

	public ReportingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
