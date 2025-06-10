package com.example.edutech.usuario.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.edutech.usuario.dto.RolCreateDTO; // IMPORTAR
import com.example.edutech.usuario.dto.RolDTO;
import com.example.edutech.usuario.dto.RolUpdateDTO; // IMPORTAR
import com.example.edutech.usuario.model.Permiso;
import com.example.edutech.usuario.model.Rol;
import com.example.edutech.usuario.repository.RolRepository;
import com.example.edutech.usuario.repository.UsuarioRepository;

@Service
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public RolService(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public RolDTO crearRol(RolCreateDTO dto) { // CAMBIO: Recibe RolCreateDTO
        // Las validaciones de @NotBlank y @Size del DTO son manejadas por @Valid en el controlador
        String nombreNormalizado = dto.getNombre().toUpperCase().replace(" ", "_");
        if (rolRepository.existsById(nombreNormalizado)) {
            logger.warn("Intento de crear rol duplicado: {}", nombreNormalizado);
            throw new IllegalArgumentException("El rol '" + nombreNormalizado + "' ya existe.");
        }

        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombreNormalizado);
        nuevoRol.setDescripcion(dto.getDescripcion());

        Rol rolGuardado = rolRepository.save(nuevoRol);
        logger.info("Rol '{}' creado exitosamente.", nombreNormalizado);
        return mapToRolDTOWithPermissions(rolGuardado); // Devuelve el DTO con permisos (inicialmente vacío)
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(this::mapToRolDTOWithPermissions)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolDTO obtenerRolDTOPorNombre(String nombre) {
        String nombreNormalizado = nombre.toUpperCase().replace(" ", "_");
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
    public RolDTO actualizarRol(String nombreRol, RolUpdateDTO dto) { // CAMBIO: Recibe RolUpdateDTO
        String nombreNormalizado = nombreRol.toUpperCase().replace(" ", "_");
        Rol rolExistente = rolRepository.findById(nombreNormalizado)
                .orElseThrow(() -> {
                    logger.warn("Intento de actualizar rol no existente: {}", nombreNormalizado);
                    return new IllegalArgumentException("Error: Rol '" + nombreRol + "' no encontrado para actualizar.");
                });

        // La descripción es el único campo actualizable a través de este DTO.
        // La validación @NotBlank del DTO asegura que la descripción no sea vacía.
        if (dto.getDescripcion() != null && !dto.getDescripcion().equals(rolExistente.getDescripcion())) {
            rolExistente.setDescripcion(dto.getDescripcion());
            Rol guardado = rolRepository.save(rolExistente);
            logger.info("Descripción del rol '{}' actualizada.", nombreNormalizado);
            return mapToRolDTOWithPermissions(guardado);
        } else {
            logger.info("No se realizaron cambios en la descripción del rol '{}' (descripción igual o nula en DTO pero DTO es @NotBlank).", nombreNormalizado);
            return mapToRolDTOWithPermissions(rolExistente);
        }
    }

    @Transactional
    public String eliminarRol(String nombreRol) {
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

        // Al eliminar un Rol, la tabla de unión rol_permiso manejará la eliminación de las relaciones
        // gracias a la configuración de la relación @ManyToMany.
        rolRepository.delete(rol);
        logger.info("Rol '{}' eliminado correctamente.", nombreNormalizado);
        return "Rol '" + nombreRol + "' eliminado correctamente.";
    }

    // Este método ya estaba bien, se usa para las respuestas.
    public RolDTO mapToRolDTOWithPermissions(Rol rol) {
        if (rol == null) return null;
        Set<String> nombresPermisos = rol.getPermisos().stream()
                                        .map(Permiso::getNombrePermiso)
                                        .collect(Collectors.toSet());
        return new RolDTO(rol.getNombre(), rol.getDescripcion(), nombresPermisos);
    }
}