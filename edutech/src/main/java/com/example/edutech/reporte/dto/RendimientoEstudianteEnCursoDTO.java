package com.example.edutech.reporte.dto;

import java.math.BigDecimal;

public class RendimientoEstudianteEnCursoDTO {
    private String rutEstudiante;
    private String nombreEstudiante;
    private double porcentajeProgresoCurso;
    private BigDecimal promedioCalificaciones;
    private Integer evaluacionesEntregadasCorregidas;
    private Integer totalEvaluacionesCurso;

    public RendimientoEstudianteEnCursoDTO() {
    }

    public RendimientoEstudianteEnCursoDTO(String rutEstudiante, String nombreEstudiante, double porcentajeProgresoCurso, BigDecimal promedioCalificaciones, Integer evaluacionesEntregadasCorregidas, Integer totalEvaluacionesCurso) {
        this.rutEstudiante = rutEstudiante;
        this.nombreEstudiante = nombreEstudiante;
        this.porcentajeProgresoCurso = porcentajeProgresoCurso;
        this.promedioCalificaciones = promedioCalificaciones;
        this.evaluacionesEntregadasCorregidas = evaluacionesEntregadasCorregidas;
        this.totalEvaluacionesCurso = totalEvaluacionesCurso;
    }

    // Getters
    public String getRutEstudiante() {
        return rutEstudiante;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public double getPorcentajeProgresoCurso() {
        return porcentajeProgresoCurso;
    }

    public BigDecimal getPromedioCalificaciones() {
        return promedioCalificaciones;
    }

    public Integer getEvaluacionesEntregadasCorregidas() {
        return evaluacionesEntregadasCorregidas;
    }

    public Integer getTotalEvaluacionesCurso() {
        return totalEvaluacionesCurso;
    }

    // Setters (opcionales para DTOs de respuesta)
    public void setRutEstudiante(String rutEstudiante) {
        this.rutEstudiante = rutEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public void setPorcentajeProgresoCurso(double porcentajeProgresoCurso) {
        this.porcentajeProgresoCurso = porcentajeProgresoCurso;
    }

    public void setPromedioCalificaciones(BigDecimal promedioCalificaciones) {
        this.promedioCalificaciones = promedioCalificaciones;
    }

    public void setEvaluacionesEntregadasCorregidas(Integer evaluacionesEntregadasCorregidas) {
        this.evaluacionesEntregadasCorregidas = evaluacionesEntregadasCorregidas;
    }

    public void setTotalEvaluacionesCurso(Integer totalEvaluacionesCurso) {
        this.totalEvaluacionesCurso = totalEvaluacionesCurso;
    }
}