package com.example.edutech.curso.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.dto.CursoDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.EstadoCursoEnum;

@Repository
public interface CursoRepository extends JpaRepository<Curso, String>, JpaSpecificationExecutor<Curso> {

    @Query("SELECT new com.example.edutech.curso.dto.CursoDTO(c.sigla, c.nombre) FROM Curso c")
    List<CursoDTO> listarCursosOptimizado();

    List<Curso> findByEstadoCurso(EstadoCursoEnum estadoCurso);
}