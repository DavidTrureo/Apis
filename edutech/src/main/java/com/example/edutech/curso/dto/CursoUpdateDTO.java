package com.example.edutech.curso.dto;
//REALIZADO POR: Maverick Valdes
import jakarta.validation.constraints.Size; // IMPORTAR

public class CursoUpdateDTO {
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción detallada no puede exceder los 1000 caracteres.")
    private String descripcionDetallada;

    // El estado del curso se actualiza a través de un endpoint dedicado
    // por lo que no es necesario validarlo aquí directamente para la actualización general.
    // Si se quisiera permitir en este DTO, se añadiría:
    // private EstadoCursoEnum estadoCurso;
    private String rutInstructor;


    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionDetallada() { return descripcionDetallada; }
    public void setDescripcionDetallada(String descripcionDetallada) { this.descripcionDetallada = descripcionDetallada; }

    // Si se añade estadoCurso al DTO:
    // public EstadoCursoEnum getEstadoCurso() { return estadoCurso; }
    // public void setEstadoCurso(EstadoCursoEnum estadoCurso) { this.estadoCurso = estadoCurso; }

    public String getRutInstructor() { return rutInstructor; }
    public void setRutInstructor(String rutInstructor) { this.rutInstructor = rutInstructor; }
}