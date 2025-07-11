package com.example.edutech.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EvaluacionCreateDTO {

    @NotBlank(message = "El nombre de la evaluación es obligatorio.")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres.")
    private String nombre;

    @NotBlank(message = "El tipo de evaluación es obligatorio.")
    @Size(max = 50, message = "El tipo no puede exceder los 50 caracteres.")
    private String tipo;

    @NotBlank(message = "La sigla del curso es obligatoria.")
    private String cursoSigla;


    public EvaluacionCreateDTO() {}

    public EvaluacionCreateDTO(String nombre, String tipo, String cursoSigla) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.cursoSigla = cursoSigla;
    }

    // Getters y Setters (sin cambios)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
}