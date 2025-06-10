package com.example.edutech.foro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class HiloCreateDTO {
    @NotBlank(message = "El título del hilo es obligatorio.")
    @Size(max = 255, message = "El título no puede exceder los 255 caracteres.")
    private String titulo;

    @NotBlank(message = "El contenido inicial del hilo es obligatorio.")
    private String contenidoInicial; // TEXT, sin @Size específico a menos que se requiera

    @NotBlank(message = "El RUT del autor es obligatorio.")
    private String autorRut;

    @NotNull(message = "El ID del foro es obligatorio.")
    private Integer foroId;

    public HiloCreateDTO() {
    }

    public HiloCreateDTO(String titulo, String contenidoInicial, String autorRut, Integer foroId) {
        this.titulo = titulo;
        this.contenidoInicial = contenidoInicial;
        this.autorRut = autorRut;
        this.foroId = foroId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenidoInicial() {
        return contenidoInicial;
    }

    public void setContenidoInicial(String contenidoInicial) {
        this.contenidoInicial = contenidoInicial;
    }

    public String getAutorRut() {
        return autorRut;
    }

    public void setAutorRut(String autorRut) {
        this.autorRut = autorRut;
    }

    public Integer getForoId() {
        return foroId;
    }

    public void setForoId(Integer foroId) {
        this.foroId = foroId;
    }
}