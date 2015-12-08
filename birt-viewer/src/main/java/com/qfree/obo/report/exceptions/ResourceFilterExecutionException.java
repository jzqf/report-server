package com.qfree.obo.report.exceptions;

public class ResourceFilterExecutionException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public ResourceFilterExecutionException() {
	}

	public ResourceFilterExecutionException(String message) {
		super(message);
	}

	public ResourceFilterExecutionException(Throwable cause) {
		super(cause);
	}

	public ResourceFilterExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceFilterExecutionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
