package com.example.edutech.inscripcion.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.usuario.model.Usuario;

import jakarta.transaction.Transactional;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    boolean existsByUsuarioAndCurso(Usuario usuario, Curso curso);

    List<Inscripcion> findByCurso(Curso curso);

    List<Inscripcion> findByUsuario(Usuario usuario);

    Optional<Inscripcion> findByUsuarioAndCurso_Sigla(Usuario usuario, String cursoSigla);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inscripcion i WHERE i.curso.sigla = :sigla")
    void deleteByCurso(@Param("sigla") String sigla);

    @Modifying
    @Transactional
    @Query("DELETE FROM Inscripcion i WHERE i.usuario.rut = :rutUsuario")
    void deleteByUsuarioRut(@Param("rutUsuario") String rutUsuario);

    List<Inscripcion> findByCurso_Sigla(String cursoSigla);
}