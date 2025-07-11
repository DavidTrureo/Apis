package com.example.edutech.reporte.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.reporte.dto.ReporteDTO;
import com.example.edutech.reporte.dto.ReporteInscripcionesCursoDataDTO;
import com.example.edutech.reporte.dto.ReporteRendimientoCursoDataDTO;
import com.example.edutech.reporte.model.Reporte;
import com.example.edutech.reporte.repository.ReporteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ReporteService {

    private static final Logger logger = LoggerFactory.getLogger(ReporteService.class);

    private final ReporteRepository reporteRepository;
    private final GeneracionReporteService generacionReporteService;
    private final ObjectMapper objectMapper;

    public ReporteService(ReporteRepository reporteRepository,
                        GeneracionReporteService generacionReporteService) {
        this.reporteRepository = reporteRepository;
        this.generacionReporteService = generacionReporteService;
        this.objectMapper = new ObjectMapper();
        // Siempre asegurarse de que el mapper puede manejar tipos de fecha y hora de Java 8+
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.findAndRegisterModules();
    }

    @Transactional
    public ReporteDTO generarReporteInscripciones(String cursoSigla) {
        // 1. Obtiene el objeto de datos del reporte
        ReporteInscripcionesCursoDataDTO data = generacionReporteService.generarDatosReporteInscripcionesPorCurso(cursoSigla);
        
        // 2. Guarda el reporte en la base de datos (con el contenido como JSON String) 
        //    y devuelve un DTO con el contenido como un objeto anidado.
        return guardarReporte("INSCRIPCIONES_CURSO_" + cursoSigla.toUpperCase(), data);
    }

    @Transactional
    public ReporteDTO generarReporteRendimiento(String cursoSigla) {
        ReporteRendimientoCursoDataDTO data = generacionReporteService.generarDatosReporteRendimientoPorCurso(cursoSigla);
        return guardarReporte("RENDIMIENTO_CURSO_" + cursoSigla.toUpperCase(), data);
    }

    private ReporteDTO guardarReporte(String tipoReporte, Object contenidoData) {
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setTipo(tipoReporte);
        nuevoReporte.setFechaGeneracion(LocalDate.now());

        try {
            // Convierte el objeto de datos a una cadena JSON para almacenarlo en la BD
            String contenidoJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(contenidoData);
            nuevoReporte.setContenido(contenidoJson);
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar datos del reporte de tipo {}: ", tipoReporte, e);
            throw new RuntimeException("Error al generar el contenido JSON para almacenar en la base de datos.", e);
        }

        Reporte reporteGuardado = reporteRepository.save(nuevoReporte);
        
        // Devuelve un DTO que contiene el objeto de datos original, no el string
        return mapToDTO(reporteGuardado, contenidoData);
    }

    public List<ReporteDTO> listarReportes() {
        return reporteRepository.findAll().stream()
                .map(this::mapToDTO) // Usa el mapToDTO que parsea el JSON
                .collect(Collectors.toList());
    }

    public ReporteDTO obtenerReportePorId(Integer id) {
        Reporte reporte = reporteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reporte con ID " + id + " no encontrado."));
        return mapToDTO(reporte); // Usa el mapToDTO que parsea el JSON
    }

    // Mapeador para cuando tienes el objeto de contenido a mano (al crear)
    private ReporteDTO mapToDTO(Reporte reporte, Object contenidoData) {
        if (reporte == null) return null;
        return new ReporteDTO(
                reporte.getTipo(),
                reporte.getFechaGeneracion(),
                contenidoData // Pasamos el objeto directamente
        );
    }

    // Mapeador para cuando lees desde la BD y necesitas convertir el string JSON a objeto
    private ReporteDTO mapToDTO(Reporte reporte) {
        if (reporte == null) return null;
        Object contenidoObject;
        try {
            // Convierte la cadena JSON de la BD de nuevo a un objeto genérico
            contenidoObject = objectMapper.readValue(reporte.getContenido(), Object.class);
        } catch (JsonProcessingException e) {
            logger.warn("No se pudo parsear el contenido JSON del reporte ID {}. Devolviendo como texto plano.", reporte.getId());
            contenidoObject = reporte.getContenido(); // Falla segura, devuelve el texto
        }
        return new ReporteDTO(
                reporte.getTipo(),
                reporte.getFechaGeneracion(),
                contenidoObject
        );
    }
}