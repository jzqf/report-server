package com.qfree.obo.report.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.Role;
import com.qfree.obo.report.domain.RoleRole;

/**
 * Repository interface for {@link Role} persistence.
 *
 * Only query methods that are generated by Spring Data JPA using Spring Data's
 * domain-specific language or via JPQL should be declared here.
 * 
 * @author Jeffrey Zelt
 */
public interface RoleRepository extends JpaRepository<Role, UUID>, RoleRepositoryCustom {
	  
	//	@NamedQuery(name = "Role.findByUsername", query = "select r from Role r where r.username = ?1")
	@Query("SELECT r FROM Role r WHERE r.username = :username")
	public Role findByUsername(@Param("username") String username);

	@Query("SELECT r FROM RoleReport rr INNER JOIN rr.report r WHERE rr.role.roleId = :roleId")
	public List<Report> findReportsByRoleId(@Param("roleId") UUID roleId);

	@Query("SELECT r FROM RoleReport rr INNER JOIN rr.report r WHERE rr.role.roleId = :roleId AND r.active=true")
	public List<Report> findActiveReportsByRoleId(@Param("roleId") UUID roleId);

	//	@Query(
	//			value =
	//			"SELECT CAST(r.report_id AS varchar) FROM report r INNER JOIN role_report rr ON rr.report_id=r.report_id WHERE rr.role_id=CAST(:roleId AS uuid)",
	//			nativeQuery = true)
	//	public List<String> findReportsByRoleIdRecursive(@Param("roleId") String roleId);
	//	@Query(
	//			value =
	//			"SELECT r.report_id FROM report r INNER JOIN role_report rr ON rr.report_id=r.report_id WHERE rr.role_id=CAST(:roleId AS uuid)",
	//			nativeQuery = true)
	//	public List<UUID> findReportsByRoleIdRecursive(@Param("roleId") String roleId);
	//	@Query(value =
	//			"SELECT r.* FROM report r INNER JOIN role_report rr ON rr.report_id=r.report_id WHERE rr.role_id=CAST(:roleId AS uuid)",
	//			nativeQuery = true)
	//	public List<Report> findReportsByRoleIdRecursive(@Param("roleId") String roleId);
	/**
	 * Returns a list of {@link Report}s that a specified {@link Role} has 
	 * access to. 
	 * 
	 * <p>A maximum of 10 levels of {@link RoleRole} relations will be followed. 
	 * This is to avoid endless recursion for the case where a circular loop is 
	 * created, e.g., a parent of a {@link Role} is set to be a child of that 
	 * {@link Role}. The UI should protect the user from such 
	 * situations, but in case this protection is not provided, this will 
	 * provide a last line of defense.
	 * 
	 * @param roleId		String representation of the {@link Role} for which
	 * 						reports will be returned.
	 * @param activeOnly	If {@code true}, only {@link Report}s that have 
	 * 						{@code active=true} will be returned
	 * @return
	 */
	@Query(value =
			"WITH RECURSIVE ancestor(level, role_id, username) AS (" +

					// CTE anchor member:

					"SELECT 0 AS level, role.role_id, role.username " +
					"FROM role " +
					//"WHERE role.role_id=:roleId " +
					"WHERE role.role_id=CAST(:roleId AS uuid) " +

					"UNION ALL " +

					// CTE recursive member:

					"SELECT level+1, role.role_id, role.username " +
					"FROM ancestor " +
					"INNER JOIN role_role link ON link.child_role_id=ancestor.role_id " +
					"INNER JOIN role ON role.role_id=link.parent_role_id " +
					"WHERE level<10 " +

					") " +

					// Statement using the CTE:

					/* Here, we do a select on a derived table. The reason for this
					 * approach is that we want to order the results by report.number,
					 * but since I need to eliminate duplicate rows with DISTINCT
					 * (these duplicates occur because [role_report] junction records
					 * may link both a [role] and one of its ancestor [role] records to
					 * the same [report]), the SELECT list must include the column that
					 * we order on, in this case report.number. But I only want to 
					 * return a list of report_id's; hence, I perform a select on the
					 * derived table (that takes care of the DISTINCT business for us)
					 * to create the derived table, and then I can order by DT.number 
					 * without including it in the SELECT list because this outer 
					 * SELECT does not use DISTINCT.
					 */
					"SELECT DT.report_id FROM " +
					"(" +
					"    SELECT DISTINCT CAST(report.report_id AS varchar), report.number FROM role_report " +
					"    INNER JOIN ancestor ON ancestor.role_id=role_report.role_id " +
					"    INNER JOIN report ON report.report_id=role_report.report_id " +
					"    WHERE (report.active=true OR :activeOnly=false)" +
					") DT " +
					"ORDER BY DT.number",
			nativeQuery = true)
	public List<String> findReportsByRoleIdRecursive(
			@Param("roleId") String roleId,
			@Param("activeOnly") Boolean activeOnly);
}