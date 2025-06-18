package com.example.edutech.usuario.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.usuario.dto.AsignarPermisosARolDTO;
import com.example.edutech.usuario.dto.PermisoCreateDTO;
import com.example.edutech.usuario.dto.PermisoDTO;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.service.PermisoService;
import com.example.edutech.usuario.service.RolService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/permisos")
public class PermisoController {

    private static final Logger logger = LoggerFactory.getLogger(PermisoController.class);
    private final PermisoService permisoService;
    private final RolService rolService;

    public PermisoController(PermisoService permisoService, RolService rolService) {
        this.permisoService = permisoService;
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<?> crearPermiso(@Valid @RequestBody PermisoCreateDTO dto,
                                        @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("POST /api/permisos solicitado por admin {}", adminRutSolicitante);
        try {
            PermisoDTO nuevoPermiso = permisoService.crearPermiso(dto, adminRutSolicitante);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPermiso);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de crear permiso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al crear permiso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PermisoDTO>> listarPermisos() {
        logger.info("GET /api/permisos solicitado");
        return ResponseEntity.ok(permisoService.listarTodosLosPermisos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPermisoPorId(@PathVariable Integer id) {
        logger.info("GET /api/permisos/{} solicitado", id);
        try {
            PermisoDTO permiso = permisoService.obtenerPermisoPorId(id);
            return ResponseEntity.ok(permiso);
        } catch (IllegalArgumentException e) {
            logger.warn("Permiso con ID {} no encontrado.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/nombre/{nombrePermiso}")
    public ResponseEntity<?> obtenerPermisoPorNombre(@PathVariable String nombrePermiso) {
        logger.info("GET /api/permisos/nombre/{} solicitado", nombrePermiso);
        try {
            PermisoDTO permiso = permisoService.obtenerPermisoPorNombre(nombrePermiso);
            return ResponseEntity.ok(permiso);
        } catch (IllegalArgumentException e) {
            logger.warn("Permiso con nombre {} no encontrado.", nombrePermiso);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/rol/{nombreRol}/asignar")
    public ResponseEntity<?> asignarPermisosARol(@PathVariable String nombreRol,
                                                @Valid @RequestBody AsignarPermisosARolDTO dto,
                                                @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("POST /api/permisos/rol/{}/asignar solicitado por admin {}", nombreRol, adminRutSolicitante);
        try {
            Rol rolActualizado = permisoService.asignarPermisosARol(nombreRol, dto.getNombresPermisos(), adminRutSolicitante);
            return ResponseEntity.ok(rolService.mapToRolDTOWithPermissions(rolActualizado));
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de asignar permisos al rol {}: {}", nombreRol, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al asignar permisos al rol {}: {}", nombreRol, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/rol/{nombreRol}/revocar")
    public ResponseEntity<?> revocarPermisosDeRol(@PathVariable String nombreRol,
                                                @Valid @RequestBody AsignarPermisosARolDTO dto,
                                                @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("POST /api/permisos/rol/{}/revocar solicitado por admin {}", nombreRol, adminRutSolicitante);
        try {
            Rol rolActualizado = permisoService.revocarPermisosDeRol(nombreRol, dto.getNombresPermisos(), adminRutSolicitante);
            return ResponseEntity.ok(rolService.mapToRolDTOWithPermissions(rolActualizado));
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de revocar permisos del rol {}: {}", nombreRol, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al revocar permisos del rol {}: {}", nombreRol, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}