package com.example.edutech.evaluacion.dto;
//REALIZADO POR: Cristóbal Mira


public class EvaluacionDTO {
    private Integer id;
    private String nombre;
    private String tipo;
    private String cursoSigla;


    public EvaluacionDTO() {}

    public EvaluacionDTO(Integer id, String nombre, String tipo, String cursoSigla ) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cursoSigla = cursoSigla;

    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCursoSigla() { return cursoSigla; }
    public void setCursoSigla(String cursoSigla) { this.cursoSigla = cursoSigla; }

}