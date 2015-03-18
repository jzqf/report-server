package com.qfree.obo.report.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.Report;

/**
 * Repository interface with operations for {@link Report} persistence.
 * @author habuma
 */
public interface SpittleRepository extends JpaRepository<Report, Long>, SpittleRepositoryCustom {
  
  List<Report> findBySpitterId(long spitterId);
  
}
