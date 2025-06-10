package com.example.edutech.reporte.dto;

import java.time.LocalDate;

public class ReporteDTO {
    private String tipo;
    private LocalDate fechaGeneracion;
    private String contenido;

    public ReporteDTO(String tipo, LocalDate fechaGeneracion, String contenido) {
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

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

}
