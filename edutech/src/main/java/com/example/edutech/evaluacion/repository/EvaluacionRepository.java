package com.example.edutech.evaluacion.repository;
//REALIZADO POR: Cristóbal Mira
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.evaluacion.model.Evaluacion;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {
    List<Evaluacion> findByCurso(Curso curso);


}