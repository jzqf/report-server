package com.qfree.obo.report.util;

import java.io.File;
import java.io.FileFilter;

public class RptdesignFileFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		return file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".rptdesign");
	}

}
