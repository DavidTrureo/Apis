package com.example.edutech.inscripcion.dto;

import jakarta.validation.constraints.NotNull;

public class MarcarContenidoCompletadoDTO {
    @NotNull(message = "El ID de la inscripción es obligatorio.")
    private Integer inscripcionId;

    @NotNull(message = "El ID del contenido del curso es obligatorio.")
    private Integer contenidoCursoId;

    @NotNull(message = "El estado 'completado' (true/false) es obligatorio.")
    private Boolean completado;

    public MarcarContenidoCompletadoDTO() {}

    public MarcarContenidoCompletadoDTO(Integer inscripcionId, Integer contenidoCursoId, Boolean completado) {
        this.inscripcionId = inscripcionId;
        this.contenidoCursoId = contenidoCursoId;
        this.completado = completado;
    }

    public Integer getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Integer inscripcionId) { this.inscripcionId = inscripcionId; }
    public Integer getContenidoCursoId() { return contenidoCursoId; }
    public void setContenidoCursoId(Integer contenidoCursoId) { this.contenidoCursoId = contenidoCursoId; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
}