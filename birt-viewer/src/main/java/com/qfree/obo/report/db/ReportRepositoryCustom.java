package com.qfree.obo.report.db;

import java.util.List;

import com.qfree.obo.report.domain.Report;

/**
 * "Custom" repository interface for {@link Report} persistence.
 *
 * Only query methods that are implemented in 
 * {@link ReportRepositoryImpl} should be declared here.
 * 
 * @author jeffreyz
 */
public interface ReportRepositoryCustom {

  List<Report> findRecentlyCreated();

  List<Report> findRecentlyCreated(int count);

}