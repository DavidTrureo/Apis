package com.example.edutech.evaluacion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.evaluacion.model.EntregaEvaluacion;
import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.usuario.model.Usuario;

@Repository
public interface EntregaEvaluacionRepository extends JpaRepository<EntregaEvaluacion, Integer> {

    List<EntregaEvaluacion> findByEvaluacion_Id(Integer evaluacionId);

    Optional<EntregaEvaluacion> findByEstudianteAndEvaluacion(Usuario estudiante, Evaluacion evaluacion);

    // Nuevo: Encontrar entregas de un estudiante para una lista de evaluaciones
    List<EntregaEvaluacion> findByEstudianteAndEvaluacionIn(Usuario estudiante, List<Evaluacion> evaluaciones);
}