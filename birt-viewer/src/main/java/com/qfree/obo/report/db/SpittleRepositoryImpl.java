package com.qfree.obo.report.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.obo.report.domain.Report;

public class SpittleRepositoryImpl implements ReportRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Report> findRecentlyCreated() {
    return findRecentlyCreated(10);
  }

  public List<Report> findRecentlyCreated(int count) {
		return (List<Report>) entityManager.createQuery("select r from Report r order by r.createdOn desc")
        .setMaxResults(count)
        .getResultList();
  }
  
}
