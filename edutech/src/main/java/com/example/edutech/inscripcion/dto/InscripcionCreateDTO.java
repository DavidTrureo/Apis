package com.example.edutech.inscripcion.dto;

import jakarta.validation.constraints.NotBlank;

public class InscripcionCreateDTO {
    @NotBlank(message = "El RUT del usuario es obligatorio.")
    private String usuarioRut;

    @NotBlank(message = "La sigla del curso es obligatoria.")
    private String cursoSigla;

    private String codigoCupon; // Opcional


    public InscripcionCreateDTO() {}


    public InscripcionCreateDTO(String usuarioRut, String cursoSigla, String codigoCupon) {
        this.usuarioRut = usuarioRut;
        this.cursoSigla = cursoSigla;
        this.codigoCupon = codigoCupon;
    }

    public String getUsuarioRut() { return usuarioRut; }
    public void setUsuarioRut(String usuarioRut) { this.usuarioRut = usuarioRut; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }
    public String getCodigoCupon() { return codigoCupon; }
    public void setCodigoCupon(String codigoCupon) { this.codigoCupon = codigoCupon; }
}