package com.example.edutech.soporte.dto;

import java.time.LocalDateTime;

import com.example.edutech.soporte.model.EstadoTicketSoporte;
import com.example.edutech.soporte.model.PrioridadTicketSoporte; // Un DTO simple para el usuario
import com.example.edutech.usuario.dto.UsuarioDTO;

public class SoporteResponseDTO {
    private Integer id;
    private String titulo;
    private String descripcion;
    private EstadoTicketSoporte estado;
    private PrioridadTicketSoporte prioridad;
    private String categoria;
    private UsuarioDTO usuarioReporta; // Mostrar info del usuario que reportó
    private UsuarioDTO agenteAsignado; // Mostrar info del agente asignado (puede ser null)
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActualizacion;
    private LocalDateTime fechaResolucion;
    private String solucionAplicada;

    public SoporteResponseDTO() {}

    // Constructor completo (o usa un builder/mapper)
    public SoporteResponseDTO(Integer id, String titulo, String descripcion, EstadoTicketSoporte estado, PrioridadTicketSoporte prioridad, String categoria, UsuarioDTO usuarioReporta, UsuarioDTO agenteAsignado, LocalDateTime fechaCreacion, LocalDateTime fechaUltimaActualizacion, LocalDateTime fechaResolucion, String solucionAplicada) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.categoria = categoria;
        this.usuarioReporta = usuarioReporta;
        this.agenteAsignado = agenteAsignado;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
        this.fechaResolucion = fechaResolucion;
        this.solucionAplicada = solucionAplicada;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public EstadoTicketSoporte getEstado() { return estado; }
    public void setEstado(EstadoTicketSoporte estado) { this.estado = estado; }
    public PrioridadTicketSoporte getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadTicketSoporte prioridad) { this.prioridad = prioridad; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public UsuarioDTO getUsuarioReporta() { return usuarioReporta; }
    public void setUsuarioReporta(UsuarioDTO usuarioReporta) { this.usuarioReporta = usuarioReporta; }
    public UsuarioDTO getAgenteAsignado() { return agenteAsignado; }
    public void setAgenteAsignado(UsuarioDTO agenteAsignado) { this.agenteAsignado = agenteAsignado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) { this.fechaUltimaActualizacion = fechaUltimaActualizacion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    public String getSolucionAplicada() { return solucionAplicada; }
    public void setSolucionAplicada(String solucionAplicada) { this.solucionAplicada = solucionAplicada; }
}