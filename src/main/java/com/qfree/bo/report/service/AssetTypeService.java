package com.qfree.bo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.bo.report.db.AssetTypeRepository;
import com.qfree.bo.report.domain.AssetType;
import com.qfree.bo.report.dto.AssetTypeResource;
import com.qfree.bo.report.util.RestUtils;

@Component
@Transactional
public class AssetTypeService {

	private static final Logger logger = LoggerFactory.getLogger(AssetTypeService.class);

	private final AssetTypeRepository assetTypeRepository;

	@Autowired
	public AssetTypeService(AssetTypeRepository assetTypeRepository) {
		this.assetTypeRepository = assetTypeRepository;
	}

	@Transactional
	public AssetType saveNewFromResource(AssetTypeResource assetTypeResource) {
		RestUtils.ifNewResourceIdNotNullThen403(assetTypeResource.getAssetTypeId(), AssetType.class,
				"assetTypeId", assetTypeResource.getAssetTypeId());

		RestUtils.ifAttrNullThen403(assetTypeResource.getName(), AssetType.class, "name");
		RestUtils.ifAttrNullThen403(assetTypeResource.getAbbreviation(), AssetType.class, "abbreviation");
		RestUtils.ifAttrNullThen403(assetTypeResource.getDirectory(), AssetType.class, "directory");

		return saveOrUpdateFromResource(assetTypeResource);
	}

	@Transactional
	public AssetType saveExistingFromResource(AssetTypeResource assetTypeResource) {
		return saveOrUpdateFromResource(assetTypeResource);
	}

	@Transactional
	public AssetType saveOrUpdateFromResource(AssetTypeResource assetTypeResource) {
		logger.debug("assetTypeResource = {}", assetTypeResource);

		AssetType assetType = new AssetType(assetTypeResource);
		logger.debug("assetType = {}", assetType);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * AssetType.
		 */
		assetType = assetTypeRepository.save(assetType);
		logger.debug("assetType (created/updated) = {}", assetType);

		return assetType;
	}
}
