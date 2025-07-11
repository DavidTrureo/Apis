package com.example.edutech.inscripcion.model;

import java.time.LocalDateTime;

import com.example.edutech.curso.model.ContenidoCurso;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "progreso_estudiante", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"inscripcion_id", "contenido_curso_id"})
})
public class ProgresoEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    @JsonIgnoreProperties({"usuario", "curso", "progreso", "hibernateLazyInitializer"}) 
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenido_curso_id", nullable = false)
    @JsonIgnoreProperties({"curso", "hibernateLazyInitializer"})
    private ContenidoCurso contenidoCurso;

    @Column(nullable = false)
    private boolean completado = false; 

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCompletado;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaUltimaActualizacion;

    public ProgresoEstudiante() {
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    public ProgresoEstudiante(Inscripcion inscripcion, ContenidoCurso contenidoCurso) {
        this();
        this.inscripcion = inscripcion;
        this.contenidoCurso = contenidoCurso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public ContenidoCurso getContenidoCurso() {
        return contenidoCurso;
    }

    public void setContenidoCurso(ContenidoCurso contenidoCurso) {
        this.contenidoCurso = contenidoCurso;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
        if (completado && this.fechaCompletado == null) {
            this.fechaCompletado = LocalDateTime.now();
        }
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    @Override
    public String toString() {
        return "ProgresoEstudiante{" +
                "id=" + id +
                ", inscripcionId=" + (inscripcion != null ? inscripcion.getId() : "null") +
                ", contenidoCursoId=" + (contenidoCurso != null ? contenidoCurso.getId() : "null") +
                ", completado=" + completado +
                ", fechaCompletado=" + fechaCompletado +
                '}';
    }
}