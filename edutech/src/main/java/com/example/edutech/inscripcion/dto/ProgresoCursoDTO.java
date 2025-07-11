package com.example.edutech.inscripcion.dto;

public class ProgresoCursoDTO {
    private Integer inscripcionId;
    private String usuarioRut;
    private String usuarioNombre;
    private String cursoSigla;
    private String cursoNombre;
    private int totalContenidos;
    private int contenidosCompletados;
    private double porcentajeCompletado;
    private String mensaje;


    public ProgresoCursoDTO() {
    }

    public ProgresoCursoDTO(Integer inscripcionId, String usuarioRut, String usuarioNombre, String cursoSigla, String cursoNombre, int totalContenidos, int contenidosCompletados, double porcentajeCompletado, String mensaje) {
        this.inscripcionId = inscripcionId;
        this.usuarioRut = usuarioRut;
        this.usuarioNombre = usuarioNombre;
        this.cursoSigla = cursoSigla;
        this.cursoNombre = cursoNombre;
        this.totalContenidos = totalContenidos;
        this.contenidosCompletados = contenidosCompletados;
        this.porcentajeCompletado = porcentajeCompletado;
        this.mensaje = mensaje;
    }

    
    public Integer getInscripcionId() {
        return inscripcionId;
    }

    public String getUsuarioRut() {
        return usuarioRut;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public String getCursoSigla() {
        return cursoSigla;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public int getTotalContenidos() {
        return totalContenidos;
    }

    public int getContenidosCompletados() {
        return contenidosCompletados;
    }

    public double getPorcentajeCompletado() {
        return porcentajeCompletado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setInscripcionId(Integer inscripcionId) {
        this.inscripcionId = inscripcionId;
    }

    public void setUsuarioRut(String usuarioRut) {
        this.usuarioRut = usuarioRut;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public void setCursoSigla(String cursoSigla) {
        this.cursoSigla = cursoSigla;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

    public void setTotalContenidos(int totalContenidos) {
        this.totalContenidos = totalContenidos;
    }

    public void setContenidosCompletados(int contenidosCompletados) {
        this.contenidosCompletados = contenidosCompletados;
    }

    public void setPorcentajeCompletado(double porcentajeCompletado) {
        this.porcentajeCompletado = porcentajeCompletado;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}