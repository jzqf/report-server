package com.qfree.obo.report.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.ReportCategory;

/**
 * Repository interface with operations for {@link ReportCategory} persistence.
 * @author habuma
 */
public interface SpitterRepository extends JpaRepository<ReportCategory, Long>, SpitterSweeper {
	  
	ReportCategory findByDescription(String description);
	
	List<ReportCategory> findByDescriptionOrFullNameLike(String description, String fullName);

}
