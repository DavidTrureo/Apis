package com.example.edutech.evaluacion.dto;

import java.time.LocalDateTime; // Para el estudiante y el instructor

import com.example.edutech.usuario.dto.UsuarioDTO;

public class EntregaEvaluacionResponseDTO {
    private Integer id;
    private UsuarioDTO estudiante;
    private Integer evaluacionId;

    private String respuestasJson;
    private String estado;
    private LocalDateTime fechaEntrega;
    private Double calificacion;
    private String comentariosInstructor;
    private UsuarioDTO instructorCorrector;
    private LocalDateTime fechaCorreccion;

    public EntregaEvaluacionResponseDTO() {}

    public EntregaEvaluacionResponseDTO(Integer id, UsuarioDTO estudiante, Integer evaluacionId, String respuestasJson, String estado, LocalDateTime fechaEntrega, Double calificacion, String comentariosInstructor, UsuarioDTO instructorCorrector, LocalDateTime fechaCorreccion) {
        this.id = id;
        this.estudiante = estudiante;
        this.evaluacionId = evaluacionId;
        this.respuestasJson = respuestasJson;
        this.estado = estado;
        this.fechaEntrega = fechaEntrega;
        this.calificacion = calificacion;
        this.comentariosInstructor = comentariosInstructor;
        this.instructorCorrector = instructorCorrector;
        this.fechaCorreccion = fechaCorreccion;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public UsuarioDTO getEstudiante() { return estudiante; }
    public void setEstudiante(UsuarioDTO estudiante) { this.estudiante = estudiante; }
    public Integer getEvaluacionId() { return evaluacionId; }
    public void setEvaluacionId(Integer evaluacionId) { this.evaluacionId = evaluacionId; }
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
    public UsuarioDTO getInstructorCorrector() { return instructorCorrector; }
    public void setInstructorCorrector(UsuarioDTO instructorCorrector) { this.instructorCorrector = instructorCorrector; }
    public LocalDateTime getFechaCorreccion() { return fechaCorreccion; }
    public void setFechaCorreccion(LocalDateTime fechaCorreccion) { this.fechaCorreccion = fechaCorreccion; }
}