package com.qfree.bo.report.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qfree.bo.report.domain.RoleParameter;

/**
 * Repository interface for {@link RoleParameter} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language or via JPQL should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface RoleParameterRepository extends JpaRepository<RoleParameter, UUID> {
	  
	@Query("SELECT rp FROM RoleParameter rp WHERE rp.role.roleId = :roleId AND rp.reportParameter.reportParameterId = :reportParameterId")
	public RoleParameter findByRoleAndReportParameter(
			@Param("roleId") UUID roleId,
			@Param("reportParameterId") UUID reportParameterId);

}