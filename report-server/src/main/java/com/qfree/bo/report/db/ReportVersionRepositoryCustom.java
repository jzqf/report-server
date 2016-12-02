package com.qfree.obo.report.db;

import com.qfree.obo.report.domain.ReportVersion;

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