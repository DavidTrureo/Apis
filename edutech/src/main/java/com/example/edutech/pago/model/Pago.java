package com.example.edutech.pago.model;

import java.math.BigDecimal; 
import java.time.LocalDateTime;

import com.example.edutech.inscripcion.model.Inscripcion;
import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_rut", nullable = false) 
    @JsonIgnoreProperties({"cursos", "password", "hibernateLazyInitializer", "roles", "inscripciones", "resenas", "pagos", "soportes", "progresos"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id") 
    @JsonIgnoreProperties({"usuario", "curso", "progreso", "hibernateLazyInitializer"})
    private Inscripcion inscripcion; 

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 50)
    private String metodoPago; 

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaPago;

    @Column(nullable = false, length = 50)
    private String estadoPago; 
    @Column(length = 255)
    private String idTransaccionProveedor; 

    public Pago() {
        this.fechaPago = LocalDateTime.now();
        this.estadoPago = "PENDIENTE";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Inscripcion getInscripcion() { return inscripcion; }
    public void setInscripcion(Inscripcion inscripcion) { this.inscripcion = inscripcion; }
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

    @Override
    public String toString() {
        return "Pago{" + "id=" + id + ", monto=" + monto + ", estadoPago='" + estadoPago + '\'' + '}';
    }
}