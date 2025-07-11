package com.example.edutech.soporte.model;

public enum EstadoTicketSoporte {
    ABIERTO,
    EN_PROCESO,
    EN_ESPERA_CLIENTE, // Cuando se necesita más información del usuario
    RESUELTO,
    CERRADO
}