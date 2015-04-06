package com.qfree.obo.report.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.Job;

/**
 * Repository interface for {@link Job} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface JobRepository extends JpaRepository<Job, Long> {
	  
}
