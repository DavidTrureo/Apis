package com.example.edutech.inscripcion.dto;
//REALIZADO POR: Cristóbal Mira
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InscripcionResponseDTO {
    private Integer id;
    private String usuarioRut;
    private String usuarioNombre;
    private String cursoSigla;
    private String cursoNombre;
    private LocalDateTime fechaInscripcion;
    private BigDecimal precioPagado;
    private String estadoInscripcion;
    private String estadoPago;

    public InscripcionResponseDTO() {}

    public InscripcionResponseDTO(Integer id, String usuarioRut, String usuarioNombre, String cursoSigla, String cursoNombre, LocalDateTime fechaInscripcion, BigDecimal precioPagado, String codigoCuponAplicado, String estadoInscripcion, String estadoPago) {
        this.id = id;
        this.usuarioRut = usuarioRut;
        this.usuarioNombre = usuarioNombre;
        this.cursoSigla = cursoSigla;
        this.cursoNombre = cursoNombre;
        this.fechaInscripcion = fechaInscripcion;
        this.precioPagado = precioPagado;
        this.estadoInscripcion = estadoInscripcion;
        this.estadoPago = estadoPago;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsuarioRut() { return usuarioRut; }
    public void setUsuarioRut(String usuarioRut) { this.usuarioRut = usuarioRut; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }
    public String getEstadoInscripcion() { return estadoInscripcion; }
    public void setEstadoInscripcion(String estadoInscripcion) { this.estadoInscripcion = estadoInscripcion; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}