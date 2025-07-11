package com.example.edutech.curso.dto;

import com.example.edutech.curso.model.EstadoCursoEnum;
import com.example.edutech.usuario.dto.UsuarioDTO;

public class CursoResponseDTO {
    private String sigla;
    private String nombre;
    private String descripcionDetallada;
    private EstadoCursoEnum estadoCurso;
    private String categoria;
    private UsuarioDTO instructor;

    public CursoResponseDTO() {}

    public CursoResponseDTO(String sigla, String nombre, String descripcionDetallada, EstadoCursoEnum estadoCurso, String categoria, UsuarioDTO instructor) {
        this.sigla = sigla;
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.estadoCurso = estadoCurso;
        this.categoria = categoria;
        this.instructor = instructor;
    }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionDetallada() { return descripcionDetallada; }
    public void setDescripcionDetallada(String descripcionDetallada) { this.descripcionDetallada = descripcionDetallada; }
    public EstadoCursoEnum getEstadoCurso() { return estadoCurso; }
    public void setEstadoCurso(EstadoCursoEnum estadoCurso) { this.estadoCurso = estadoCurso; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public UsuarioDTO getInstructor() { return instructor; }
    public void setInstructor(UsuarioDTO instructor) { this.instructor = instructor; }
}