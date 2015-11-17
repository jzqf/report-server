package com.qfree.obo.report.exceptions;

public class ParseResourceFilterException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public ParseResourceFilterException() {
	}

	public ParseResourceFilterException(String message) {
		super(message);
	}

	public ParseResourceFilterException(Throwable cause) {
		super(cause);
	}

	public ParseResourceFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseResourceFilterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
