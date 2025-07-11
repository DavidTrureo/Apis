package com.example.edutech.curso.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.dto.ResenaCalificacionCreateDTO;
import com.example.edutech.curso.dto.ResenaCalificacionResponseDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.ResenaCalificacion;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.curso.repository.ResenaCalificacionRepository;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class ResenaCalificacionService {

    private final ResenaCalificacionRepository resenaCalificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;

    public ResenaCalificacionService(ResenaCalificacionRepository resenaCalificacionRepository,
                                    UsuarioRepository usuarioRepository,
                                    CursoRepository cursoRepository,
                                    InscripcionRepository inscripcionRepository) {
        this.resenaCalificacionRepository = resenaCalificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    @Transactional
    public ResenaCalificacionResponseDTO crearResena(ResenaCalificacionCreateDTO dto) {
        if (dto.getUsuarioRut() == null || dto.getUsuarioRut().isBlank()) {
            throw new IllegalArgumentException("El RUT del usuario es obligatorio para crear una reseña.");
        }
        if (dto.getCursoSigla() == null || dto.getCursoSigla().isBlank()) {
            throw new IllegalArgumentException("La sigla del curso es obligatoria.");
        }
        if (dto.getPuntuacion() == null) {
            throw new IllegalArgumentException("La puntuación es obligatoria.");
        }
        if (dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioRut())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con RUT: " + dto.getUsuarioRut()));

        Curso curso = cursoRepository.findById(dto.getCursoSigla())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con sigla: " + dto.getCursoSigla()));

        if (!inscripcionRepository.existsByUsuarioAndCurso(usuario, curso)) {
            throw new IllegalStateException("El usuario no está inscrito en este curso y no puede dejar una reseña.");
        }

        Optional<ResenaCalificacion> existente = resenaCalificacionRepository.findByUsuarioAndCurso(usuario, curso);
        if (existente.isPresent()) {
            throw new IllegalArgumentException("El usuario ya ha realizado una reseña para este curso.");
        }

        ResenaCalificacion nuevaResena = new ResenaCalificacion();
        nuevaResena.setUsuario(usuario);
        nuevaResena.setCurso(curso);
        nuevaResena.setPuntuacion(dto.getPuntuacion());
        nuevaResena.setComentario(dto.getComentario());
        nuevaResena.setFechaResena(LocalDateTime.now());
        nuevaResena.setEsAprobada(true); 

        ResenaCalificacion guardada = resenaCalificacionRepository.save(nuevaResena);
        return mapToResponseDTO(guardada);
    }

    public ResenaCalificacionResponseDTO obtenerResenaPorId(Integer id) {
        return resenaCalificacionRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    public List<ResenaCalificacionResponseDTO> listarTodasLasResenas() {
        return resenaCalificacionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResenaCalificacionResponseDTO> listarResenasPorCurso(String cursoSigla, boolean soloAprobadas) {
        Curso curso = cursoRepository.findById(cursoSigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado con sigla: " + cursoSigla));

        List<ResenaCalificacion> resenas;
        if (soloAprobadas) {
            resenas = resenaCalificacionRepository.findByCursoAndEsAprobadaTrue(curso);
        } else {
            resenas = resenaCalificacionRepository.findByCurso(curso);
        }
        return resenas.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ResenaCalificacionResponseDTO> listarResenasPorUsuario(String usuarioRut) {
        Usuario usuario = usuarioRepository.findById(usuarioRut)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con RUT: " + usuarioRut));
        return resenaCalificacionRepository.findByUsuario(usuario).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResenaCalificacionResponseDTO actualizarEstadoAprobacion(Integer id, boolean aprobada) {
        ResenaCalificacion resena = resenaCalificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada con ID: " + id));
        resena.setEsAprobada(aprobada);
        ResenaCalificacion guardada = resenaCalificacionRepository.save(resena);
        return mapToResponseDTO(guardada);
    }

    @Transactional
    public void eliminarResena(Integer id) {
        if (!resenaCalificacionRepository.existsById(id)) {
            throw new IllegalArgumentException("Reseña no encontrada con ID: " + id);
        }
        resenaCalificacionRepository.deleteById(id);
    }

    private ResenaCalificacionResponseDTO mapToResponseDTO(ResenaCalificacion resena) {
        if (resena == null) return null;
        String usuarioNombre = (resena.getUsuario() != null && resena.getUsuario().getNombre() != null)
                ? resena.getUsuario().getNombre() : "Usuario Desconocido";
        String cursoNombre = (resena.getCurso() != null && resena.getCurso().getNombre() != null)
                ? resena.getCurso().getNombre() : "Curso Desconocido";
        String cursoSigla = (resena.getCurso() != null && resena.getCurso().getSigla() != null)
                ? resena.getCurso().getSigla() : "N/A";

        return new ResenaCalificacionResponseDTO(
                resena.getId(),
                usuarioNombre,
                cursoNombre,
                cursoSigla,
                resena.getPuntuacion(),
                resena.getComentario(),
                resena.getFechaResena(),
                resena.isEsAprobada()
        );
    }
}