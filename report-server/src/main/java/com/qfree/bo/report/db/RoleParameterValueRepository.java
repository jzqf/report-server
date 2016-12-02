package com.qfree.bo.report.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.bo.report.domain.RoleParameter;
import com.qfree.bo.report.domain.RoleParameterValue;

/**
 * Repository interface for {@link RoleParameterValue} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language or via JPQL should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface RoleParameterValueRepository extends JpaRepository<RoleParameterValue, UUID> {

	List<RoleParameterValue> findByRoleParameter(RoleParameter roleParameter);
	  
}
