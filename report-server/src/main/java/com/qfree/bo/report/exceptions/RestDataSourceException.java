package com.qfree.bo.report.exceptions;

public class RestDataSourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RestDataSourceException() {
	}

	public RestDataSourceException(String message) {
		super(message);
	}

	public RestDataSourceException(Throwable cause) {
		super(cause);
	}

	public RestDataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestDataSourceException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
