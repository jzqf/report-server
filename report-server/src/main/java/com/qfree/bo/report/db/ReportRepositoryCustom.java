package com.qfree.bo.report.db;

import java.util.List;

import com.qfree.bo.report.domain.Report;

/**
 * "Custom" repository interface for {@link Report} persistence.
 * <p>
 * Only query methods that are implemented in 
 * {@link ReportRepositoryImpl} should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface ReportRepositoryCustom {

	public Report refresh(Report report);

	List<Report> findRecentlyCreated();

	List<Report> findRecentlyCreated(int count);

}