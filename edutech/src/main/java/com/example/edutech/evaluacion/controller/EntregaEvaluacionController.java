package com.example.edutech.evaluacion.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.evaluacion.dto.EntregaEvaluacionCorreccionDTO;
import com.example.edutech.evaluacion.dto.EntregaEvaluacionCreateDTO;
import com.example.edutech.evaluacion.dto.EntregaEvaluacionResponseDTO; // IMPORTAR
import com.example.edutech.evaluacion.service.EntregaEvaluacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entregas-evaluacion")
public class EntregaEvaluacionController {

    private static final Logger logger = LoggerFactory.getLogger(EntregaEvaluacionController.class);
    private final EntregaEvaluacionService entregaEvaluacionService;

    public EntregaEvaluacionController(EntregaEvaluacionService entregaEvaluacionService) {
        this.entregaEvaluacionService = entregaEvaluacionService;
    }

    @PostMapping
    public ResponseEntity<?> registrarEntrega(@Valid @RequestBody EntregaEvaluacionCreateDTO dto) {
        try {
            EntregaEvaluacionResponseDTO nuevaEntregaDTO = entregaEvaluacionService.registrarEntregaDTO(dto); // CAMBIO
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEntregaDTO);
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error al registrar entrega de evaluación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al registrar entrega de evaluación:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar la entrega.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEntregaPorId(@PathVariable Integer id) {
        try {
            EntregaEvaluacionResponseDTO entregaDTO = entregaEvaluacionService.obtenerEntregaDTOPorId(id); // CAMBIO
            return ResponseEntity.ok(entregaDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Entrega de evaluación con ID {} no encontrada.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EntregaEvaluacionResponseDTO>> listarEntregas() { // CAMBIO
        List<EntregaEvaluacionResponseDTO> entregasDTO = entregaEvaluacionService.listarEntregasDTO(); // CAMBIO
        return ResponseEntity.ok(entregasDTO);
    }


    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<?> listarEntregasPorEvaluacion(@PathVariable Integer evaluacionId) {
        try {
            List<EntregaEvaluacionResponseDTO> entregasDTO = entregaEvaluacionService.listarEntregasDTOPorEvaluacionId(evaluacionId); // CAMBIO
            return ResponseEntity.ok(entregasDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de listar entregas para evaluación no existente ID: {}", evaluacionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @PutMapping("/{entregaId}/corregir")
    public ResponseEntity<?> corregirEntregaEvaluacion(
            @PathVariable Integer entregaId,
            @Valid @RequestBody EntregaEvaluacionCorreccionDTO correccionDTO) {
        try {
            EntregaEvaluacionResponseDTO entregaCorregidaDTO = entregaEvaluacionService.corregirEntregaDTO(entregaId, correccionDTO); // CAMBIO
            return ResponseEntity.ok(entregaCorregidaDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al corregir entrega de evaluación ID {}: {}", entregaId, e.getMessage());
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al corregir entrega de evaluación ID {}:", entregaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al corregir la entrega.");
        }
    }
}