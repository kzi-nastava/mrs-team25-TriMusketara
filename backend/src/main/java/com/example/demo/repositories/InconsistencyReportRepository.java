package com.example.demo.repositories;

import com.example.demo.model.InconsistencyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InconsistencyReportRepository extends JpaRepository<InconsistencyReport, Long> {}
