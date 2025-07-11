package com.example.edutech.soporte.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.soporte.dto.SoporteCreateDTO;
import com.example.edutech.soporte.dto.SoporteResolverDTO;
import com.example.edutech.soporte.dto.SoporteResponseDTO;
import com.example.edutech.soporte.model.EstadoTicketSoporte;
import com.example.edutech.soporte.model.Soporte;
import com.example.edutech.soporte.repository.SoporteRepository;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class SoporteService {

    private final SoporteRepository soporteRepository;
    private final UsuarioRepository usuarioRepository;

    public SoporteService(SoporteRepository soporteRepository, UsuarioRepository usuarioRepository) {
        this.soporteRepository = soporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public SoporteResponseDTO crearTicket(SoporteCreateDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título del ticket es obligatorio.");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción del ticket es obligatoria.");
        }
        if (dto.getUsuarioReportaRut() == null || dto.getUsuarioReportaRut().isBlank()) {
            throw new IllegalArgumentException("El RUT del usuario que reporta es obligatorio.");
        }

        Usuario usuarioReporta = usuarioRepository.findById(dto.getUsuarioReportaRut())
                .orElseThrow(() -> new IllegalArgumentException("Usuario con RUT '" + dto.getUsuarioReportaRut() + "' no encontrado."));

        Soporte nuevoTicket = new Soporte();
        nuevoTicket.setTitulo(dto.getTitulo());
        nuevoTicket.setDescripcion(dto.getDescripcion());
        nuevoTicket.setUsuarioReporta(usuarioReporta);
        nuevoTicket.setCategoria(dto.getCategoria());
        
        Soporte ticketGuardado = soporteRepository.save(nuevoTicket);
        return mapToResponseDTO(ticketGuardado);
    }

    @Transactional(readOnly = true) // <-- ANOTACIÓN AÑADIDA
    public SoporteResponseDTO obtenerTicketPorId(Integer id) {
        Soporte ticket = soporteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket de soporte con ID " + id + " no encontrado."));
        return mapToResponseDTO(ticket);
    }

    @Transactional(readOnly = true) // <-- ANOTACIÓN AÑADIDA
    public List<SoporteResponseDTO> listarTodosLosTickets() {
        return soporteRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SoporteResponseDTO asignarAgente(Integer ticketId, String rutAgenteSoporte) {
        Soporte ticket = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket de soporte con ID " + ticketId + " no encontrado."));

        Usuario agente = usuarioRepository.findById(rutAgenteSoporte)
                .orElseThrow(() -> new IllegalArgumentException("Agente de soporte con RUT '" + rutAgenteSoporte + "' no encontrado."));

        if (agente.getRol() == null || !"SOPORTE".equalsIgnoreCase(agente.getRol().getNombre())) {
            throw new IllegalArgumentException("El usuario con RUT '" + rutAgenteSoporte + "' no tiene el rol de SOPORTE.");
        }

        ticket.setAgenteAsignado(agente);
        if (ticket.getEstado() == EstadoTicketSoporte.ABIERTO) {
            ticket.setEstado(EstadoTicketSoporte.EN_PROCESO);
        }
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        Soporte ticketActualizado = soporteRepository.save(ticket);
        return mapToResponseDTO(ticketActualizado);
    }

    @Transactional
    public SoporteResponseDTO cambiarEstadoTicket(Integer ticketId, EstadoTicketSoporte nuevoEstado) {
        Soporte ticket = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket de soporte con ID " + ticketId + " no encontrado."));

        ticket.setEstado(nuevoEstado);
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        if (nuevoEstado == EstadoTicketSoporte.RESUELTO || nuevoEstado == EstadoTicketSoporte.CERRADO) {
            if (ticket.getFechaResolucion() == null) {
                ticket.setFechaResolucion(LocalDateTime.now());
            }
        } else {
            ticket.setFechaResolucion(null);
        }

        Soporte ticketActualizado = soporteRepository.save(ticket);
        return mapToResponseDTO(ticketActualizado);
    }

    @Transactional
    public SoporteResponseDTO registrarSolucion(Integer ticketId, SoporteResolverDTO dto) {
        if (dto.getRutAgenteResolutor() == null || dto.getRutAgenteResolutor().isBlank()) {
            throw new IllegalArgumentException("Se requiere el RUT del agente que resuelve el ticket.");
        }
        if (dto.getSolucionAplicada() == null || dto.getSolucionAplicada().isBlank()) {
            throw new IllegalArgumentException("La solución aplicada es obligatoria.");
        }

        Soporte ticket = soporteRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket de soporte con ID " + ticketId + " no encontrado."));
        
        Usuario agenteResolutor = usuarioRepository.findById(dto.getRutAgenteResolutor())
                .orElseThrow(() -> new IllegalArgumentException("Agente con RUT '" + dto.getRutAgenteResolutor() + "' no encontrado."));

        if (agenteResolutor.getRol() == null || !"SOPORTE".equalsIgnoreCase(agenteResolutor.getRol().getNombre())) {
            throw new IllegalArgumentException("El usuario con RUT '" + dto.getRutAgenteResolutor() + "' no tiene el rol de SOPORTE para resolver tickets.");
        }

        ticket.setSolucionAplicada(dto.getSolucionAplicada());
        ticket.setEstado(EstadoTicketSoporte.RESUELTO);
        ticket.setFechaResolucion(LocalDateTime.now());
        ticket.setFechaUltimaActualizacion(LocalDateTime.now());
        if (ticket.getAgenteAsignado() == null) {
            ticket.setAgenteAsignado(agenteResolutor);
        }

        Soporte ticketActualizado = soporteRepository.save(ticket);
        return mapToResponseDTO(ticketActualizado);
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

    private SoporteResponseDTO mapToResponseDTO(Soporte ticket) {
        if (ticket == null) return null;
        return new SoporteResponseDTO(
                ticket.getId(),
                ticket.getTitulo(),
                ticket.getDescripcion(),
                ticket.getEstado(),
                ticket.getPrioridad(),
                ticket.getCategoria(),
                mapUsuarioToDTO(ticket.getUsuarioReporta()),
                mapUsuarioToDTO(ticket.getAgenteAsignado()),
                ticket.getFechaCreacion(),
                ticket.getFechaUltimaActualizacion(),
                ticket.getFechaResolucion(),
                ticket.getSolucionAplicada()
        );
    }
}