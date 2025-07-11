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

import com.example.edutech.evaluacion.dto.PreguntaCreateDTO;
import com.example.edutech.evaluacion.dto.PreguntaResponseDTO;
import com.example.edutech.evaluacion.service.PreguntaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {
    private static final Logger logger = LoggerFactory.getLogger(PreguntaController.class);
    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @PostMapping
    public ResponseEntity<?> crearPregunta(@Valid @RequestBody PreguntaCreateDTO dto) {
        try {
            PreguntaResponseDTO nuevaPreguntaDTO = preguntaService.crearPreguntaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPreguntaDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear pregunta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear pregunta:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la pregunta.");
        }
    }

    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<?> listarPreguntasPorEvaluacion(@PathVariable Integer evaluacionId) {
        try {
            List<PreguntaResponseDTO> preguntasDTO = preguntaService.listarPreguntasDTOPorEvaluacionId(evaluacionId);
            return ResponseEntity.ok(preguntasDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al listar preguntas para evaluación ID {}: {}", evaluacionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPreguntaPorId(@PathVariable Integer id) {
        try {
            PreguntaResponseDTO preguntaDTO = preguntaService.obtenerPreguntaDTOPorId(id);
            return ResponseEntity.ok(preguntaDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PreguntaResponseDTO>> listarTodasLasPreguntas() {
        List<PreguntaResponseDTO> preguntasDTO = preguntaService.listarTodasLasPreguntasDTO();
        return ResponseEntity.ok(preguntasDTO);
    }
}