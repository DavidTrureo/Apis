package com.example.edutech.reporte.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EstudianteInscritoDTO {
    private String rut;
    private String nombre;
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInscripcion;

    private BigDecimal precioPagado;
    private String codigoCuponAplicado; // Puede ser null
    private String estadoPago;

    public EstudianteInscritoDTO() {
    }

    public EstudianteInscritoDTO(String rut, String nombre, String email, LocalDateTime fechaInscripcion, BigDecimal precioPagado, String codigoCuponAplicado, String estadoPago) {
        this.rut = rut;
        this.nombre = nombre;
        this.email = email;
        this.fechaInscripcion = fechaInscripcion;
        this.precioPagado = precioPagado;
        this.codigoCuponAplicado = codigoCuponAplicado;
        this.estadoPago = estadoPago;
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public BigDecimal getPrecioPagado() {
        return precioPagado;
    }

    public String getCodigoCuponAplicado() {
        return codigoCuponAplicado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public void setPrecioPagado(BigDecimal precioPagado) {
        this.precioPagado = precioPagado;
    }

    public void setCodigoCuponAplicado(String codigoCuponAplicado) {
        this.codigoCuponAplicado = codigoCuponAplicado;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}