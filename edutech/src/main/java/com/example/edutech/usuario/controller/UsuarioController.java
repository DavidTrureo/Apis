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

import com.example.edutech.usuario.dto.PerfilUpdateDTO;
import com.example.edutech.usuario.dto.UsuarioCreateDTO;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.dto.UsuarioUpdateDTO;
import com.example.edutech.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios") // Ruta base estandarizada
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService usuarioService;

    //@Autowired
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

    // --- Endpoints para la gestión del perfil del propio usuario ---
    @GetMapping("/me")
    // @PreAuthorize("isAuthenticated()") // Proteger este endpoint
    public ResponseEntity<?> obtenerMiPerfil() {
        try {
            UsuarioDTO perfil = usuarioService.obtenerMiPerfil();
            return ResponseEntity.ok(perfil);
        } catch (IllegalStateException e) { // Usuario no autenticado
            logger.warn("Intento de obtener perfil sin autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) { // Usuario autenticado pero no encontrado en BD
            logger.error("Error al obtener perfil del usuario autenticado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al obtener mi perfil:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al obtener el perfil.");
        }
    }

    @PutMapping("/me")
    // @PreAuthorize("isAuthenticated()") // Proteger este endpoint
    public ResponseEntity<?> actualizarMiPerfil(@Valid @RequestBody PerfilUpdateDTO perfilUpdateDTO) {
        try {
            UsuarioDTO perfilActualizado = usuarioService.actualizarMiPerfil(perfilUpdateDTO);
            return ResponseEntity.ok(perfilActualizado);
        } catch (IllegalStateException e) { // Usuario no autenticado
            logger.warn("Intento de actualizar perfil sin autenticación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) { // RUT no encontrado o email duplicado
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

    // --- Endpoints para administración de usuarios (roles: ADMIN, GERENTE_CURSOS, etc.) ---
    @GetMapping("/{rut}")
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_GERENTE_CURSOS')")
    public ResponseEntity<?> obtenerUsuarioPorRut(@PathVariable String rut) { // Renombrado de obtenerUsuario
        try {
            UsuarioDTO usuarioDTO = usuarioService.obtenerUsuarioDTOPorRut(rut);
            return ResponseEntity.ok(usuarioDTO);
        } catch (IllegalArgumentException e){
            logger.info("Usuario con RUT {} no encontrado (admin).", rut);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    // @PreAuthorize("hasAnyAuthority('ROL_ADMIN', 'ROL_GERENTE_CURSOS')")
    public ResponseEntity<List<UsuarioDTO>> listarTodosLosUsuarios() { // Renombrado de listar
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PutMapping("/{rut}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<?> actualizarUsuarioPorAdmin(@PathVariable String rut, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) { // Renombrado de actualizar
        try {
            String mensaje = usuarioService.actualizarUsuario(rut, usuarioUpdateDTO);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar usuario {} por admin: {}", rut, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al actualizar usuario {} por admin:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el usuario.");
        }
    }

    @DeleteMapping("/{rut}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<String> eliminarUsuarioPorAdmin(@PathVariable String rut) { // Renombrado de eliminar
        try {
            String mensaje = usuarioService.eliminarUsuario(rut);
            logger.info("Usuario {} eliminado por admin.", rut);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Intento de admin de eliminar usuario no existente {}: {}", rut, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al eliminar usuario {} por admin:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el usuario.");
        }
    }

    @PutMapping("/{rut}/rol/{nombreRol}")
    // @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public ResponseEntity<String> asignarRolAUsuarioPorAdmin(@PathVariable String rut, @PathVariable String nombreRol) { // Renombrado de asignarRol
        try {
            String mensaje = usuarioService.asignarRolAUsuario(rut, nombreRol);
            return ResponseEntity.ok(mensaje);
        } catch (IllegalArgumentException e) {
            logger.warn("Error al asignar rol {} a usuario {} por admin: {}", nombreRol, rut, e.getMessage());
            if (e.getMessage().toLowerCase().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al asignar rol a usuario {} por admin:", rut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al asignar el rol.");
        }
    }
}