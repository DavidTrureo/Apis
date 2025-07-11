package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PermisoCreateDTO {

    @NotBlank(message = "El nombre del permiso es obligatorio") // Descomentar
    @Size(max = 100, message = "El nombre del permiso no puede exceder los 100 caracteres.")
    private String nombrePermiso; // Ej: "CURSO_CREATE", "USUARIO_VIEW_ALL"

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String descripcion;

    // Getters y Setters
    public String getNombrePermiso() {
        return nombrePermiso;
    }

    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}