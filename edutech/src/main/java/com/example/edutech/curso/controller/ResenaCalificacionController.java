package com.example.edutech.curso.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.curso.dto.ResenaCalificacionCreateDTO;
import com.example.edutech.curso.dto.ResenaCalificacionResponseDTO;
import com.example.edutech.curso.service.ResenaCalificacionService;

@RestController
@RequestMapping("/api/resenas")
public class ResenaCalificacionController {

    private static final Logger logger = LoggerFactory.getLogger(ResenaCalificacionController.class);
    private final ResenaCalificacionService resenaCalificacionService;

    public ResenaCalificacionController(ResenaCalificacionService resenaCalificacionService) {
        this.resenaCalificacionService = resenaCalificacionService;
    }

    @PostMapping
    public ResponseEntity<?> crearResena(@RequestBody ResenaCalificacionCreateDTO createDTO) {
        try {
            ResenaCalificacionResponseDTO responseDTO = resenaCalificacionService.crearResena(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear reseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            logger.warn("Error de estado al crear reseña (ej. no inscrito): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.warn("Conflicto de datos al crear reseña (ej. duplicado): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: El usuario ya ha realizado una reseña para este curso o hay un problema de integridad de datos.");
        } catch (Exception e) {
            logger.error("Error interno al procesar la solicitud de creación de reseña:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la solicitud de reseña.");
        }
    }

    @GetMapping
    public ResponseEntity<List<ResenaCalificacionResponseDTO>> listarTodasLasResenas() {
        List<ResenaCalificacionResponseDTO> resenas = resenaCalificacionService.listarTodasLasResenas();
        return ResponseEntity.ok(resenas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerResenaPorId(@PathVariable Integer id) {
        ResenaCalificacionResponseDTO responseDTO = resenaCalificacionService.obtenerResenaPorId(id);
        if (responseDTO == null) {
            logger.info("Reseña con ID {} no encontrada.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reseña con ID " + id + " no encontrada.");
        }
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/curso/{cursoSigla}")
    public ResponseEntity<?> listarResenasPorCurso(
            @PathVariable String cursoSigla,
            @RequestParam(required = false, defaultValue = "false") boolean soloAprobadas) {
        try {
            List<ResenaCalificacionResponseDTO> resenas = resenaCalificacionService.listarResenasPorCurso(cursoSigla, soloAprobadas);
            return ResponseEntity.ok(resenas);
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de listar reseñas para curso no existente: {}", cursoSigla);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioRut}")
    public ResponseEntity<?> listarResenasPorUsuario(@PathVariable String usuarioRut) {
        try {
            List<ResenaCalificacionResponseDTO> resenas = resenaCalificacionService.listarResenasPorUsuario(usuarioRut);
            return ResponseEntity.ok(resenas);
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de listar reseñas para usuario no existente: {}", usuarioRut);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/aprobacion")
    public ResponseEntity<?> gestionarAprobacionResena(@PathVariable Integer id, @RequestBody Map<String, Boolean> payload) {
        Boolean aprobada = payload.get("aprobada");
        if (aprobada == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'aprobada' (true/false) es requerido.");
        }
        try {
            ResenaCalificacionResponseDTO responseDTO = resenaCalificacionService.actualizarEstadoAprobacion(id, aprobada);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de gestionar aprobación para reseña no existente: ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarResena(@PathVariable Integer id) {
        try {
            resenaCalificacionService.eliminarResena(id);
            logger.info("Reseña con ID {} eliminada.", id);
            return ResponseEntity.ok("Reseña con ID " + id + " eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de eliminar reseña no existente: ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}