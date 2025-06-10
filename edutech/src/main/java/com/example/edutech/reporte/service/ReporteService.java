package com.example.edutech.reporte.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.reporte.dto.ReporteDTO;
import com.example.edutech.reporte.dto.ReporteInscripcionesCursoDataDTO; // Importar Logger
import com.example.edutech.reporte.dto.ReporteRendimientoCursoDataDTO; // Importar LoggerFactory
import com.example.edutech.reporte.model.Reporte;
import com.example.edutech.reporte.repository.ReporteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class); // Definir Logger

    private final ReporteRepository reporteRepository;
    private final GeneracionReporteService generacionReporteService;
    private final ObjectMapper objectMapper;

    //@Autowired
    public ReporteService(ReporteRepository reporteRepository,
                        GeneracionReporteService generacionReporteService) {
        this.reporteRepository = reporteRepository;
        this.generacionReporteService = generacionReporteService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules(); // Para otros módulos como Jdk8Module (Optional, etc.)
    }

    @Transactional
    public ReporteDTO generarReporteInscripciones(String cursoSigla) {
        ReporteInscripcionesCursoDataDTO data = generacionReporteService.generarDatosReporteInscripcionesPorCurso(cursoSigla);
        String contenidoJson;
        try {
            contenidoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar datos del reporte de inscripciones para curso {}: ", cursoSigla, e);
            throw new RuntimeException("Error al generar el contenido JSON del reporte de inscripciones.", e);
        }

        return guardarReporte("INSCRIPCIONES_CURSO_" + cursoSigla.toUpperCase(), contenidoJson);
    }

    @Transactional
    public ReporteDTO generarReporteRendimiento(String cursoSigla) {
        ReporteRendimientoCursoDataDTO data = generacionReporteService.generarDatosReporteRendimientoPorCurso(cursoSigla);
        String contenidoJson;
        try {
            contenidoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar datos del reporte de rendimiento para curso {}: ", cursoSigla, e);
            throw new RuntimeException("Error al generar el contenido JSON del reporte de rendimiento.", e);
        }
        return guardarReporte("RENDIMIENTO_CURSO_" + cursoSigla.toUpperCase(), contenidoJson);
    }

    private ReporteDTO guardarReporte(String tipoReporte, String contenidoJson) {
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setTipo(tipoReporte);
        nuevoReporte.setFechaGeneracion(LocalDate.now());
        nuevoReporte.setContenido(contenidoJson);

        Reporte reporteGuardado = reporteRepository.save(nuevoReporte);
        return mapToDTO(reporteGuardado);
    }

    public List<ReporteDTO> listarReportes() {
        return reporteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReporteDTO obtenerReportePorId(Integer id) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reporte con ID " + id + " no encontrado."));
        return mapToDTO(reporte);
    }

    private ReporteDTO mapToDTO(Reporte reporte) {
        if (reporte == null) return null;
        return new ReporteDTO(
                reporte.getTipo(),
                reporte.getFechaGeneracion(),
                reporte.getContenido()
        );
    }
}