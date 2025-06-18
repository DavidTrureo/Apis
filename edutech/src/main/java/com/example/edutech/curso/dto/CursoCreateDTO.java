package com.example.edutech.curso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CursoCreateDTO {

    @NotBlank(message = "La sigla del curso es obligatoria.")
    @Size(max = 10, message = "La sigla no puede exceder los 10 caracteres.")
    private String sigla;

    @NotBlank(message = "El nombre del curso es obligatorio.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción detallada no puede exceder los 1000 caracteres.")
    private String descripcionDetallada;

    @Size(max = 100, message = "La categoría no puede exceder los 100 caracteres.")
    private String categoria;

    private String rutInstructor;

    public CursoCreateDTO() {}

    // Getters y Setters
    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionDetallada() { return descripcionDetallada; }
    public void setDescripcionDetallada(String descripcionDetallada) { this.descripcionDetallada = descripcionDetallada; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getRutInstructor() { return rutInstructor; }
    public void setRutInstructor(String rutInstructor) { this.rutInstructor = rutInstructor; }
}