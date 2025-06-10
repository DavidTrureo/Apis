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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.foro.dto.HiloCreateDTO;
import com.example.edutech.foro.dto.HiloResponseDTO;
import com.example.edutech.foro.service.HiloDiscusionService;

@RestController
@RequestMapping("/api/hilos")
public class HiloDiscusionController {

    private static final Logger logger = LoggerFactory.getLogger(HiloDiscusionController.class);
    private final HiloDiscusionService hiloService;

    public HiloDiscusionController(HiloDiscusionService hiloService) {
        this.hiloService = hiloService;
    }

    @PostMapping
    // @PreAuthorize("isAuthenticated()") // Cualquier usuario autenticado puede crear un hilo
    public ResponseEntity<?> crearHilo(@RequestBody HiloCreateDTO createDTO) {
        try {
            HiloResponseDTO nuevoHilo = hiloService.crearHilo(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHilo);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear hilo de discusión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear hilo de discusión:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el hilo.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerHiloPorId(@PathVariable Integer id) {
        try {
            HiloResponseDTO hilo = hiloService.obtenerHiloPorId(id);
            return ResponseEntity.ok(hilo);
        } catch (IllegalArgumentException e) {
            logger.warn("Hilo de discusión con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/foro/{foroId}")
    public ResponseEntity<?> listarHilosPorForo(
            @PathVariable Integer foroId,
            @RequestParam(required = false) String buscarTitulo) {
        try {
            List<HiloResponseDTO> hilos;
            if (buscarTitulo != null && !buscarTitulo.isBlank()) {
                hilos = hiloService.buscarHilosPorTituloEnForo(foroId, buscarTitulo);
            } else {
                hilos = hiloService.listarHilosPorForo(foroId);
            }
            return ResponseEntity.ok(hilos);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al listar hilos para foro ID {}: {}", foroId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}