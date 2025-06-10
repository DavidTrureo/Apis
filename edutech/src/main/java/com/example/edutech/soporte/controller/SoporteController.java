package com.example.edutech.soporte.controller;

import java.util.List; // Importar todos los DTOs de soporte

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.soporte.dto.SoporteActualizarEstadoDTO;
import com.example.edutech.soporte.dto.SoporteAsignarAgenteDTO;
import com.example.edutech.soporte.dto.SoporteCreateDTO;
import com.example.edutech.soporte.dto.SoporteResolverDTO;
import com.example.edutech.soporte.dto.SoporteResponseDTO;
import com.example.edutech.soporte.service.SoporteService;

@RestController
@RequestMapping("/api/soporte/tickets") // Ruta base actualizada
public class SoporteController {

    private static final Logger logger = LoggerFactory.getLogger(SoporteController.class);
    private final SoporteService soporteService;

    //@Autowired
    public SoporteController(SoporteService soporteService) {
        this.soporteService = soporteService;
    }

    @PostMapping
    public ResponseEntity<?> crearTicket(@RequestBody SoporteCreateDTO createDTO) {
        try {
            SoporteResponseDTO nuevoTicket = soporteService.crearTicket(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTicket);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear ticket de soporte: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear ticket de soporte:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el ticket de soporte.");
        }
    }

    @GetMapping
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_SOPORTE')")
    public ResponseEntity<List<SoporteResponseDTO>> listarTodosLosTickets() {
        List<SoporteResponseDTO> tickets = soporteService.listarTodosLosTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_SOPORTE') or @soporteSecurityService.esPropietarioDelTicket(#id, authentication)")
    public ResponseEntity<?> obtenerTicketPorId(@PathVariable Integer id) {
        try {
            SoporteResponseDTO ticket = soporteService.obtenerTicketPorId(id);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            logger.warn("Ticket de soporte con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/asignar-agente")
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_SOPORTE')") // O un rol de supervisor de soporte
    public ResponseEntity<?> asignarAgente(@PathVariable Integer id, @RequestBody SoporteAsignarAgenteDTO dto) {
        try {
            SoporteResponseDTO ticketActualizado = soporteService.asignarAgente(id, dto.getRutAgenteAsignado());
            return ResponseEntity.ok(ticketActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al asignar agente al ticket ID {}: {}", id, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_SOPORTE')")
    public ResponseEntity<?> cambiarEstadoTicket(@PathVariable Integer id, @RequestBody SoporteActualizarEstadoDTO dto) {
        try {
            SoporteResponseDTO ticketActualizado = soporteService.cambiarEstadoTicket(id, dto.getNuevoEstado());
            return ResponseEntity.ok(ticketActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al cambiar estado del ticket ID {}: {}", id, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/resolver")
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_SOPORTE')")
    public ResponseEntity<?> resolverTicket(@PathVariable Integer id, @RequestBody SoporteResolverDTO dto) {
        try {
            SoporteResponseDTO ticketResuelto = soporteService.registrarSolucion(id, dto);
            return ResponseEntity.ok(ticketResuelto);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al resolver ticket ID {}: {}", id, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    // Considera un endpoint DELETE si se permite eliminar tickets (con permisos adecuados)
}