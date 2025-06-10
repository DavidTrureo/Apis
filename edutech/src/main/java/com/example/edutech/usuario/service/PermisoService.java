package com.example.edutech.usuario.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.usuario.dto.PermisoCreateDTO;
import com.example.edutech.usuario.dto.PermisoDTO;
import com.example.edutech.usuario.model.Permiso;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.repository.PermisoRepository;
import com.example.edutech.usuario.repository.RolRepository;

@Service
public class PermisoService {

    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;

    //@Autowired
    public PermisoService(PermisoRepository permisoRepository, RolRepository rolRepository) {
        this.permisoRepository = permisoRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public PermisoDTO crearPermiso(PermisoCreateDTO dto) {
        if (dto.getNombrePermiso() == null || dto.getNombrePermiso().isBlank()) {
            throw new IllegalArgumentException("El nombre del permiso es obligatorio.");
        }
        String nombreNormalizado = dto.getNombrePermiso().toUpperCase().replace(" ", "_");
        if (permisoRepository.existsByNombrePermiso(nombreNormalizado)) {
            throw new IllegalArgumentException("El permiso '" + nombreNormalizado + "' ya existe.");
        }
        Permiso permiso = new Permiso(nombreNormalizado, dto.getDescripcion());
        Permiso guardado = permisoRepository.save(permiso);
        return mapToPermisoDTO(guardado);
    }

    public PermisoDTO obtenerPermisoPorId(Integer id) {
        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Permiso con ID " + id + " no encontrado."));
        return mapToPermisoDTO(permiso);
    }

    public PermisoDTO obtenerPermisoPorNombre(String nombrePermiso) {
        String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
        Permiso permiso = permisoRepository.findByNombrePermiso(nombreNormalizado)
                .orElseThrow(() -> new IllegalArgumentException("Permiso con nombre '" + nombrePermiso + "' no encontrado."));
        return mapToPermisoDTO(permiso);
    }

    public List<PermisoDTO> listarTodosLosPermisos() {
        return permisoRepository.findAll().stream()
                .map(this::mapToPermisoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Rol asignarPermisosARol(String nombreRol, Set<String> nombresPermisos) {
        Rol rol = rolRepository.findById(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombreRol + "' no encontrado."));

        Set<Permiso> permisosParaAsignar = new HashSet<>();
        for (String nombrePermiso : nombresPermisos) {
            String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
            Permiso permiso = permisoRepository.findByNombrePermiso(nombreNormalizado)
                    .orElseThrow(() -> new IllegalArgumentException("Permiso '" + nombrePermiso + "' no encontrado."));
            permisosParaAsignar.add(permiso);
        }

        // Limpiar permisos existentes y añadir los nuevos, o solo añadir si se desea acumular
        rol.getPermisos().clear(); // Para reemplazar completamente
        for(Permiso p : permisosParaAsignar) {
            rol.addPermiso(p); // Usa el método helper de la entidad Rol
        }
        // rol.setPermisos(permisosParaAsignar); // Alternativa si no usas addPermiso

        return rolRepository.save(rol);
    }
    
    @Transactional
    public Rol revocarPermisosDeRol(String nombreRol, Set<String> nombresPermisosARevocar) {
        Rol rol = rolRepository.findById(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol '" + nombreRol + "' no encontrado."));

        for (String nombrePermiso : nombresPermisosARevocar) {
            String nombreNormalizado = nombrePermiso.toUpperCase().replace(" ", "_");
            permisoRepository.findByNombrePermiso(nombreNormalizado).ifPresent(rol::removePermiso);
        }
        return rolRepository.save(rol);
    }


    private PermisoDTO mapToPermisoDTO(Permiso permiso) {
        if (permiso == null) return null;
        return new PermisoDTO(permiso.getId(), permiso.getNombrePermiso(), permiso.getDescripcion());
    }
}