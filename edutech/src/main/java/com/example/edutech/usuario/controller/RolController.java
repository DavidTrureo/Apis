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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.edutech.usuario.dto.RolCreateDTO;
import com.example.edutech.usuario.dto.RolDTO;
import com.example.edutech.usuario.dto.RolUpdateDTO;
import com.example.edutech.usuario.service.RolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);
    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<?> crearRol(@Valid @RequestBody RolCreateDTO createDTO,
                                    @RequestHeader(name = "X-Admin-RUT", required = true) String adminRutSolicitante) {
        try {
            RolDTO rolCreadoDTO = rolService.crearRol(createDTO, adminRutSolicitante);
            return ResponseEntity.status(HttpStatus.CREATED).body(rolCreadoDTO);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de crear rol (RUT solicitante: {}): {}", adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al crear rol (solicitado por {}): {}", adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.warn("Conflicto de datos al crear rol (solicitado por {}): {}", adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Ya existe un rol con el nombre especificado.");
        }
        catch (Exception e) {
            logger.error("Error interno al crear rol (solicitado por {}):", adminRutSolicitante, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el rol.");
        }
    }

    @GetMapping
    public ResponseEntity<List<RolDTO>> listarRoles() {
        logger.info("GET /api/roles solicitado");
        List<RolDTO> rolesDTO = rolService.listarRoles();
        return ResponseEntity.ok(rolesDTO);
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<?> obtenerRolPorNombre(@PathVariable String nombre) {
        logger.info("GET /api/roles/{} solicitado", nombre);
        try {
            RolDTO rolDTO = rolService.obtenerRolDTOPorNombre(nombre);
            return ResponseEntity.ok(rolDTO);
        } catch (IllegalArgumentException e) {
            logger.warn("GET /api/roles/{}: Rol no encontrado.", nombre);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{nombre}")
    public ResponseEntity<?> actualizarRol(@PathVariable String nombre,
                                        @Valid @RequestBody RolUpdateDTO updateDTO,
                                        @RequestHeader(name = "X-Admin-RUT", required = true) String adminRutSolicitante) {
        logger.info("PUT /api/roles/{} solicitado por admin {}", nombre, adminRutSolicitante);
        try {
            RolDTO rolActualizadoDTO = rolService.actualizarRol(nombre, updateDTO, adminRutSolicitante);
            return ResponseEntity.ok(rolActualizadoDTO);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de actualizar rol '{}' (RUT solicitante: {}): {}", nombre, adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar rol '{}' (solicitado por {}): {}", nombre, adminRutSolicitante, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar rol '{}' (solicitado por {}):", nombre, adminRutSolicitante, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el rol.");
        }
    }

    @DeleteMapping("/{nombre}")
    public ResponseEntity<String> eliminarRol(@PathVariable String nombre,
                                            @RequestHeader(name = "X-Admin-RUT", required = true) String adminRutSolicitante) {
        logger.info("DELETE /api/roles/{} solicitado por admin {}", nombre, adminRutSolicitante);
        try {
            String mensaje = rolService.eliminarRol(nombre, adminRutSolicitante);
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de eliminar rol '{}' (RUT solicitante: {}): {}", nombre, adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalStateException e) { // Para rol en uso
            logger.warn("Conflicto al eliminar rol '{}' (solicitado por {}): {}", nombre, adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Para rol no encontrado
            logger.warn("Intento de eliminar rol no existente '{}' (solicitado por {}): {}", nombre, adminRutSolicitante, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar rol '{}' (solicitado por {}):", nombre, adminRutSolicitante, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el rol.");
        }
    }
}