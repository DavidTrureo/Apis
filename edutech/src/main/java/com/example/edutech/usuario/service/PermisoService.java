package com.example.edutech.usuario.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.config.AdminRutChecker;
import com.example.edutech.usuario.dto.PermisoCreateDTO;
import com.example.edutech.usuario.dto.PermisoDTO;
import com.example.edutech.usuario.model.Permiso;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.repository.PermisoRepository;
import com.example.edutech.usuario.repository.RolRepository;

@Service
public class PermisoService {

    private static final Logger logger = LoggerFactory.getLogger(PermisoService.class); // AÑADIR LOGGER

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;
    private final AdminRutChecker adminRutChecker; // INYECTAR

    public PermisoService(PermisoRepository permisoRepository, 
                        RolRepository rolRepository,
                        AdminRutChecker adminRutChecker) { // AÑADIR AL CONSTRUCTOR
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
        this.adminRutChecker = adminRutChecker; // ASIGNAR
    }

    @Transactional
    public PermisoDTO crearPermiso(PermisoCreateDTO dto, String adminRutSolicitante) { // AÑADIR adminRutSolicitante
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de crear permiso por RUT (no admin): {}", adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para crear permisos.");
        }

        String nombreNormalizado = dto.getNombrePermiso().toUpperCase().replace(" ", "_");
        if (permisoRepository.existsByNombrePermiso(nombreNormalizado)) {
            logger.warn("Intento de crear permiso duplicado: {}", nombreNormalizado);
            throw new IllegalArgumentException("El permiso '" + nombreNormalizado + "' ya existe.");
        }
        Permiso permiso = new Permiso(nombreNormalizado, dto.getDescripcion());
        Permiso guardado = permisoRepository.save(permiso);
        logger.info("Permiso '{}' creado exitosamente por admin {}.", nombreNormalizado, adminRutSolicitante);
        return mapToPermisoDTO(guardado);
    }

    @Transactional(readOnly = true)
    public PermisoDTO obtenerPermisoPorId(Integer id) {
        logger.debug("Buscando permiso por ID: {}", id);
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso con ID " + id + " no encontrado."));
        return mapToPermisoDTO(permiso);
    }

    @Transactional(readOnly = true)
    public PermisoDTO obtenerPermisoPorNombre(String nombrePermiso) {
        String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
        logger.debug("Buscando permiso por nombre: {} (normalizado: {})", nombrePermiso, nombreNormalizado);
        Permiso permiso = permisoRepository.findByNombrePermiso(nombreNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Permiso con nombre '" + nombrePermiso + "' no encontrado."));
        return mapToPermisoDTO(permiso);
    }

    @Transactional(readOnly = true)
    public List<PermisoDTO> listarTodosLosPermisos() {
        logger.debug("Listando todos los permisos.");
        return permisoRepository.findAll().stream()
                .map(this::mapToPermisoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Rol asignarPermisosARol(String nombreRol, Set<String> nombresPermisos, String adminRutSolicitante) { // AÑADIR adminRutSolicitante
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de asignar permisos al rol {} por RUT (no admin): {}", nombreRol, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para asignar permisos.");
        }

        Rol rol = rolRepository.findById(nombreRol.toUpperCase().replace(" ", "_")) // Normalizar nombre del rol
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombreRol + "' no encontrado."));

        Set<Permiso> permisosParaAsignar = new HashSet<>();
        for (String nombrePermiso : nombresPermisos) {
            String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
            Permiso permiso = permisoRepository.findByNombrePermiso(nombreNormalizado)
                    .orElseThrow(() -> new IllegalArgumentException("Permiso '" + nombrePermiso + "' no encontrado."));
            permisosParaAsignar.add(permiso);
        }

        rol.getPermisos().clear(); // Reemplazar completamente los permisos existentes
        for(Permiso p : permisosParaAsignar) {
            rol.addPermiso(p);
        }
        
        logger.info("Permisos actualizados para el rol '{}' por admin {}.", nombreRol, adminRutSolicitante);
        return rolRepository.save(rol);
    }

    @Transactional
    public Rol revocarPermisosDeRol(String nombreRol, Set<String> nombresPermisosARevocar, String adminRutSolicitante) { // AÑADIR adminRutSolicitante
        if (!adminRutChecker.isAdmin(adminRutSolicitante)) {
            logger.warn("Intento no autorizado de revocar permisos del rol {} por RUT (no admin): {}", nombreRol, adminRutSolicitante);
            throw new SecurityException("Acción no autorizada: Se requieren privilegios de administrador para revocar permisos.");
        }

        Rol rol = rolRepository.findById(nombreRol.toUpperCase().replace(" ", "_")) // Normalizar nombre del rol
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombreRol + "' no encontrado."));

        for (String nombrePermiso : nombresPermisosARevocar) {
            String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
            permisoRepository.findByNombrePermiso(nombreNormalizado).ifPresent(rol::removePermiso);
        }
        logger.info("Permisos revocados para el rol '{}' por admin {}.", nombreRol, adminRutSolicitante);
        return rolRepository.save(rol);
    }

    private PermisoDTO mapToPermisoDTO(Permiso permiso) {
        if (permiso == null) return null;
        return new PermisoDTO(permiso.getId(), permiso.getNombrePermiso(), permiso.getDescripcion());
    }
}