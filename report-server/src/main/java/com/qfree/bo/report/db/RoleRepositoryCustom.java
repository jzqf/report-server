package com.qfree.obo.report.db;

import com.qfree.obo.report.domain.Role;

/**
 * "Custom" repository interface for {@link Role} persistence.
 * <p>
 * Only query methods that are implemented in 
 * {@link RoleRepositoryImpl} should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface RoleRepositoryCustom {

	public Role refresh(Role role);

}