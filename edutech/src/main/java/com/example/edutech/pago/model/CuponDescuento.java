package com.example.edutech.pago.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.edutech.curso.model.Curso;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cupon_descuento")
public class CuponDescuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(length = 255)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoDescuento tipoDescuento; 

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento; 

    @Column
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaInicioValidez; 
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaExpiracion;

    @Column
    private Integer usosMaximos; 

    @Column(nullable = false)
    private int usosActuales = 0; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_aplicable_sigla", referencedColumnName = "sigla") 
    @JsonIgnoreProperties({"contenidos", "instructor", "alumnos", "inscripciones", "resenas", "foro", "hibernateLazyInitializer"}) // Ajusta según las relaciones de Curso
    private Curso cursoAplicable;

    @Column(nullable = false)
    private boolean activo = true; 
    

    public CuponDescuento() {
    }

  
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoDescuento getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(TipoDescuento tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(BigDecimal valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public LocalDate getFechaInicioValidez() {
        return fechaInicioValidez;
    }

    public void setFechaInicioValidez(LocalDate fechaInicioValidez) {
        this.fechaInicioValidez = fechaInicioValidez;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Integer getUsosMaximos() {
        return usosMaximos;
    }

    public void setUsosMaximos(Integer usosMaximos) {
        this.usosMaximos = usosMaximos;
    }

    public int getUsosActuales() {
        return usosActuales;
    }

    public void setUsosActuales(int usosActuales) {
        this.usosActuales = usosActuales;
    }

    public Curso getCursoAplicable() {
        return cursoAplicable;
    }

    public void setCursoAplicable(Curso cursoAplicable) {
        this.cursoAplicable = cursoAplicable;
    }

    public boolean isActivo() { 
        return activo;
    }

    public void setActivo(boolean activo) { 
        this.activo = activo;
    }

    
    public void incrementarUso() {
        this.usosActuales++;
    }

   
    public boolean esValido(Curso cursoAlQueSeAplica) {
        LocalDate hoy = LocalDate.now();

        
        if (!this.activo) {
            return false;
        }

        if (this.fechaInicioValidez != null && hoy.isBefore(this.fechaInicioValidez)) {
            return false; 
        }
        if (hoy.isAfter(this.fechaExpiracion)) {
            return false; 
        }

        
        if (this.usosMaximos != null && this.usosActuales >= this.usosMaximos) {
            return false; 
        }

        
        if (this.cursoAplicable != null) { 
            if (cursoAlQueSeAplica == null || !this.cursoAplicable.getSigla().equals(cursoAlQueSeAplica.getSigla())) {
                return false; 
            }
        }
       
        return true;
    }

    @Override
    public String toString() {
        return "CuponDescuento{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", tipoDescuento=" + tipoDescuento +
                ", valorDescuento=" + valorDescuento +
                ", fechaExpiracion=" + fechaExpiracion +
                ", activo=" + activo +
                ", usosActuales=" + usosActuales +
                (cursoAplicable != null ? ", cursoAplicable=" + cursoAplicable.getSigla() : "") +
                '}';
    }
}