package com.example.edutech.evaluacion.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.evaluacion.dto.RespuestaCreateDTO;
import com.example.edutech.evaluacion.dto.RespuestaResponseDTO;
import com.example.edutech.evaluacion.model.Pregunta;
import com.example.edutech.evaluacion.model.Respuesta;
import com.example.edutech.evaluacion.repository.PreguntaRepository;
import com.example.edutech.evaluacion.repository.RespuestaRepository;

@Service
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final PreguntaRepository preguntaRepository;

    public RespuestaService(RespuestaRepository respuestaRepository, PreguntaRepository preguntaRepository) {
        this.respuestaRepository = respuestaRepository;
        this.preguntaRepository = preguntaRepository;
    }

    private RespuestaResponseDTO mapToResponseDTO(Respuesta respuesta) {
        if (respuesta == null) return null;
        return new RespuestaResponseDTO(
                respuesta.getId(),
                respuesta.getContenido(),
                respuesta.isEsCorrecta(),
                respuesta.getPregunta() != null ? respuesta.getPregunta().getId() : null
        );
    }

    @Transactional
    public RespuestaResponseDTO guardarRespuestaDTO(RespuestaCreateDTO dto) {
        if (dto.getPreguntaId() == null) {
            throw new IllegalArgumentException("Se requiere el ID de la pregunta para guardar la respuesta.");
        }

        Pregunta preguntaExistente = preguntaRepository.findById(dto.getPreguntaId())
                .orElseThrow(() -> new IllegalArgumentException("Pregunta con ID " + dto.getPreguntaId() + " no encontrada."));

        Respuesta nuevaRespuesta = new Respuesta();
        nuevaRespuesta.setContenido(dto.getContenido());
        nuevaRespuesta.setEsCorrecta(dto.getEsCorrecta());
        nuevaRespuesta.setPregunta(preguntaExistente);

        Respuesta guardada = respuestaRepository.save(nuevaRespuesta);
        return mapToResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<RespuestaResponseDTO> listarRespuestasDTOPorPreguntaId(Integer preguntaId){
        if (!preguntaRepository.existsById(preguntaId)) {
            throw new IllegalArgumentException("Pregunta con ID " + preguntaId + " no encontrada.");
        }
        return respuestaRepository.findByPreguntaId(preguntaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RespuestaResponseDTO> listarTodasLasRespuestasDTO(){
        return respuestaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RespuestaResponseDTO obtenerRespuestaDTOPorId(Integer id) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Respuesta con ID " + id + " no encontrada."));
        return mapToResponseDTO(respuesta);
    }
}