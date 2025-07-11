package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RolCreateDTO {
    @NotBlank(message = "El nombre del rol es obligatorio.")
    @Size(max = 50, message = "El nombre del rol no puede exceder los 50 caracteres.")
    private String nombre;

    @NotBlank(message = "La descripción del rol es obligatoria.")
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String descripcion;

    public RolCreateDTO() {}

    public RolCreateDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}