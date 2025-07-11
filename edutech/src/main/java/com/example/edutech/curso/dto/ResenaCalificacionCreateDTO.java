package com.example.edutech.curso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResenaCalificacionCreateDTO {

    @NotBlank(message = "El RUT del usuario es obligatorio.")
    private String usuarioRut;

    @NotBlank(message = "La sigla del curso es obligatoria.")
    private String cursoSigla;

    @NotNull(message = "La puntuación es obligatoria.")
    @Min(value = 1, message = "La puntuación mínima es 1.")
    @Max(value = 5, message = "La puntuación máxima es 5.")
    private Integer puntuacion;

    @Size(max = 1000, message = "El comentario no puede exceder los 1000 caracteres.")
    private String comentario;


    public ResenaCalificacionCreateDTO() {
    }


    public ResenaCalificacionCreateDTO(String usuarioRut, String cursoSigla, Integer puntuacion, String comentario) {
        this.usuarioRut = usuarioRut;
        this.cursoSigla = cursoSigla;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }


    public String getUsuarioRut() { return usuarioRut; }
    public void setUsuarioRut(String usuarioRut) { this.usuarioRut = usuarioRut; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}