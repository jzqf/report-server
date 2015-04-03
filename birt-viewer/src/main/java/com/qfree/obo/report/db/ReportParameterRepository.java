package com.qfree.obo.report.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.qfree.obo.report.domain.Report;
import com.qfree.obo.report.domain.ReportParameter;

public interface ReportParameterRepository extends JpaRepository<ReportParameter, UUID> {

	@Query("select MAX(rp.orderIndex) from ReportParameter rp where rp.report=:report")
	Integer maxOrderIndex(@Param("report") Report report);
}
