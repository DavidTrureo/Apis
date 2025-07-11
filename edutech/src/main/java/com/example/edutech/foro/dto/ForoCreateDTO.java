package com.example.edutech.foro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ForoCreateDTO {
    @NotBlank(message = "El título del foro es obligatorio.")
    @Size(max = 255, message = "El título no puede exceder los 255 caracteres.")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.")
    private String descripcion;

    @NotBlank(message = "La sigla del curso es obligatoria.")
    private String cursoSigla;

    public ForoCreateDTO() {
    }

    public ForoCreateDTO(String titulo, String descripcion, String cursoSigla) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.cursoSigla = cursoSigla;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }
}