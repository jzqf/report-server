package com.qfree.obo.report.db;

import java.util.List;

import com.qfree.obo.report.domain.Spittle;

public interface SpittleRepositoryCustom {

  List<Spittle> findRecent();

  List<Spittle> findRecent(int count);

}