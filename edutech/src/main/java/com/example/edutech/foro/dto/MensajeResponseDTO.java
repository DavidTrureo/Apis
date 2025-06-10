package com.example.edutech.foro.dto;

import java.time.LocalDateTime;

import com.example.edutech.usuario.dto.UsuarioDTO;

public class MensajeResponseDTO {
    private Integer id;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private UsuarioDTO autor;
    private Integer hiloId;

    public MensajeResponseDTO() {
    }

    public MensajeResponseDTO(Integer id, String contenido, LocalDateTime fechaCreacion, UsuarioDTO autor, Integer hiloId) {
        this.id = id;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
        this.autor = autor;
        this.hiloId = hiloId;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public UsuarioDTO getAutor() { return autor; }
    public void setAutor(UsuarioDTO autor) { this.autor = autor; }
    public Integer getHiloId() { return hiloId; }
    public void setHiloId(Integer hiloId) { this.hiloId = hiloId; }
}