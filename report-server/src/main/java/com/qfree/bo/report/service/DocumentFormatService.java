package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.DocumentFormatRepository;
import com.qfree.obo.report.domain.DocumentFormat;
import com.qfree.obo.report.dto.DocumentFormatResource;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class DocumentFormatService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentFormatService.class);

	private final DocumentFormatRepository documentFormatRepository;

	@Autowired
	public DocumentFormatService(DocumentFormatRepository documentFormatRepository) {
		this.documentFormatRepository = documentFormatRepository;
	}

	@Transactional
	public DocumentFormat saveNewFromResource(DocumentFormatResource documentFormatResource) {
		RestUtils.ifNewResourceIdNotNullThen403(documentFormatResource.getDocumentFormatId(), DocumentFormat.class,
				"documentFormatId", documentFormatResource.getDocumentFormatId());
		return saveOrUpdateFromResource(documentFormatResource);
	}

	@Transactional
	public DocumentFormat saveExistingFromResource(DocumentFormatResource documentFormatResource) {
		return saveOrUpdateFromResource(documentFormatResource);
	}

	@Transactional
	public DocumentFormat saveOrUpdateFromResource(DocumentFormatResource documentFormatResource) {
		logger.debug("documentFormatResource = {}", documentFormatResource);

		DocumentFormat documentFormat = new DocumentFormat(documentFormatResource);
		logger.debug("documentFormat = {}", documentFormat);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * DocumentFormat.
		 */
		documentFormat = documentFormatRepository.save(documentFormat);
		logger.debug("documentFormat (created/updated) = {}", documentFormat);

		return documentFormat;
	}
}
