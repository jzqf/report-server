package com.qfree.bo.report.db;

import com.qfree.bo.report.domain.ReportVersion;

/**
 * "Custom" repository interface for {@link ReportVersion} persistence.
 * <p>
 * Only query methods that are implemented in 
 * {@link ReportVersionRepositoryImpl} should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface ReportVersionRepositoryCustom {

	public ReportVersion refresh(ReportVersion reportVersion);

}