package com.example.edutech.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.evaluacion.model.Pregunta;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Integer> {
    List<Pregunta> findByEvaluacion(Evaluacion evaluacion);
    List<Pregunta> findByEvaluacionId(Integer evaluacionId); 
}