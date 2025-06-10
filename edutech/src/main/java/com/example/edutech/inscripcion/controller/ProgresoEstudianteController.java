package com.example.edutech.inscripcion.controller;
//REALIZADO POR: Cristóbal Mira
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.inscripcion.dto.MarcarContenidoCompletadoDTO;
import com.example.edutech.inscripcion.dto.ProgresoCursoDTO;
import com.example.edutech.inscripcion.dto.ProgresoEstudianteDTO;
import com.example.edutech.inscripcion.service.ProgresoEstudianteService;

@RestController
@RequestMapping("/api/progreso")
public class ProgresoEstudianteController {

    private static final Logger logger = LoggerFactory.getLogger(ProgresoEstudianteController.class);
    private final ProgresoEstudianteService progresoEstudianteService;

   
    public ProgresoEstudianteController(ProgresoEstudianteService progresoEstudianteService) {
        this.progresoEstudianteService = progresoEstudianteService;
    }

   
    @PutMapping("/marcar-contenido")
    public ResponseEntity<?> marcarContenido(@RequestBody MarcarContenidoCompletadoDTO dto) {
        try {
            ProgresoEstudianteDTO progresoActualizado = progresoEstudianteService.marcarContenido(dto);
            return ResponseEntity.ok(progresoActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al marcar contenido como completado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al marcar contenido:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al marcar el contenido.");
        }
    }

   
    @GetMapping("/inscripcion/{inscripcionId}")
    public ResponseEntity<?> obtenerProgresoPorInscripcion(@PathVariable Integer inscripcionId) {
        try {
            List<ProgresoEstudianteDTO> progreso = progresoEstudianteService.obtenerProgresoPorInscripcion(inscripcionId);
            return ResponseEntity.ok(progreso);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al obtener progreso para inscripción ID {}: {}", inscripcionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    
    @GetMapping("/usuario/{usuarioRut}/curso/{cursoSigla}")
    public ResponseEntity<?> obtenerProgresoGeneralCurso(@PathVariable String usuarioRut, @PathVariable String cursoSigla) {
        try {
            ProgresoCursoDTO progresoGeneral = progresoEstudianteService.obtenerProgresoGeneralCurso(usuarioRut, cursoSigla);
            return ResponseEntity.ok(progresoGeneral);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al obtener progreso general para usuario {} en curso {}: {}", usuarioRut, cursoSigla, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}