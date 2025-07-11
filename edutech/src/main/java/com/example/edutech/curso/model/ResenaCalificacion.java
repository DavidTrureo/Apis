package com.example.edutech.curso.model;
//REALIZADO POR: Maverick Valdes
import java.time.LocalDateTime;

import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "resena_calificacion", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "curso_id"})
})
public class ResenaCalificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"cursos", "password", "inscripciones", "hibernateLazyInitializer"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties({"instructor", "contenidos", "alumnos", "inscripciones", "hibernateLazyInitializer"})
    private Curso curso;

    @Column(nullable = false)
    private int puntuacion;  

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaResena;

    @Column(nullable = false)
    private boolean esAprobada = false; 


    public ResenaCalificacion() {
        this.fechaResena = LocalDateTime.now(); 
    }


    public ResenaCalificacion(Usuario usuario, Curso curso, int puntuacion, String comentario) {
        this(); 
        this.usuario = usuario;
        this.curso = curso;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaResena() {
        return fechaResena;
    }

    public void setFechaResena(LocalDateTime fechaResena) {
        this.fechaResena = fechaResena;
    }

    public boolean isEsAprobada() {
        return esAprobada;
    }

    public void setEsAprobada(boolean esAprobada) {
        this.esAprobada = esAprobada;
    }

    @Override
    public String toString() {
        return "ResenaCalificacion{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getRut() : "N/A") +
                ", curso=" + (curso != null ? curso.getSigla() : "N/A") +
                ", puntuacion=" + puntuacion +
                ", comentario='" + (comentario != null ? comentario.substring(0, Math.min(comentario.length(), 20)) + "..." : "N/A") + '\'' +
                ", fechaResena=" + fechaResena +
                ", esAprobada=" + esAprobada +
                '}';
    }
}