package com.example.edutech.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponseDTO {
    private Integer id;
    private String usuarioRut;
    private Integer inscripcionId;
    private BigDecimal monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private String estadoPago;
    private String idTransaccionProveedor;

    public PagoResponseDTO() {}

    public PagoResponseDTO(Integer id, String usuarioRut, Integer inscripcionId, BigDecimal monto, String metodoPago, LocalDateTime fechaPago, String estadoPago, String idTransaccionProveedor) {
        this.id = id;
        this.usuarioRut = usuarioRut;
        this.inscripcionId = inscripcionId;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.estadoPago = estadoPago;
        this.idTransaccionProveedor = idTransaccionProveedor;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsuarioRut() { return usuarioRut; }
    public void setUsuarioRut(String usuarioRut) { this.usuarioRut = usuarioRut; }
    public Integer getInscripcionId() { return inscripcionId; }
    public void setInscripcionId(Integer inscripcionId) { this.inscripcionId = inscripcionId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public String getIdTransaccionProveedor() { return idTransaccionProveedor; }
    public void setIdTransaccionProveedor(String idTransaccionProveedor) { this.idTransaccionProveedor = idTransaccionProveedor; }
}