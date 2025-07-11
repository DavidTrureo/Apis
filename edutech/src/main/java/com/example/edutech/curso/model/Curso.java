package com.example.edutech.curso.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso")
public class Curso {
    @Id
    @Column(name = "sigla", nullable = false, unique = true, length = 10)
    private String sigla;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 1000)
    private String descripcionDetallada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoCursoEnum estadoCurso;

    @Column(length = 100)
    private String categoria; // Campo para la categoría del curso

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_rut")
    @JsonIgnoreProperties({"cursos", "password", "hibernateLazyInitializer", "roles", "inscripciones", "resenas", "pagos", "soportes", "progresos"})
    private Usuario instructor;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"curso", "hibernateLazyInitializer"})
    private List<ContenidoCurso> contenidos = new ArrayList<>();

    public Curso() {
        this.precioBase = BigDecimal.ZERO;
        this.estadoCurso = EstadoCursoEnum.BORRADOR;
    }

    // Constructor actualizado para incluir categoria
    public Curso(String sigla, String nombre, String descripcionDetallada, EstadoCursoEnum estadoCurso, String categoria, BigDecimal precioBase) {
        this.sigla = sigla;
        this.nombre = nombre;
        this.descripcionDetallada = descripcionDetallada;
        this.estadoCurso = (estadoCurso != null) ? estadoCurso : EstadoCursoEnum.BORRADOR;
        this.categoria = categoria;
        this.precioBase = (precioBase != null) ? precioBase : BigDecimal.ZERO;
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

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public Usuario getInstructor() { return instructor; }
    public void setInstructor(Usuario instructor) { this.instructor = instructor; }

    public List<ContenidoCurso> getContenidos() { return contenidos; }
    public void setContenidos(List<ContenidoCurso> contenidos) { this.contenidos = contenidos; }

    public int getTotalContenidos() {
        return this.contenidos != null ? this.contenidos.size() : 0;
    }

    @Override
    public String toString() {
        return "Curso{" +
                "sigla='" + sigla + '\'' +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", estadoCurso=" + (estadoCurso != null ? estadoCurso.getDescripcion() : "N/A") +
                ", precioBase=" + precioBase +
                ", instructor=" + (instructor != null ? instructor.getNombre() : "No asignado") +
                '}';
    }
}