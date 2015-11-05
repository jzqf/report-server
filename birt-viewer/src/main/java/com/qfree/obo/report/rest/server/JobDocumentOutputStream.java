package com.qfree.obo.report.rest.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qfree.obo.report.domain.Job;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;

public class JobDocumentOutputStream implements StreamingOutput {

	private static final Logger logger = LoggerFactory.getLogger(JobDocumentOutputStream.class);

	private Job job;

	public JobDocumentOutputStream(Job job) {
		this.job = job;
	}

	@Override
	public void write(OutputStream output) throws WebApplicationException {

		if (job.getDocument() != null) {

			byte[] documentBytes;
			if (job.getEncoded()) {
				/*
				 * job.getDocument() is a Base64 sequence of bytes stored in a
				 * String.
				 */
				documentBytes = Base64.getDecoder().decode(job.getDocument());
				logger.info("Writing {} bytes to output stream", documentBytes.length);
			} else {
				/*
				 * job.getDocument() is a String.
				 */
				documentBytes = job.getDocument().getBytes();
			}

			try {
				output.write(documentBytes);
				output.flush();
			} catch (IOException e) {
				throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_DOCUMENT_STREAM, e);
			}

		} else {
			throw new RestApiException(RestError.NOT_FOUND_RENDERED_REPORT_FOR_JOB, Job.class);
		}

	}

}
