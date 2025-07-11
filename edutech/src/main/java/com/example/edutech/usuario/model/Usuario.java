package com.example.edutech.usuario.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @Column(name = "rut", nullable = false, unique = true, length = 15)
    private String rut;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private String estadoCuenta = "Activo";

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_nombre", referencedColumnName = "nombre")
    private Rol rol;

    public Usuario() {}

    public Usuario(String rut, String nombre, String email, String estadoCuenta, String password, Rol rol) {
        this.rut = rut;
        this.nombre = nombre;
        this.email = email;
        this.estadoCuenta = estadoCuenta;
        this.password = password;
        this.rol = rol;
    }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "Usuario{" +
                "rut='" + rut + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", estadoCuenta='" + estadoCuenta + '\'' +
                ", rol=" + (rol != null ? rol.getNombre() : "Sin rol asignado") +
                '}';
    }
}