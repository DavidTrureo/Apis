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

import com.example.edutech.usuario.dto.PerfilUpdateDTO;
import com.example.edutech.usuario.dto.UsuarioCreateDTO;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.dto.UsuarioUpdateDTO;
import com.example.edutech.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        try {
            String mensaje = usuarioService.registrarUsuario(usuarioCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al registrar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            logger.warn("Conflicto de datos al registrar usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error de integridad de datos, es posible que el RUT o email ya existan.");
        } catch (Exception e) {
            logger.error("Error interno al registrar usuario:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar el registro.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> obtenerMiPerfil(
            // Añadí un header opcional para simular la autenticación en las pruebas
            @RequestHeader(name = "X-User-RUT", required = false) String userRutHeader) {
        try {
            UsuarioDTO perfil = usuarioService.obtenerMiPerfil(userRutHeader); // Pasamos el header al servicio
            return ResponseEntity.ok(perfil);
        } catch (IllegalStateException e) { 
            logger.warn("Intento de obtener perfil sin autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) { 
            logger.error("Error al obtener perfil del usuario autenticado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al obtener mi perfil:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al obtener el perfil.");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> actualizarMiPerfil(
            @Valid @RequestBody PerfilUpdateDTO perfilUpdateDTO,
            // Añadimos un header opcional para simular la autenticación en las pruebas
            @RequestHeader(name = "X-User-RUT", required = false) String userRutHeader) {
        try {
            UsuarioDTO perfilActualizado = usuarioService.actualizarMiPerfil(perfilUpdateDTO, userRutHeader); // Pasamos el header al servicio
            return ResponseEntity.ok(perfilActualizado);
        } catch (IllegalStateException e) { 
            logger.warn("Intento de actualizar perfil sin autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) { 
            logger.warn("Error al actualizar perfil: {}", e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar mi perfil:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el perfil.");
        }
    }

    @GetMapping("/{rut}")
    public ResponseEntity<?> obtenerUsuarioPorRut(@PathVariable String rut) {
        logger.info("GET /api/usuarios/{} solicitado", rut);
        try {
            UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioDTOPorRut(rut);
            return ResponseEntity.ok(usuarioDTO);
        } catch (IllegalArgumentException e){
            logger.warn("Usuario con RUT {} no encontrado.", rut);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodosLosUsuarios() {
        logger.info("GET /api/usuarios solicitado");
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PutMapping("/{rut}")
    public ResponseEntity<?> actualizarUsuarioPorAdmin(@PathVariable String rut,
                                                    @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO,
                                                    @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("PUT /api/usuarios/{} solicitado por admin {}", rut, adminRutSolicitante);
        try {
            String mensaje = usuarioService.actualizarUsuario(rut, usuarioUpdateDTO, adminRutSolicitante);
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de actualizar usuario {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar usuario {}: {}", rut, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar usuario {}:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el usuario.");
        }
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<String> eliminarUsuarioPorAdmin(@PathVariable String rut,
                                                        @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("DELETE /api/usuarios/{} solicitado por admin {}", rut, adminRutSolicitante);
        try {
            String mensaje = usuarioService.eliminarUsuario(rut, adminRutSolicitante);
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de eliminar usuario {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Intento de admin de eliminar usuario no existente {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar usuario {}:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el usuario.");
        }
    }

    @PutMapping("/{rut}/rol/{nombreRol}")
    public ResponseEntity<String> asignarRolAUsuarioPorAdmin(@PathVariable String rut,
                                                            @PathVariable String nombreRol,
                                                            @RequestHeader("X-Admin-RUT") String adminRutSolicitante) {
        logger.info("PUT /api/usuarios/{}/rol/{} solicitado por admin {}", rut, nombreRol, adminRutSolicitante);
        try {
            String mensaje = usuarioService.asignarRolAUsuario(rut, nombreRol, adminRutSolicitante);
            return ResponseEntity.ok(mensaje);
        } catch (SecurityException e) {
            logger.warn("Intento no autorizado de asignar rol a usuario {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            logger.warn("Error al asignar rol {} a usuario {}: {}", nombreRol, rut, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al asignar rol a usuario {}:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al asignar el rol.");
        }
    }
}