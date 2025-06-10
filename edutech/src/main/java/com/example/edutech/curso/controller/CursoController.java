package com.example.edutech.curso.controller;
//REALIZADO POR: Maverick Valdes
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // IMPORTAR
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.curso.dto.CursoCreateDTO;
import com.example.edutech.curso.dto.CursoResponseDTO;
import com.example.edutech.curso.dto.CursoUpdateDTO;
import com.example.edutech.curso.model.EstadoCursoEnum; // IMPORTAR
import com.example.edutech.curso.service.CursoService;

import jakarta.validation.Valid; // IMPORTAR

@RestController
@RequestMapping("/api/cursos") // Estandarizar ruta base
public class CursoController {

    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);
    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping("/registrar")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> registrarCurso(@Valid @RequestBody CursoCreateDTO cursoCreateDTO) {
        try {
            CursoResponseDTO cursoCreado = cursoService.crearCurso(cursoCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoCreado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al registrar curso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al registrar curso:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el curso.");
        }
    }

    @GetMapping
    // @PreAuthorize("isAuthenticated()") // Permitir a todos los autenticados, filtrar en el servicio
    public ResponseEntity<?> listarTodosLosCursos(
            @RequestParam(required = false) String estado // Parámetro opcional para filtrar por estado
    ) {
        List<CursoResponseDTO> cursos;
        try {
            if (estado != null && !estado.isBlank()) {
                EstadoCursoEnum estadoEnum = EstadoCursoEnum.fromString(estado.toUpperCase());
                cursos = cursoService.listarCursosPorEstado(estadoEnum);
            } else {
                // TODO: Ajustar la lógica en el servicio para filtrar cursos PUBLICADOS para ESTUDIANTES
                //       y todos los cursos para GERENTE_CURSOS/ADMIN.
                //       Por ahora, el servicio lista todos si no se especifica estado.
                cursos = cursoService.listarTodosLosCursos();
            }
            return ResponseEntity.ok(cursos);
        } catch (IllegalArgumentException e) {
            // Esto se dispara si EstadoCursoEnum.fromString falla o si el servicio lanza IllegalArgumentException
            logger.warn("Error al listar cursos con estado '{}': {}", estado, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al procesar la solicitud: " + e.getMessage());
        }
    }


    @GetMapping("/{sigla}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtenerCurso(@PathVariable String sigla) {
        try {
            CursoResponseDTO curso = cursoService.obtenerCursoPorSigla(sigla);
            // TODO: Añadir lógica de visibilidad aquí o en el servicio:
            // Si el usuario es ESTUDIANTE, solo puede ver si curso.getEstadoCurso() == PUBLICADO.
            return ResponseEntity.ok(curso);
        } catch (IllegalArgumentException e) {
            logger.warn("Curso con sigla '{}' no encontrado.", sigla);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{sigla}")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> actualizarCurso(@PathVariable String sigla, @Valid @RequestBody CursoUpdateDTO cursoUpdateDTO) {
        try {
            CursoResponseDTO cursoActualizado = cursoService.actualizarCurso(sigla, cursoUpdateDTO);
            return ResponseEntity.ok(cursoActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar curso '{}': {}", sigla, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el curso.");
        }
    }

    @PutMapping("/{sigla}/estado")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> actualizarEstadoCurso(@PathVariable String sigla, @RequestBody Map<String, String> payload) {
        String nuevoEstadoStr = payload.get("nuevoEstado");
        if (nuevoEstadoStr == null || nuevoEstadoStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'nuevoEstado' es requerido en el payload (ej: {\"nuevoEstado\": \"PUBLICADO\"}).");
        }
        try {
            EstadoCursoEnum nuevoEstado = EstadoCursoEnum.fromString(nuevoEstadoStr.toUpperCase());
            CursoResponseDTO cursoActualizado = cursoService.actualizarEstadoCurso(sigla, nuevoEstado);
            return ResponseEntity.ok(cursoActualizado);
        } catch (IllegalArgumentException e) { // Captura si el String no es un Enum válido o si el curso no se encuentra
            logger.warn("Error al actualizar estado del curso '{}' a '{}': {}", sigla, nuevoEstadoStr, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de curso no válido o error: " + e.getMessage());
        } catch (IllegalStateException e) { // Captura errores de lógica de negocio (ej. no se puede publicar sin contenido)
            logger.warn("Conflicto al actualizar estado del curso '{}' a '{}': {}", sigla, nuevoEstadoStr, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar estado del curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el estado del curso.");
        }
    }


    @DeleteMapping("/{sigla}")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<String> eliminarCurso(@PathVariable String sigla) {
        try {
            String mensaje = cursoService.eliminarCurso(sigla);
            logger.info("Curso '{}' eliminado exitosamente.", sigla);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) { // Para curso no encontrado
            logger.warn("Intento de eliminar curso no existente '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el curso.");
        }
    }


    @PutMapping("/{sigla}/instructor")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<String> gestionarInstructorCurso(@PathVariable String sigla, @RequestBody Map<String, String> payload) {
        String rutInstructor = payload.get("rutInstructor");

        if (rutInstructor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'rutInstructor' es requerido en el cuerpo de la solicitud.");
        }

        try {
            String mensaje;
            if (rutInstructor.trim().isEmpty()) {
                mensaje = cursoService.desasignarInstructorDeCurso(sigla);
            } else {
                mensaje = cursoService.asignarInstructorACurso(sigla, rutInstructor);
            }
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al gestionar instructor para curso '{}': {}", sigla, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al gestionar instructor para curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al gestionar el instructor.");
        }
    }

    @GetMapping("/reportes")
    // @PreAuthorize("hasAuthority('ROL_GERENTE_CURSOS') or hasAuthority('ROL_ADMIN')")
    public ResponseEntity<List<CursoResponseDTO>> generarReportes() {
        // Este endpoint es un placeholder como estaba antes. La generación real está en ReporteController.
        return ResponseEntity.ok(cursoService.generarReportes());
    }
}