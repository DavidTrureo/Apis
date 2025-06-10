package com.example.edutech.reporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.reporte.model.Reporte;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
}
