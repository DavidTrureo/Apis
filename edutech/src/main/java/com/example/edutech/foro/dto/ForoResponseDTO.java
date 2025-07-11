package com.example.edutech.foro.dto;

import java.time.LocalDateTime; // Usaremos un DTO simple de curso

import com.example.edutech.curso.dto.CursoDTO;

public class ForoResponseDTO {
    private Integer id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private CursoDTO curso; // Información resumida del curso
    private int totalHilos;

    public ForoResponseDTO() {
    }

    public ForoResponseDTO(Integer id, String titulo, String descripcion, LocalDateTime fechaCreacion, CursoDTO curso, int totalHilos) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.curso = curso;
        this.totalHilos = totalHilos;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public CursoDTO getCurso() { return curso; }
    public void setCurso(CursoDTO curso) { this.curso = curso; }
    public int getTotalHilos() { return totalHilos; }
    public void setTotalHilos(int totalHilos) { this.totalHilos = totalHilos; }
}