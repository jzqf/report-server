package com.qfree.bo.report.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.qfree.bo.report.domain.Role;

/**
 * Implementation class for "custom" repository query methods for 
 * {@link Role} persistence.
 * <p>
 * Only query methods that are declared in 
 * {@link RoleRepositoryCustom} should be declared here. These are
 * methods that <i>cannot</i> be created using Spring Data's domain specific 
 * language. Query methods created using Spring Data's domain specific language
 * are declared in {@link RoleRepository}.
 * 
 * @author Jeffrey Zelt
 */
public class RoleRepositoryImpl implements RoleRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Refresh the Role entity in case it was updated by another 
	 * process/thread that the current EntityManager is unaware of. This is 
	 * useful in at least during integration testing of ReST endpoints when we 
	 * know an entity is updated by the endpoint. 
	 * 
	 * @param role
	 * @return
	 */
	public Role refresh(Role role) {
		entityManager.refresh(role);
		return role;
	}
}
