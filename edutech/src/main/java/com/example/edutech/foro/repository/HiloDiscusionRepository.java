package com.example.edutech.foro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.foro.model.HiloDiscusion;

@Repository
public interface HiloDiscusionRepository extends JpaRepository<HiloDiscusion, Integer> {
    List<HiloDiscusion> findByForoIdOrderByFechaUltimaActividadDesc(Integer foroId);
    List<HiloDiscusion> findByForoIdAndTituloContainingIgnoreCaseOrderByFechaUltimaActividadDesc(Integer foroId, String titulo);
}