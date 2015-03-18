package com.qfree.obo.report.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.obo.report.domain.Report;

public class SpittleRepositoryImpl implements SpittleRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Report> findRecent() {
    return findRecent(10);
  }

  public List<Report> findRecent(int count) {
		return (List<Report>) entityManager.createQuery("select r from Report r order by r.postedTime desc")
        .setMaxResults(count)
        .getResultList();
  }
  
}
