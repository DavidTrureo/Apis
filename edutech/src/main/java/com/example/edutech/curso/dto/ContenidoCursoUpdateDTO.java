package com.example.edutech.curso.dto;

import jakarta.validation.constraints.Size;

public class ContenidoCursoUpdateDTO {

    @Size(max = 255)
    private String titulo; // Opcional

    @Size(max = 50)
    private String tipo; // Opcional

    // @URL si aplica
    private String url; // Opcional

    private String cursoSigla; // Opcional, para cambiar la asociación del curso

    public ContenidoCursoUpdateDTO() {}

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
}