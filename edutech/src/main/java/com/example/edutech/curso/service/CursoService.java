package com.example.edutech.curso.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.curso.dto.CursoCreateDTO;
import com.example.edutech.curso.dto.CursoResponseDTO;
import com.example.edutech.curso.dto.CursoUpdateDTO;
import com.example.edutech.curso.model.Curso;
import com.example.edutech.curso.model.EstadoCursoEnum;
import com.example.edutech.curso.repository.CursoRepository;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Service
public class CursoService {

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final AdminRutChecker adminRutChecker;

    public CursoService(CursoRepository cursoRepository,
                        UsuarioRepository usuarioRepository,
                        InscripcionRepository inscripcionRepository,
                        AdminRutChecker adminRutChecker) {
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.adminRutChecker = adminRutChecker;
    }

    // ... (otros métodos como crearCurso, obtenerCursoPorSigla, etc. no cambian) ...

    @Transactional
    public CursoResponseDTO crearCurso(CursoCreateDTO cursoDTO, String adminRutSolicitante) {
        logger.debug("Admin {} intentando crear curso con sigla: {}", adminRutSolicitante, cursoDTO.getSigla());
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de crear curso por RUT (no admin): {}", adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para crear un curso.");
        }

        if (cursoDTO.getSigla() == null || cursoDTO.getSigla().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: La sigla del curso no puede estar vacía.");
        }
        if (cursoDTO.getNombre() == null || cursoDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre del curso no puede estar vacío.");
        }
        if (cursoRepository.existsById(cursoDTO.getSigla())) {
            logger.warn("Intento de crear curso con sigla duplicada: {}", cursoDTO.getSigla());
            throw new IllegalArgumentException("Error: Ya existe un curso con la sigla " + cursoDTO.getSigla());
        }

        Usuario instructorAsignado = null;
        if (cursoDTO.getRutInstructor() != null && !cursoDTO.getRutInstructor().trim().isEmpty()) {
            instructorAsignado = usuarioRepository.findById(cursoDTO.getRutInstructor())
                .orElseThrow(() -> {
                    logger.warn("Instructor con RUT '{}' no encontrado al crear curso '{}'.", cursoDTO.getRutInstructor(), cursoDTO.getSigla());
                    return new IllegalArgumentException("Error: Instructor con RUT '" + cursoDTO.getRutInstructor() + "' no encontrado.");
                });
            if (instructorAsignado.getRol() == null || !"INSTRUCTOR".equalsIgnoreCase(instructorAsignado.getRol().getNombre())) {
                logger.warn("Usuario RUT '{}' no tiene rol INSTRUCTOR al intentar asignarlo al curso '{}'.", cursoDTO.getRutInstructor(), cursoDTO.getSigla());
                throw new IllegalArgumentException("Error: El usuario con RUT '" + cursoDTO.getRutInstructor() + "' no tiene el rol de INSTRUCTOR.");
            }
        }

        Curso nuevoCurso = new Curso();
        nuevoCurso.setSigla(cursoDTO.getSigla());
        nuevoCurso.setNombre(cursoDTO.getNombre());
        nuevoCurso.setDescripcionDetallada(cursoDTO.getDescripcionDetallada());
        nuevoCurso.setCategoria(cursoDTO.getCategoria());
        nuevoCurso.setEstadoCurso(EstadoCursoEnum.BORRADOR);
        if (instructorAsignado != null) {
            nuevoCurso.setInstructor(instructorAsignado);
        }

        Curso cursoGuardado = cursoRepository.save(nuevoCurso);
        logger.info("Curso {} - '{}' creado exitosamente por admin {}.", cursoGuardado.getSigla(), cursoGuardado.getNombre(), adminRutSolicitante);
        return mapToCursoResponseDTO(cursoGuardado);
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerCursoPorSigla(String sigla) {
        logger.debug("Obteniendo curso por sigla: {}", sigla);
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));
        return mapToCursoResponseDTO(curso);
    }

    @Transactional(readOnly = true)
    public Page<CursoResponseDTO> buscarCursosConFiltros(
            String palabraClave, String categoria, String rutInstructor,
            EstadoCursoEnum estadoCurso, BigDecimal precioMin, BigDecimal precioMax,
            Pageable pageable) {
        logger.debug("Buscando cursos con filtros: palabraClave={}, categoria={}, rutInstructor={}, estado={}, precioMin={}, precioMax={}, pageable={}",
            palabraClave, categoria, rutInstructor, estadoCurso, precioMin, precioMax, pageable);

        Specification<Curso> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (palabraClave != null && !palabraClave.isBlank()) {
                String searchTerm = "%" + palabraClave.toLowerCase() + "%";
                Predicate nombreLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), searchTerm);
                Predicate descripcionLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcionDetallada")), searchTerm);
                predicates.add(criteriaBuilder.or(nombreLike, descripcionLike));
            }
            if (categoria != null && !categoria.isBlank()) {
                // <-- LÍNEA MODIFICADA: De 'equal' a 'like' para un filtro más flexible
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("categoria")), "%" + categoria.toLowerCase() + "%"));
            }
            if (rutInstructor != null && !rutInstructor.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.join("instructor", JoinType.LEFT).get("rut"), rutInstructor));
            }
            if (estadoCurso != null) {
                predicates.add(criteriaBuilder.equal(root.get("estadoCurso"), estadoCurso));
            }
            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precioBase"), precioMin));
            }
            if (precioMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precioBase"), precioMax));
            }

            // Evita un `SELECT *` si no hay predicados, aunque findAll(spec, pageable) lo maneja.
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<Curso> paginaCursos = cursoRepository.findAll(spec, pageable);
        return paginaCursos.map(this::mapToCursoResponseDTO);
    }
    
    // ... (resto de los métodos del servicio como actualizar, eliminar, etc. no cambian) ...

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarCursosPorEstado(EstadoCursoEnum estado) {
        logger.debug("Listando cursos por estado: {}", estado);
        if (estado == null) {
            throw new IllegalArgumentException("El estado para filtrar no puede ser nulo.");
        }
        return cursoRepository.findByEstadoCurso(estado).stream()
                .map(this::mapToCursoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Curso obtenerEntidadCursoPorSigla(String sigla) {
        return cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listarTodosLosCursos() {
        logger.debug("Listando todos los cursos.");
        return cursoRepository.findAll().stream()
                .map(this::mapToCursoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CursoResponseDTO actualizarCurso(String sigla, CursoUpdateDTO cursoUpdateDTO, String adminRutSolicitante) {
        logger.debug("Admin {} intentando actualizar curso con sigla: {}", adminRutSolicitante, sigla);
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de actualizar curso {} por RUT (no admin): {}", sigla, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para actualizar cursos.");
        }
        Curso cursoExistente = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + sigla + "' no encontrado."));

        boolean modificado = false;
        if (cursoUpdateDTO.getNombre() != null && !cursoUpdateDTO.getNombre().isBlank() && !cursoUpdateDTO.getNombre().equals(cursoExistente.getNombre())) {
            cursoExistente.setNombre(cursoUpdateDTO.getNombre());
            modificado = true;
        }
        if (cursoUpdateDTO.getDescripcionDetallada() != null && !cursoUpdateDTO.getDescripcionDetallada().equals(cursoExistente.getDescripcionDetallada())) {
            cursoExistente.setDescripcionDetallada(cursoUpdateDTO.getDescripcionDetallada());
            modificado = true;
        }
        if (cursoUpdateDTO.getCategoria() != null && !cursoUpdateDTO.getCategoria().equals(cursoExistente.getCategoria())) {
            cursoExistente.setCategoria(cursoUpdateDTO.getCategoria());
            modificado = true;
        }
        if (cursoUpdateDTO.getRutInstructor() != null) {
            if (cursoUpdateDTO.getRutInstructor().trim().isEmpty()) {
                if (cursoExistente.getInstructor() != null) {
                    logger.info("Desasignando instructor del curso {}", sigla);
                    cursoExistente.setInstructor(null);
                    modificado = true;
                }
            } else {
                if (cursoExistente.getInstructor() == null || !cursoExistente.getInstructor().getRut().equals(cursoUpdateDTO.getRutInstructor())) {
                    logger.info("Cambiando instructor del curso {} al RUT {}", sigla, cursoUpdateDTO.getRutInstructor());
                    asignarInstructorInterno(cursoExistente, cursoUpdateDTO.getRutInstructor());
                    modificado = true;
                }
            }
        }

        if (modificado) {
            Curso cursoGuardado = cursoRepository.save(cursoExistente);
            logger.info("Curso {} actualizado exitosamente por admin {}.", sigla, adminRutSolicitante);
            return mapToCursoResponseDTO(cursoGuardado);
        } else {
            logger.info("No se realizaron cambios en el curso {}.", sigla);
            return mapToCursoResponseDTO(cursoExistente);
        }
    }

    @Transactional
    public CursoResponseDTO actualizarEstadoCurso(String sigla, EstadoCursoEnum nuevoEstado, String adminRutSolicitante) {
        logger.debug("Admin {} intentando actualizar estado del curso {} a {}", adminRutSolicitante, sigla, nuevoEstado);
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de actualizar estado del curso {} por RUT (no admin): {}", sigla, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para cambiar el estado del curso.");
        }
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Curso con sigla '" + sigla + "' no encontrado."));

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        if (nuevoEstado == EstadoCursoEnum.PUBLICADO) {
            if (curso.getInstructor() == null) {
                throw new IllegalStateException("No se puede publicar un curso sin instructor asignado.");
            }
            if (curso.getContenidos() == null || curso.getContenidos().isEmpty()) {
                throw new IllegalStateException("No se puede publicar un curso sin contenido.");
            }
            if (curso.getPrecioBase() == null || curso.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException("No se puede publicar un curso sin un precio base válido (debe ser >= 0).");
            }
        }
        curso.setEstadoCurso(nuevoEstado);
        Curso cursoActualizado = cursoRepository.save(curso);
        logger.info("Estado del curso {} actualizado a {} por admin {}", sigla, nuevoEstado, adminRutSolicitante);
        return mapToCursoResponseDTO(cursoActualizado);
    }

    @Transactional
    public String eliminarCurso(String sigla, String adminRutSolicitante) {
        logger.debug("Admin {} intentando eliminar curso con sigla: {}", adminRutSolicitante, sigla);
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de eliminar curso {} por RUT (no admin): {}", sigla, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para eliminar cursos.");
        }
        Curso curso = cursoRepository.findById(sigla)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + sigla + "' no encontrado."));

        logger.info("Eliminando inscripciones asociadas al curso {}", sigla);
        inscripcionRepository.deleteByCurso(sigla);
        
        cursoRepository.delete(curso);
        logger.info("Curso '{}' y sus inscripciones asociadas eliminados correctamente por admin {}.", sigla, adminRutSolicitante);
        return "Curso '" + sigla + "' y sus datos asociados eliminados correctamente.";
    }

    @Transactional
    public String asignarInstructorACurso(String siglaCurso, String rutInstructor, String adminRutSolicitante) {
        logger.debug("Admin {} intentando asignar instructor {} al curso {}", adminRutSolicitante, rutInstructor, siglaCurso);
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de asignar instructor al curso {} por RUT (no admin): {}", siglaCurso, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para gestionar instructores.");
        }
        Curso curso = cursoRepository.findById(siglaCurso)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + siglaCurso + "' no encontrado."));
        if (rutInstructor == null || rutInstructor.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El RUT del instructor no puede ser vacío para asignación.");
        }
        asignarInstructorInterno(curso, rutInstructor);
        cursoRepository.save(curso);
        String nombreInstructor = (curso.getInstructor() != null) ? curso.getInstructor().getNombre() : "Desconocido";
        logger.info("Instructor {} asignado al curso {} por admin {}", nombreInstructor, siglaCurso, adminRutSolicitante);
        return "Instructor " + nombreInstructor + " asignado correctamente al curso " + curso.getNombre() + ".";
    }

    @Transactional
    public String desasignarInstructorDeCurso(String siglaCurso, String adminRutSolicitante) {
        logger.debug("Admin {} intentando desasignar instructor del curso {}", adminRutSolicitante, siglaCurso);
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de desasignar instructor del curso {} por RUT (no admin): {}", siglaCurso, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para gestionar instructores.");
        }
        Curso curso = cursoRepository.findById(siglaCurso)
                .orElseThrow(() -> new IllegalArgumentException("Error: Curso con sigla '" + siglaCurso + "' no encontrado."));
        if (curso.getInstructor() == null) {
            return "El curso '" + curso.getNombre() + "' ya no tiene un instructor asignado.";
        }
        String nombreInstructorAnterior = curso.getInstructor().getNombre();
        curso.setInstructor(null);
        cursoRepository.save(curso);
        logger.info("Instructor {} desasignado del curso {} por admin {}", nombreInstructorAnterior, siglaCurso, adminRutSolicitante);
        return "Instructor " + nombreInstructorAnterior + " desasignado correctamente del curso " + curso.getNombre() + ".";
    }

    private void asignarInstructorInterno(Curso curso, String rutInstructor) {
        Usuario instructor = usuarioRepository.findById(rutInstructor)
            .orElseThrow(() -> new IllegalArgumentException("Error: Instructor con RUT '" + rutInstructor + "' no encontrado."));
        if (instructor.getRol() == null || !"INSTRUCTOR".equalsIgnoreCase(instructor.getRol().getNombre())) {
            throw new IllegalArgumentException("Error: El usuario con RUT '" + rutInstructor + "' no tiene el rol de INSTRUCTOR.");
        }
        curso.setInstructor(instructor);
    }

    private CursoResponseDTO mapToCursoResponseDTO(Curso curso) {
        if (curso == null) return null;
        UsuarioDTO instructorDTO = null;
        if (curso.getInstructor() != null) {
            Usuario instructor = curso.getInstructor();
            instructorDTO = new UsuarioDTO(
                instructor.getRut(),
                instructor.getNombre(),
                instructor.getEmail(),
                instructor.getRol() != null ? instructor.getRol().getNombre() : "Rol no especificado",
                instructor.getEstadoCuenta()
            );
        }
        return new CursoResponseDTO(
                curso.getSigla(),
                curso.getNombre(),
                curso.getDescripcionDetallada() != null ? curso.getDescripcionDetallada() : "Descripción no disponible",
                curso.getEstadoCurso(),
                curso.getCategoria() != null ? curso.getCategoria() : "Categoría no especificada",
                instructorDTO
        );
    }

    @Transactional(readOnly = true)
    public List<CursoResponseDTO> generarReportes() {
        logger.info("Generando 'reporte' simple de listado de todos los cursos desde CursoService.");
        return listarTodosLosCursos();
    }
}