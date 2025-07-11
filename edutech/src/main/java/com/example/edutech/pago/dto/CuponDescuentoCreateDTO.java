package com.example.edutech.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.edutech.pago.model.TipoDescuento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CuponDescuentoCreateDTO {

    @NotBlank(message = "El código del cupón es obligatorio.")
    @Size(max = 50, message = "El código no puede exceder los 50 caracteres.")
    private String codigo;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres.")
    private String descripcion;

    @NotNull(message = "El tipo de descuento es obligatorio.")
    private TipoDescuento tipoDescuento;

    @NotNull(message = "El valor del descuento es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El valor del descuento debe ser mayor que cero.")
    // Se añade validación específica en el servicio para porcentajes (0 < valor <= 1)
    private BigDecimal valorDescuento;

    // fechaInicioValidez es opcional, si es null, se asume hoy
    @FutureOrPresent(message = "La fecha de inicio de validez no puede ser pasada.")
    private LocalDate fechaInicioValidez;

    @NotNull(message = "La fecha de expiración es obligatoria.")
    @FutureOrPresent(message = "La fecha de expiración no puede ser pasada.")
    private LocalDate fechaExpiracion;

    @Min(value = 0, message = "Los usos máximos no pueden ser negativos. 0 para ilimitado si se desea manejar así en el servicio.")
    private Integer usosMaximos; // Puede ser null para ilimitado

    private String cursoAplicableSigla; // Opcional

    private Boolean activo; // Opcional, default true en el servicio

    public CuponDescuentoCreateDTO() {
    }

    // Getters y Setters (sin cambios)
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
    public String getCursoAplicableSigla() { return cursoAplicableSigla; }
    public void setCursoAplicableSigla(String cursoAplicableSigla) { this.cursoAplicableSigla = cursoAplicableSigla; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}