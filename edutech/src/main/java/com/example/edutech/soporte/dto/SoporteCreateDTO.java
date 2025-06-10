package com.example.edutech.soporte.dto;

import jakarta.validation.constraints.NotBlank; // Importar
import jakarta.validation.constraints.Size;   // Importar

public class SoporteCreateDTO {
    @NotBlank(message = "El título es obligatorio.") // Descomentar
    @Size(max = 255, message = "El título no puede exceder los 255 caracteres.")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria.") // Descomentar
    private String descripcion;

    @NotBlank(message = "El RUT del usuario que reporta es obligatorio.") // Descomentar
    private String usuarioReportaRut;

    @Size(max = 100, message = "La categoría no puede exceder los 100 caracteres.")
    private String categoria; // Opcional al crear

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUsuarioReportaRut() { return usuarioReportaRut; }
    public void setUsuarioReportaRut(String usuarioReportaRut) { this.usuarioReportaRut = usuarioReportaRut; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}