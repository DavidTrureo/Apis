package com.example.edutech.curso.service;
//REALIZADO POR: Maverick Valdes
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.dto.CursoCreateDTO;
import com.example.edutech.curso.dto.CursoResponseDTO;
import com.example.edutech.curso.dto.CursoUpdateDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.EstadoCursoEnum; // IMPORTAR
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;

    public CursoService(CursoRepository cursoRepository,
                        UsuarioRepository usuarioRepository,
                        InscripcionRepository inscripcionRepository) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional
    public CursoResponseDTO crearCurso(CursoCreateDTO cursoDTO) {
        if (cursoDTO.getSigla() == null || cursoDTO.getSigla().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: La sigla del curso no puede estar vacía.");
        }
        if (cursoDTO.getNombre() == null || cursoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre del curso no puede estar vacío.");
        }


        if (cursoRepository.existsById(cursoDTO.getSigla())) {
            throw new IllegalArgumentException("Error: Ya existe un curso con la sigla " + cursoDTO.getSigla());
        }

        Usuario instructorAsignado = null;
        if (cursoDTO.getRutInstructor() != null && !cursoDTO.getRutInstructor().trim().isEmpty()) {
            instructorAsignado = usuarioRepository.findByRut(cursoDTO.getRutInstructor());
            if (instructorAsignado == null) {
                throw new IllegalArgumentException("Error: Instructor con RUT '" + cursoDTO.getRutInstructor() + "' no encontrado.");
            }
            if (instructorAsignado.getRol() == null || !"INSTRUCTOR".equalsIgnoreCase(instructorAsignado.getRol().getNombre())) {
                throw new IllegalArgumentException("Error: El usuario con RUT '" + cursoDTO.getRutInstructor() + "' no tiene el rol de INSTRUCTOR.");
            }
        }

        Curso nuevoCurso = new Curso();
        nuevoCurso.setSigla(cursoDTO.getSigla());
        nuevoCurso.setNombre(cursoDTO.getNombre());
        nuevoCurso.setDescripcionDetallada(cursoDTO.getDescripcionDetallada());
        nuevoCurso.setEstadoCurso(EstadoCursoEnum.BORRADOR); // Estado por defecto al crear

        if (instructorAsignado != null) {
            nuevoCurso.setInstructor(instructorAsignado);
        }

        Curso cursoGuardado = cursoRepository.save(nuevoCurso);
        return mapToCursoResponseDTO(cursoGuardado);
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerCursoPorSigla(String sigla) {
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));
        // Aquí se podría añadir lógica de seguridad para roles si es necesario
        return mapToCursoResponseDTO(curso);
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosPorEstado(EstadoCursoEnum estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado para filtrar no puede ser nulo.");
        }
        return cursoRepository.findByEstadoCurso(estado).stream()
                .map(this::mapToCursoResponseDTO)
                .collect(Collectors.toList());
    }


    public Curso obtenerEntidadCursoPorSigla(String sigla) { // Método de utilidad
        return cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarTodosLosCursos() {
        // TODO: Implementar lógica de visibilidad según rol.
        // Por ahora, lista todos. Si el usuario es ESTUDIANTE, solo debería ver los PUBLICADOS.
        return cursoRepository.findAll().stream()
                .map(this::mapToCursoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CursoResponseDTO actualizarCurso(String sigla, CursoUpdateDTO cursoUpdateDTO) {
        Curso cursoExistente = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + sigla + "' no encontrado."));

        boolean modificado = false;

        if (cursoUpdateDTO.getNombre() != null && !cursoUpdateDTO.getNombre().isBlank() && !cursoUpdateDTO.getNombre().equals(cursoExistente.getNombre())) {
            cursoExistente.setNombre(cursoUpdateDTO.getNombre());
            modificado = true;
        }
        if (cursoUpdateDTO.getDescripcionDetallada() != null && !cursoUpdateDTO.getDescripcionDetallada().equals(cursoExistente.getDescripcionDetallada())) {
            cursoExistente.setDescripcionDetallada(cursoUpdateDTO.getDescripcionDetallada());
            modificado = true;
        }
        // El estado del curso se actualiza a través de un método específico.
        // Si se permitiera actualizar el estado aquí, sería:
        // if (cursoUpdateDTO.getEstadoCurso() != null && !cursoUpdateDTO.getEstadoCurso().equals(cursoExistente.getEstadoCurso())) {
        //    cursoExistente.setEstadoCurso(cursoUpdateDTO.getEstadoCurso());
        //    modificado = true;
        // }


        if (cursoUpdateDTO.getRutInstructor() != null) {
            if (cursoUpdateDTO.getRutInstructor().trim().isEmpty()) {
                if (cursoExistente.getInstructor() != null) {
                    cursoExistente.setInstructor(null);
                    modificado = true;
                }
            } else {
                if (cursoExistente.getInstructor() == null || !cursoExistente.getInstructor().getRut().equals(cursoUpdateDTO.getRutInstructor())) {
                    asignarInstructorInterno(cursoExistente, cursoUpdateDTO.getRutInstructor());
                    modificado = true;
                }
            }
        }

        if (modificado) {
            Curso cursoGuardado = cursoRepository.save(cursoExistente);
            return mapToCursoResponseDTO(cursoGuardado);
        } else {
            return mapToCursoResponseDTO(cursoExistente); // Devuelve el existente si no hubo cambios
        }
    }

    @Transactional
    public CursoResponseDTO actualizarEstadoCurso(String sigla, EstadoCursoEnum nuevoEstado) {
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }

        // Lógica de transición de estados (ejemplos)
        if (nuevoEstado == EstadoCursoEnum.PUBLICADO) {
            if (curso.getInstructor() == null) {
                throw new IllegalStateException("No se puede publicar un curso sin instructor asignado.");
            }
            if (curso.getContenidos() == null || curso.getContenidos().isEmpty()) {
                throw new IllegalStateException("No se puede publicar un curso sin contenido.");
            }
            // Podrías añadir más validaciones: ¿tiene precio base si no es gratuito? etc.
        }

        curso.setEstadoCurso(nuevoEstado);
        Curso cursoActualizado = cursoRepository.save(curso);
        return mapToCursoResponseDTO(cursoActualizado);
    }


    @Transactional
    public String eliminarCurso(String sigla) {
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + sigla + "' no encontrado."));

        // Eliminar inscripciones asociadas
        inscripcionRepository.deleteByCurso(sigla);
        // Otras dependencias (evaluaciones, foros, etc.) se eliminarán por Cascade si está configurado en la entidad Curso.
        // Si no, necesitarías eliminarlas explícitamente aquí.

        cursoRepository.delete(curso);
        return "Curso '" + sigla + "' y sus datos asociados eliminados correctamente.";
    }

    @Transactional
    public String asignarInstructorACurso(String siglaCurso, String rutInstructor) {
        Curso curso = cursoRepository.findById(siglaCurso)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + siglaCurso + "' no encontrado."));

        if (rutInstructor == null || rutInstructor.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El RUT del instructor no puede ser vacío para asignación.");
        }
        asignarInstructorInterno(curso, rutInstructor);
        cursoRepository.save(curso);

        String nombreInstructor = (curso.getInstructor() != null) ? curso.getInstructor().getNombre() : "Desconocido";
        return "Instructor " + nombreInstructor + " asignado correctamente al curso " + curso.getNombre() + ".";
    }

    @Transactional
    public String desasignarInstructorDeCurso(String siglaCurso) {
        Curso curso = cursoRepository.findById(siglaCurso)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + siglaCurso + "' no encontrado."));

        if (curso.getInstructor() == null) {
            return "El curso '" + curso.getNombre() + "' ya no tiene un instructor asignado.";
        }

        String nombreInstructorAnterior = curso.getInstructor().getNombre();
        curso.setInstructor(null);
        cursoRepository.save(curso);
        return "Instructor " + nombreInstructorAnterior + " desasignado correctamente del curso " + curso.getNombre() + ".";
    }

    private void asignarInstructorInterno(Curso curso, String rutInstructor) {
        Usuario instructor = usuarioRepository.findByRut(rutInstructor);
        if (instructor == null) {
            throw new IllegalArgumentException("Error: Instructor con RUT '" + rutInstructor + "' no encontrado.");
        }
        if (instructor.getRol() == null || !"INSTRUCTOR".equalsIgnoreCase(instructor.getRol().getNombre())) {
            throw new IllegalArgumentException("Error: El usuario con RUT '" + rutInstructor + "' no tiene el rol de INSTRUCTOR.");
        }
        curso.setInstructor(instructor);
    }

    private CursoResponseDTO mapToCursoResponseDTO(Curso curso) {
        if (curso == null) return null;
        UsuarioDTO instructorDTO = null;
        if (curso.getInstructor() != null) {
            Usuario instructor = curso.getInstructor();
            instructorDTO = new UsuarioDTO(
                instructor.getRut(),
                instructor.getNombre(),
                instructor.getEmail(),
                instructor.getRol() != null ? instructor.getRol().getNombre() : null,
                instructor.getEstadoCuenta()
            );
        }
        return new CursoResponseDTO(
                curso.getSigla(),
                curso.getNombre(),
                curso.getDescripcionDetallada(),
                curso.getEstadoCurso(),
                instructorDTO
        );
    }

    public List<CursoResponseDTO> generarReportes() {
        // Este es un placeholder. La generación de reportes real está en ReporteService.
        // Aquí podría devolver, por ejemplo, un resumen de cursos para un reporte.
        return listarTodosLosCursos();
    }
}