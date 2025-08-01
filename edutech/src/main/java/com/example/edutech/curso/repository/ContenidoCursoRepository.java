package com.example.edutech.curso.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.model.ContenidoCurso;

@Repository
public interface ContenidoCursoRepository extends JpaRepository<ContenidoCurso, Integer> {
}
