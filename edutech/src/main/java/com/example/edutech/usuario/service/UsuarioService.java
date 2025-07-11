package com.example.edutech.usuario.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.inscripcion.repository.InscripcionRepository;
import com.example.edutech.usuario.dto.PerfilUpdateDTO;
import com.example.edutech.usuario.dto.UsuarioCreateDTO;
import com.example.edutech.usuario.dto.UsuarioDTO;
import com.example.edutech.usuario.dto.UsuarioUpdateDTO;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.model.Usuario;
import com.example.edutech.usuario.repository.RolRepository;
import com.example.edutech.usuario.repository.UsuarioRepository;
@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final InscripcionRepository inscripcionRepository;
    private final AdminRutChecker adminRutChecker;

    public UsuarioService(UsuarioRepository usuarioRepository,
                        RolRepository rolRepository,
                        PasswordEncoder passwordEncoder,
                        InscripcionRepository inscripcionRepository,
                        AdminRutChecker adminRutChecker) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.inscripcionRepository = inscripcionRepository;
        this.adminRutChecker = adminRutChecker;
    }
    
    // MÉTODO MODIFICADO para aceptar un RUT del header para simular autenticación
    private String getAuthenticatedUsername(String userRutHeader) {
        // Opción 1: Usar el header si está presente (para pruebas en Postman)
        if (userRutHeader != null && !userRutHeader.isBlank()) {
            logger.debug("Simulando autenticación para usuario con RUT desde header: {}", userRutHeader);
            return userRutHeader;
        }

        // Opción 2: Usar el SecurityContextHolder (para cuando se implemente Spring Security)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Intento de acceso sin autenticación o por usuario anónimo y sin header de simulación.");
            throw new IllegalStateException("Usuario no autenticado. Se requiere autenticación para esta operación.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetailsPrincipal) {
            return userDetailsPrincipal.getUsername();
        }
        if (principal instanceof String stringPrincipal) {
            return stringPrincipal;
        }
        logger.error("El objeto principal de autenticación no es del tipo esperado (UserDetails o String): {}",
                    principal != null ? principal.getClass().getName() : "null");
        throw new IllegalStateException("No se pudo determinar el nombre de usuario del principal de autenticación. Tipo inesperado.");
    }

    private UsuarioDTO mapToUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        return new UsuarioDTO(
            usuario.getRut(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN ROL",
            usuario.getEstadoCuenta()
        );
    }

    // MÉTODO MODIFICADO para recibir el header
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerMiPerfil(String userRutHeader) {
        String username = getAuthenticatedUsername(userRutHeader);
        Usuario usuario = usuarioRepository.findById(username)
                .orElseThrow(() -> {
                    logger.error("Usuario autenticado con identificador '{}' no encontrado en la base de datos.", username);
                    return new IllegalArgumentException("Usuario autenticado no encontrado.");
                });
        return mapToUsuarioDTO(usuario);
    }

    // MÉTODO MODIFICADO para recibir el header
    @Transactional
    public UsuarioDTO actualizarMiPerfil(PerfilUpdateDTO dto, String userRutHeader) {
        String rutUsuarioAutenticado = getAuthenticatedUsername(userRutHeader);
        Usuario usuarioExistente = usuarioRepository.findById(rutUsuarioAutenticado)
                .orElseThrow(() -> {
                    logger.error("Error: Usuario autenticado con RUT '{}' no encontrado en la base de datos al intentar actualizar perfil.", rutUsuarioAutenticado);
                    return new IllegalArgumentException("Error: Usuario autenticado no encontrado.");
                });
        boolean modificado = false;
        if (dto.getNombre() != null && !dto.getNombre().isBlank() && !dto.getNombre().equals(usuarioExistente.getNombre())) {
            usuarioExistente.setNombre(dto.getNombre());
            modificado = true;
            logger.info("Perfil del usuario {}: nombre actualizado a '{}'", rutUsuarioAutenticado, dto.getNombre());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(usuarioExistente.getEmail())) {
            Optional<Usuario> usuarioConNuevoEmail = usuarioRepository.findByEmail(dto.getEmail());
            if (usuarioConNuevoEmail.isPresent() && !usuarioConNuevoEmail.get().getRut().equals(rutUsuarioAutenticado)) {
                logger.warn("Intento de actualizar perfil del usuario {} con email '{}' que ya está en uso por otro usuario.", rutUsuarioAutenticado, dto.getEmail());
                throw new IllegalArgumentException("Error: El email '" + dto.getEmail() + "' ya está registrado por otro usuario.");
            }
            usuarioExistente.setEmail(dto.getEmail());
            modificado = true;
            logger.info("Perfil del usuario {}: email actualizado a '{}'", rutUsuarioAutenticado, dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
            modificado = true;
            logger.info("Perfil del usuario {}: contraseña actualizada.", rutUsuarioAutenticado);
        }
        if (modificado) {
            Usuario usuarioGuardado = usuarioRepository.save(usuarioExistente);
            logger.info("Perfil del usuario {} actualizado exitosamente.", rutUsuarioAutenticado);
            return mapToUsuarioDTO(usuarioGuardado);
        } else {
            logger.info("Perfil del usuario {}: no se realizaron cambios.", rutUsuarioAutenticado);
            return mapToUsuarioDTO(usuarioExistente);
        }
    }
    
    // ... resto de los métodos del servicio sin cambios ...

    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioEntidadPorRut(String rut) {
        return usuarioRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con RUT '" + rut + "' no encontrado."));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO obtenerUsuarioDTOPorRut(String rut) {
        Usuario usuario = usuarioRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con RUT '" + rut + "' no encontrado."));
        return mapToUsuarioDTO(usuario);
    }

    @Transactional
    public String registrarUsuario(UsuarioCreateDTO dto) {
        if (dto.getRut() == null || dto.getRut().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El RUT del usuario no puede estar vacío.");
        }
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre del usuario no puede estar vacío.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El email del usuario no puede estar vacío.");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: La contraseña no puede estar vacía.");
        }
        if (dto.getRolNombre() == null || dto.getRolNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre del rol no puede estar vacío.");
        }

        if (usuarioRepository.existsById(dto.getRut())) {
            logger.warn("Intento de registrar usuario con RUT duplicado: {}", dto.getRut());
            throw new IllegalArgumentException("Error: El usuario con RUT " + dto.getRut() + " ya existe.");
        }

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            logger.warn("Intento de registrar usuario con email duplicado: {}", dto.getEmail());
            throw new IllegalArgumentException("Error: El email '" + dto.getEmail() + "' ya está registrado.");
        }

        Optional<Rol> rolOpt = rolRepository.findById(dto.getRolNombre().toUpperCase().replace(" ", "_"));
        if (rolOpt.isEmpty()) {
            logger.warn("Intento de registrar usuario con rol inexistente: {}", dto.getRolNombre());
            throw new IllegalArgumentException("Error: El rol '" + dto.getRolNombre() + "' no existe.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setRut(dto.getRut());
        nuevoUsuario.setNombre(dto.getNombre());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        nuevoUsuario.setRol(rolOpt.get());
        nuevoUsuario.setEstadoCuenta(dto.getEstadoCuenta() != null && !dto.getEstadoCuenta().isBlank() ? dto.getEstadoCuenta() : "Activo");

        usuarioRepository.save(nuevoUsuario);
        logger.info("Usuario {} registrado exitosamente con rol {}.", nuevoUsuario.getNombre(), nuevoUsuario.getRol().getNombre());
        return "Usuario " + nuevoUsuario.getNombre() + " registrado correctamente con rol " + nuevoUsuario.getRol().getNombre();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        logger.debug("Listando todos los usuarios.");
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String actualizarUsuario(String rut, UsuarioUpdateDTO dto, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de actualizar usuario {} por RUT (no admin): {}", rut, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para actualizar este usuario.");
        }

        Usuario usuarioExistente = usuarioRepository.findById(rut)
                .orElseThrow(() -> {
                    logger.warn("Admin {} intentó actualizar usuario no existente con RUT: {}", adminRutSolicitante, rut);
                    return new IllegalArgumentException("Error: Usuario con RUT '" + rut + "' no encontrado.");
                });

        boolean modificado = false;

        if (dto.getNombre() != null && !dto.getNombre().isBlank() && !dto.getNombre().equals(usuarioExistente.getNombre())) {
            usuarioExistente.setNombre(dto.getNombre());
            modificado = true;
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank() && !dto.getEmail().equals(usuarioExistente.getEmail())) {
            Optional<Usuario> usuarioConNuevoEmail = usuarioRepository.findByEmail(dto.getEmail());
            if (usuarioConNuevoEmail.isPresent() && !usuarioConNuevoEmail.get().getRut().equals(rut)) {
                throw new IllegalArgumentException("Error: El email '" + dto.getEmail() + "' ya está registrado por otro usuario.");
            }
            usuarioExistente.setEmail(dto.getEmail());
            modificado = true;
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(dto.getPassword()));
            modificado = true;
        }
        if (dto.getRolNombre() != null && !dto.getRolNombre().isBlank()) {
            String nombreRolNormalizado = dto.getRolNombre().toUpperCase().replace(" ", "_");
            if (usuarioExistente.getRol() == null || !nombreRolNormalizado.equals(usuarioExistente.getRol().getNombre())) {
                Optional<Rol> rolOpt = rolRepository.findById(nombreRolNormalizado);
                if (rolOpt.isPresent()) {
                    usuarioExistente.setRol(rolOpt.get());
                    modificado = true;
                } else {
                    throw new IllegalArgumentException("Error: El rol '" + dto.getRolNombre() + "' no existe.");
                }
            }
        }
        if (dto.getEstadoCuenta() != null && !dto.getEstadoCuenta().isBlank() && !dto.getEstadoCuenta().equals(usuarioExistente.getEstadoCuenta())) {
            usuarioExistente.setEstadoCuenta(dto.getEstadoCuenta());
            modificado = true;
        }

        if (modificado) {
            usuarioRepository.save(usuarioExistente);
            logger.info("Usuario {} actualizado por admin {}.", rut, adminRutSolicitante);
            return "Usuario actualizado correctamente.";
        } else {
            logger.info("Usuario {}: no se realizaron cambios por admin {}.", rut, adminRutSolicitante);
            return "No se realizaron cambios en el usuario.";
        }
    }

    @Transactional
    public String eliminarUsuario(String rut, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de eliminar usuario {} por RUT (no admin): {}", rut, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para eliminar usuarios.");
        }

        Usuario usuario = usuarioRepository.findById(rut)
                .orElseThrow(() -> {
                    logger.warn("Admin {} intentó eliminar usuario no existente con RUT: {}", adminRutSolicitante, rut);
                    return new IllegalArgumentException("Error: Usuario con RUT '" + rut + "' no encontrado.");
                });

        logger.info("Iniciando eliminación de dependencias para usuario RUT: {} (solicitado por admin {})", rut, adminRutSolicitante);
        try {
            inscripcionRepository.deleteByUsuarioRut(rut);
            logger.info("Inscripciones eliminadas para usuario RUT: {}", rut);
        } catch (Exception e) {
            logger.error("Error al eliminar dependencias (inscripciones) para usuario RUT: {}. Error: {}", rut, e.getMessage(), e);
        }

        usuarioRepository.delete(usuario);
        logger.info("Usuario {} eliminado exitosamente por admin {}.", rut, adminRutSolicitante);
        return "Usuario '" + rut + "' y sus datos asociados (como inscripciones) eliminados correctamente.";
    }

    @Transactional
    public String asignarRolAUsuario(String rut, String nombreRol, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de asignar rol a usuario {} por RUT (no admin): {}", rut, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para asignar roles.");
        }

        Usuario usuario = usuarioRepository.findById(rut)
                .orElseThrow(() -> new IllegalArgumentException("Error: Usuario con RUT '" + rut + "' no encontrado."));

        String nombreRolNormalizado = nombreRol.toUpperCase().replace(" ", "_");
        Rol rol = rolRepository.findById(nombreRolNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Error: Rol '" + nombreRol + "' no encontrado."));

        usuario.setRol(rol);
        usuarioRepository.save(usuario);
        logger.info("Rol '{}' asignado al usuario {} por admin {}.", nombreRol, rut, adminRutSolicitante);
        return "Rol '" + nombreRol + "' asignado correctamente al usuario " + rut;
    }

    public void listarTodosLosUsuariosConsola() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados en la base de datos.");
            return;
        }
        System.out.println("Listado de Usuarios en BD:");
        for (Usuario u : usuarios) {
            System.out.println(" - RUT: " + u.getRut() +
                            ", Nombre: " + u.getNombre() +
                            ", Email: " + u.getEmail() +
                            ", Rol: " + (u.getRol() != null ? u.getRol().getNombre() : "N/A") +
                            ", Estado: " + u.getEstadoCuenta());
        }
    }
}