package com.qfree.obo.report.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.JobParameter;

/**
 * Repository interface for {@link JobParameter} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language or via JPQL should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface JobParameterRepository extends JpaRepository<JobParameter, Long> {
	  
}