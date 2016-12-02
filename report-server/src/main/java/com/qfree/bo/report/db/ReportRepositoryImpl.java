package com.qfree.bo.report.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.bo.report.domain.Report;

/**
 * Implementation class for "custom" repository query methods for 
 * {@link Report} persistence.
 * <p>
 * Only query methods that are declared in 
 * {@link ReportRepositoryCustom} should be declared here. These are
 * methods that <i>cannot</i> be created using Spring Data's domain specific 
 * language. Query methods created using Spring Data's domain specific language
 * are declared in {@link ReportRepository}.
 * 
 * @author Jeffrey Zelt
 */
public class ReportRepositoryImpl implements ReportRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Refresh the Report entity in case it was updated by another 
	 * process/thread that the current EntityManager is unaware of. This is 
	 * useful in at least during integration testing of ReST endpoints when we 
	 * know an entity is updated by the endpoint. 
	 * 
	 * @param report
	 * @return
	 */
	public Report refresh(Report report) {
		entityManager.refresh(report);
		return report;
	}

	public List<Report> findRecentlyCreated() {
		return findRecentlyCreated(10);
	}

	/*
	 * This version uses a static, named query that is defined in the 
	 * Report entity class.
	 */
	public List<Report> findRecentlyCreated(int count) {
		return entityManager.createNamedQuery("Report.findByCreated", Report.class)
				.setMaxResults(count)
				.getResultList();
	}

	/*
	 * This version uses a dynamic query.
	 */
	//	public List<Report> findRecentlyCreated(int count) {
	//		return (List<Report>) entityManager.createQuery("select r from Report r order by r.createdOn desc")
	//				.setMaxResults(count)
	//				.getResultList();
	//	}

}
