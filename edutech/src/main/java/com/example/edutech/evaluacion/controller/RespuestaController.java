package com.example.edutech.evaluacion.controller;

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

import com.example.edutech.evaluacion.dto.RespuestaCreateDTO;
import com.example.edutech.evaluacion.dto.RespuestaResponseDTO; // IMPORTAR
import com.example.edutech.evaluacion.service.RespuestaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/respuestas")
public class RespuestaController {
    private static final Logger logger = LoggerFactory.getLogger(RespuestaController.class);
    private final RespuestaService respuestaService;

    public RespuestaController(RespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    @PostMapping
    public ResponseEntity<?> crearRespuesta(@Valid @RequestBody RespuestaCreateDTO dto) {
        try {
            RespuestaResponseDTO respuestaGuardadaDTO = respuestaService.guardarRespuestaDTO(dto); // CAMBIO
            return ResponseEntity.status(HttpStatus.CREATED).body(respuestaGuardadaDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al guardar respuesta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al guardar respuesta:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al guardar la respuesta.");
        }
    }

    @GetMapping("/pregunta/{preguntaId}")
    public ResponseEntity<?> listarRespuestasPorPregunta(@PathVariable Integer preguntaId) {
        try {
            List<RespuestaResponseDTO> respuestasDTO = respuestaService.listarRespuestasDTOPorPreguntaId(preguntaId); // CAMBIO
            return ResponseEntity.ok(respuestasDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al listar respuestas para pregunta ID {}: {}", preguntaId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}") // NUEVO ENDPOINT
    public ResponseEntity<?> obtenerRespuestaPorId(@PathVariable Integer id) {
        try {
            RespuestaResponseDTO respuestaDTO = respuestaService.obtenerRespuestaDTOPorId(id);
            return ResponseEntity.ok(respuestaDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Respuesta con ID {} no encontrada.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<List<RespuestaResponseDTO>> listarTodasLasRespuestas() { // CAMBIO
        List<RespuestaResponseDTO> respuestasDTO = respuestaService.listarTodasLasRespuestasDTO(); // CAMBIO
        return ResponseEntity.ok(respuestasDTO);
    }
}