package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RolUpdateDTO {
    @NotBlank(message = "La descripción no puede estar vacía al actualizar.") // Opcional si se permite descripción vacía
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String descripcion;

    public RolUpdateDTO() {}

    public RolUpdateDTO(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}