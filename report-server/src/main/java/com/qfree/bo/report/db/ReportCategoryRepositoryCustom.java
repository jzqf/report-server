package com.qfree.bo.report.db;

import com.qfree.bo.report.domain.ReportCategory;

/**
 * "Custom" repository interface for {@link ReportCategory} persistence.
 *
 * Only query methods that are implemented in 
 * {@link ReportCategoryRepositoryImpl} should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface ReportCategoryRepositoryCustom {

	public ReportCategory refresh(ReportCategory reportCategory);

	//	int eliteSweep();

}
