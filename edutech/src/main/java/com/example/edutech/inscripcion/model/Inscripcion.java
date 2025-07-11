package com.example.edutech.inscripcion.model;

import java.math.BigDecimal;
import java.time.LocalDateTime; 
import java.util.List;

import com.example.edutech.curso.model.Curso;
import com.example.edutech.pago.model.CuponDescuento;
import com.example.edutech.usuario.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat; 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "inscripcion")
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_rut", nullable = false)
    @JsonIgnoreProperties({"cursos", "password", "hibernateLazyInitializer", "roles", "resenas", "pagos", "soportes", "progresos"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_sigla", nullable = false)
    @JsonIgnoreProperties({"contenidos", "instructor", "alumnos", "hibernateLazyInitializer", "resenas"})
    private Curso curso;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaInscripcion;

    @Column(precision = 10, scale = 2) 
    private BigDecimal precioPagado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupon_descuento_id") 
    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private CuponDescuento cuponAplicado;

    @Column(length = 50)
    private String estadoInscripcion; 

    @Column(length = 50)
    private String estadoPago; 

    
    @OneToMany(mappedBy = "inscripcion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("inscripcion")
    private List<ProgresoEstudiante> progreso;


    public Inscripcion() {
        this.fechaInscripcion = LocalDateTime.now();
        this.estadoInscripcion = "PENDIENTE_PAGO"; 
        this.estadoPago = "PENDIENTE";
    }


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public BigDecimal getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(BigDecimal precioPagado) { this.precioPagado = precioPagado; }
    public CuponDescuento getCuponAplicado() { return cuponAplicado; }
    public void setCuponAplicado(CuponDescuento cuponAplicado) { this.cuponAplicado = cuponAplicado; }
    public String getEstadoInscripcion() { return estadoInscripcion; }
    public void setEstadoInscripcion(String estadoInscripcion) { this.estadoInscripcion = estadoInscripcion; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public List<ProgresoEstudiante> getProgreso() { return progreso; }
    public void setProgreso(List<ProgresoEstudiante> progreso) { this.progreso = progreso; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "id=" + id +
                ", usuario=" + (usuario != null ? usuario.getRut() : null) +
                ", curso=" + (curso != null ? curso.getSigla() : null) +
                ", fechaInscripcion=" + fechaInscripcion +
                ", precioPagado=" + precioPagado +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }
}