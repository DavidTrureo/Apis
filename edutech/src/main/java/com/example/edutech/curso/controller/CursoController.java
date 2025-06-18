package com.example.edutech.curso.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.curso.dto.CursoCreateDTO;
import com.example.edutech.curso.dto.CursoResponseDTO;
import com.example.edutech.curso.dto.CursoUpdateDTO;
import com.example.edutech.curso.model.EstadoCursoEnum;
import com.example.edutech.curso.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);
    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCurso(@Valid @RequestBody CursoCreateDTO cursoCreateDTO,
                                          @RequestHeader("X-Admin-RUT") String adminRutSolicitante) { // AÑADIR cabecera
        logger.info("POST /api/cursos/registrar solicitado por admin {}", adminRutSolicitante);
        try {
            CursoResponseDTO cursoCreado = cursoService.crearCurso(cursoCreateDTO, adminRutSolicitante); // PASAR adminRut
            return ResponseEntity.status(HttpStatus.CREATED).body(cursoCreado);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de registrar curso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al registrar curso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al registrar curso:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el curso.");
        }
    }
    
    @GetMapping
    public ResponseEntity<?> listarCursos(
            @RequestParam(required = false) String palabraClave,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String rutInstructor,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            // Asumimos que el que solicita el listado completo con filtros es un admin o la lógica de visibilidad
            // se aplicará en el servicio si no se pasa un RUT de admin explícito.
            // @RequestHeader(name = "X-User-RUT", required = false) String userRut, // Para una futura lógica de visibilidad
            @PageableDefault(page = 0, size = 10, sort = "nombre") Pageable pageable) {
        
        logger.info("GET /api/cursos solicitado con filtros - palabraClave: {}, categoria: {}, rutInstructor: {}, estado: {}, precioMin: {}, precioMax: {}, pageable: {}",
            palabraClave, categoria, rutInstructor, estado, precioMin, precioMax, pageable);

        try {
            EstadoCursoEnum estadoEnum = null;
            if (estado != null && !estado.isBlank()) {
                try {
                    estadoEnum = EstadoCursoEnum.fromString(estado.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.warn("Valor de estado inválido proporcionado: {}", estado);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de curso no válido: " + estado);
                }
            }
            
            Page<CursoResponseDTO> paginaCursos = cursoService.buscarCursosConFiltros(
                    palabraClave, categoria, rutInstructor, estadoEnum, precioMin, precioMax, pageable
            );
            return ResponseEntity.ok(paginaCursos);

        } catch (IllegalArgumentException e) { // Captura general para errores de parámetros de búsqueda
            logger.warn("Error al buscar/listar cursos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al procesar la solicitud de cursos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al buscar/listar cursos:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al obtener los cursos.");
        }
    }

    @GetMapping("/{sigla}")
    public ResponseEntity<?> obtenerCurso(@PathVariable String sigla) {
        logger.info("GET /api/cursos/{} solicitado", sigla);
        try {
            CursoResponseDTO curso = cursoService.obtenerCursoPorSigla(sigla);
            return ResponseEntity.ok(curso);
        } catch (IllegalArgumentException e) {
            logger.warn("Curso con sigla '{}' no encontrado.", sigla);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{sigla}")
    public ResponseEntity<?> actualizarCurso(@PathVariable String sigla,
                                            @Valid @RequestBody CursoUpdateDTO cursoUpdateDTO,
                                            @RequestHeader("X-Admin-RUT") String adminRutSolicitante) { // AÑADIR cabecera
        logger.info("PUT /api/cursos/{} solicitado por admin {}", sigla, adminRutSolicitante);
        try {
            CursoResponseDTO cursoActualizado = cursoService.actualizarCurso(sigla, cursoUpdateDTO, adminRutSolicitante); // PASAR adminRut
            return ResponseEntity.ok(cursoActualizado);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de actualizar curso '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
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
    public ResponseEntity<?> actualizarEstadoCurso(@PathVariable String sigla,
                                                @RequestBody Map<String, String> payload,
                                                @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        String nuevoEstadoStr = payload.get("nuevoEstado");
        logger.info("PUT /api/cursos/{}/estado solicitado con nuevoEstado: {} por admin {}", sigla, nuevoEstadoStr, adminRutSolicitante);
        if (nuevoEstadoStr == null || nuevoEstadoStr.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'nuevoEstado' es requerido en el payload (ej: {\"nuevoEstado\": \"PUBLICADO\"}).");
        }
        try {
            EstadoCursoEnum nuevoEstado = EstadoCursoEnum.fromString(nuevoEstadoStr.toUpperCase());
            CursoResponseDTO cursoActualizado = cursoService.actualizarEstadoCurso(sigla, nuevoEstado, adminRutSolicitante);
            return ResponseEntity.ok(cursoActualizado);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de actualizar estado del curso '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) { 
            logger.warn("Error al actualizar estado del curso '{}' a '{}': {}", sigla, nuevoEstadoStr, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Estado de curso no válido o error: " + e.getMessage());
        } catch (IllegalStateException e) { 
            logger.warn("Conflicto al actualizar estado del curso '{}' a '{}': {}", sigla, nuevoEstadoStr, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar estado del curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el estado del curso.");
        }
    }

    @DeleteMapping("/{sigla}")
    public ResponseEntity<String> eliminarCurso(@PathVariable String sigla,
                                                @RequestHeader("X-Admin-RUT") String adminRutSolicitante) { // AÑADIR cabecera
        logger.info("DELETE /api/cursos/{} solicitado por admin {}", sigla, adminRutSolicitante);
        try {
            String mensaje = cursoService.eliminarCurso(sigla, adminRutSolicitante); // PASAR adminRut
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de eliminar curso '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) { 
            logger.warn("Intento de eliminar curso no existente '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar curso '{}':", sigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el curso.");
        }
    }

    @PutMapping("/{sigla}/instructor")
    public ResponseEntity<String> gestionarInstructorCurso(@PathVariable String sigla,
                                                        @RequestBody Map<String, String> payload,
                                                        @RequestHeader("X-Admin-RUT") String adminRutSolicitante) { // AÑADIR cabecera
        String rutInstructor = payload.get("rutInstructor");
        logger.info("PUT /api/cursos/{}/instructor solicitado con rutInstructor: {} por admin {}", sigla, rutInstructor, adminRutSolicitante);

        if (rutInstructor == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'rutInstructor' es requerido en el cuerpo de la solicitud.");
        }
        try {
            String mensaje;
            if (rutInstructor.trim().isEmpty()) {
                mensaje = cursoService.desasignarInstructorDeCurso(sigla, adminRutSolicitante); // PASAR adminRut
            } else {
                mensaje = cursoService.asignarInstructorACurso(sigla, rutInstructor, adminRutSolicitante); // PASAR adminRut
            }
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de gestionar instructor para curso '{}': {}", sigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
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
    public ResponseEntity<List<CursoResponseDTO>> generarReportes() {
        // Este endpoint es un placeholder. La lógica real está en ReporteController.
        logger.info("GET /api/cursos/reportes solicitado (placeholder)");
        return ResponseEntity.ok(cursoService.generarReportes());
    }
}