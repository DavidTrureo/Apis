package com.example.edutech.foro.model;

import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hilo_discusion")
public class HiloDiscusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contenidoInicial;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaUltimaActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foro_id", nullable = false)
    @JsonIgnoreProperties({"hilos", "curso", "hibernateLazyInitializer"})
    private Foro foro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_rut", referencedColumnName = "rut", nullable = false)
    @JsonIgnoreProperties({"cursos", "password", "inscripciones", "roles", "resenas", "pagos", "soportes", "progresos", "hibernateLazyInitializer"})
    private Usuario autor;

    @OneToMany(mappedBy = "hilo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("hilo")
    @OrderBy("fechaCreacion ASC")
    private List<MensajeForo> mensajes = new ArrayList<>();

    public HiloDiscusion() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaUltimaActividad = now;
    }

    public HiloDiscusion(String titulo, String contenidoInicial, Foro foro, Usuario autor) {
        this();
        this.titulo = titulo;
        this.contenidoInicial = contenidoInicial;
        this.foro = foro;
        this.autor = autor;
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
    public Foro getForo() { return foro; }
    public void setForo(Foro foro) { this.foro = foro; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public List<MensajeForo> getMensajes() { return mensajes; }
    public void setMensajes(List<MensajeForo> mensajes) { this.mensajes = mensajes; }

    public void addMensaje(MensajeForo mensaje) {
        mensajes.add(mensaje);
        mensaje.setHilo(this);
        this.fechaUltimaActividad = mensaje.getFechaCreacion();
    }

    public void removeMensaje(MensajeForo mensaje) {
        mensajes.remove(mensaje);
        mensaje.setHilo(null);
    }

    @Override
    public String toString() {
        return "HiloDiscusion{" + "id=" + id + ", titulo='" + titulo + '\'' + ", foroId=" + (foro != null ? foro.getId() : null) + '}';
    }
}