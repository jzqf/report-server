package com.qfree.obo.report.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.obo.report.domain.ReportCategory;

/**
 * Implementation class for "custom" repository query methods for 
 * {@link ReportCategory} persistence.
 *
 * Only query methods that are declared in 
 * {@link ReportCategoryRepositoryCustom} should be declared here. These are
 * methods that <i>cannot</i> be created using Spring Data's domain specific 
 * language. Query methods created using Spring Data's domain specific language
 * are declared in {@link ReportCategoryRepository}.
 * 
 * @author Jeffrey Zelt
 */
public class ReportCategoryRepositoryImpl implements ReportCategoryRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	/**
	 * Refresh the ReportCategory entity in case it was updated by another 
	 * process/thread that the current EntityManager is unaware of. This is 
	 * useful in at least during integration testing of ReST endpoints when we 
	 * know an entity is updated by the endpoint. 
	 * 
	 * @param reportCategory
	 * @return
	 */
	public ReportCategory refresh(ReportCategory reportCategory) {
		em.refresh(reportCategory);
		return reportCategory;
	}

	//	public int eliteSweep() {
	//	  String update = 
	//	      "UPDATE ReportCategory reportCategory " +
	//	   		"SET reportCategory.unusedReportCategoryField3 = 'Elite' " +
	//	   		"WHERE reportCategory.unusedReportCategoryField3 = 'Newbie' " +
	//	   		"AND reportCategory.reportCategoryId IN (" +
	//	   		"SELECT rc FROM ReportCategory rc WHERE (" +
	//	   		"  SELECT COUNT(reports) FROM rc.reports reports) > 10000" +
	//	   		")";
	//		return em.createQuery(update).executeUpdate();
	//	}
	
}
