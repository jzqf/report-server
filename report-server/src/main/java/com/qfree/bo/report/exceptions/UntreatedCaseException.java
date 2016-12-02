package com.qfree.obo.report.exceptions;

public class UntreatedCaseException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public UntreatedCaseException() {
	}

	public UntreatedCaseException(String message) {
		super(message);
	}

	public UntreatedCaseException(Throwable cause) {
		super(cause);
	}

	public UntreatedCaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public UntreatedCaseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
