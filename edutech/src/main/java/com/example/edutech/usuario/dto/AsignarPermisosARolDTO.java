package com.example.edutech.usuario.dto;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty; // Descomentar
import jakarta.validation.constraints.NotNull; // Añadir para el Set en sí

public class AsignarPermisosARolDTO {

    @NotNull(message = "La lista de nombres de permisos no puede ser nula.")
    @NotEmpty(message = "Debe proporcionar al menos un nombre de permiso.") // Descomentar
    private Set<String> nombresPermisos; // Lista de nombres de permisos a asignar

    public AsignarPermisosARolDTO() {
    }

    public AsignarPermisosARolDTO(Set<String> nombresPermisos) {
        this.nombresPermisos = nombresPermisos;
    }

    // Getter y Setter
    public Set<String> getNombresPermisos() {
        return nombresPermisos;
    }

    public void setNombresPermisos(Set<String> nombresPermisos) {
        this.nombresPermisos = nombresPermisos;
    }
}