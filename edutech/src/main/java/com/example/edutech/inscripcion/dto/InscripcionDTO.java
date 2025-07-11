package com.example.edutech.inscripcion.dto;
//REALIZADO POR: Cristóbal Mira
public class InscripcionDTO {
    private String usuarioRut;
    private String cursoSigla;



   
    public InscripcionDTO(String usuarioRut, String cursoSigla) {
        this.usuarioRut = usuarioRut;
        this.cursoSigla = cursoSigla;
    }
    
    

    public String getUsuarioRut() {
        return usuarioRut;
    }

    public void setUsuarioRut(String usuarioRut) {
        this.usuarioRut = usuarioRut;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

}
