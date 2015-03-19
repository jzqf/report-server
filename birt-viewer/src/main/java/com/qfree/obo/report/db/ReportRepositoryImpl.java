package com.qfree.obo.report.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.obo.report.domain.Report;

/**
 * Implementation class for "custom" repository query methods for 
 * {@link Report} persistence.
 *
 * Only query methods that are declared in 
 * {@link ReportRepositoryCustom} should be declared here. These are
 * methods that <i>cannot</i> be created using Spring Data's domain specific 
 * language. Query methods created using Spring Data's domain specific language
 * are declared in {@link ReportRepository}.
 * 
 * @author jeffreyz
 */
public class ReportRepositoryImpl implements ReportRepositoryCustom {

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
