package com.example.edutech.usuario.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "permiso")
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombrePermiso; // Ej: "CREAR_CURSO", "VER_USUARIOS", "ELIMINAR_RESENA"

    @Column(length = 255)
    private String descripcion;

    @ManyToMany(mappedBy = "permisos", fetch = FetchType.LAZY)
    @JsonIgnore // Evitar serialización cíclica y exposición innecesaria
    private Set<Rol> roles = new HashSet<>();

    public Permiso() {
    }

    public Permiso(String nombrePermiso, String descripcion) {
        this.nombrePermiso = nombrePermiso;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombrePermiso() {
        return nombrePermiso;
    }

    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permiso permiso = (Permiso) o;
        return nombrePermiso.equals(permiso.nombrePermiso);
    }

    @Override
    public int hashCode() {
        return nombrePermiso.hashCode();
    }

    @Override
    public String toString() {
        return "Permiso{" +
                "id=" + id +
                ", nombrePermiso='" + nombrePermiso + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}