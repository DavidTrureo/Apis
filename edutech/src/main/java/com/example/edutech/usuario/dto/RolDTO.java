package com.example.edutech.usuario.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.edutech.usuario.model.Permiso; // Necesario si PermisoDTO no es estático o está en otro paquete
import com.example.edutech.usuario.model.Rol;

public class RolDTO {
    private String nombre;
    private String descripcion;
    private Set<String> permisos; // Nombres de los permisos

    public RolDTO() {
    }

    public RolDTO(String nombre, String descripcion, Set<String> permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.permisos = permisos;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Set<String> getPermisos() { return permisos; }
    public void setPermisos(Set<String> permisos) { this.permisos = permisos; }

    // Método estático de fábrica o un mapeador en el servicio
    public static RolDTO fromEntity(Rol rol) {
        if (rol == null) return null;
        Set<String> nombresPermisos = rol.getPermisos().stream()
                                        .map(Permiso::getNombrePermiso)
                                        .collect(Collectors.toSet());
        return new RolDTO(rol.getNombre(), rol.getDescripcion(), nombresPermisos);
    }
}