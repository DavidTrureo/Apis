package com.example.edutech.evaluacion.service;
//REALIZADO POR: David Trureo
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.evaluacion.dto.EvaluacionCreateDTO;
import com.example.edutech.evaluacion.dto.EvaluacionDTO;
import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.evaluacion.repository.EvaluacionRepository;

@Service
public class EvaluacionService {
    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;


    public EvaluacionService(EvaluacionRepository evaluacionRepository, CursoRepository cursoRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public EvaluacionDTO crearEvaluacion(EvaluacionCreateDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()){
            throw new IllegalArgumentException("El nombre de la evaluación es obligatorio.");
        }
        if (dto.getTipo() == null || dto.getTipo().isBlank()){
            throw new IllegalArgumentException("El tipo de la evaluación es obligatorio.");
        }
        if (dto.getCursoSigla() == null || dto.getCursoSigla().isBlank()){
            throw new IllegalArgumentException("La sigla del curso para la evaluación es obligatoria.");
        }

        Curso curso = cursoRepository.findById(dto.getCursoSigla())
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + dto.getCursoSigla() + "' no encontrado."));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setNombre(dto.getNombre());
        evaluacion.setTipo(dto.getTipo());
        evaluacion.setCurso(curso);

        Evaluacion evaluacionGuardada = evaluacionRepository.save(evaluacion);
        return mapToDTO(evaluacionGuardada);
    }

    public EvaluacionDTO obtenerEvaluacionPorId(Integer id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evaluación con ID " + id + " no encontrada."));
        return mapToDTO(evaluacion);
    }

    public List<EvaluacionDTO> listarEvaluacionesPorCurso(String cursoSigla) {
        Curso curso = cursoRepository.findById(cursoSigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + cursoSigla + "' no encontrado."));
        return evaluacionRepository.findByCurso(curso).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EvaluacionDTO> listarTodasLasEvaluaciones() {
        return evaluacionRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private EvaluacionDTO mapToDTO(Evaluacion evaluacion) {
        if (evaluacion == null) return null;
        return new EvaluacionDTO(
                evaluacion.getId(),
                evaluacion.getNombre(),
                evaluacion.getTipo(),
                evaluacion.getCurso() != null ? evaluacion.getCurso().getSigla() : null
        );
    }
}