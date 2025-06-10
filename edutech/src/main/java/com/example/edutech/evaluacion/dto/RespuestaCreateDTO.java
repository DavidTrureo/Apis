package com.example.edutech.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RespuestaCreateDTO {

    @NotBlank(message = "El contenido de la respuesta es obligatorio.")
    private String contenido;

    @NotNull(message = "Debe indicar si la respuesta es correcta (true/false).")
    private Boolean esCorrecta;

    @NotNull(message = "El ID de la pregunta es obligatorio.")
    private Integer preguntaId;

    public RespuestaCreateDTO() {}

    public RespuestaCreateDTO(String contenido, Boolean esCorrecta, Integer preguntaId) {
        this.contenido = contenido;
        this.esCorrecta = esCorrecta;
        this.preguntaId = preguntaId;
    }

    // Getters y Setters (sin cambios)
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public Boolean getEsCorrecta() { return esCorrecta; }
    public void setEsCorrecta(Boolean esCorrecta) { this.esCorrecta = esCorrecta; }
    public Integer getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Integer preguntaId) { this.preguntaId = preguntaId; }
}