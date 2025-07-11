package com.example.edutech.proveedor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProveedorDTO {

    @NotBlank(message = "El RUT del proveedor es obligatorio.")
    @Size(max = 15, message = "El RUT no puede exceder los 15 caracteres.")
    private String rut;

    @NotBlank(message = "El nombre del proveedor es obligatorio.")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres.")
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.") // Ajusta si es necesario
    private String descripcion;

    @NotBlank(message = "El email del proveedor es obligatorio.")
    @Email(message = "El formato del email no es válido.")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres.")
    private String mail;

    // Constructor vacío para Jackson
    public ProveedorDTO() {
    }

    public ProveedorDTO(String rut, String nombre, String descripcion, String mail) {
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