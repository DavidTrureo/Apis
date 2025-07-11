package com.example.edutech.curso.dto;

public class CursoDTO {
    private String sigla;
    private String nombre;

    // Constructor para que Spring Data JPA pueda instanciarlo en proyecciones
    public CursoDTO(String sigla, String nombre) {
        this.sigla = sigla;
        this.nombre = nombre;
    }

    // Getters y Setters
    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}