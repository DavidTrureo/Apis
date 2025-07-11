package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioCreateDTO {

    @NotBlank(message = "El RUT es obligatorio.")
    // Podría añadir una validación de formato de RUT si tengo una librería o regex
    private String rut;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre;

    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    @NotBlank(message = "El nombre del rol es obligatorio.")
    private String rolNombre;

    // estadoCuenta es opcional al crear, se puede poner un default en el servicio
    private String estadoCuenta;

    public UsuarioCreateDTO() {
    }

    public UsuarioCreateDTO(String rut, String nombre, String email, String password, String rolNombre, String estadoCuenta) {
        this.rut = rut;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolNombre = rolNombre;
        this.estadoCuenta = estadoCuenta;
    }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
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