package com.example.edutech.proveedor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedor") // Nombre de la tabla
public class Proveedor {

    @Id
    @Column(nullable = false, unique = true, length = 15) // Ajustar longitud si es necesario
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100) // Ajustar longitud si es necesario
    private String mail;

    public Proveedor() {
    }

    public Proveedor(String rut, String nombre, String descripcion, String mail) {
        this.rut = rut;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.mail = mail;
    }

    public String getRut() {
        return rut;
    }
    public void setRut(String rut) {
        this.rut = rut;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
}