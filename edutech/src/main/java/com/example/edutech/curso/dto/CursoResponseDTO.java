package com.example.edutech.curso.dto;
//REALIZADO POR: Maverick Valdes
import com.example.edutech.curso.model.EstadoCursoEnum;
import com.example.edutech.usuario.dto.UsuarioDTO; // IMPORTAR

public class CursoResponseDTO {
    private String sigla;
    private String nombre;
    private String descripcionDetallada;
    private EstadoCursoEnum estadoCurso; // CAMBIO DE String A EstadoCursoEnum
    private UsuarioDTO instructor;


    public CursoResponseDTO(String sigla, String nombre, String descripcionDetallada, EstadoCursoEnum estadoCurso, UsuarioDTO instructor) { // CAMBIO EN CONSTRUCTOR
        this.sigla = sigla;
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.estadoCurso = estadoCurso;
        this.instructor = instructor;
    }


    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcionDetallada() { return descripcionDetallada; }
    public void setDescripcionDetallada(String descripcionDetallada) { this.descripcionDetallada = descripcionDetallada; }

    public EstadoCursoEnum getEstadoCurso() { return estadoCurso; } // CAMBIO DE TIPO DE RETORNO
    public void setEstadoCurso(EstadoCursoEnum estadoCurso) { this.estadoCurso = estadoCurso; } // CAMBIO DE TIPO DE PARÁMETRO

    public UsuarioDTO getInstructor() { return instructor; }
    public void setInstructor(UsuarioDTO instructor) { this.instructor = instructor; }
}