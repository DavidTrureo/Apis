package com.example.edutech.curso.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ContenidoCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo; 

    @Column(nullable = false)
    private String url;  

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties({"contenidos", "instructor", "alumnos", "inscripciones", "hibernateLazyInitializer"}) 
    private Curso curso;


    public ContenidoCurso() {
    }


    public ContenidoCurso(String titulo, String tipo, String url, Curso curso) {
        this.titulo = titulo;
        this.tipo = tipo;
        this.url = url;
        this.curso = curso;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "ContenidoCurso{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", url='" + url + '\'' +
                ", cursoId=" + (curso != null ? curso.getSigla() : "null") + 
                '}';
    }
}