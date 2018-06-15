package com.qfree.bo.report.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qfree.bo.report.domain.ReportParameter;
import com.qfree.bo.report.domain.ReportVersion;

public interface ReportParameterRepository extends JpaRepository<ReportParameter, UUID> {

	//	@Query("select MAX(rp.orderIndex) from ReportParameter rp where rp.report=:report")
	//	Integer maxOrderIndex(@Param("report") Report report);
	@Query("select MAX(rp.orderIndex) from ReportParameter rp where rp.reportVersion=:reportVersion")
	Integer maxOrderIndex(@Param("reportVersion") ReportVersion reportVersion);
}
