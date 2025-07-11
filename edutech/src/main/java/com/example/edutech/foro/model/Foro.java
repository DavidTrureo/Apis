package com.example.edutech.foro.model;

import com.example.edutech.curso.model.Curso;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "foro")
public class Foro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_sigla", referencedColumnName = "sigla", nullable = false, unique = true)
    @JsonIgnoreProperties({"foro", "contenidos", "instructor", "alumnos", "inscripciones", "resenas", "hibernateLazyInitializer", "precioBase", "estadoCurso", "totalContenidos"})
    private Curso curso;

    @OneToMany(mappedBy = "foro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("foro")
    @OrderBy("fechaUltimaActividad DESC")
    private List<HiloDiscusion> hilos = new ArrayList<>();

    public Foro() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Foro(String titulo, Curso curso) {
        this();
        this.titulo = titulo;
        this.curso = curso;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public List<HiloDiscusion> getHilos() { return hilos; }
    public void setHilos(List<HiloDiscusion> hilos) { this.hilos = hilos; }

    public void addHilo(HiloDiscusion hilo) {
        hilos.add(hilo);
        hilo.setForo(this);
    }

    public void removeHilo(HiloDiscusion hilo) {
        hilos.remove(hilo);
        hilo.setForo(null);
    }

    @Override
    public String toString() {
        return "Foro{" + "id=" + id + ", titulo='" + titulo + '\'' + ", cursoSigla=" + (curso != null ? curso.getSigla() : null) + '}';
    }
}