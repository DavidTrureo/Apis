package com.example.edutech.evaluacion.model;
//REALIZADO POR: Cristóbal Mira
import java.util.ArrayList;
import java.util.List;

import com.example.edutech.curso.model.Curso;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "evaluacion")
public class Evaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(length = 50)
    private String tipo; 



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_sigla", nullable = false) 
    @JsonIgnoreProperties({"contenidos", "instructor", "alumnos", "inscripciones", "hibernateLazyInitializer"})
    private Curso curso;

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("evaluacion") 
    private List<Pregunta> preguntas = new ArrayList<>();

    public Evaluacion() {

    }


    public Evaluacion(String nombre, String tipo, Curso curso) {
        this();
        this.nombre = nombre;
        this.tipo = tipo;
        this.curso = curso;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

  
    @Override
    public String toString() {
        return "Evaluacion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", cursoSigla=" + (curso != null ? curso.getSigla() : "null") +
                '}';
    }
}