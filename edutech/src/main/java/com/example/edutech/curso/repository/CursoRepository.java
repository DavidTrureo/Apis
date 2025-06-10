package com.example.edutech.curso.repository;
//REALIZADO POR: Maverick Valdes
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.dto.CursoDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.EstadoCursoEnum; // IMPORTAR

@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {

    @Query("SELECT new com.example.edutech.curso.dto.CursoDTO(c.sigla, c.nombre) FROM Curso c")
    List<CursoDTO> listarCursosOptimizado();

    // NUEVO MÉTODO
    List<Curso> findByEstadoCurso(EstadoCursoEnum estadoCurso);

    // Podrías añadir más para filtros combinados, ej:
    // List<Curso> findByEstadoCursoAndInstructorIsNull(EstadoCursoEnum estadoCurso);
}