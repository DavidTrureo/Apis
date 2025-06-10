package com.example.edutech.usuario.controller;

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

import com.example.edutech.usuario.dto.RolCreateDTO;
import com.example.edutech.usuario.dto.RolDTO;
import com.example.edutech.usuario.dto.RolUpdateDTO;
import com.example.edutech.usuario.service.RolService;

import jakarta.validation.Valid; // IMPORTAR

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> crearRol(@Valid @RequestBody RolCreateDTO createDTO) { // CAMBIO: Usa RolCreateDTO y @Valid
        try {
            RolDTO rolCreadoDTO = rolService.crearRol(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(rolCreadoDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear rol: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.warn("Conflicto de datos al crear rol (probablemente nombre duplicado): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Ya existe un rol con ese nombre.");
        }
        catch (Exception e) {
            logger.error("Error interno al crear rol:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el rol.");
        }
    }

    @GetMapping
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<List<RolDTO>> listarRoles() {
        List<RolDTO> rolesDTO = rolService.listarRoles();
        return ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/{nombre}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> obtenerRolPorNombre(@PathVariable String nombre) {
        try {
            RolDTO rolDTO = rolService.obtenerRolDTOPorNombre(nombre);
            return ResponseEntity.ok(rolDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("GET /api/roles/{}: Rol no encontrado.", nombre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{nombre}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> actualizarRol(@PathVariable String nombre, @Valid @RequestBody RolUpdateDTO updateDTO) { // CAMBIO: Usa RolUpdateDTO y @Valid
        try {
            RolDTO rolActualizadoDTO = rolService.actualizarRol(nombre, updateDTO);
            return ResponseEntity.ok(rolActualizadoDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar rol '{}': {}", nombre, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar rol '{}':", nombre, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el rol.");
        }
    }

    @DeleteMapping("/{nombre}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<String> eliminarRol(@PathVariable String nombre) {
        try {
            String mensaje = rolService.eliminarRol(nombre);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalStateException e) { // Para rol en uso
            logger.warn("Conflicto al eliminar rol '{}': {}", nombre, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Para rol no encontrado
            logger.warn("Intento de eliminar rol no existente '{}': {}", nombre, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar rol '{}':", nombre, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el rol.");
        }
    }
}