package com.qfree.obo.report.db;

import java.util.List;

import com.qfree.obo.report.domain.Report;

public interface SpittleRepositoryCustom {

  List<Report> findRecent();

  List<Report> findRecent(int count);

}