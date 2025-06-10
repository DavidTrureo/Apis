package com.example.edutech.foro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MensajeCreateDTO {
    @NotBlank(message = "El contenido del mensaje es obligatorio.")
    private String contenido; // TEXT

    @NotBlank(message = "El RUT del autor es obligatorio.")
    private String autorRut;

    @NotNull(message = "El ID del hilo de discusión es obligatorio.")
    private Integer hiloId;

    public MensajeCreateDTO() {
    }

    public MensajeCreateDTO(String contenido, String autorRut, Integer hiloId) {
        this.contenido = contenido;
        this.autorRut = autorRut;
        this.hiloId = hiloId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getAutorRut() {
        return autorRut;
    }

    public void setAutorRut(String autorRut) {
        this.autorRut = autorRut;
    }

    public Integer getHiloId() {
        return hiloId;
    }

    public void setHiloId(Integer hiloId) {
        this.hiloId = hiloId;
    }
}