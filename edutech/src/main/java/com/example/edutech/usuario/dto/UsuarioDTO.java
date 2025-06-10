
package com.example.edutech.usuario.dto;
//REALIZADO POR: David Trureo
public class UsuarioDTO {
    private String rut;
    private String nombre;
    private String email;
    private String rol; 
    private String estadoCuenta; 

   
    public UsuarioDTO(String rut, String nombre, String email, String rol, String estadoCuenta) {
        this.rut = rut;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
    }

    
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }
}