package com.qfree.obo.report.db;

import java.util.List;

import com.qfree.obo.report.domain.Report;

public interface ReportRepositoryCustom {

  List<Report> findRecentlyCreated();

  List<Report> findRecentlyCreated(int count);

}