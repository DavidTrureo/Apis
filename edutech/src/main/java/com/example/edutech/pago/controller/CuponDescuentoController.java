package com.example.edutech.pago.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.pago.dto.CuponDescuentoCreateDTO;
import com.example.edutech.pago.dto.CuponDescuentoDTO;
import com.example.edutech.pago.service.CuponDescuentoService;

@RestController
@RequestMapping("/api/cupones") 
public class CuponDescuentoController {

    private static final Logger logger = LoggerFactory.getLogger(CuponDescuentoController.class);
    private final CuponDescuentoService cuponDescuentoService;

    public CuponDescuentoController(CuponDescuentoService cuponDescuentoService) {
        this.cuponDescuentoService = cuponDescuentoService;
    }

    @PostMapping
    public ResponseEntity<?> crearCupon(@RequestBody CuponDescuentoCreateDTO dto) {
        try {
            CuponDescuentoDTO nuevoCupon = cuponDescuentoService.crearCupon(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCupon);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear cupón: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear cupón:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el cupón.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCuponPorId(@PathVariable Integer id) {
        try {
            CuponDescuentoDTO cupon = cuponDescuentoService.obtenerCuponPorId(id);
            return ResponseEntity.ok(cupon);
        } catch (IllegalArgumentException e) {
            logger.warn("Cupón con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerCuponPorCodigo(@PathVariable String codigo) {
        try {
            CuponDescuentoDTO cupon = cuponDescuentoService.obtenerCuponPorCodigo(codigo);
            return ResponseEntity.ok(cupon);
        } catch (IllegalArgumentException e) {
            logger.warn("Cupón con código '{}' no encontrado.", codigo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CuponDescuentoDTO>> listarCupones() {
        List<CuponDescuentoDTO> cupones = cuponDescuentoService.listarCupones();
        return ResponseEntity.ok(cupones);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCupon(@PathVariable Integer id, @RequestBody CuponDescuentoCreateDTO dto) {
        try {
            CuponDescuentoDTO cuponActualizado = cuponDescuentoService.actualizarCupon(id, dto);
            return ResponseEntity.ok(cuponActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar cupón ID {}: {}", id, e.getMessage());
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCupon(@PathVariable Integer id) {
        try {
            cuponDescuentoService.eliminarCupon(id);
            return ResponseEntity.ok("Cupón con ID " + id + " eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}