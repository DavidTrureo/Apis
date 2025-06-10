package com.example.edutech.evaluacion.service;

import java.util.List;
import java.util.stream.Collectors; // IMPORTAR

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.evaluacion.dto.PreguntaCreateDTO;
import com.example.edutech.evaluacion.dto.PreguntaResponseDTO; // IMPORTAR
import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.evaluacion.model.Pregunta;
import com.example.edutech.evaluacion.repository.EvaluacionRepository;
import com.example.edutech.evaluacion.repository.PreguntaRepository;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public PreguntaService(PreguntaRepository preguntaRepository, EvaluacionRepository evaluacionRepository) {
        this.preguntaRepository = preguntaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    private PreguntaResponseDTO mapToResponseDTO(Pregunta pregunta) {
        if (pregunta == null) return null;
        return new PreguntaResponseDTO(
                pregunta.getId(),
                pregunta.getEnunciado(),
                pregunta.getEvaluacion() != null ? pregunta.getEvaluacion().getId() : null
        );
    }

    @Transactional
    public PreguntaResponseDTO crearPreguntaDTO(PreguntaCreateDTO dto) { // CAMBIO
        // ... (validaciones existentes)
        Evaluacion evaluacion = evaluacionRepository.findById(dto.getEvaluacionId())
                .orElseThrow(() -> new IllegalArgumentException("Evaluación con ID " + dto.getEvaluacionId() + " no encontrada."));

        Pregunta pregunta = new Pregunta();
        pregunta.setEnunciado(dto.getEnunciado());
        pregunta.setEvaluacion(evaluacion);
        Pregunta guardada = preguntaRepository.save(pregunta);
        return mapToResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<PreguntaResponseDTO> listarPreguntasDTOPorEvaluacionId(Integer evaluacionId) { // CAMBIO
        if (!evaluacionRepository.existsById(evaluacionId)) {
            throw new IllegalArgumentException("Evaluación con ID " + evaluacionId + " no encontrada.");
        }
        return preguntaRepository.findByEvaluacionId(evaluacionId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PreguntaResponseDTO> listarTodasLasPreguntasDTO(){ // CAMBIO
        return preguntaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PreguntaResponseDTO obtenerPreguntaDTOPorId(Integer id) { // CAMBIO
        Pregunta pregunta = preguntaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pregunta con ID " + id + " no encontrada."));
        return mapToResponseDTO(pregunta);
    }
    
    // Mantener el método que devuelve la entidad si es necesario internamente
    public Pregunta obtenerEntidadPreguntaPorId(Integer id) {
        return preguntaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Pregunta con ID " + id + " no encontrada."));
    }
}