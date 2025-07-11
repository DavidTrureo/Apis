package com.example.edutech.soporte.dto;

import jakarta.validation.constraints.NotBlank;

public class SoporteResolverDTO {
    @NotBlank(message = "La solución aplicada es obligatoria.") // Descomentar
    private String solucionAplicada;

    @NotBlank(message = "El RUT del agente que resuelve es obligatorio.") // Descomentar
    private String rutAgenteResolutor; // Para registrar quién lo resolvió

    public String getSolucionAplicada() { return solucionAplicada; }
    public void setSolucionAplicada(String solucionAplicada) { this.solucionAplicada = solucionAplicada; }
    public String getRutAgenteResolutor() { return rutAgenteResolutor; }
    public void setRutAgenteResolutor(String rutAgenteResolutor) { this.rutAgenteResolutor = rutAgenteResolutor; }
}