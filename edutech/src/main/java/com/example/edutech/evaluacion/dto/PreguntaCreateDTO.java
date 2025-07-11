package com.example.edutech.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PreguntaCreateDTO {

    @NotBlank(message = "El enunciado de la pregunta es obligatorio.")
    private String enunciado;

    @NotNull(message = "El ID de la evaluación es obligatorio.")
    private Integer evaluacionId;

    public PreguntaCreateDTO() {}

    public PreguntaCreateDTO(String enunciado, Integer evaluacionId) {
        this.enunciado = enunciado;
        this.evaluacionId = evaluacionId;
    }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public Integer getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Integer evaluacionId) { this.evaluacionId = evaluacionId; }
}