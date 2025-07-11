package com.example.edutech.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EntregaEvaluacionCreateDTO {
    @NotBlank(message = "El RUT del estudiante es obligatorio.")
    private String estudianteRut;

    @NotNull(message = "El ID de la evaluación es obligatorio.")
    private Integer evaluacionId;

    @NotBlank(message = "Las respuestas (JSON) no pueden estar vacías.")
    private String respuestasJson; // Se asume que es un JSON con las respuestas del estudiante


    public EntregaEvaluacionCreateDTO() {}

    public EntregaEvaluacionCreateDTO(String estudianteRut, Integer evaluacionId, String respuestasJson) {
        this.estudianteRut = estudianteRut;
        this.evaluacionId = evaluacionId;
        this.respuestasJson = respuestasJson;
    }

    // Getters y Setters (sin cambios)
    public String getEstudianteRut() { return estudianteRut; }
    public void setEstudianteRut(String estudianteRut) { this.estudianteRut = estudianteRut; }
    public Integer getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Integer evaluacionId) { this.evaluacionId = evaluacionId; }
    public String getRespuestasJson() { return respuestasJson; }
    public void setRespuestasJson(String respuestasJson) { this.respuestasJson = respuestasJson; }
}