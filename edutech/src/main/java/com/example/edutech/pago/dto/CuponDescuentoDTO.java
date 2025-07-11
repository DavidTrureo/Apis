package com.example.edutech.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.edutech.pago.model.TipoDescuento;

public class CuponDescuentoDTO {
    private Integer id;
    private String codigo;
    private String descripcion;
    private TipoDescuento tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDate fechaInicioValidez;
    private LocalDate fechaExpiracion;
    private Integer usosMaximos;
    private int usosActuales;
    private String cursoAplicableSigla; // Solo la sigla para el DTO
    private boolean activo;

    public CuponDescuentoDTO() {}

    public CuponDescuentoDTO(Integer id, String codigo, String descripcion, TipoDescuento tipoDescuento, BigDecimal valorDescuento, LocalDate fechaInicioValidez, LocalDate fechaExpiracion, Integer usosMaximos, int usosActuales, String cursoAplicableSigla, boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipoDescuento = tipoDescuento;
        this.valorDescuento = valorDescuento;
        this.fechaInicioValidez = fechaInicioValidez;
        this.fechaExpiracion = fechaExpiracion;
        this.usosMaximos = usosMaximos;
        this.usosActuales = usosActuales;
        this.cursoAplicableSigla = cursoAplicableSigla;
        this.activo = activo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public TipoDescuento getTipoDescuento() { return tipoDescuento; }
    public void setTipoDescuento(TipoDescuento tipoDescuento) { this.tipoDescuento = tipoDescuento; }
    public BigDecimal getValorDescuento() { return valorDescuento; }
    public void setValorDescuento(BigDecimal valorDescuento) { this.valorDescuento = valorDescuento; }
    public LocalDate getFechaInicioValidez() { return fechaInicioValidez; }
    public void setFechaInicioValidez(LocalDate fechaInicioValidez) { this.fechaInicioValidez = fechaInicioValidez; }
    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDate fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public Integer getUsosMaximos() { return usosMaximos; }
    public void setUsosMaximos(Integer usosMaximos) { this.usosMaximos = usosMaximos; }
    public int getUsosActuales() { return usosActuales; }
    public void setUsosActuales(int usosActuales) { this.usosActuales = usosActuales; }
    public String getCursoAplicableSigla() { return cursoAplicableSigla; }
    public void setCursoAplicableSigla(String cursoAplicableSigla) { this.cursoAplicableSigla = cursoAplicableSigla; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}