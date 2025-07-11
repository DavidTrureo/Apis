package com.example.edutech.inscripcion.controller;

import java.util.List;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.inscripcion.dto.InscripcionCreateDTO;
import com.example.edutech.inscripcion.dto.InscripcionResponseDTO;
import com.example.edutech.inscripcion.service.InscripcionService;

@RestController
@RequestMapping("/api/inscripciones") 
public class InscripcionController {
    
    private static final Logger logger = LoggerFactory.getLogger(InscripcionController.class);
    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarInscripcion(@RequestBody InscripcionCreateDTO createDTO) {
        try {
            InscripcionResponseDTO nuevaInscripcion = inscripcionService.registrarInscripcionConPago(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaInscripcion);
        } catch (IllegalArgumentException | IllegalStateException e) { 
            logger.warn("Error al registrar inscripción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al registrar inscripción:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado al procesar la inscripción.");
        }
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResponseDTO>> listarTodasLasInscripciones() {
        List<InscripcionResponseDTO> inscripciones = inscripcionService.listarTodasLasInscripciones();
        return ResponseEntity.ok(inscripciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerInscripcionPorId(@PathVariable Integer id) {
        try {
            InscripcionResponseDTO inscripcion = inscripcionService.obtenerInscripcionPorId(id);
            return ResponseEntity.ok(inscripcion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/usuario/{rutUsuario}")
    public ResponseEntity<?> listarInscripcionesPorUsuario(@PathVariable String rutUsuario) {
        try {
            List<InscripcionResponseDTO> inscripciones = inscripcionService.listarInscripcionesPorUsuario(rutUsuario);
            return ResponseEntity.ok(inscripciones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}