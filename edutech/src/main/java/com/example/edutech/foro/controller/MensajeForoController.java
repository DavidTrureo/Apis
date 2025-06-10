package com.example.edutech.foro.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.foro.dto.MensajeCreateDTO;
import com.example.edutech.foro.dto.MensajeResponseDTO;
import com.example.edutech.foro.service.MensajeForoService;

@RestController
@RequestMapping("/api/mensajes-foro")
public class MensajeForoController {

    private static final Logger logger = LoggerFactory.getLogger(MensajeForoController.class);
    private final MensajeForoService mensajeService;

    public MensajeForoController(MensajeForoService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    // @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado puede publicar un mensaje
    public ResponseEntity<?> crearMensaje(@RequestBody MensajeCreateDTO createDTO) {
        try {
            MensajeResponseDTO nuevoMensaje = mensajeService.crearMensaje(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear mensaje en foro: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear mensaje en foro:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el mensaje.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMensajePorId(@PathVariable Integer id) {
        try {
            MensajeResponseDTO mensaje = mensajeService.obtenerMensajePorId(id);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Mensaje de foro con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/hilo/{hiloId}")
    public ResponseEntity<?> listarMensajesPorHilo(@PathVariable Integer hiloId) {
        try {
            List<MensajeResponseDTO> mensajes = mensajeService.listarMensajesPorHilo(hiloId);
            return ResponseEntity.ok(mensajes);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al listar mensajes para hilo ID {}: {}", hiloId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}