package com.example.edutech.curso.dto;

public class ContenidoCursoResponseDTO {
    private int id;
    private String titulo;
    private String tipo;
    private String url;
    private String cursoSigla; // Solo la sigla para evitar bucles o demasiada info
    private String cursoNombre;

    public ContenidoCursoResponseDTO() {
    }

    public ContenidoCursoResponseDTO(int id, String titulo, String tipo, String url, String cursoSigla, String cursoNombre) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.url = url;
        this.cursoSigla = cursoSigla;
        this.cursoNombre = cursoNombre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }
}