package com.example.edutech.reporte.dto;

import java.time.LocalDate;

public class ReporteDTO {
    private String tipo;
    private LocalDate fechaGeneracion;
    private Object contenido;


    public ReporteDTO() {
    }

    public ReporteDTO(String tipo, LocalDate fechaGeneracion, Object contenido) {
        this.tipo = tipo;
        this.fechaGeneracion = fechaGeneracion;
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public Object getContenido() {
        return contenido;
    }

    public void setContenido(Object contenido) {
        this.contenido = contenido;
    }
}