package com.example.edutech.soporte.dto;

import jakarta.validation.constraints.NotBlank; // Importar

public class SoporteAsignarAgenteDTO {
    @NotBlank(message = "El RUT del agente de soporte es obligatorio.") // Descomentar
    private String rutAgenteAsignado;
    public String getRutAgenteAsignado() { return rutAgenteAsignado; }
    public void setRutAgenteAsignado(String rutAgenteAsignado) { this.rutAgenteAsignado = rutAgenteAsignado; }
}