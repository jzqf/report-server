package com.qfree.obo.report.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qfree.obo.report.domain.Authority;
import com.qfree.obo.report.domain.Role;

/**
 * Repository interface for {@link Authority} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language or via JPQL should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

	Authority findByName(String name);

	List<Authority> findByActiveTrue();

	/**
	 * Returns a {@link List}&lt;{@link String}&gt; that contains the ids (as
	 * strings) of {@link Authority} entities that have been granted
	 * <b>directly</b> to a {@link Role} with a specified value of roleId.
	 * 
	 * @param roleId
	 *            String representation of the id of the {@link Role} for which
	 *            {@link Authority} id values will be returned.
	 * @return
	 */
	@Query(value = "SELECT CAST(a.authority_id AS varchar) AS authority_id FROM authority a " +
			"INNER JOIN role_authority ra ON ra.authority_id=a.authority_id " +
			"INNER JOIN role r ON r.role_id=ra.role_id " +
			"WHERE r.role_id=CAST(:roleId AS uuid) AND a.active=true " +
			"ORDER BY a.name",
			nativeQuery = true)
	public List<String> findActiveAuthorityIdsByRoleId(
			@Param("roleId") String roleId);

	/**
	 * Returns a {@link List}&lt;{@link String}&gt; that contains the ids (as
	 * strings) of {@link Authority} entities that have <b>either</b> been
	 * granted <i>directly</> or <i>indirectly</> to a {@link Role} with a
	 * specified value of roleId.
	 * 
	 * @param roleId
	 *            String representation of the id of the {@link Role} for which
	 *            {@link Authority} id values will be returned.
	 * @return
	 */
	@Query(value = "WITH RECURSIVE ancestor(level, role_id) AS (" +

	// CTE anchor member:

			"SELECT 0 AS level, role.role_id FROM role " +
			//"WHERE role.role_id=:roleId " +
			"WHERE role.role_id=CAST(:roleId AS uuid) " +

			"UNION ALL " +

	// CTE recursive member:

			"SELECT level+1, role.role_id FROM ancestor " +
			"INNER JOIN role_role link ON link.child_role_id=ancestor.role_id " +
			"INNER JOIN role ON role.role_id=link.parent_role_id " +
			"WHERE level<10 " +

			") " +

	// Statement using the CTE:

	/* Here, we do a select on a derived table. The reason for this
	 * approach is that we want to order the results by authority.name,
	 * but since I need to eliminate duplicate rows with DISTINCT
	 * (these duplicates occur because [role_authority] junction records may
	 * link both a [role] as well as one or more of its ancestor [role] records
	 * to the same [authority]), the SELECT list must include the column that
	 * we order on, in this case authority.name. But I only want to 
	 * return a list of authority_id's; Therefore, I perform the  DISTINCT 
	 * operation in the definition of the derived table, and then I use
	 * the derived table in the FROM clause of the outer SELECT, where I am free
	 * to also order by DT.name since this outer SELECT does not use DISTINCT.
	 */
			"SELECT DT.authority_id FROM " +
			"(" +
			"    SELECT DISTINCT CAST(authority.authority_id AS varchar) AS authority_id, authority.name " +
			"    FROM role_authority " +
			"    INNER JOIN ancestor ON ancestor.role_id=role_authority.role_id " +
			"    INNER JOIN authority ON authority.authority_id=role_authority.authority_id " +
			"    WHERE authority.active=true" +
			") DT " +
			"ORDER BY DT.name",
			nativeQuery = true)
	public List<String> findActiveAuthorityIdsByRoleIdRecursive(
			@Param("roleId") String roleId);

	/**
	 * Returns a {@link List}&lt;{@link String}&gt; that contains the names of
	 * {@link Authority} entities that have <b>either</b> been granted
	 * <i>directly</> or <i>indirectly</> to a {@link Role} with a specified
	 * value of roleId.
	 * 
	 * @param roleId
	 *            String representation of the id of the {@link Role} for which
	 *            {@link Authority} id values will be returned.
	 * @return
	 */
	@Query(value = "WITH RECURSIVE ancestor(level, role_id) AS (" +

	// CTE anchor member:

			"SELECT 0 AS level, role.role_id FROM role " +
			//"WHERE role.role_id=:roleId " +
			"WHERE role.role_id=CAST(:roleId AS uuid) " +

			"UNION ALL " +

	// CTE recursive member:

			"SELECT level+1, role.role_id FROM ancestor " +
			"INNER JOIN role_role link ON link.child_role_id=ancestor.role_id " +
			"INNER JOIN role ON role.role_id=link.parent_role_id " +
			"WHERE level<10 " +

			") " +

	// Statement using the CTE:

	/* Here, we do a select on a derived table. The reason for this
	 * approach is that we want to order the results by authority.name,
	 * but since I need to eliminate duplicate rows with DISTINCT
	 * (these duplicates occur because [role_authority] junction records may
	 * link both a [role] as well as one or more of its ancestor [role] records
	 * to the same [authority]), the SELECT list must include the column that
	 * we order on, in this case authority.name. Therefore, I perform the 
	 * DISTINCT operation in the definition of the derived table, and then I use
	 * the derived table in the FROM clause of the outer SELECT, where I am free
	 * to also order by DT.name since this outer SELECT does not use DISTINCT.
	 */
			"SELECT DT.name FROM " +
			"(" +
			"    SELECT DISTINCT name AS name " +
			"    FROM role_authority " +
			"    INNER JOIN ancestor ON ancestor.role_id=role_authority.role_id " +
			"    INNER JOIN authority ON authority.authority_id=role_authority.authority_id " +
			"    WHERE authority.active=true" +
			") DT " +
			"ORDER BY DT.name",
			nativeQuery = true)
	public List<String> findActiveAuthorityNamesByRoleIdRecursive(
			@Param("roleId") String roleId);

}
