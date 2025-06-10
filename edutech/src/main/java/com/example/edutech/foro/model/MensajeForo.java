package com.example.edutech.foro.model;

import java.time.LocalDateTime;

import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensaje_foro")
public class MensajeForo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hilo_discusion_id", nullable = false)
    @JsonIgnoreProperties({"mensajes", "foro", "autor", "hibernateLazyInitializer"})
    private HiloDiscusion hilo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_rut", referencedColumnName = "rut", nullable = false)
    @JsonIgnoreProperties({"cursos", "password", "inscripciones", "roles", "resenas", "pagos", "soportes", "progresos", "hibernateLazyInitializer"})
    private Usuario autor;

    public MensajeForo() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public MensajeForo(String contenido, HiloDiscusion hilo, Usuario autor) {
        this();
        this.contenido = contenido;
        this.hilo = hilo;
        this.autor = autor;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public HiloDiscusion getHilo() { return hilo; }
    public void setHilo(HiloDiscusion hilo) { this.hilo = hilo; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    @Override
    public String toString() {
        return "MensajeForo{" + "id=" + id + ", autor=" + (autor != null ? autor.getRut() : null) + ", hiloId=" + (hilo != null ? hilo.getId() : null) + '}';
    }
}