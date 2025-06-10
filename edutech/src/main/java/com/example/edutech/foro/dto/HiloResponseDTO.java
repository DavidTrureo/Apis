package com.example.edutech.foro.dto;

import java.time.LocalDateTime; // Usaremos un DTO simple de usuario

import com.example.edutech.usuario.dto.UsuarioDTO;

public class HiloResponseDTO {
    private Integer id;
    private String titulo;
    private String contenidoInicial;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimaActividad;
    private UsuarioDTO autor;
    private Integer foroId;
    private int totalMensajes; // Excluyendo el contenido inicial del hilo

    public HiloResponseDTO() {
    }

    public HiloResponseDTO(Integer id, String titulo, String contenidoInicial, LocalDateTime fechaCreacion, LocalDateTime fechaUltimaActividad, UsuarioDTO autor, Integer foroId, int totalMensajes) {
        this.id = id;
        this.titulo = titulo;
        this.contenidoInicial = contenidoInicial;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaActividad = fechaUltimaActividad;
        this.autor = autor;
        this.foroId = foroId;
        this.totalMensajes = totalMensajes;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getContenidoInicial() { return contenidoInicial; }
    public void setContenidoInicial(String contenidoInicial) { this.contenidoInicial = contenidoInicial; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaUltimaActividad() { return fechaUltimaActividad; }
    public void setFechaUltimaActividad(LocalDateTime fechaUltimaActividad) { this.fechaUltimaActividad = fechaUltimaActividad; }
    public UsuarioDTO getAutor() { return autor; }
    public void setAutor(UsuarioDTO autor) { this.autor = autor; }
    public Integer getForoId() { return foroId; }
    public void setForoId(Integer foroId) { this.foroId = foroId; }
    public int getTotalMensajes() { return totalMensajes; }
    public void setTotalMensajes(int totalMensajes) { this.totalMensajes = totalMensajes; }
}