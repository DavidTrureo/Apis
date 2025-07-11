package com.example.edutech.reporte.dto;

import java.util.List;

public class ReporteRendimientoCursoDataDTO {
    private String cursoSigla;
    private String cursoNombre;
    private long totalEstudiantesConProgreso; // Estudiantes que tienen al menos una inscripción
    private List<RendimientoEstudianteEnCursoDTO> rendimientoEstudiantes;

    public ReporteRendimientoCursoDataDTO() {
    }

    public ReporteRendimientoCursoDataDTO(String cursoSigla, String cursoNombre, long totalEstudiantesConProgreso, List<RendimientoEstudianteEnCursoDTO> rendimientoEstudiantes) {
        this.cursoSigla = cursoSigla;
        this.cursoNombre = cursoNombre;
        this.totalEstudiantesConProgreso = totalEstudiantesConProgreso;
        this.rendimientoEstudiantes = rendimientoEstudiantes;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public long getTotalEstudiantesConProgreso() {
        return totalEstudiantesConProgreso;
    }

    public List<RendimientoEstudianteEnCursoDTO> getRendimientoEstudiantes() {
        return rendimientoEstudiantes;
    }

    // Setters (opcionales)
    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public void setTotalEstudiantesConProgreso(long totalEstudiantesConProgreso) {
        this.totalEstudiantesConProgreso = totalEstudiantesConProgreso;
    }

    public void setRendimientoEstudiantes(List<RendimientoEstudianteEnCursoDTO> rendimientoEstudiantes) {
        this.rendimientoEstudiantes = rendimientoEstudiantes;
    }
}