package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UsuarioUpdateDTO {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre; // Opcional

    @Email(message = "El formato del email no es válido.")
    private String email; // Opcional

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password; // Opcional (solo si se quiere cambiar)

    private String rolNombre; // Opcional

    private String estadoCuenta; // Opcional

    public UsuarioUpdateDTO() {
    }

    public UsuarioUpdateDTO(String nombre, String email, String password, String rolNombre, String estadoCuenta) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolNombre = rolNombre;
        this.estadoCuenta = estadoCuenta;
    }

    // Getters y Setters (sin cambios)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }
}