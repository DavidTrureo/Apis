package com.example.edutech.proveedor.controller;

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

import com.example.edutech.proveedor.dto.ProveedorDTO;
import com.example.edutech.proveedor.service.ProveedorService;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    //@Autowired
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_LOGISTICA_SOPORTE')")
    public ResponseEntity<?> crearProveedor(@RequestBody ProveedorDTO dto) {
        try {
            ProveedorDTO proveedorCreado = proveedorService.guardarProveedor(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(proveedorCreado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear proveedor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear proveedor:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el proveedor.");
        }
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        List<ProveedorDTO> proveedores = proveedorService.listarProveedores();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{rut}")
    public ResponseEntity<?> obtenerProveedorPorRut(@PathVariable String rut) {
        try {
            ProveedorDTO proveedor = proveedorService.obtenerProveedorPorRut(rut);
            return ResponseEntity.ok(proveedor);
        } catch (IllegalArgumentException e) {
            logger.warn("Proveedor con RUT {} no encontrado.", rut);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{rut}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_LOGISTICA_SOPORTE')")
    public ResponseEntity<?> actualizarProveedor(@PathVariable String rut, @RequestBody ProveedorDTO dto) {
        try {
            ProveedorDTO proveedorActualizado = proveedorService.actualizarProveedor(rut, dto);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar proveedor RUT {}: {}", rut, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{rut}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_LOGISTICA_SOPORTE')")
    public ResponseEntity<String> eliminarProveedor(@PathVariable String rut) {
        try {
            proveedorService.eliminarProveedor(rut);
            return ResponseEntity.ok("Proveedor con RUT " + rut + " eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            logger.warn("Error al eliminar proveedor RUT {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}