package com.example.edutech.curso.dto;
//REALIZADO POR: Maverick Valdes
import java.time.LocalDateTime;

public class ResenaCalificacionResponseDTO {
    private Integer id;
    private String usuarioNombre;
    private String cursoNombre;
    private String cursoSigla;
    private int puntuacion;
    private String comentario;
    private LocalDateTime fechaResena;
    private boolean esAprobada;

    public ResenaCalificacionResponseDTO() {}

    public ResenaCalificacionResponseDTO(Integer id, String usuarioNombre, String cursoNombre, String cursoSigla, int puntuacion, String comentario, LocalDateTime fechaResena, boolean esAprobada) {
        this.id = id;
        this.usuarioNombre = usuarioNombre;
        this.cursoNombre = cursoNombre;
        this.cursoSigla = cursoSigla;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fechaResena = fechaResena;
        this.esAprobada = esAprobada;
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String cursoNombre) { this.cursoNombre = cursoNombre; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
    public int getPuntuacion() { return puntuacion; }
    public void setPuntuacion(int puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getFechaResena() { return fechaResena; }
    public void setFechaResena(LocalDateTime fechaResena) { this.fechaResena = fechaResena; }
    public boolean isEsAprobada() { return esAprobada; }
    public void setEsAprobada(boolean esAprobada) { this.esAprobada = esAprobada; }
}