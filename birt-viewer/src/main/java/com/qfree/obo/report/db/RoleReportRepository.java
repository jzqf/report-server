package com.qfree.obo.report.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.RoleReport;

/**
 * Repository interface for {@link RoleReport} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface RoleReportRepository extends JpaRepository<RoleReport, UUID> {
	  
}
