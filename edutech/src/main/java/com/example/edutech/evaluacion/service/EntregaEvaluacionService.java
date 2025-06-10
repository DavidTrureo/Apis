package com.example.edutech.evaluacion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // IMPORTAR

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.evaluacion.dto.EntregaEvaluacionCorreccionDTO;
import com.example.edutech.evaluacion.dto.EntregaEvaluacionCreateDTO;
import com.example.edutech.evaluacion.dto.EntregaEvaluacionResponseDTO; // IMPORTAR
import com.example.edutech.evaluacion.model.EntregaEvaluacion;
import com.example.edutech.evaluacion.model.Evaluacion;
import com.example.edutech.evaluacion.repository.EntregaEvaluacionRepository;
import com.example.edutech.evaluacion.repository.EvaluacionRepository;
import com.example.edutech.usuario.dto.UsuarioDTO; // IMPORTAR
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class EntregaEvaluacionService {

    private final EntregaEvaluacionRepository entregaEvaluacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EvaluacionRepository evaluacionRepository;

    public EntregaEvaluacionService(EntregaEvaluacionRepository entregaEvaluacionRepository,
                                    UsuarioRepository usuarioRepository,
                                    EvaluacionRepository evaluacionRepository) {
        this.entregaEvaluacionRepository = entregaEvaluacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    private UsuarioDTO mapUsuarioToDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDTO(
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol() != null ? usuario.getRol().getNombre() : null,
                usuario.getEstadoCuenta()
        );
    }

    private EntregaEvaluacionResponseDTO mapToResponseDTO(EntregaEvaluacion entrega) {
        if (entrega == null) return null;
        return new EntregaEvaluacionResponseDTO(
                entrega.getId(),
                mapUsuarioToDTO(entrega.getEstudiante()),
                entrega.getEvaluacion() != null ? entrega.getEvaluacion().getId() : null,
                entrega.getRespuestasJson(),
                entrega.getEstado(),
                entrega.getFechaEntrega(),
                entrega.getCalificacion(),
                entrega.getComentariosInstructor(),
                mapUsuarioToDTO(entrega.getInstructorCorrector()),
                entrega.getFechaCorreccion()
        );
    }


    @Transactional(readOnly = true)
    public EntregaEvaluacionResponseDTO obtenerEntregaDTOPorId(Integer id) { // CAMBIO
        EntregaEvaluacion entrega = entregaEvaluacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega de evaluación con ID " + id + " no encontrada."));
        return mapToResponseDTO(entrega);
    }

    @Transactional(readOnly = true)
    public List<EntregaEvaluacionResponseDTO> listarEntregasDTO() { // CAMBIO
        return entregaEvaluacionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregaEvaluacionResponseDTO> listarEntregasDTOPorEvaluacionId(Integer evaluacionId) { // CAMBIO
        if (!evaluacionRepository.existsById(evaluacionId)) {
            throw new IllegalArgumentException("Evaluación con ID " + evaluacionId + " no encontrada.");
        }
        return entregaEvaluacionRepository.findByEvaluacion_Id(evaluacionId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EntregaEvaluacionResponseDTO registrarEntregaDTO(EntregaEvaluacionCreateDTO dto) { // CAMBIO
        // ... (validaciones existentes)
        Usuario estudiante = usuarioRepository.findById(dto.getEstudianteRut())
                .orElseThrow(() -> new IllegalArgumentException("Estudiante con RUT '" + dto.getEstudianteRut() + "' no encontrado."));

        Evaluacion evaluacion = evaluacionRepository.findById(dto.getEvaluacionId())
                .orElseThrow(() -> new IllegalArgumentException("Evaluación con ID " + dto.getEvaluacionId() + " no encontrada."));

        Optional<EntregaEvaluacion> entregaPrevia = entregaEvaluacionRepository.findByEstudianteAndEvaluacion(estudiante, evaluacion);
        if (entregaPrevia.isPresent()) {
            throw new IllegalStateException("El estudiante ya ha realizado una entrega para esta evaluación.");
        }
        // ...

        EntregaEvaluacion nuevaEntrega = new EntregaEvaluacion();
        nuevaEntrega.setEstudiante(estudiante);
        nuevaEntrega.setEvaluacion(evaluacion);
        nuevaEntrega.setRespuestasJson(dto.getRespuestasJson());
        nuevaEntrega.setEstado("Entregada");
        nuevaEntrega.setFechaEntrega(LocalDateTime.now());

        EntregaEvaluacion guardada = entregaEvaluacionRepository.save(nuevaEntrega);
        return mapToResponseDTO(guardada);
    }

    @Transactional
    public EntregaEvaluacionResponseDTO corregirEntregaDTO(Integer entregaId, EntregaEvaluacionCorreccionDTO correccionDTO) { // CAMBIO
        // ... (validaciones existentes)
        EntregaEvaluacion entrega = entregaEvaluacionRepository.findById(entregaId)
                .orElseThrow(() -> new IllegalArgumentException("Entrega de evaluación con ID " + entregaId + " no encontrada."));


        Usuario instructor = usuarioRepository.findById(correccionDTO.getRutInstructorCorrector())
                .orElseThrow(() -> new IllegalArgumentException("Instructor con RUT '" + correccionDTO.getRutInstructorCorrector() + "' no encontrado."));
        // ... (validación rol instructor)
        if (instructor.getRol() == null || !"INSTRUCTOR".equalsIgnoreCase(instructor.getRol().getNombre())) {
            throw new IllegalArgumentException("El usuario con RUT '" + correccionDTO.getRutInstructorCorrector() + "' no tiene el rol de INSTRUCTOR.");
        }


        entrega.setCalificacion(correccionDTO.getCalificacion());
        entrega.setComentariosInstructor(correccionDTO.getComentariosInstructor());
        entrega.setInstructorCorrector(instructor);
        entrega.setFechaCorreccion(LocalDateTime.now());
        entrega.setEstado("Corregida");

        EntregaEvaluacion guardada = entregaEvaluacionRepository.save(entrega);
        return mapToResponseDTO(guardada);
    }
}