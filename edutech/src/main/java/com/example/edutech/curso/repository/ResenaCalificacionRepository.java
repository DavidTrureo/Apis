package com.example.edutech.curso.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.ResenaCalificacion;
import com.example.edutech.usuario.model.Usuario;

@Repository
public interface ResenaCalificacionRepository extends JpaRepository<ResenaCalificacion, Integer> {

    Optional<ResenaCalificacion> findByUsuarioAndCurso(Usuario usuario, Curso curso);

    List<ResenaCalificacion> findByCurso(Curso curso);


    List<ResenaCalificacion> findByCursoAndEsAprobadaTrue(Curso curso);

    List<ResenaCalificacion> findByUsuario(Usuario usuario);


    List<ResenaCalificacion> findByCursoSigla(String cursoSigla);
    List<ResenaCalificacion> findByUsuarioRut(String usuarioRut);
}