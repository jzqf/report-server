package com.qfree.bo.report.rest.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.bo.report.domain.Document;
import com.qfree.bo.report.dto.RestErrorResource.RestError;
import com.qfree.bo.report.exceptions.RestApiException;

public class DocumentContentOutputStream implements StreamingOutput {

	private static final Logger logger = LoggerFactory.getLogger(DocumentContentOutputStream.class);

	private Document document;

	public DocumentContentOutputStream(Document document) {
		this.document = document;
	}

	@Override
	public void write(OutputStream output) throws WebApplicationException {

		//		if (document.getContent() != null) {
		try {
			output.write(document.getContent());
			output.flush();
		} catch (IOException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_DOCUMENT_STREAM, e);
		}

		//		} else {
		//			throw new RestApiException(RestError.NOT_FOUND_RENDERED_REPORT_FOR_JOB, Document.class);
		//		}

	}
}
