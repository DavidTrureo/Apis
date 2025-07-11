package com.example.edutech.evaluacion.controller;
//REALIZADO POR: David Trureo
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

import com.example.edutech.evaluacion.dto.EvaluacionCreateDTO;
import com.example.edutech.evaluacion.dto.EvaluacionDTO;
import com.example.edutech.evaluacion.service.EvaluacionService;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {
    private static final Logger logger = LoggerFactory.getLogger(EvaluacionController.class);
    private final EvaluacionService evaluacionService;


    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @PostMapping
    public ResponseEntity<?> crearEvaluacion(@RequestBody EvaluacionCreateDTO dto) {
        try {
            EvaluacionDTO nuevaEvaluacion = evaluacionService.crearEvaluacion(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEvaluacion);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear evaluación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear evaluación:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear la evaluación.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEvaluacionPorId(@PathVariable Integer id) {
        try {
            EvaluacionDTO evaluacion = evaluacionService.obtenerEvaluacionPorId(id);
            return ResponseEntity.ok(evaluacion);
        } catch (IllegalArgumentException e) {
            logger.warn("Evaluación con ID {} no encontrada.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/curso/{cursoSigla}")
    public ResponseEntity<?> listarEvaluacionesPorCurso(@PathVariable String cursoSigla) {
        try {
            List<EvaluacionDTO> evaluaciones = evaluacionService.listarEvaluacionesPorCurso(cursoSigla);
            return ResponseEntity.ok(evaluaciones);
        } catch (IllegalArgumentException e) {
            logger.warn("No se encontraron evaluaciones o el curso {} no existe.", cursoSigla);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}