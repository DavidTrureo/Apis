package com.example.edutech.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

// DTO específico para que el usuario actualice su propio perfil
public class PerfilUpdateDTO {

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre; // Opcional

    @Email(message = "El formato del email no es válido.")
    private String email; // Opcional

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password; // Opcional (solo si se quiere cambiar)
    
    // No se incluye rolNombre ni estadoCuenta, ya que el usuario no debería poder cambiarlos.
    // Detalles de pago y preferencias podrían añadirse aquí si se modelan.

    public PerfilUpdateDTO() {
    }

    public PerfilUpdateDTO(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}