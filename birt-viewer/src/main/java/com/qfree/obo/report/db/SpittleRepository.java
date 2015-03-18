package com.qfree.obo.report.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.Report;

/**
 * Repository interface for {@link Report} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface SpittleRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
  
	//	List<Report> findBySpitterReportCategoryId(long spitterId);
	List<Report> findByReportCategoryReportCategoryId(long spitterId);
  
}
