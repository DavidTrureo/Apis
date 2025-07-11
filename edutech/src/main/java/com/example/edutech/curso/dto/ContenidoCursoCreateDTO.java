package com.example.edutech.curso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContenidoCursoCreateDTO {

    @NotBlank(message = "El título del contenido es obligatorio.")
    @Size(max = 255)
    private String titulo;

    @NotBlank(message = "El tipo de contenido es obligatorio.")
    @Size(max = 50)
    private String tipo;

    @NotBlank(message = "La URL del contenido es obligatoria.")
    // Podría añadir @URL si es una URL web, o una validación de formato específica
    private String url;

    @NotBlank(message = "La sigla del curso asociado es obligatoria.")
    private String cursoSigla; // En lugar de un objeto Curso anidado

    public ContenidoCursoCreateDTO() {}


    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
}