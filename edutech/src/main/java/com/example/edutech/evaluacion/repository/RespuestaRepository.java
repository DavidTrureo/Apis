package com.example.edutech.evaluacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.evaluacion.model.Pregunta;
import com.example.edutech.evaluacion.model.Respuesta;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Integer> {
    List<Respuesta> findByPregunta(Pregunta pregunta);
    List<Respuesta> findByPreguntaId(Integer preguntaId); 
}