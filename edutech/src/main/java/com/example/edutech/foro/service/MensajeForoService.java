package com.example.edutech.foro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.foro.dto.MensajeCreateDTO;
import com.example.edutech.foro.dto.MensajeResponseDTO;
import com.example.edutech.foro.model.HiloDiscusion;
import com.example.edutech.foro.model.MensajeForo;
import com.example.edutech.foro.repository.HiloDiscusionRepository;
import com.example.edutech.foro.repository.MensajeForoRepository;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class MensajeForoService {

    private final MensajeForoRepository mensajeRepository;
    private final HiloDiscusionRepository hiloRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeForoService(MensajeForoRepository mensajeRepository, HiloDiscusionRepository hiloRepository, UsuarioRepository usuarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.hiloRepository = hiloRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public MensajeResponseDTO crearMensaje(MensajeCreateDTO dto) {
        if (dto.getContenido() == null || dto.getContenido().isBlank()) {
            throw new IllegalArgumentException("El contenido del mensaje es obligatorio.");
        }
        if (dto.getAutorRut() == null || dto.getAutorRut().isBlank()) {
            throw new IllegalArgumentException("El RUT del autor es obligatorio.");
        }
        if (dto.getHiloId() == null) {
            throw new IllegalArgumentException("El ID del hilo de discusión es obligatorio.");
        }

        HiloDiscusion hilo = hiloRepository.findById(dto.getHiloId())
                .orElseThrow(() -> new IllegalArgumentException("Hilo de discusión con ID " + dto.getHiloId() + " no encontrado."));

        Usuario autor = usuarioRepository.findById(dto.getAutorRut())
                .orElseThrow(() -> new IllegalArgumentException("Usuario autor con RUT '" + dto.getAutorRut() + "' no encontrado."));

        MensajeForo mensaje = new MensajeForo();
        mensaje.setContenido(dto.getContenido());
        mensaje.setAutor(autor);
        mensaje.setHilo(hilo);
        // fechaCreacion se establece en el constructor de MensajeForo

        MensajeForo mensajeGuardado = mensajeRepository.save(mensaje);

        // Actualizar fecha de última actividad del hilo
        hilo.setFechaUltimaActividad(mensajeGuardado.getFechaCreacion());
        hiloRepository.save(hilo); // Guardar el hilo actualizado

        return mapToMensajeResponseDTO(mensajeGuardado);
    }

    @Transactional(readOnly = true)
    public MensajeResponseDTO obtenerMensajePorId(Integer id) {
        MensajeForo mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje con ID " + id + " no encontrado."));
        return mapToMensajeResponseDTO(mensaje);
    }

    @Transactional(readOnly = true)
    public List<MensajeResponseDTO> listarMensajesPorHilo(Integer hiloId) {
        if (!hiloRepository.existsById(hiloId)) {
            throw new IllegalArgumentException("Hilo de discusión con ID " + hiloId + " no encontrado.");
        }
        return mensajeRepository.findByHiloIdOrderByFechaCreacionAsc(hiloId).stream()
                .map(this::mapToMensajeResponseDTO)
                .collect(Collectors.toList());
    }

    // Considerar añadir métodos para actualizar (si se permite editar mensajes) y eliminar mensajes.

    private MensajeResponseDTO mapToMensajeResponseDTO(MensajeForo mensaje) {
        if (mensaje == null) return null;
        UsuarioDTO autorDTO = null;
        if (mensaje.getAutor() != null) {
            autorDTO = new UsuarioDTO(
                    mensaje.getAutor().getRut(),
                    mensaje.getAutor().getNombre(),
                    mensaje.getAutor().getEmail(),
                    mensaje.getAutor().getRol() != null ? mensaje.getAutor().getRol().getNombre() : null,
                    mensaje.getAutor().getEstadoCuenta()
            );
        }
        return new MensajeResponseDTO(
                mensaje.getId(),
                mensaje.getContenido(),
                mensaje.getFechaCreacion(),
                autorDTO,
                mensaje.getHilo() != null ? mensaje.getHilo().getId() : null
        );
    }
}