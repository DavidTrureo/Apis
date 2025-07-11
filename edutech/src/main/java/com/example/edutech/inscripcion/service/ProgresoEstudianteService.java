package com.example.edutech.inscripcion.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.curso.model.ContenidoCurso;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.repository.ContenidoCursoRepository;
import com.example.edutech.inscripcion.dto.MarcarContenidoCompletadoDTO;
import com.example.edutech.inscripcion.dto.ProgresoCursoDTO;
import com.example.edutech.inscripcion.dto.ProgresoEstudianteDTO;
import com.example.edutech.inscripcion.model.Inscripcion; 
import com.example.edutech.inscripcion.model.ProgresoEstudiante;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.inscripcion.repository.ProgresoEstudianteRepository;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class ProgresoEstudianteService {

    private final ProgresoEstudianteRepository progresoEstudianteRepository;
    private final InscripcionRepository inscripcionRepository;
    private final ContenidoCursoRepository contenidoCursoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProgresoEstudianteService(ProgresoEstudianteRepository progresoEstudianteRepository,
                                    InscripcionRepository inscripcionRepository,
                                    ContenidoCursoRepository contenidoCursoRepository,
                                    UsuarioRepository usuarioRepository) {
        this.progresoEstudianteRepository = progresoEstudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.contenidoCursoRepository = contenidoCursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ProgresoEstudianteDTO marcarContenido(MarcarContenidoCompletadoDTO dto) {
        if (dto.getInscripcionId() == null) {
            throw new IllegalArgumentException("El ID de la inscripción es obligatorio.");
        }
        if (dto.getContenidoCursoId() == null) {
            throw new IllegalArgumentException("El ID del contenido del curso es obligatorio.");
        }
        if (dto.getCompletado() == null) {
            throw new IllegalArgumentException("El estado 'completado' es obligatorio.");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(dto.getInscripcionId())
                .orElseThrow(() -> new IllegalArgumentException("Inscripción con ID " + dto.getInscripcionId() + " no encontrada."));

        ContenidoCurso contenidoCurso = contenidoCursoRepository.findById(dto.getContenidoCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Contenido de curso con ID " + dto.getContenidoCursoId() + " no encontrado."));


        if (!contenidoCurso.getCurso().getSigla().equals(inscripcion.getCurso().getSigla())) {
            throw new IllegalArgumentException("El contenido no pertenece al curso de la inscripción.");
        }

        ProgresoEstudiante progreso = progresoEstudianteRepository.findByInscripcionAndContenidoCurso(inscripcion, contenidoCurso)
                .orElseGet(() -> new ProgresoEstudiante(inscripcion, contenidoCurso));

        progreso.setCompletado(dto.getCompletado());
        if (dto.getCompletado()) {
            progreso.setFechaCompletado(LocalDateTime.now());
        } else {
            progreso.setFechaCompletado(null); 
        }
        progreso.setFechaUltimaActualizacion(LocalDateTime.now());

        ProgresoEstudiante guardado = progresoEstudianteRepository.save(progreso);
        return mapToDTO(guardado);
    }

    @Transactional(readOnly = true) // <-- ANOTACIÓN AÑADIDA
    public List<ProgresoEstudianteDTO> obtenerProgresoPorInscripcion(Integer inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción con ID " + inscripcionId + " no encontrada."));
        // La transacción asegura que las relaciones LAZY se puedan cargar aquí
        return progresoEstudianteRepository.findByInscripcion(inscripcion).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // <-- ANOTACIÓN AÑADIDA
    public ProgresoCursoDTO obtenerProgresoGeneralCurso(String usuarioRut, String cursoSigla) {
        Usuario usuario = usuarioRepository.findById(usuarioRut)
            .orElseThrow(() -> new IllegalArgumentException("Usuario con RUT " + usuarioRut + " no encontrado."));

        Inscripcion inscripcion = inscripcionRepository.findByUsuarioAndCurso_Sigla(usuario, cursoSigla) 
            .orElseThrow(() -> new IllegalArgumentException("Usuario " + usuarioRut + " no está inscrito en el curso " + cursoSigla + "."));

        List<ContenidoCurso> contenidosDelCurso = inscripcion.getCurso().getContenidos();
        
        if (contenidosDelCurso == null || contenidosDelCurso.isEmpty()) {
            return new ProgresoCursoDTO(inscripcion.getId(), usuarioRut, usuario.getNombre(), cursoSigla, inscripcion.getCurso().getNombre(), 0, 0, 0.0, "El curso no tiene contenido definido.");
        }

        long totalContenidos = contenidosDelCurso.size();
        long contenidosCompletados = progresoEstudianteRepository.countByInscripcionAndCompletadoTrue(inscripcion);

        double porcentajeCompletado = (totalContenidos > 0) ? ((double) contenidosCompletados / totalContenidos) * 100 : 0.0;

        return new ProgresoCursoDTO(
                inscripcion.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                cursoSigla,
                inscripcion.getCurso().getNombre(),
                (int) totalContenidos,
                (int) contenidosCompletados,
                Math.round(porcentajeCompletado * 100.0) / 100.0, 
                "Progreso calculado."
        );
    }


    private ProgresoEstudianteDTO mapToDTO(ProgresoEstudiante progreso) {
        if (progreso == null) return null;
        Inscripcion inscripcion = progreso.getInscripcion();
        ContenidoCurso contenido = progreso.getContenidoCurso();
        Usuario estudiante = inscripcion != null ? inscripcion.getUsuario() : null;
        Curso curso = inscripcion != null ? inscripcion.getCurso() : null;

        return new ProgresoEstudianteDTO(
                progreso.getId(),
                inscripcion != null ? inscripcion.getId() : null,
                estudiante != null ? estudiante.getRut() : null,
                estudiante != null ? estudiante.getNombre() : null,
                curso != null ? curso.getSigla() : null,
                curso != null ? curso.getNombre() : null,
                contenido != null ? contenido.getId() : null,
                contenido != null ? contenido.getTitulo() : null,
                progreso.isCompletado(),
                progreso.getFechaCompletado(),
                progreso.getFechaUltimaActualizacion()
        );
    }
}