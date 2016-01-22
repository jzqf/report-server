package com.qfree.obo.report.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.qfree.obo.report.db.AssetRepository;
import com.qfree.obo.report.db.AssetTreeRepository;
import com.qfree.obo.report.db.AssetTypeRepository;
import com.qfree.obo.report.domain.Asset;
import com.qfree.obo.report.domain.AssetTree;
import com.qfree.obo.report.domain.AssetType;
import com.qfree.obo.report.dto.AssetSyncResource;
import com.qfree.obo.report.dto.ResourcePath;
import com.qfree.obo.report.dto.RestErrorResource.RestError;
import com.qfree.obo.report.exceptions.RestApiException;
import com.qfree.obo.report.util.AssetFileFilter;
import com.qfree.obo.report.util.ReportUtils;
import com.qfree.obo.report.util.RestUtils;
import com.qfree.obo.report.util.RestUtils.RestApiVersion;

@Component
@Transactional
public class AssetSyncService {

	private static final Logger logger = LoggerFactory.getLogger(AssetSyncService.class);

	private final AssetRepository assetRepository;
	private final AssetTreeRepository assetTreeRepository;
	private final AssetTypeRepository assetTypeRepository;

	@Autowired
	public AssetSyncService(
			AssetRepository assetRepository,
			AssetTreeRepository assetTreeRepository,
			AssetTypeRepository assetTypeRepository) {
		this.assetRepository = assetRepository;
		this.assetTreeRepository = assetTreeRepository;
		this.assetTypeRepository = assetTypeRepository;
	}

	public AssetSyncResource syncAssetsWithFileSystem(ServletContext servletContext, UriInfo uriInfo,
			Map<String, List<String>> queryParams, RestApiVersion apiVersion) {

		List<String> showAll = queryParams.get(ResourcePath.SHOWALL_QP_KEY);
		Boolean syncInactiveAssets = ResourcePath.showAll(Asset.class, showAll);

		return syncAssetsWithFileSystem(Paths.get(servletContext.getRealPath("")), syncInactiveAssets);
	}

	public AssetSyncResource syncAssetsWithFileSystem(Path absoluteContextPath, Boolean syncInactiveAssets) {

		List<String> assetsDeleted = new ArrayList<>();
		List<String> assetsNotDeleted = new ArrayList<>();
		List<String> assetsCreated = new ArrayList<>();
		List<String> assetsNotCreated = new ArrayList<>(); // not currently used

		try {
			if (ReportUtils.assetSyncSemaphore.tryAcquire(ReportUtils.MAX_WAIT_ACQUIRE_ASSETSYNCSEMAPHORE,
					TimeUnit.SECONDS)) {
				try {

					/*
					 * Delete *all* files in each asset directory in each asset 
					 * tree, except for files named "ReadMe.txt".
					 * 
					 * No attempt is made to delete directories that are no 
					 * longer used or to delete files in directories that are no
					 * longer used. Those directories will automatically 
					 * disappear the next time a new WAR file is installed.
					 * 
					 * This step is not strictly necessary. It is difficult to
					 * image problems that could arise because of the presence 
					 * of asset files in these directories that are not matched 
					 * with files stored in the report server database, but this
					 * is done anyway, to avoid the unlikely event of such a
					 * problem arising one day.
					 */
					for (AssetTree assetTree : assetTreeRepository.findByActiveTrue()) {
						for (AssetType assetType : assetTypeRepository.findByActiveTrue()) {
							File assetDirectory = absoluteContextPath
									.resolve(ReportUtils.ASSET_FILES_PARENT_FOLDER)
									.resolve(assetTree.getDirectory())
									.resolve(assetType.getDirectory())
									.toFile();
							logger.info("Deleting files from directory {} ...", assetDirectory.toString());
							if (assetDirectory.isDirectory()) {
								File[] files = assetDirectory.listFiles(new AssetFileFilter());
								if (files != null) {
									for (File file : files) {
										if (file.delete()) {
											assetsDeleted.add(file.getAbsolutePath());
											logger.info("Deleted file \"{}\"", file.getAbsolutePath());
										} else {
											assetsNotDeleted.add(file.getAbsolutePath());
											logger.warn("Unable to delete file \"{}\"", file.getAbsolutePath());
										}
									}
								}
							} else {
								logger.info("Directory {} does not exist. This is OK.", assetDirectory.toString());
							}
						}
					}

					/*
					* Write out asset files from the report server database. 
					* Directories are created, if necessary.
					*/
					List<Asset> assets = null;
					if (RestUtils.FILTER_INACTIVE_RECORDS
							&& !(syncInactiveAssets != null ? syncInactiveAssets : false)) {
						assets = assetRepository.findByActiveTrue();
					} else {
						assets = assetRepository.findAll();
					}
					for (Asset asset : assets) {
						logger.info("Writing asset = {}, number of bytes = {}", asset.getFilename(),
								asset.getDocument().getContent().length);
						/*
						 * Write uploaded asset file to the file system of the 
						 * report server, overwriting a file with the same name,
						 * if one exists.
						 */
						Path assetFilePath = ReportUtils.writeAssetFile(asset, absoluteContextPath.toString());
						assetsCreated.add(assetFilePath.toAbsolutePath().toString());
					}

				} catch (IOException e) {
					/*
					 * Can be thrown by: Files.createDirectories(...)
					 */
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_ASSET_SYNC, e);
				} finally {
					ReportUtils.assetSyncSemaphore.release();
				}
			} else {
				throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_NO_PERMIT);
			}
		} catch (InterruptedException e) {
			throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_RPTDESIGN_SYNC_INTERRUPT, e);
		}

		return new AssetSyncResource(assetsDeleted, assetsNotDeleted, assetsCreated, assetsNotCreated);
	}

	/**
	 * Write BIRT asset file to the file system of the report server,
	 * overwriting a file with the same name if one exists.
	 * 
	 * This method simply calls ReportUtils.writeAssetFile(Asset, String), but
	 * it wraps it in a mutual exclusion lock with a semaphore. It also checks
	 * that this application has a directory tree to which the file can be
	 * written.
	 * 
	 * @param asset
	 * @param absoluteAppContextPath
	 * @return
	 */
	public Path writeAssetFile(Asset asset, String absoluteAppContextPath) {
		Path assetFilePath = null;
		/*
		 * We only write the file if we detect that there is an appropriate
		 * directory tree in which is can be written.
		 */
		if (ReportUtils.applicationPackagedAsWar()) {
			try {
				if (ReportUtils.assetSyncSemaphore.tryAcquire(ReportUtils.MAX_WAIT_ACQUIRE_ASSETSYNCSEMAPHORE,
						TimeUnit.SECONDS)) {
					try {
						/*
						 * Write uploaded asset file to the file system of the
						 * report server, overwriting a file with the same name, if
						 * one exists.
						 */
						assetFilePath = ReportUtils.writeAssetFile(asset, absoluteAppContextPath);
					} catch (IOException e) {
						throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_ASSET_SYNC, e);
					} finally {
						ReportUtils.assetSyncSemaphore.release();
					}
				} else {
					throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_ASSET_SYNC_NO_PERMIT);
				}
			} catch (InterruptedException e) {
				/*
				 * Can be thrown by:  ReportUtils.assetSyncSemaphore.tryAcquire(...)
				 */
				throw new RestApiException(RestError.INTERNAL_SERVER_ERROR_ASSET_SYNC_INTERRUPT, e);
			}
		}
		return assetFilePath;
	}

}
