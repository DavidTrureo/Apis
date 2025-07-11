package com.example.edutech.evaluacion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "respuesta")
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private boolean esCorrecta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id", nullable = false)
    @JsonIgnoreProperties({"respuestas", "evaluacion", "hibernateLazyInitializer"}) 
    private Pregunta pregunta;

    public Respuesta() {
    }

    public Respuesta(String contenido, boolean esCorrecta, Pregunta pregunta) {
        this.contenido = contenido;
        this.esCorrecta = esCorrecta;
        this.pregunta = pregunta;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isEsCorrecta() {
        return esCorrecta;
    }

    public void setEsCorrecta(boolean esCorrecta) {
        this.esCorrecta = esCorrecta;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    @Override
    public String toString() {
        return "Respuesta{" +
                "id=" + id +
                ", contenido='" + contenido + '\'' +
                ", esCorrecta=" + esCorrecta +
                ", preguntaId=" + (pregunta != null ? pregunta.getId() : "null") +
                '}';
    }
}