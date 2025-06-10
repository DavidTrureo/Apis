package com.example.edutech.reporte.dto;

import java.math.BigDecimal;
import java.util.List;

public class ReporteInscripcionesCursoDataDTO {
    private String cursoSigla;
    private String cursoNombre;
    private long totalInscritos;
    private BigDecimal ingresosTotalesConfirmados;
    private List<EstudianteInscritoDTO> estudiantesInscritos;


    public ReporteInscripcionesCursoDataDTO() {
    }

    public ReporteInscripcionesCursoDataDTO(String cursoSigla, String cursoNombre, long totalInscritos, BigDecimal ingresosTotalesConfirmados, List<EstudianteInscritoDTO> estudiantesInscritos) {
        this.cursoSigla = cursoSigla;
        this.cursoNombre = cursoNombre;
        this.totalInscritos = totalInscritos;
        this.ingresosTotalesConfirmados = ingresosTotalesConfirmados;
        this.estudiantesInscritos = estudiantesInscritos;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public long getTotalInscritos() {
        return totalInscritos;
    }

    public BigDecimal getIngresosTotalesConfirmados() {
        return ingresosTotalesConfirmados;
    }

    public List<EstudianteInscritoDTO> getEstudiantesInscritos() {
        return estudiantesInscritos;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public void setTotalInscritos(long totalInscritos) {
        this.totalInscritos = totalInscritos;
    }

    public void setIngresosTotalesConfirmados(BigDecimal ingresosTotalesConfirmados) {
        this.ingresosTotalesConfirmados = ingresosTotalesConfirmados;
    }

    public void setEstudiantesInscritos(List<EstudianteInscritoDTO> estudiantesInscritos) {
        this.estudiantesInscritos = estudiantesInscritos;
    }
}