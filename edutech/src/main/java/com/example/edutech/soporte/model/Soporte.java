package com.example.edutech.soporte.model;

import java.time.LocalDateTime;

import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_soporte") //Tabla para tickets de soporte
public class Soporte { // Considerar renombrar la clase a TicketSoporte para mayor claridad

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String titulo; // Añadido para un resumen del problema

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoTicketSoporte estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PrioridadTicketSoporte prioridad;

    @Column(length = 100)
    private String categoria; // Ej: "Problema Técnico", "Consulta de Curso", "Facturación"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_reporta_rut", referencedColumnName = "rut", nullable = false)
    @JsonIgnoreProperties({"cursos", "password", "hibernateLazyInitializer", "roles", "inscripciones", "resenas", "pagos", "soportes", "progresos"})
    private Usuario usuarioReporta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_asignado_rut", referencedColumnName = "rut") // Nullable
    @JsonIgnoreProperties({"cursos", "password", "hibernateLazyInitializer", "roles", "inscripciones", "resenas", "pagos", "soportes", "progresos"})
    private Usuario agenteAsignado;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaUltimaActualizacion;

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaResolucion; // Cuando se marca como RESUELTO

    @Column(columnDefinition = "TEXT")
    private String solucionAplicada;

    public Soporte() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
        this.estado = EstadoTicketSoporte.ABIERTO; // Estado inicial por defecto
        this.prioridad = PrioridadTicketSoporte.MEDIA; // Prioridad por defecto
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
    public Usuario getUsuarioReporta() { return usuarioReporta; }
    public void setUsuarioReporta(Usuario usuarioReporta) { this.usuarioReporta = usuarioReporta; }
    public Usuario getAgenteAsignado() { return agenteAsignado; }
    public void setAgenteAsignado(Usuario agenteAsignado) { this.agenteAsignado = agenteAsignado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) { this.fechaUltimaActualizacion = fechaUltimaActualizacion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    public String getSolucionAplicada() { return solucionAplicada; }
    public void setSolucionAplicada(String solucionAplicada) { this.solucionAplicada = solucionAplicada; }

    @Override
    public String toString() {
        return "Soporte{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", estado=" + estado +
                ", usuarioReporta=" + (usuarioReporta != null ? usuarioReporta.getRut() : null) +
                ", agenteAsignado=" + (agenteAsignado != null ? agenteAsignado.getRut() : null) +
                '}';
    }
}