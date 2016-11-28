package com.qfree.obo.report.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.obo.report.domain.ReportVersion;

/**
 * Implementation class for "custom" repository query methods for 
 * {@link ReportVersion} persistence.
 * <p>
 * Only query methods that are declared in 
 * {@link ReportVersionRepositoryCustom} should be declared here. These are
 * methods that <i>cannot</i> be created using Spring Data's domain specific 
 * language. Query methods created using Spring Data's domain specific language
 * are declared in {@link ReportVersionRepository}.
 * 
 * @author Jeffrey Zelt
 */
public class ReportVersionRepositoryImpl implements ReportVersionRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Refresh the ReportVersion entity in case it was updated by another 
	 * process/thread that the current EntityManager is unaware of. This is 
	 * useful in at least during integration testing of ReST endpoints when we 
	 * know an entity is updated by the endpoint. 
	 * 
	 * @param reportVersion
	 * @return
	 */
	public ReportVersion refresh(ReportVersion reportVersion) {
		entityManager.refresh(reportVersion);
		return reportVersion;
	}
}
