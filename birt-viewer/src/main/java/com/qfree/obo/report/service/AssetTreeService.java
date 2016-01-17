package com.qfree.obo.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.AssetTreeRepository;
import com.qfree.obo.report.domain.AssetTree;
import com.qfree.obo.report.domain.ParameterGroup;
import com.qfree.obo.report.dto.AssetTreeResource;
import com.qfree.obo.report.util.RestUtils;

@Component
@Transactional
public class AssetTreeService {

	private static final Logger logger = LoggerFactory.getLogger(AssetTreeService.class);

	private final AssetTreeRepository assetTreeRepository;

	@Autowired
	public AssetTreeService(AssetTreeRepository assetTreeRepository) {
		this.assetTreeRepository = assetTreeRepository;
	}

	@Transactional
	public AssetTree saveNewFromResource(AssetTreeResource assetTreeResource) {
		RestUtils.ifNewResourceIdNotNullThen403(assetTreeResource.getAssetTreeId(), AssetTree.class,
				"assetTreeId", assetTreeResource.getAssetTreeId());

		RestUtils.ifAttrNullThen403(assetTreeResource.getName(), ParameterGroup.class, "name");
		RestUtils.ifAttrNullThen403(assetTreeResource.getAbbreviation(), ParameterGroup.class, "abbreviation");
		RestUtils.ifAttrNullThen403(assetTreeResource.getDirectory(), ParameterGroup.class, "directory");

		return saveOrUpdateFromResource(assetTreeResource);
	}

	@Transactional
	public AssetTree saveExistingFromResource(AssetTreeResource assetTreeResource) {
		return saveOrUpdateFromResource(assetTreeResource);
	}

	@Transactional
	public AssetTree saveOrUpdateFromResource(AssetTreeResource assetTreeResource) {
		logger.debug("assetTreeResource = {}", assetTreeResource);

		AssetTree assetTree = new AssetTree(assetTreeResource);
		logger.debug("assetTree = {}", assetTree);
		/*
		 * This "save" method will persist or merge the given entity using the
		 * underlying JPA EntityManager. If the entity has not been persisted 
		 * yet, Spring Data JPA will save the entity via a call to the 
		 * entityManager.persist(...) method; otherwise, the 
		 * entityManager.merge(...) method will be called. But since the id of
		 * this entity is not set above, currently this will always save a new
		 * AssetTree.
		 */
		assetTree = assetTreeRepository.save(assetTree);
		logger.debug("assetTree (created/updated) = {}", assetTree);

		return assetTree;
	}
}
