package com.qfree.bo.report.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.AssetRepository;
import com.qfree.bo.report.db.AssetTreeRepository;
import com.qfree.bo.report.db.AssetTypeRepository;
import com.qfree.bo.report.db.DocumentRepository;
import com.qfree.bo.report.domain.Asset;
import com.qfree.bo.report.domain.AssetTree;
import com.qfree.bo.report.domain.AssetType;
import com.qfree.bo.report.domain.Document;
import com.qfree.bo.report.dto.AssetResource;
import com.qfree.bo.report.dto.AssetTreeResource;
import com.qfree.bo.report.dto.AssetTypeResource;
import com.qfree.bo.report.dto.DocumentResource;
import com.qfree.bo.report.dto.RestErrorResource.RestError;
import com.qfree.bo.report.exceptions.RestApiException;
import com.qfree.bo.report.util.RestUtils;

@Component
@Transactional
public class AssetService {

	private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

	private final AssetRepository assetRepository;
	private final AssetTreeRepository assetTreeRepository;
	private final AssetTypeRepository assetTypeRepository;
	private final DocumentRepository documentRepository;

	@Autowired
	public AssetService(
			AssetRepository assetRepository,
			AssetTreeRepository assetTreeRepository,
			AssetTypeRepository assetTypeRepository,
			DocumentRepository documentRepository) {
		this.assetRepository = assetRepository;
		this.assetTreeRepository = assetTreeRepository;
		this.assetTypeRepository = assetTypeRepository;
		this.documentRepository = documentRepository;
	}

	@Transactional
	public Asset saveNewFromResource(AssetResource assetResource) {
		RestUtils.ifNewResourceIdNotNullThen403(assetResource.getAssetId(), Asset.class,
				"assetId", assetResource.getAssetId());
		return saveOrUpdateFromResource(assetResource);
	}

	@Transactional
	public Asset saveExistingFromResource(AssetResource assetResource) {
		return saveOrUpdateFromResource(assetResource);
	}

	@Transactional
	public Asset saveOrUpdateFromResource(AssetResource assetResource) {
		logger.debug("assetResource = {}", assetResource);

		RestUtils.ifAttrNullThen403(assetResource.getFilename(), Asset.class, "filename");

		/*
		 * Retrieve the AssetTreeResource from assetResource. The assetTreeId
		 * attribute of this object is used to specify the AssetTree that will
		 * be associated with the Asset entity that is be saved/created below.
		 * It is not necessary for any of the other AssetTreeResource attributes
		 * to have non-null values.
		 * 
		 * If assetTreeId is *not* provided here, we throw a custom exception. 
		 */
		AssetTreeResource assetTreeResource = assetResource.getAssetTreeResource();
		logger.debug("assetTreeResource = {}", assetTreeResource);
		UUID assetTreeId = null;
		if (assetTreeResource != null) {
			assetTreeId = assetTreeResource.getAssetTreeId();
		}
		logger.debug("assetTreeId = {}", assetTreeId);
		AssetTree assetTree = null;
		if (assetTreeId != null) {
			assetTree = assetTreeRepository.findOne(assetTreeId);
			RestUtils.ifNullThen404(assetTree, AssetTree.class, "assetTreeId", assetTreeId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_ASSET_ASSETTREE_NULL, Asset.class, "assetTreeId");
		}

		/*
		 * Retrieve the AssetTypeResource from assetResource. The assetTypeId
		 * attribute of this object is used to specify the AssetType that will
		 * be associated with the Asset entity that is be saved/created below.
		 * It is not necessary for any of the other AssetTypeResource attributes
		 * to have non-null values.
		 * 
		 * If assetTypeId is *not* provided here, we throw a custom exception. 
		 */
		AssetTypeResource assetTypeResource = assetResource.getAssetTypeResource();
		logger.debug("assetTypeResource = {}", assetTypeResource);
		UUID assetTypeId = null;
		if (assetTypeResource != null) {
			assetTypeId = assetTypeResource.getAssetTypeId();
		}
		logger.debug("assetTypeId = {}", assetTypeId);
		AssetType assetType = null;
		if (assetTypeId != null) {
			assetType = assetTypeRepository.findOne(assetTypeId);
			RestUtils.ifNullThen404(assetType, AssetType.class, "assetTypeId", assetTypeId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_ASSET_ASSETTYPE_NULL, Asset.class, "assetTypeId");
		}

		/*
		 * Retrieve the DocumentResource from assetResource. The documentId
		 * attribute of this object is used to specify the Document that will
		 * be associated with the Asset entity that is be saved/created below.
		 * It is not necessary for any of the other DocumentResource attributes
		 * to have non-null values.
		 * 
		 * If documentId is *not* provided here, we throw a custom exception. 
		 */
		DocumentResource documentResource = assetResource.getDocumentResource();
		logger.debug("documentResource = {}", documentResource);
		UUID documentId = null;
		if (documentResource != null) {
			documentId = documentResource.getDocumentId();
		}
		logger.debug("documentId = {}", documentId);
		Document document = null;
		if (documentId != null) {
			document = documentRepository.findOne(documentId);
			RestUtils.ifNullThen404(document, Document.class, "documentId", documentId.toString());
		} else {
			throw new RestApiException(RestError.FORBIDDEN_ASSET_DOCUMENT_NULL, Asset.class, "documentId");
		}

		Asset asset = new Asset(assetResource, assetTree, assetType, document);
		logger.debug("asset = {}", asset);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * Asset.
		 */
		asset = assetRepository.save(asset);
		logger.debug("asset (created/updated) = {}", asset);

		return asset;
	}
}
