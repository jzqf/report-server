package com.qfree.obo.report.db;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qfree.obo.report.domain.ReportParameter;

public interface ReportParameterRepository extends JpaRepository<ReportParameter, UUID> {

}
