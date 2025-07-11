package com.example.edutech.soporte.dto;

import com.example.edutech.soporte.model.EstadoTicketSoporte;

import jakarta.validation.constraints.NotNull;

public class SoporteActualizarEstadoDTO {
    @NotNull(message = "El nuevo estado es obligatorio.") // Descomentar
    private EstadoTicketSoporte nuevoEstado;
    public EstadoTicketSoporte getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(EstadoTicketSoporte nuevoEstado) { this.nuevoEstado = nuevoEstado; }
}