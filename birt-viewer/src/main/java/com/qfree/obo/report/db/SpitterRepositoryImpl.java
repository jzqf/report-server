package com.qfree.obo.report.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SpitterRepositoryImpl implements ReportCategoryCustom {

	@PersistenceContext
	private EntityManager em;
	
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
