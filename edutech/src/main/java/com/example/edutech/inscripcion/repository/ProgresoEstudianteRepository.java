package com.example.edutech.inscripcion.repository;
//REALIZADO POR: Cristóbal Mira
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.edutech.curso.model.ContenidoCurso;
import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.inscripcion.model.ProgresoEstudiante;

@Repository
public interface ProgresoEstudianteRepository extends JpaRepository<ProgresoEstudiante, Integer> {

    Optional<ProgresoEstudiante> findByInscripcionAndContenidoCurso(Inscripcion inscripcion, ContenidoCurso contenidoCurso);

    List<ProgresoEstudiante> findByInscripcion(Inscripcion inscripcion);

    List<ProgresoEstudiante> findByInscripcion_Id(Integer inscripcionId);

    @Query("SELECT p FROM ProgresoEstudiante p WHERE p.inscripcion.usuario.rut = :usuarioRut AND p.inscripcion.curso.sigla = :cursoSigla")
    List<ProgresoEstudiante> findByUsuarioRutAndCursoSigla(@Param("usuarioRut") String usuarioRut, @Param("cursoSigla") String cursoSigla);

    long countByInscripcionAndCompletadoTrue(Inscripcion inscripcion);
}