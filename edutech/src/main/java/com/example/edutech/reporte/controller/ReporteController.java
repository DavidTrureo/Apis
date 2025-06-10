package com.example.edutech.reporte.controller;

import java.util.List;
import java.util.Map;

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

import com.example.edutech.reporte.dto.ReporteDTO;
import com.example.edutech.reporte.service.ReporteService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);
    private final ReporteService reporteService;

    //@Autowired
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping("/generar/inscripciones-curso")
    public ResponseEntity<?> generarReporteInscripciones(@RequestBody Map<String, String> payload) {
        String cursoSigla = payload.get("cursoSigla");
        if (cursoSigla == null || cursoSigla.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'cursoSigla' es requerido en el cuerpo de la solicitud.");
        }
        try {
            ReporteDTO reporteGenerado = reporteService.generarReporteInscripciones(cursoSigla);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporteGenerado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al generar reporte de inscripciones para curso {}: {}", cursoSigla, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error de runtime al generar reporte de inscripciones para curso {}:", cursoSigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud: " + e.getMessage());
        }
         catch (Exception e) { // Captura genérica final
            logger.error("Error interno inesperado al generar reporte de inscripciones para curso {}:", cursoSigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al generar el reporte de inscripciones.");
        }
    }

    @PostMapping("/generar/rendimiento-curso")
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_GERENTE_CURSOS')") // Seguridad futura
    public ResponseEntity<?> generarReporteRendimiento(@RequestBody Map<String, String> payload) {
        String cursoSigla = payload.get("cursoSigla");
        if (cursoSigla == null || cursoSigla.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'cursoSigla' es requerido en el cuerpo de la solicitud.");
        }
        try {
            ReporteDTO reporteGenerado = reporteService.generarReporteRendimiento(cursoSigla);
            return ResponseEntity.status(HttpStatus.CREATED).body(reporteGenerado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al generar reporte de rendimiento para curso {}: {}", cursoSigla, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Error de runtime al generar reporte de rendimiento para curso {}:", cursoSigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud: " + e.getMessage());
        }
        catch (Exception e) {
            logger.error("Error interno inesperado al generar reporte de rendimiento para curso {}:", cursoSigla, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al generar el reporte de rendimiento.");
        }
    }

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> listarReportes() {
        List<ReporteDTO> reportes = reporteService.listarReportes();
        return ResponseEntity.ok(reportes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReportePorId(@PathVariable Integer id) {
        try {
            ReporteDTO reporte = reporteService.obtenerReportePorId(id);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            logger.warn("Reporte con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}