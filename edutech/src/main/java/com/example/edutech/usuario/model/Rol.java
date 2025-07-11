package com.example.edutech.usuario.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol") // Nombre de la tabla explícito
public class Rol {
    @Id
    @Column(name = "nombre", nullable = false, unique = true, length = 50) // Ajusta la longitud si es necesario
    private String nombre; // Este es el nombre del rol y la PK

    @Column(nullable = false, length = 255)
    private String descripcion;

    // --- NUEVA RELACIÓN MUCHOS-A-MUCHOS CON PERMISO ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "rol_permiso",
            joinColumns = @JoinColumn(name = "rol_nombre", referencedColumnName = "nombre"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id", referencedColumnName = "id")
    )
    @JsonIgnoreProperties("roles") // Para evitar bucles al serializar si se cargan los permisos
    private Set<Permiso> permisos = new HashSet<>();
    // --- FIN NUEVA RELACIÓN ---

    public Rol() {
    }

    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    // Métodos helper para la relación
    public void addPermiso(Permiso permiso) {
        this.permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    public void removePermiso(Permiso permiso) {
        this.permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return nombre.equals(rol.nombre);
    }

    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    @Override
    public String toString() {
        return "Rol{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}