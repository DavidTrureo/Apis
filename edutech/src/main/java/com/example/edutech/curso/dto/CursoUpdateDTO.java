package com.example.edutech.curso.dto;

import jakarta.validation.constraints.Size;

public class CursoUpdateDTO {
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción detallada no puede exceder los 1000 caracteres.")
    private String descripcionDetallada;
    
    @Size(max = 100, message = "La categoría no puede exceder los 100 caracteres.")
    private String categoria;

    // El estado del curso se actualiza mediante un endpoint dedicado
    // private EstadoCursoEnum estadoCurso; 
    
    private String rutInstructor;

    public CursoUpdateDTO() {}

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionDetallada() { return descripcionDetallada; }
    public void setDescripcionDetallada(String descripcionDetallada) { this.descripcionDetallada = descripcionDetallada; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    // public EstadoCursoEnum getEstadoCurso() { return estadoCurso; }
    // public void setEstadoCurso(EstadoCursoEnum estadoCurso) { this.estadoCurso = estadoCurso; }
    public String getRutInstructor() { return rutInstructor; }
    public void setRutInstructor(String rutInstructor) { this.rutInstructor = rutInstructor; }
}