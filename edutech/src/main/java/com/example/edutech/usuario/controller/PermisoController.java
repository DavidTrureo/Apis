package com.example.edutech.usuario.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus; // Asumiendo que tienes o crearás un RolResponseDTO
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // Para mapear Rol a RolDTO
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.usuario.dto.AsignarPermisosARolDTO;
import com.example.edutech.usuario.dto.PermisoCreateDTO;
import com.example.edutech.usuario.dto.PermisoDTO;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.service.PermisoService;
import com.example.edutech.usuario.service.RolService;

@RestController
@RequestMapping("/api/permisos")
// @PreAuthorize("hasAuthority('ROL_ADMIN')") // Proteger toda la clase para administradores
public class PermisoController {

    private static final Logger logger = LoggerFactory.getLogger(PermisoController.class);
    private final PermisoService permisoService;
    private final RolService rolService; // Para mapear Rol a RolDTO

    //@Autowired
    public PermisoController(PermisoService permisoService, RolService rolService) {
        this.permisoService = permisoService;
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<?> crearPermiso(@RequestBody PermisoCreateDTO dto) {
        try {
            PermisoDTO nuevoPermiso = permisoService.crearPermiso(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPermiso);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear permiso: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<PermisoDTO>> listarPermisos() {
        return ResponseEntity.ok(permisoService.listarTodosLosPermisos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPermisoPorId(@PathVariable Integer id) {
        try {
            PermisoDTO permiso = permisoService.obtenerPermisoPorId(id);
            return ResponseEntity.ok(permiso);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/nombre/{nombrePermiso}")
    public ResponseEntity<?> obtenerPermisoPorNombre(@PathVariable String nombrePermiso) {
        try {
            PermisoDTO permiso = permisoService.obtenerPermisoPorNombre(nombrePermiso);
            return ResponseEntity.ok(permiso);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint para asignar un conjunto de permisos a un rol
    @PostMapping("/rol/{nombreRol}/asignar")
    public ResponseEntity<?> asignarPermisosARol(@PathVariable String nombreRol, @RequestBody AsignarPermisosARolDTO dto) {
        try {
            Rol rolActualizado = permisoService.asignarPermisosARol(nombreRol, dto.getNombresPermisos());
            // Devolver el RolDTO actualizado con sus permisos
            return ResponseEntity.ok(rolService.mapToRolDTOWithPermissions(rolActualizado)); // Necesitas este método en RolService
        } catch (IllegalArgumentException e) {
            logger.warn("Error al asignar permisos al rol {}: {}", nombreRol, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    // Endpoint para revocar un conjunto de permisos de un rol
    @PostMapping("/rol/{nombreRol}/revocar")
    public ResponseEntity<?> revocarPermisosDeRol(@PathVariable String nombreRol, @RequestBody AsignarPermisosARolDTO dto) {
        try {
            Rol rolActualizado = permisoService.revocarPermisosDeRol(nombreRol, dto.getNombresPermisos());
            return ResponseEntity.ok(rolService.mapToRolDTOWithPermissions(rolActualizado));
        } catch (IllegalArgumentException e) {
            logger.warn("Error al revocar permisos del rol {}: {}", nombreRol, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Considera añadir endpoints PUT para actualizar permisos y DELETE para eliminarlos.
}