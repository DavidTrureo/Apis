package com.example.edutech.evaluacion.model;

import java.time.LocalDateTime;

import com.example.edutech.usuario.model.Usuario;
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

@Entity
@Table(name = "entrega_evaluacion")
public class EntregaEvaluacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false) 
    @JsonIgnoreProperties({"cursos", "password", "inscripciones", "hibernateLazyInitializer", "roles", "resenas", "pagos", "soportes", "progresos"})
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    @JsonIgnoreProperties({"preguntas", "curso", "hibernateLazyInitializer"})
    private Evaluacion evaluacion;

    @Column(columnDefinition = "TEXT")
    private String respuestasJson;

    @Column(nullable = false, length = 50)
    private String estado; 

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaEntrega;


    @Column
    private Double calificacion;

    @Column(columnDefinition = "TEXT")
    private String comentariosInstructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_corrector_id") 
    @JsonIgnoreProperties({"cursos", "password", "inscripciones", "hibernateLazyInitializer", "roles", "resenas", "pagos", "soportes", "progresos"})
    private Usuario instructorCorrector;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCorreccion;



    public EntregaEvaluacion() {
        this.fechaEntrega = LocalDateTime.now();
        this.estado = "Entregada";
    }


    public EntregaEvaluacion(Usuario estudiante, Evaluacion evaluacion, String respuestasJson) {
        this();
        this.estudiante = estudiante;
        this.evaluacion = evaluacion;
        this.respuestasJson = respuestasJson;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Usuario getEstudiante() { return estudiante; }
    public void setEstudiante(Usuario estudiante) { this.estudiante = estudiante; }
    public Evaluacion getEvaluacion() { return evaluacion; }
    public void setEvaluacion(Evaluacion evaluacion) { this.evaluacion = evaluacion; }
    public String getRespuestasJson() { return respuestasJson; }
    public void setRespuestasJson(String respuestasJson) { this.respuestasJson = respuestasJson; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }
    public String getComentariosInstructor() { return comentariosInstructor; }
    public void setComentariosInstructor(String comentariosInstructor) { this.comentariosInstructor = comentariosInstructor; }
    public Usuario getInstructorCorrector() { return instructorCorrector; }
    public void setInstructorCorrector(Usuario instructorCorrector) { this.instructorCorrector = instructorCorrector; }
    public LocalDateTime getFechaCorreccion() { return fechaCorreccion; }
    public void setFechaCorreccion(LocalDateTime fechaCorreccion) { this.fechaCorreccion = fechaCorreccion; }

    @Override
    public String toString() {
        return "EntregaEvaluacion{" +
                "id=" + id +
                ", estudiante=" + (estudiante != null ? estudiante.getRut() : "null") +
                ", evaluacion=" + (evaluacion != null ? evaluacion.getId() : "null") +
                ", estado='" + estado + '\'' +
                ", fechaEntrega=" + fechaEntrega +
                ", calificacion=" + calificacion +
                '}';
    }
}