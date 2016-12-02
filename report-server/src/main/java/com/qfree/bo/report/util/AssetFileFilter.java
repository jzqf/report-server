package com.qfree.bo.report.util;

import java.io.File;
import java.io.FileFilter;

/**
 * {@link FileFilter} to select all files in an asset directory <i>other</i>
 * than files named "ReadMe.txt".
 * 
 * @author Jeffrey Zelt
 *
 */
public class AssetFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.exists() && file.isFile() && !file.getName().toLowerCase().equals("readme.txt");
	}

}
