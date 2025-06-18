package com.example.edutech.usuario.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.usuario.dto.RolCreateDTO;
import com.example.edutech.usuario.dto.RolDTO;
import com.example.edutech.usuario.dto.RolUpdateDTO;
import com.example.edutech.usuario.model.Permiso;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.repository.RolRepository;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminRutChecker adminRutChecker;

    public RolService(RolRepository rolRepository,
                    UsuarioRepository usuarioRepository,
                    AdminRutChecker adminRutChecker) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.adminRutChecker = adminRutChecker;
    }

    @Transactional
    public RolDTO crearRol(RolCreateDTO dto, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de crear rol por RUT (no admin): {}", adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para crear un rol.");
        }

        String nombreNormalizado = dto.getNombre().toUpperCase().replace(" ", "_");
        if (rolRepository.existsById(nombreNormalizado)) {
            logger.warn("Intento de crear rol duplicado: {}", nombreNormalizado);
            throw new IllegalArgumentException("El rol '" + nombreNormalizado + "' ya existe.");
        }

        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombreNormalizado);
        nuevoRol.setDescripcion(dto.getDescripcion());

        Rol rolGuardado = rolRepository.save(nuevoRol);
        logger.info("Rol '{}' creado exitosamente por admin {}.", nombreNormalizado, adminRutSolicitante);
        return mapToRolDTOWithPermissions(rolGuardado);
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        logger.debug("Listando todos los roles.");
        return rolRepository.findAll().stream()
                .map(this::mapToRolDTOWithPermissions)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolDTO obtenerRolDTOPorNombre(String nombre) {
        String nombreNormalizado = nombre.toUpperCase().replace(" ", "_");
        logger.debug("Buscando rol por nombre: {} (normalizado: {})", nombre, nombreNormalizado);
        Rol rol = rolRepository.findById(nombreNormalizado)
                .orElseThrow(() -> {
                    logger.warn("Rol con nombre '{}' (normalizado: '{}') no encontrado.", nombre, nombreNormalizado);
                    return new IllegalArgumentException("Rol con nombre '" + nombre + "' no encontrado.");
                });
        return mapToRolDTOWithPermissions(rol);
    }

    @Transactional(readOnly = true)
    public Rol obtenerEntidadRolPorNombre(String nombre) {
        String nombreNormalizado = nombre.toUpperCase().replace(" ", "_");
        return rolRepository.findById(nombreNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombre + "' (normalizado: '"+nombreNormalizado+"') no encontrado."));
    }

    @Transactional
    public RolDTO actualizarRol(String nombreRol, RolUpdateDTO dto, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de actualizar rol {} por RUT (no admin): {}", nombreRol, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para actualizar un rol.");
        }
        String nombreNormalizado = nombreRol.toUpperCase().replace(" ", "_");
        Rol rolExistente = rolRepository.findById(nombreNormalizado)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar rol no existente: {}", nombreNormalizado);
                    return new IllegalArgumentException("Error: Rol '" + nombreRol + "' no encontrado para actualizar.");
                });

        if (dto.getDescripcion() != null && !dto.getDescripcion().equals(rolExistente.getDescripcion())) {
            rolExistente.setDescripcion(dto.getDescripcion());
            Rol guardado = rolRepository.save(rolExistente);
            logger.info("Descripción del rol '{}' actualizada por admin {}.", nombreNormalizado, adminRutSolicitante);
            return mapToRolDTOWithPermissions(guardado);
        } else {
            logger.info("No se realizaron cambios en la descripción del rol '{}'. La descripción proporcionada era igual a la existente o nula (DTO está validado con @NotBlank).", nombreNormalizado);
            return mapToRolDTOWithPermissions(rolExistente);
        }
    }

    @Transactional
    public String eliminarRol(String nombreRol, String adminRutSolicitante) {
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de eliminar rol {} por RUT (no admin): {}", nombreRol, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para eliminar un rol.");
        }
        String nombreNormalizado = nombreRol.toUpperCase().replace(" ", "_");
        Rol rol = rolRepository.findById(nombreNormalizado)
                .orElseThrow(() -> {
                    logger.warn("Intento de eliminar rol no existente: {}", nombreNormalizado);
                    return new IllegalArgumentException("Error: Rol '" + nombreRol + "' no encontrado.");
                });

        long countUsuariosConRol = usuarioRepository.countByRolNombre(nombreNormalizado);
        if (countUsuariosConRol > 0) {
            logger.warn("Intento de eliminar rol '{}' que está en uso por {} usuario(s).", nombreNormalizado, countUsuariosConRol);
            throw new IllegalStateException("Error: No se puede eliminar el rol '" + nombreRol + "' porque está asignado a " + countUsuariosConRol + " usuario(s).");
        }
        rolRepository.delete(rol);
        logger.info("Rol '{}' eliminado correctamente por admin {}.", nombreNormalizado, adminRutSolicitante);
        return "Rol '" + nombreRol + "' eliminado correctamente.";
    }

    public RolDTO mapToRolDTOWithPermissions(Rol rol) {
        if (rol == null) return null;
        Set<String> nombresPermisos = Collections.emptySet();
        if (rol.getPermisos() != null) {
            nombresPermisos = rol.getPermisos().stream()
                                            .filter(permiso -> permiso != null && permiso.getNombrePermiso() != null)
                                            .map(Permiso::getNombrePermiso)
                                            .collect(Collectors.toSet());
        }
        return new RolDTO(rol.getNombre(), rol.getDescripcion(), nombresPermisos);
    }
}