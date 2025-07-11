package com.example.edutech.foro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.foro.dto.HiloCreateDTO;
import com.example.edutech.foro.dto.HiloResponseDTO;
import com.example.edutech.foro.model.Foro;
import com.example.edutech.foro.model.HiloDiscusion;
import com.example.edutech.foro.repository.ForoRepository;
import com.example.edutech.foro.repository.HiloDiscusionRepository;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class HiloDiscusionService {

    private final HiloDiscusionRepository hiloRepository;
    private final ForoRepository foroRepository;
    private final UsuarioRepository usuarioRepository;

    public HiloDiscusionService(HiloDiscusionRepository hiloRepository, ForoRepository foroRepository, UsuarioRepository usuarioRepository) {
        this.hiloRepository = hiloRepository;
        this.foroRepository = foroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public HiloResponseDTO crearHilo(HiloCreateDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del hilo es obligatorio.");
        }
        if (dto.getContenidoInicial() == null || dto.getContenidoInicial().isBlank()) {
            throw new IllegalArgumentException("El contenido inicial del hilo es obligatorio.");
        }
        if (dto.getAutorRut() == null || dto.getAutorRut().isBlank()) {
            throw new IllegalArgumentException("El RUT del autor es obligatorio.");
        }
        if (dto.getForoId() == null) {
            throw new IllegalArgumentException("El ID del foro es obligatorio.");
        }

        Foro foro = foroRepository.findById(dto.getForoId())
                .orElseThrow(() -> new IllegalArgumentException("Foro con ID " + dto.getForoId() + " no encontrado."));

        Usuario autor = usuarioRepository.findById(dto.getAutorRut())
                .orElseThrow(() -> new IllegalArgumentException("Usuario autor con RUT '" + dto.getAutorRut() + "' no encontrado."));

        HiloDiscusion hilo = new HiloDiscusion();
        hilo.setTitulo(dto.getTitulo());
        hilo.setContenidoInicial(dto.getContenidoInicial());
        hilo.setAutor(autor);
        hilo.setForo(foro);
        // fechaCreacion y fechaUltimaActividad se establecen en el constructor de HiloDiscusion

        HiloDiscusion hiloGuardado = hiloRepository.save(hilo);
        return mapToHiloResponseDTO(hiloGuardado);
    }

    @Transactional(readOnly = true)
    public HiloResponseDTO obtenerHiloPorId(Integer id) {
        HiloDiscusion hilo = hiloRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hilo de discusión con ID " + id + " no encontrado."));
        return mapToHiloResponseDTO(hilo);
    }

    @Transactional(readOnly = true)
    public List<HiloResponseDTO> listarHilosPorForo(Integer foroId) {
        if (!foroRepository.existsById(foroId)) {
            throw new IllegalArgumentException("Foro con ID " + foroId + " no encontrado.");
        }
        return hiloRepository.findByForoIdOrderByFechaUltimaActividadDesc(foroId).stream()
                .map(this::mapToHiloResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<HiloResponseDTO> buscarHilosPorTituloEnForo(Integer foroId, String titulo) {
        if (!foroRepository.existsById(foroId)) {
            throw new IllegalArgumentException("Foro con ID " + foroId + " no encontrado.");
        }
        if (titulo == null || titulo.isBlank()) {
            return listarHilosPorForo(foroId); // Devuelve todos si la búsqueda está vacía
        }
        return hiloRepository.findByForoIdAndTituloContainingIgnoreCaseOrderByFechaUltimaActividadDesc(foroId, titulo).stream()
                .map(this::mapToHiloResponseDTO)
                .collect(Collectors.toList());
    }

    // Considerar añadir métodos para actualizar (ej. título) y eliminar hilos.

    private HiloResponseDTO mapToHiloResponseDTO(HiloDiscusion hilo) {
        if (hilo == null) return null;
        UsuarioDTO autorDTO = null;
        if (hilo.getAutor() != null) {
            autorDTO = new UsuarioDTO(
                    hilo.getAutor().getRut(),
                    hilo.getAutor().getNombre(),
                    hilo.getAutor().getEmail(),
                    hilo.getAutor().getRol() != null ? hilo.getAutor().getRol().getNombre() : null,
                    hilo.getAutor().getEstadoCuenta()
            );
        }
        return new HiloResponseDTO(
                hilo.getId(),
                hilo.getTitulo(),
                hilo.getContenidoInicial(),
                hilo.getFechaCreacion(),
                hilo.getFechaUltimaActividad(),
                autorDTO,
                hilo.getForo() != null ? hilo.getForo().getId() : null,
                hilo.getMensajes() != null ? hilo.getMensajes().size() : 0
        );
    }
}