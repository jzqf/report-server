package com.qfree.obo.report.exceptions;

public class ResourceFilterParseException extends ReportingException {

	private static final long serialVersionUID = 1L;

	public ResourceFilterParseException() {
	}

	public ResourceFilterParseException(String message) {
		super(message);
	}

	public ResourceFilterParseException(Throwable cause) {
		super(cause);
	}

	public ResourceFilterParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceFilterParseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
