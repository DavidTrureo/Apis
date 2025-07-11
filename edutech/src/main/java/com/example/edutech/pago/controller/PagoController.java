package com.example.edutech.pago.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.pago.dto.PagoResponseDTO;
import com.example.edutech.pago.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or @pagoSecurityService.esPropietarioDelPago(#id, authentication)")
    public ResponseEntity<?> obtenerPagoPorId(@PathVariable Integer id) {
        try {
            PagoResponseDTO pago = pagoService.obtenerPagoPorId(id);
            return ResponseEntity.ok(pago);
        } catch (IllegalArgumentException e) {
            logger.warn("GET /api/pagos/{}: Pago no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioRut}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or #usuarioRut == authentication.name")
    public ResponseEntity<?> listarPagosPorUsuario(@PathVariable String usuarioRut) {
        try {
            List<PagoResponseDTO> pagos = pagoService.listarPagosPorUsuario(usuarioRut);
            return ResponseEntity.ok(pagos);
        } catch (IllegalArgumentException e) {
            logger.warn("GET /api/pagos/usuario/{}: Usuario no encontrado o sin pagos. {}", usuarioRut, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<List<PagoResponseDTO>> listarTodosLosPagos() {
        List<PagoResponseDTO> pagos = pagoService.listarTodosLosPagos();
        return ResponseEntity.ok(pagos);
    }
}