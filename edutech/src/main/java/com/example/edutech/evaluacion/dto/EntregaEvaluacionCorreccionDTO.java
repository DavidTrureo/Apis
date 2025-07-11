package com.example.edutech.evaluacion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EntregaEvaluacionCorreccionDTO {

    @NotNull(message = "La calificación es obligatoria.")
    @DecimalMin(value = "0.0", message = "La calificación mínima es 0.0.")
    @DecimalMax(value = "100.0", message = "La calificación máxima es 100.0 (o ajusta según tu escala).") // Ajusta el máximo si es necesario
    private Double calificacion;

    private String comentariosInstructor; // Opcional

    @NotBlank(message = "El RUT del instructor que corrige es obligatorio.")
    private String rutInstructorCorrector;

    // Getters y Setters (sin cambios)
    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }
    public String getComentariosInstructor() { return comentariosInstructor; }
    public void setComentariosInstructor(String comentariosInstructor) { this.comentariosInstructor = comentariosInstructor; }
    public String getRutInstructorCorrector() { return rutInstructorCorrector; }
    public void setRutInstructorCorrector(String rutInstructorCorrector) { this.rutInstructorCorrector = rutInstructorCorrector; }
}