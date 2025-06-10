package com.example.edutech.curso.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.curso.dto.ContenidoCursoCreateDTO;
import com.example.edutech.curso.dto.ContenidoCursoResponseDTO;
import com.example.edutech.curso.dto.ContenidoCursoUpdateDTO;
import com.example.edutech.curso.service.ContenidoCursoService; // IMPORTAR

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contenido-curso")
public class ContenidoCursoController {

    private static final Logger logger = LoggerFactory.getLogger(ContenidoCursoController.class);
    private final ContenidoCursoService contenidoCursoService;

    public ContenidoCursoController(ContenidoCursoService contenidoCursoService) {
        this.contenidoCursoService = contenidoCursoService;
    }

    @PostMapping
    public ResponseEntity<?> crearContenido(@Valid @RequestBody ContenidoCursoCreateDTO dto) {
        try {
            ContenidoCursoResponseDTO nuevoContenidoDTO = contenidoCursoService.crearContenidoDTO(dto); // CAMBIO
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoContenidoDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear contenido de curso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear contenido de curso:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al crear el contenido del curso.");
        }
    }

    @GetMapping
    public ResponseEntity<List<ContenidoCursoResponseDTO>> listarTodosLosContenidos() { // CAMBIO
        List<ContenidoCursoResponseDTO> contenidosDTO = contenidoCursoService.listarContenidosDTO(); // CAMBIO
        return ResponseEntity.ok(contenidosDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerContenidoPorId(@PathVariable int id) {
        try {
            ContenidoCursoResponseDTO contenidoDTO = contenidoCursoService.obtenerContenidoDTOPorId(id); // CAMBIO
            return ResponseEntity.ok(contenidoDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Contenido de curso con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarContenido(@PathVariable int id, @Valid @RequestBody ContenidoCursoUpdateDTO dto) {
        try {
            ContenidoCursoResponseDTO contenidoActualizadoDTO = contenidoCursoService.actualizarContenidoDTO(id, dto); // CAMBIO
            return ResponseEntity.ok(contenidoActualizadoDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar contenido de curso con ID {}: {}", id, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar contenido de curso con ID {}:", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al actualizar el contenido del curso.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarContenido(@PathVariable int id) {
        try {
            contenidoCursoService.eliminarContenido(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de eliminar contenido de curso no existente con ID: {} - {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}